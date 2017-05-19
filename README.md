This is the definition of the official Hadoop Apache docker image.

To build the images use:

```
docker build -t apache/hadoop .
```

To start a cluster go the example directory and run the following commands:

At the first time:
```
docker-compose run namenode hdfs namenode -format
```

To start the hdfs cluster:

```
docker-compose up -d namenode
docker-compose up -d datanode
```

Test the hdfs cluster:

```
docker exec example_namenode_1 hdfs dfs -mkdir /test
```

Or just check http://localhost:50070

To use multiple datanode instance:

```
docker-compose scale datanode=2
```

Similar to start a yarn cluster:

```
docker-compose up -d resourcemanager
```
