# CBlock dozone configuration

How to use:


Create a volume after you checked the scm and you have a helthy datanode:
```
docker-compose exec cblock hdfs cblock -c bilbo volume2 1GB 1

```

Mount the iscsi volume:

```
sudo iscsiadm -m node -o new -T bilbo:volume2 -p 127.0.0.1
sudo iscsiadm -m node -T bilbo:volume2 --login
```
