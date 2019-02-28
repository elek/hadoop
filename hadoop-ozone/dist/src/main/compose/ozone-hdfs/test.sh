#!/usr/bin/env bash
docker-compose exec resourcemanager yarn jar ./share/hadoop/mapreduce/hadoop-mapreduce-examples-3.1.1.jar pi -libjars /opt/ozone/share/ozone/lib/hadoop-ozone-filesystem-lib-legacy-0.4.0-SNAPSHOT.jar  10 10
