FROM frolvlad/alpine-oraclejdk8:cleaned
RUN apk add --update bash ca-certificates openssl jq curl && rm -rf /var/cache/apk/* && update-ca-certificates

RUN wget -O /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.0/dumb-init_1.2.0_amd64
RUN chmod +x /usr/local/bin/dumb-init

ADD https://github.com/elek/envtoconf/releases/download/1.1.1/linux_amd64_envtoconf /usr/bin/envtoconf
RUN chmod +x /usr/bin/envtoconf

ENV JAVA_HOME=/usr/lib/jvm/java-8-oracle
ENV PATH $PATH:/opt/hadoop/bin

WORKDIR /opt

ADD .bashrc /root/


ADD url /opt
RUN wget `cat url` -O hadoop.tar.gz && tar zxf hadoop.tar.gz && rm hadoop.tar.gz && mv hadoop* hadoop && rm -rf /opt/hadoop/share/doc

ADD log4j.properties /opt/hadoop/etc/hadoop/

VOLUME /opt/hadoop/data

ADD starter.sh /opt/starter.sh

ENTRYPOINT ["/usr/local/bin/dumb-init", "--", "/opt/starter.sh"]
