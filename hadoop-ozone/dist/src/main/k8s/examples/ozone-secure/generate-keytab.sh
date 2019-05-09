#!/usr/bin/env bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This is a simple example to create a keytab config file from MIT kerberos server.
# You should generate the keytab files and add it to a configmap as base64 encoded string

KDC_POD_NAME=$(kubectl get pod -l app=krb5 -o name)
#remove pod/ prefix
KDC_POD_NAME="${KDC_POD_NAME:4}"
OUTPUT_FILE=keytab_config.yaml

create_keytab(){
   FILE=$1
   kubectl exec -it "$KDC_POD_NAME" -- rm "/tmp/$FILE"
}


add_principal(){
   FILE=$1
   NAME=$2
   PRINCIPAL=$NAME@EXAMPLE.COM
   kubectl exec -it "$KDC_POD_NAME" -- kadmin.local -q "addprinc -randkey $PRINCIPAL"
   kubectl exec -it "$KDC_POD_NAME" -- kadmin.local -q "ktadd -k /tmp/$FILE $PRINCIPAL"
}

get_keytab() {
   FILE=$1
   KEYTAB_CONTENT=$(kubectl exec -it "$KDC_POD_NAME" -- base64 -w 0 "/tmp/$FILE")
   echo "   $FILE: $KEYTAB_CONTENT" >> "$OUTPUT_FILE"
}

cat << EOF > $OUTPUT_FILE
apiVersion: v1
kind: ConfigMap
metadata:
  name: keytabs
binaryData:
EOF

create_keytab scm.keytab
add_principal scm.keytab scm/scm
get_keytab scm.keytab

create_keytab om.keytab
add_principal om.keytab om/om
get_keytab om.keytab

create_keytab HTTP.keytab
add_principal HTTP.keytab HTTP/scm
add_principal HTTP.keytab HTTP/om
add_principal HTTP.keytab HTTP/s3g
get_keytab HTTP.keytab

create_keytab dn.keytab
add_principal dn.keytab dn/dn
get_keytab dn.keytab
