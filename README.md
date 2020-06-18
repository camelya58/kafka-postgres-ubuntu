# kafka-postgres-ubuntu

Simple project using Apache Kafka, PostgreSQL and Ubuntu 20.04.

For testing Kafka use Conduktor and for testing PostgreSQL use DBeaver.

## Step 1
Install [Apache Kafka](https://kafka.apache.org/downloads) for Linux(Ubuntu 20.04).

Create directoty named kafka and change directory.
```
$ mkdir ~/kafka && cd ~/kafka
```
Move your package to your directory and unpacked. 
```
$ tar -xvzf ~/kafka-2.5.0-src.tgz --strip 1
```

## Step 2
Configure kafka settings.

Open the file server.properties. 
```
$ nano ~/kafka/config/server.properties
```
Add a setting that allows to delete kafka topic.
```
delete.topic.enable = true
```

## Step 3
Create the files systemd.unit and start kafka server.

Create a unit-file for Zookeeper.
```
$ sudo nano /etc/systemd/system/zookeeper.service
```
Add this information:
```unit
[Unit]
Requires=network.target remote-fs.target
After=network.target remote-fs.target

[Service]
Type=simple
User=kafka
ExecStart=/home/kafka/kafka-2.5.0/bin/zookeeper-server-start.sh /home/kafka/kafka-2.5.0/config/zookeeper.properties
ExecStop=/home/kafka/kafka-2.5.0/bin/zookeeper-server-stop.sh
Restart=on-abnormal

[Install]
WantedBy=multi-user.target
```
It's necessary to change on own user and own directory.

Create a unit-file for kafka.
```
$ sudo nano /etc/systemd/system/kafka.service
```
```unit
[Unit]
Requires=zookeeper.service
After=zookeeper.service

[Service]
Type=simple
User=kafka
ExecStart=/bin/sh -c '/home/kafka/kafka-2.5.0/bin/kafka-server-start.sh /home/kafka/kafka-2.5.0/config/server.properties > /home/kafka/kafka-2.5.0/kafka.log 2>&1'
ExecStop=/home/kafka/kafka-2.5.0/bin/kafka-server-stop.sh
Restart=on-abnormal

[Install]
WantedBy=multi-user.target
```
This setting allows to run zookeeper after starting kafka.

Start kafka.
```
$ sudo systemctl start kafka
```
For testing it works use this command:
```
$ sudo journalctl -u kafka
```
You'll see:
```
Output
Jun 18 16:38:59 kafka-ubuntu systemd[1]: Started kafka.service.
```
Now your kafka server works at the port  - 9092.

In other day to start kafka you need to use this command
```
$ sudo systemctl enable kafka
```

## Step 4
To make the development and management of Apache Kafka install a Kafka GUI - [Conduktor](https://docs.conduktor.io/conduktor-installation/install/linux).

For Linux, Conduktor is packaged with libffi6. This library had seen its version bump to libffi7 in Ubuntu 20
so you'll end up with the error.

To resolve this problem you need to use the following commands:
```
$ curl -LO http://archive.ubuntu.com/ubuntu/pool/main/libf/libffi/libffi6_3.2.1-9_amd64.deb
$ sudo dpkg -i libffi6_3.2.1-9_amd64.deb 
$ sudo dpkg -i Conduktor-2.3.5.deb 
```
