FROM frolvlad/alpine-oraclejdk8:cleaned
RUN apk add --update bash ca-certificates openssl jq curl python libstdc++ && rm -rf /var/cache/apk/* && update-ca-certificates

RUN wget -O /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.0/dumb-init_1.2.0_amd64
RUN chmod +x /usr/local/bin/dumb-init

ENV JAVA_HOME=/usr/lib/jvm/java-8-oracle
ENV PATH $PATH:/opt/hadoop/bin


RUN mkdir -p /opt/hadoop
WORKDIR /opt/hadoop

ADD scripts /opt/

ADD .bashrc /root/

VOLUME /data

ENTRYPOINT ["/usr/local/bin/dumb-init", "--", "/opt/starter.sh"]
