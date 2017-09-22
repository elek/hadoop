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
Ozone - Getting Started
==============================

<!-- MACRO{toc|fromDepth=0|toDepth=3} -->

Ozone is an object store for Hadoop. It  is a redundant, distributed object
store build by leveraging primitives present in HDFS. Ozone supports REST
API for accessing the store.

Ozone is a work in progress and currently in alpha state. To test it you need
to [build if from the source code](https://cwiki.apache.org/confluence/display/HADOOP/Ozone) or use Hadoop version higher than 3.1.

To run an Ozone cluster you have multiple option:

 1. You can start with precreated docker images
 2. Or start a pseudo cluster locally from the downloaded release giles.


Starting Ozone with docker
--------------------------

The easiest to start an Ozone cluster is using precreated docker images uploaded to the docker hub.

Please note that the docker images are not provided by the Apache Hadoop project, [yet](https://issues.apache.org/jira/browse/HADOOP-14898). This method uses third-party docker images.

The only (two) thing what you need is a docker-compose.yaml file:

```
version: "3"
services:
   namenode:
      image: flokkr/hadoop:ozone
      hostname: namenode
      ports:
         - 50070:50070
         - 9870:9870
      environment:
          ENSURE_NAMENODE_DIR: /data/namenode
      env_file:
         - ./docker-config
      command: ["/opt/hadoop/bin/hdfs","namenode"]
   datanode:
      image: flokkr/hadoop:ozone
      ports:
        - 9864
      env_file:
         - ./docker-config
      command: ["/opt/hadoop/bin/hdfs","datanode"]
   ksm:
      image: flokkr/hadoop:ozone
      ports:
         - 9874:9874
      env_file:
          - ./docker-config
      command: ["/opt/hadoop/bin/hdfs","ksm"]
   scm:
      image: flokkr/hadoop:ozone
      ports:
         - 9876:9876
      env_file:
          - ./docker-config
      command: ["/opt/hadoop/bin/hdfs","scm"]
```

And a docker-config file:

```
CORE-SITE.XML_fs.defaultFS=hdfs://namenode:9000
OZONE-SITE.XML_ozone.ksm.address=ksm
OZONE-SITE.XML_ozone.scm.names=scm
OZONE-SITE.XML_ozone.enabled=True
OZONE-SITE.XML_ozone.scm.datanode.id=/data/datanode.id
OZONE-SITE.XML_ozone.scm.block.client.address=scm
OZONE-SITE.XML_ozone.container.metadata.dirs=/data/metadata
OZONE-SITE.XML_ozone.handler.type=distributed
OZONE-SITE.XML_ozone.scm.client.address=scm
HDFS-SITE.XML_dfs.namenode.rpc-address=namenode:9000
HDFS-SITE.XML_dfs.namenode.name.dir=/data/namenode
LOG4J.PROPERTIES_log4j.rootLogger=INFO, stdout
LOG4J.PROPERTIES_log4j.appender.stdout=org.apache.log4j.ConsoleAppender
LOG4J.PROPERTIES_log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
LOG4J.PROPERTIES_log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
```

Please note, that:

 1. The docker-config file only for defining environment variables. We just moved out to an external file to use it for all the containers.
 2. For more detailed exaplanation of the Configuration variables see the [OzoneConfiguration](./OzoneConfiguration.html) page.
 3. The flokkr base image contains a [simple script](https://github.com/elek/envtoconf) to convert environment variables to files, based on naming convention. All of the environment variables will be converted to traditional Hadoop config XMLs and log4j configuration.

After saving the two files to a directory it's time to start the cluster (run the commands
from the directory where the two files are saved to)

```
docker-compose up -d
```

You can check the status of the components:

```
docker-compose ps
```

You can check the output of the servers with:

```
docker-compose logs scm
```

As the webui ports are forwarded to the external machine, you can check the web UI:

* For the Storage Container Manager: http://localhost:9876
* For the Key Space Manager: http://localhost:9874
* For the Datanode: check the port with docker ps (as there could be multiple datanodes ports are mapped to the ephemeral port range)

You can start multiple datanodes with:

```
   docker-compose scale datanode=3
```

You can test the commands from the [OzoneCommandShell](./OzoneCommandShell.md) page after opening a new shell in one of the containers:

```
docker-compose exec datanode bash
```

Starting Ozone from a release
---------------------------------

The second easiest way is to start Ozone from a Hadoop release, but *at least Hadoop 3.1 is required.* Download the released tar file and untar it to a location.


### Configure the cluster

After unpack your distribution, create an `ozone-site.xml` to the `etc/hadoop/` directory.

```
<properties>
<property><name>ozone.ksm.address</name><value>localhost</value></property>
<property><name>ozone.scm.datanode.id</name><value>/tmp/datanode.id</value></property>
<property><name>ozone.scm.names</name><value>localhost</value></property>
<property><name>ozone.handler.type</name><value>distributed</value></property>
<property><name>ozone.container.metadata.dirs</name><value>/tmp/metadata</value></property>
<property><name>ozone.scm.block.client.address</name><value>localhost</value></property>
<property><name>ozone.scm.client.address</name><value>localhost</value></property>
<property><name>ozone.enabled</name><value>True</value></property>
</properties>
```

For more detailed exaplanation of the Configuration variables see the [OzoneConfiguration](./OzoneConfiguration.html) page.


### Start the cluster

Ozone is designed to run concurrently with HDFS. The simplest way to [start
HDFS](../hadoop-common/ClusterSetup.html) is to run `start-dfs.sh` from the
`$HADOOP/sbin/start-dfs.sh`. Once HDFS
is running, please verify it is fully functional by running some commands like

- *./hdfs dfs -mkdir /usr*
- *./hdfs dfs -ls /*

 Once you are sure that HDFS is running, start Ozone. To start  ozone, you
 need to start SCM and KSM. Currently we assume that both KSM and SCM
  is running on the same node, this will change in future.

- `./hdfs --daemon start scm`
- `./hdfs --daemon start ksm`

if you would like to start HDFS and Ozone together, you can do that by running
 a single command.
- `$HADOOP/sbin/start-ozone.sh`

 This command will start HDFS and then start the ozone components.

 Once you have ozone running you can use these ozone [shell](./OzoneCommandShell.html)
 commands to  create a  volume, bucket and keys.


### Diagnosing issues

Ozone tries not to pollute the existing HDFS streams of configuration and
logging. So ozone logs are by default configured to be written to a file
called `ozone.log`. This is controlled by the settings in `log4j.properties`
file in the hadoop configuration directory.

Here is the log4j properties that are added by ozone.


```
   #
   # Add a logger for ozone that is separate from the Datanode.
   #
   #log4j.debug=true
   log4j.logger.org.apache.hadoop.ozone=DEBUG,OZONE,FILE

   # Do not log into datanode logs. Remove this line to have single log.
   log4j.additivity.org.apache.hadoop.ozone=false

   # For development purposes, log both to console and log file.
   log4j.appender.OZONE=org.apache.log4j.ConsoleAppender
   log4j.appender.OZONE.Threshold=info
   log4j.appender.OZONE.layout=org.apache.log4j.PatternLayout
   log4j.appender.OZONE.layout.ConversionPattern=%d{ISO8601} [%t] %-5p \
    %X{component} %X{function} %X{resource} %X{user} %X{request} - %m%n

   # Real ozone logger that writes to ozone.log
   log4j.appender.FILE=org.apache.log4j.DailyRollingFileAppender
   log4j.appender.FILE.File=${hadoop.log.dir}/ozone.log
   log4j.appender.FILE.Threshold=debug
   log4j.appender.FILE.layout=org.apache.log4j.PatternLayout
   log4j.appender.FILE.layout.ConversionPattern=%d{ISO8601} [%t] %-5p \
     (%F:%L) %X{function} %X{resource} %X{user} %X{request} - \
      %m%n
```

If you would like to have a single datanode log instead of ozone stuff
getting written to ozone.log, please remove this line or set this to true.

 ` log4j.additivity.org.apache.hadoop.ozone=false`

On the SCM/KSM side, you will be able to see

- `hadoop-hdfs-ksm-hostname.log`
- `hadoop-hdfs-scm-hostname.log`

Please file any issues you see under [Object store in HDFS (HDFS-7240)](https://issues.apache.org/jira/browse/HDFS-7240) as this is still a work in progress.
