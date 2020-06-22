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

## Step 5
Install PostgreSQL for Linux(Ubuntu 20.04) and create the desired user and database.
```
$ sudo apt-get install postgresql 
```
Run PostgreSQL.
```
$ sudo service postgresql start
```
After the installation, a postgres user with administration priviliges was created with empty default password. 
As the first step, we need to set a password for postgres.
``` 
$ sudo -u postgres psql postgres
postgres=# \password postgres
Enter new password: 
Enter it again: 
```
Create a new user and set his role in databases.
```
postgres=# CREATE USER root WITH PASSWORD 'root';
postgres=# ALTER ROLE root CREATEROLE CREATEDB;
```
or use the following commands:
``` 
$ sudo -u postgres createuser --interactive --password root
Shall the new role be a superuser? (y/n) n
Shall the new role be allowed to create databases? (y/n) y
Shall the new role be allowed to create more new roles? (y/n) y
Password:
```
Create a new database, which is going to be owned by root.
``` 
$ sudo -u postgres createdb testdb -O user12
```
Restart PostgreSQL to enable the changes.
``` 
$ sudo service postgresql restart
```
Enter to the database.
``` 
$ sudo -u root psql testdb
psql (12.2 (Ubuntu 12.2-4))
Type "help" for help.

testdb=>
```
or use the following commands:
``` 
$ psql -U root -d testdb -W
Password for user root: 
psql (9.5.10)
Type "help" for help.

testdb=>
```
Now we can use the psql tool to connect to the database.

## Step 6
Create a Maven project and add dependencies.
```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
</dependencies>
```

## Step 7
Fill the file application.properties with configurations for postgreSQL and kafka.
```properties
# Database & datasource
server.port=9090
spring.datasource.url=jdbc:postgresql://localhost:5432/testdb
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.platform=postgres
spring.jpa.database=POSTGRESQL
spring.datasource.initialization-mode=always
# JPA config
spring.jpa.generate-ddl=true
#spring.jpa.hibernate.ddl-auto=none
# Kafka
spring.kafka.consumer.group-id=app.3
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.template.default-topic=test-topic
```