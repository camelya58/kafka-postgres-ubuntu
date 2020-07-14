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
Create a Maven project and add dependencies: [Spring Boot Starter Web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web/2.3.1.RELEASE),
[Apache Kafka](https://mvnrepository.com/artifact/org.apache.kafka/kafka_2.12/2.5.0),
[Spring Boot Starter Data Jpa](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa/2.3.1.RELEASE),
[Spring Boot Starter Validation](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-validation/2.3.1.RELEASE),
[PostgreSQl](https://mvnrepository.com/artifact/org.postgresql/postgresql/42.2.14),
[Lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok/1.18.12).
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
spring.jpa.hibernate.ddl-auto=update
# Kafka
spring.kafka.consumer.group-id=app.3
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.template.default-topic=test-topic
# logging
#logging.level.root=DEBUG
#logging.level.org.hibernate.SQL=DEBUG
```
Phrase "spring.jpa.generate-ddl=true" means that you want jpa creates tables from your entities.

Phrase "spring.jpa.hibernate.ddl-auto=update" means that you want jpa updates the schema if necessary. 
The default value is "create-drop" that means to create and then destroy the schema at the end of the session.

In a JPA-based app, you can choose to let Hibernate create the schema or use schema.sql, but you cannot do both. 
Make sure to disable spring.jpa.hibernate.ddl-auto if you use a schema.sql.

Spring Boot automatically creates the schema of an embedded DataSource. 
Phrase "spring.datasource.initialization-mode=always" meant that you want it from spring boot.

Phrase "logging.level.org.hibernate.SQL=DEBUG" allows to see sql query created by jpa for you.

## Step 8
Create main class and set there a consumer using annotations @EnableKafka for class and @KafkaListener for method.
```java
@EnableKafka
@SpringBootApplication
public class UbuntuApplication {

    @KafkaListener(topics = "test-topic")
    public void messageListener(ConsumerRecord<Long, Object> record) {
        System.out.println(record.partition());
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.topic());
    }

    public static void main(String[] args) {
        SpringApplication.run(UbuntuApplication.class, args);
    }
}
```
Then create configuration for Kafka Consumer.
```java
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;
    @Value("${spring.kafka.consumer.group-id}")
    private String kafkaGroupId;
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupId);
        return props;
    }
    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
    @Bean
    public ConsumerFactory<Long, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }
}
```

## Step 9 
Create class KafkaRepository and set there a producer using the object of KafkaTemplate and method "send" with parameters.
```java
@RequiredArgsConstructor
@Repository
public class KafkaRepository {

    @Value("${spring.kafka.template.default-topic}")
    private String kafkaTopic;


    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public void sendMessage(Long msgId, Object data) {
        ListenableFuture<SendResult<Long, Object>> future = kafkaTemplate.send(kafkaTopic, msgId, data);
        future.addCallback(System.out::println, System.err::println);
        kafkaTemplate.flush();
    }
}
```
Then create configuration for Kafka Producer.
```java
@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }
    @Bean
    public ProducerFactory<Long, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }
    @Bean
    public KafkaTemplate<Long, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

## Step 10
Create models.
To describe all kind of relations create 4 models: Student, Address, Assignment, Teacher.
```java
@Getter
@Setter
@Entity
@Table(name = "students")
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Assignment> assignments;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @ManyToMany
    @JoinTable(name = "student_teacher",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "ID")
    )
    private Set<Teacher> teachers;

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }
}
```
### 10.1 OneToOne
Bidirectional relations  between a student and an address or a teacher and an assignment.

Class Student has following command mappedBy = "student", 
this attribute is used to define the referencing side (non-owning side) of the relationship.
```java
   @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;
```
Class Address has a special column with student id.
```java
    @OneToOne
    @JoinColumn(name = "student_id")
    @JsonIgnore
    private Student student;
```
Class Teacher has a special column with assignment id.
```java
    @OneToOne
    @JoinColumn(name = "assignment_id")
    private Assignment assignment;
```    
Class Assignment has following command mappedBy = "assignment",
this attribute is used to define the referencing side (non-owning side) of the relationship.
```java
    @OneToOne(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Teacher teacher;
```
Annotation @JsonIgnore allows to ignore nested data.

### 10.2 OneToMany and ManyToOne
Bidirectional relations a student and assignments.

Class Student has following command mappedBy = "student", 
this attribute is used to define the referencing side (non-owning side) of the relationship.
```java
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Assignment> assignments;
```

Class Assignment has a special column with student id.
```java
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;
```
### 10.3 ManyToMany
Bidirectional relations between students and teachers.

Class Student has an annotation @JoinTable which allows to add a new table with given name ang columns.
```java
    @ManyToMany
    @JoinTable(name = "student_teacher",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "ID")
    )
    private Set<Teacher> teachers;
```
Class Teacher has following command mappedBy = "teachers", 
this attribute is used to define the referencing side (non-owning side) of the relationship.
```java
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "teachers")
    @JsonIgnore
    private Set<Student> students;
```

## Step 11
Create repositories for all models.
```java
@Repository
@SuppressWarnings("unused")
public interface StudentRepository extends JpaRepository<Student, Long> {

    Set<Student> findStudentByAgeLessThan(int age);

    Set<Student> findStudentsByAddressCity(String city);

    @Modifying
    @Query(value = "SELECT s.id, s.age, s.name FROM students s left outer JOIN student_teacher t " +
            "ON s.id=t.student_id where t.teacher_id = :teacherId", nativeQuery = true)
    Set<Student> findStudentsByTeacherId(Long teacherId);

    @Modifying
    @Query(value = "SELECT s.id, s.age, s.name FROM students s left outer JOIN assignments a " +
            "ON s.id=a.student_id where a.id = :assignmentId", nativeQuery = true)
    Set<Student> findStudentsByAssignmentId(Long assignmentId);

    @Modifying
    @Query(value = "SELECT s.id, s.age, s.name FROM students s left outer JOIN assignments a " +
            "ON s.id=a.student_id where a.name = :assignmentName", nativeQuery = true)
    Set<Student> findStudentsByAssignmentName(String assignmentName);
}
```
You can use jpa commands or SQL queries.

## Step 12
Create controllers for each model with CRUD operations using the logic and dependencies between models. 
```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    private final KafkaRepository kafkaRepository;

    @GetMapping("/student/{studentId}/teachers")
    public Set<Teacher> getTeachersByStudentId(@PathVariable Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new NotFoundException("Student not found!");
        }
        return teacherRepository.findTeachersByStudentId(studentId);
    }

    @GetMapping("/teacher/{id}")
    public Teacher getTeacherById(@PathVariable Long id) {
        return teacherRepository.findById(id).
                orElseThrow(() -> new NotFoundException("Teacher not found with id " + id));
    }

    @PostMapping("/assignment/{assignmentId}/teacher")
    public Teacher addTeacherByAssignmentId(@PathVariable Long assignmentId,
                                            @Valid @RequestBody Teacher teacher) {

        Assignment assignment = assignmentRepository.findById(assignmentId).
                orElseThrow(() -> new NotFoundException("Assignment not found!"));
        if (assignment.getTeacher() == null) {
            teacher.setAssignment(assignment);
            Teacher savedTeacher = teacherRepository.save(teacher);
            Set<Student> students = studentRepository.findStudentsByAssignmentName(assignment.getName());
            savedTeacher.setStudents(students);
            for (Student s : students) {
                s.addTeacher(savedTeacher);
                studentRepository.save(s);
            }
            kafkaRepository.sendMessage(savedTeacher.getId(), savedTeacher);
            return savedTeacher;
        } else throw new AlreadyExistsException("This assignment has already had a teacher!");
    }

    @PutMapping("/teacher/{id}")
    public Teacher updateTeacher(@PathVariable Long id,
                                 @Valid @RequestBody Teacher teacherUpdated) {
        return teacherRepository.findById(id)
                .map(teacher -> {
                    teacher.setName(teacherUpdated.getName());
                    return teacherRepository.save(teacher);
                }).orElseThrow(() -> new NotFoundException("Teacher not found with id " + id));
    }

    @DeleteMapping("/teacher/{id}")
    public String deleteStudent(@PathVariable Long id) {
        return teacherRepository.findById(id)
                .map(teacher -> {
                    teacherRepository.delete(teacher);
                    return "Delete Successfully";
                }).orElseThrow(() -> new NotFoundException("Teacher not found with id " + id));
    }
}
```
## Step 13
Create own exceptions: NotFoundException and AlreadyExistsException.
```java
@SuppressWarnings("unused")
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "AlreadyExistsException already exists")
public class AlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AlreadyExistsException(String message) {
        super(message);
    }

    public AlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
```
Annotation @ResponseStatus allows to handle the exception and get the error code.

## Step 14 
To simplify the development and management of PostgreSQL install a universal database tool [DBeaver](https://dbeaver.io/download/).

The instruction about how to install  and to use DBeaver you can find [here](https://computingforgeeks.com/install-and-configure-dbeaver-on-ubuntu-debian/).
