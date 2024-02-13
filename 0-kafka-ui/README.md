
https://github.com/provectus/kafka-ui


````bash
java -Dspring.config.additional-location=./application.yml --add-opens java.rmi/javax.rmi.ssl=ALL-UNNAMED -jar ./lib/kafka-ui-api-v0.7.1.jar
```