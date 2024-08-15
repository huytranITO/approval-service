FROM docker-dso.msb.com.vn/dso/openjdk:8-ol7

ENV SERVER_PORT 8080

COPY target/approval*.jar /opt/service/app.jar

WORKDIR /opt/service

RUN chgrp -R 0 ./ && chmod -R g=u ./

CMD ["java", "-jar", "app.jar"]