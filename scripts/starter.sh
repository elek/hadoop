#!/usr/bin/env bash
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
$DIR/envtoconf.py --destination /opt/hadoop/etc/hadoop

if [ -n "$ENSURE_NAMENODE_DIR" ]; then
   if [ ! -d "$ENSURE_NAMENODE_DIR" ]; then
      /opt/hadoop/bin/hdfs namenode -format
   fi
fi

if [ -n "$ENSURE_SCM_INITIALIZED" ]; then
   if [ ! -f "$ENSURE_SCM_INITIALIZED" ]; then
      /opt/hadoop/bin/hdfs scm -init
   fi
fi

$@

