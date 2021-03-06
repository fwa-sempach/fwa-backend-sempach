# Multistage Docker build.
# 1st stage, build the app
FROM maven:3.6-jdk-12 as build

WORKDIR /helidon

# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update the pom
ADD pom.xml .
RUN mvn package -DskipTests

# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD src src
RUN mvn package -DskipTests

RUN echo "done!"

# 2nd stage, build the runtime image
FROM adoptopenjdk/openjdk12:jre-12.0.1_12
WORKDIR /helidon

# Copy the binary built in the 1st stage
COPY --from=build /helidon/target/fwa-backend-sempach.jar ./
COPY --from=build /helidon/target/libs ./libs
ADD example_config ./config

EXPOSE 8080

CMD ["java", "-jar", "fwa-backend-sempach.jar"]