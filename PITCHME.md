# Hadoop in Docker (HADOOP-14898) 

Three usecases to use docker images with Hadoop 

---

### Case #1: Test your development build locally

 1. Do a full dist build
 2. Go to hadoop-dist/target/compose
 3. Start a multi-node cluster in docker with the latest build

---

### Case #1 requirements

 - Base hadoop-runner image (HADOOP-15083) on dockerhub (doesn't contain any hadoop distribution just the starter script)
 - Docker compose file for the pseudo cluster (HDFS: HADOOP-12258)

---

### Case #2: Demonstrate features of Hadoop

 1. Don't need to do a build. 
 2. Just download example docker-compose and config from documentation
 3. And start a cluster locally

---

### Case #2: Requirements

 - Full Hadoop docker image on dockerhub (hadoop2: HADOOP-15256, hadoop3: HADOOP-15084). 
 - Additional docker-compose/config for selected features (HA example: HADOOP-12258)

---

### Case #3: Start real cluster from custom build 

 1. Do a full dist build
 2. Create docker container from the result distribution 
 3. Push it/use it/run it
 
---

### Case #3. Requirements

 1. Dockerfile in the hadoop-dist project (HADOOP-12258)

---

### Thank you.

 - Please comment on HADOOP-14898
 - Marton Elek (elek@apache)

