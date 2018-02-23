FROM apache/hadoop-runner
ARG HADOOP_URL=http://mirror.jax.hugeserver.com/apache/hadoop/common/hadoop-2.9.0/hadoop-2.9.0.tar.gz
WORKDIR /opt
RUN sudo rm -rf /opt/hadoop && wget $HADOOP_URL -O hadoop.tar.gz && tar zxf hadoop.tar.gz && rm hadoop.tar.gz && mv hadoop* hadoop && rm -rf /opt/hadoop/share/doc
WORKDIR /opt/hadoop
ADD log4j.properties /opt/hadoop/etc/hadoop/log4j.properties
