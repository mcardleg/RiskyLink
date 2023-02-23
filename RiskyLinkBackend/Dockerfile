# FROM eclipse-temurin:11 as build
# WORKDIR /app
# COPY mvnw .
# COPY .mvn .mvn
# COPY pom.xml .
# COPY src src
# COPY alignapi-version-4.10 alignapi-version-4.10
# RUN ./mvnw install:install-file -Dfile=alignapi-version-4.10/lib/align.jar -DpomFile=alignapi-version-4.10/lib/align.pom -Dpackaging=jar
# RUN ./mvnw install:install-file -Dfile=alignapi-version-4.10/lib/procalign.jar -DpomFile=alignapi-version-4.10/lib/procalign.pom -Dpackaging=jar
# # RUN ./mvnw install:install-file -Dfile=/alignapi-version-4.10/lib/ontowrap.jar -DpomFile=/alignapi-version-4.10/lib/ontowrap.pom -Dpackaging=jar
# # RUN ./mvnw install:install-file -Dfile=/alignapi-version-4.10/lib/alignsvc.jar -DpomFile=/alignapi-version-4.10/lib/alignsvc.pom -Dpackaging=jar
# RUN ./mvnw install -DskipTests
#
# FROM eclipse-temurin:11
# VOLUME /tmp
# COPY --from=build /app/target/RiskyLinkBackend-1.0.0.jar /app/app.jar
# COPY --from=build /app/alignapi-version-4.10/lib/align.jar /lib/align.jar
# COPY --from=build /app/alignapi-version-4.10/lib/procalign.jar /lib/procalign.jar
# ENV CLASSPATH /lib/align.jar:/lib/procalign.jar
# ENTRYPOINT ["java", "-jar", "app/app.jar"]

# COPY /alignapi-version-4.10 /align
# CMD ["java", "-jar", "/align/lib/procalign.jar", "file:///align/examples/rdf/onto1.owl", "file:///align/examples/rdf/onto2.owl"]

FROM eclipse-temurin:11
WORKDIR /app
COPY target/RiskyLinkBackend-1.0.0.jar /app/app.jar
COPY alignapi-version-4.10/lib/align.jar /app/lib/
COPY alignapi-version-4.10/lib/procalign.jar /app/lib/
COPY resources resources
# CMD ["java", "-jar", "app.jar"]
# CMD ["java", "-cp", "app.jar:/app/lib/*", "-jar", "app.jar"]
FROM eclipse-temurin:11
WORKDIR /app
COPY target/RiskyLinkBackend-1.0.0.jar /app/app.jar
COPY alignapi-version-4.10/lib/align.jar /app/lib/
COPY alignapi-version-4.10/lib/procalign.jar /app/lib/
COPY resources resources
# CMD ["java", "-jar", "app.jar"]
# CMD ["java", "-cp", "app.jar:/app/lib/*", "-jar", "app.jar"]
CMD ["java", "-cp", "/app/lib/*:app.jar", "-jar", "app.jar"]
# CMD ["java", "-cp", "/app/lib/*", "-cp", "app.jar", "ie.tcd.mcardleg.RiskyLinkBackend.RiskyLinkBackendApplication"]
