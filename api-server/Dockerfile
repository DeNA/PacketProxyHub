FROM debian:bullseye as build-stage

RUN apt-get update && \
    apt-get upgrade -y --no-install-recommends && \
    apt-get install -y --no-install-recommends locales-all curl java-common unzip

ENV LANG ja_JP.UTF-8  

RUN curl -k -O "https://corretto.aws/downloads/resources/11.0.10.9.1/java-11-amazon-corretto-jdk_11.0.10.9-1_amd64.deb"
RUN dpkg -i "java-11-amazon-corretto-jdk_11.0.10.9-1_amd64.deb"

RUN curl -k -O "https://downloads.gradle.org/distributions/gradle-6.7.1-bin.zip" && \
    unzip gradle-6.7.1-bin.zip && \
    mv gradle-6.7.1/bin/* /usr/bin && \
    mv gradle-6.7.1/lib/* /usr/lib

WORKDIR /root
COPY . .
RUN gradle wrapper
RUN ./gradlew build
RUN tar xf app/build/distributions/app.tar -C app/build/distributions


FROM gcr.io/distroless/java:11

WORKDIR /usr/local
COPY --from=build-stage /root/app/build/distributions/app /usr/local/

ENV APP_ENV=production
EXPOSE 1234 
ENTRYPOINT ["java", "-jar", "/usr/local/lib/app.jar"]

