Kafka version kafka_2.12-2.8.1
Java version 12.0.2

configuration instructions

download project and run maven install
run spring boot as point of entry com.hugo.test.Application

Configuration file (database and kafka url and port)
src/main/resources/application.properties

start kafka


kafka commands:
creating topic:
Linux/Mac
bin/kafka-topics.sh --create --partitions 1 --replication-factor 1 --topic novice-players --bootstrap-server localhost:9092
windows
bin/windows/kafka-topics.bat --create --partitions 1 --replication-factor 1 --topic novice-players --bootstrap-server localhost:9092

Reading kafka messages
Linux/Mac
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic novice-players --from-beginning

Windows
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic novice-players --from-beginning


Command to compile
mvn clean install

java -jar exercice-0.0.4-SNAPSHOT.jar

Testing url

http://localhost:8080/api/v1/play

try to test using curl
curl -X GET http://localhost:8080/api/v1/play -H 'Content-Type: application/json' -d '{"players": [{"name": "Sub zero","type": "expert"},{"name": "Scorpion","type": "novice"},{"name": "Reptile","type": "meh"}]}

or using postman



