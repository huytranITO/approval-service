FROM 10.1.66.33:8083/openjdk:8-ol7
ENV SERVER_PORT 8080
COPY target/approval*.jar /opt/service/app.jar
EXPOSE 8080
WORKDIR /opt/service
RUN chgrp -R 0 ./ && chmod -R g=u ./ 
CMD ["java", "-jar", "app.jar"]
