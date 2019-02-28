#!/usr/bin/env bash
docker-compose scale datanode=3
docker-compose exec scm ozone sh volume create /vol1
docker-compose exec scm ozone sh bucket create /vol1/bucket1
docker-compose exec scm sudo yum install -y awscli
docker-compose exec scm aws s3api --endpoint http://s3g:9878 create-bucket --bucket test
