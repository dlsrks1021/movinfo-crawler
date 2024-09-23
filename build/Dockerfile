# Dockerfile

FROM maven:3.9.9-eclipse-temurin-17

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 복사
COPY target/movinfo*.jar crawler.jar

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
COPY build/cron-crawling.sh /usr/local/bin/cron-crawling.sh
COPY build/check-log-and-compress.sh /usr/local/bin/check-log-and-compress.sh
RUN chmod +x /usr/local/bin/cron-crawling.sh
RUN chmod +x /usr/local/bin/check-log-and-compress.sh
RUN echo "*/3 * * * * root /usr/local/bin/cron-crawling.sh >> /var/log/cron.log 2>&1" > /etc/cron.d/cron-crawling
RUN echo "0 0 * * * root /usr/local/bin/check-log-and-compress.sh" > /etc/cron.d/check-log-and-compress
RUN chmod 0644 /etc/cron.d/cron-crawling
RUN chmod 0644 /etc/cron.d/check-log-and-compress
