<!---
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

Ozone Configuration
===================

Ozone relies on its own configuration file called `ozone-site.xml`. It is
just for convenience and ease of management --  you can add these settings
to `hdfs-site.xml`, if you don't want to keep ozone settings separate.
This document refers to `ozone-site.xml` so that ozone settings are in one
place  and not mingled with HDFS settings.


Key Ozone configuration parameters
----------------------------------


* _*ozone.enabled*_  This is the most important setting for ozone.
 Currently, Ozone is an opt-in subsystem of HDFS. By default, Ozone is
 disabled. Setting this flag to `true` enables ozone in the HDFS cluster.
 Here is an example,

```
    <property>
       <name>ozone.enabled</name>
       <value>True</value>
    </property>
```
 *  _*ozone.container.metadata.dirs*_ Ozone is designed with modern hardware
 in mind. It tries to use SSDs effectively. So users can specify where the
 datanode metadata must reside. Usually you pick your fastest disk (SSD if
 you have them on your datanodes). Datanodes will write the container metadata
 to these disks. This is a required setting, if this is missing datanodes will
 fail to come up. Here is an example,

```
   <property>
      <name>ozone.container.metadata.dirs</name>
      <value>/data/disk1/container/meta</value>
   </property>
```

* _*ozone.scm.names*_ Ozone is build on top of container framework (See Ozone
 Architecture TODO). Storage container manager(SCM) is a distributed block
 service which is used by ozone and other storage services.
 This property allows datanodes to discover where SCM is, so that
 datanodes can send heartbeat to SCM. SCM is designed to be highly available
 and datanodes assume there are multiple instances of SCM which form a highly
 available ring. The HA feature of SCM is a work in progress. So we
 configure ozone.scm.names to be a single machine. Here is an example,

```
    <property>
      <name>ozone.scm.names</name>
      <value>scm.hadoop.apache.org</value>
    </property>
```

* _*ozone.scm.datanode.id*_ Each datanode that speaks to SCM generates an ID
just like HDFS. This ID is stored is a location pointed by this setting. If
this setting is not valid, datanodes will fail to come up. Please note:
This path that is will created by datanodes to store the datanode ID. Here is an example,

```
   <property>
      <name>ozone.scm.datanode.id</name>
      <value>/data/disk1/scm/meta/node/datanode.id</value>
   </property>
```

* _*ozone.scm.block.client.address*_ Storage Container Manager(SCM) offers a
 set of services that can be used to build a distributed storage system. One
 of the services offered is the block services. KSM and HDFS would use this
 service. This property describes where KSM can discover SCM's block service
 endpoint. There is corresponding ports etc, but assuming that we are using
 default ports, the server address is the only required field. Here is an
 example,

```
    <property>
      <name>ozone.scm.block.client.address</name>
      <value>scm.hadoop.apache.org</value>
    </property>
```

* _*ozone.ksm.address*_ KSM server address. This is used by Ozonehandler and
Ozone File System.

```
    <property>
       <name>ozone.ksm.address</name>
       <value>ksm.hadoop.apache.org</value>
    </property>
```

Here is a quick summary of settings needed by Ozone.

| Setting                        | Value                        | Comment |
|--------------------------------|------------------------------|------------------------------------------------------------------|
| ozone.enabled                  | True                         | This enables SCM and  containers in HDFS cluster.                |
| ozone.container.metadata.dirs  | file path                    | The container metadata will be stored here in the datanode.      |
| ozone.scm.names                | SCM server name              | Hostname:port or or IP:port address of SCM.                      |
| ozone.scm.datanode.id          | file path                    | Data node ID is the location of  datanode's ID file              |
| ozone.scm.block.client.address | SCM server name              | Used by services like KSM                                        |
| ozone.ksm.address              | KSM server name              | Used by Ozone handler and Ozone file system.                     |

 Here is a working example of`ozone-site.xml`.

```
    <?xml version="1.0" encoding="UTF-8"?>
    <?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
    <configuration>
      <property>
          <name>ozone.enabled</name>
          <value>True</value>
        </property>

        <property>
          <name>ozone.container.metadata.dirs</name>
          <value>/data/disk1/scm/meta</value>
        </property>


        <property>
          <name>ozone.scm.names</name>
          <value>scm.hadoop.apache.org</value>
        </property>

        <property>
          <name>ozone.scm.datanode.id</name>
          <value>/data/disk1/scm/meta/node/datanode.id</value>
        </property>

        <property>
          <name>ozone.scm.block.client.address</name>
          <value>scm.hadoop.apache.org</value>
        </property>

         <property>
            <name>ozone.ksm.address</name>
            <value>ksm.hadoop.apache.org</value>
          </property>
    </configuration>
```
