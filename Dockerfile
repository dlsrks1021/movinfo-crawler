# Dockerfile

# Maven 빌드
FROM maven:3.9.0-eclipse-temurin-17 as build

# 작업 디렉토리 설정
WORKDIR /app

# Maven 의존성 캐시를 활용하기 위해 먼저 pom.xml만 복사하고 의존성 설치
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 소스 코드를 복사하고 빌드
COPY src ./src
RUN mvn package shade:shade -DskipTests

# 실제 애플리케이션이 실행될 이미지를 위한 단계
ARG TARGETARCH
FROM --platform=${TARGETARCH} selenium/standalone-firefox:latest

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 복사
COPY --from=build /app/target/*.jar crawler.jar

# 시간대를 Asia/Seoul로 설정
ENV TZ=Asia/Seoul

# tzdata, cron 설치 및 시간대 설정
USER root
RUN mkdir -p /var/lib/apt/lists/partial && \
    apt-get update && apt-get install -y tzdata cron && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    rm -rf /var/lib/apt/lists/*

# 크론 설정
COPY cron-crawling.sh /usr/local/bin/cron-crawling.sh
RUN chmod +x /usr/local/bin/cron-crawling.sh
RUN echo "0 0/5 * * * /usr/local/bin/cron-crawling.sh >> /var/log/cron.log 2>&1" > /etc/cron.d/cron-crawling
RUN chmod 0644 /etc/cron.d/cron-crawling

# 실행 및 모니터링
CMD service cron restart && \
    touch /var/log/cron.log && \
    tail -F /var/log/cron.log
