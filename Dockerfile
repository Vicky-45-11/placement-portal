# ---- build the WAR ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY db ./db
RUN mvn -B -q package -DskipTests

# ---- run it on Tomcat 10.1 ----
FROM tomcat:10.1.34-jre17-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/placement.war /usr/local/tomcat/webapps/ROOT.war
# Create tables + sample data on first start if the database is empty.
ENV DB_AUTO_INIT=true \
    UPLOAD_DIR=/usr/local/tomcat/uploads
EXPOSE 8080
# Hosts like Railway and Render pass the port to listen on in $PORT.
CMD ["sh", "-c", "sed -i \"s/port=\\\"8080\\\"/port=\\\"${PORT:-8080}\\\"/\" /usr/local/tomcat/conf/server.xml && catalina.sh run"]
