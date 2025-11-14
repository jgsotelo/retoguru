# Usa la imagen oficial de OpenJDK 17
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo en el contenedor
WORKDIR /lambda

# Copia el archivo JAR de tu aplicación a la imagen Docker
COPY target/reto-0.0.1-SNAPSHOT.jar /lambda/app.jar

# Establece el comando para ejecutar tu función Java 17 en Lambda
CMD ["java", "-jar", "app.jar"]