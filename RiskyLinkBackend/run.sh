mvn install:install-file -Dfile=alignapi-version-4.10/lib/align.jar -DpomFile=alignapi-version-4.10/lib/align.pom -Dpackaging=jar
mvn install:install-file -Dfile=alignapi-version-4.10/lib/procalign.jar -DpomFile=alignapi-version-4.10/lib/procalign.pom -Dpackaging=jar
mvn spring-boot:run

