# movinfo-crawler

## Overview
movinfo-crawler is a web scraping project designed to fetch movie theater showtimes and store the data in a MongoDB database.

It is built to run as a containerized microservice in Kubernetes.

This project is part of a larger system called movinfo, which provides real-time notifications about movie schedules through a Discord webhook using MongoDB change streams.

## Related Project
[movinfo-messenger](https://github.com/dlsrks1021/movinfo-messenger) : Check DB and send message to Discord.

## Requirements
- Kubernetes with x86_64 (aka amd64)
- MongoDB (tested 7.0.14)

## Environment Variables
| Variable            | Info                          |
|---------------------|-------------------------------|
| MONGO_URL           | Connection String for MongoDB |
| DISCORD_WEBHOOK_URL | Webhook String for Discord    |

## How to Deploy in Kubernetes
``` yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: movinfo-deployment
spec:
  selector:
    matchLabels:
      app: movinfo
  replicas: 1
  template:
    metadata:
      labels:
        app: movinfo
    spec:
      hostAliases:
        - ip: "127.0.0.1"
          hostnames:
            - "selenium"
      containers:
        - name: crawler
          image: dlsrks1021/movinfo-crawler:latest
          env:
          - name: MONGO_URL
            value: your_string
          command: ["/bin/sh", "-c"]
          args:
            - |
              sed -i "s|REPLACE_URL|$MONGO_URL|g" /usr/local/bin/cron-crawling.sh &&
              service cron restart &&
              touch /var/log/cron.log &&
              tail -F /var/log/cron.log
        - name: selenium
          image: selenium/standalone-firefox:4.24.0
          resources:
            requests:
              memory: "2Gi"
          ports:
            - containerPort: 4444
        - name: messenger
          image: dlsrks1021/movinfo-messenger:latest
          env:
          - name: DISCORD_WEBHOOK_URL
            value: your_string
          - name: MONGO_URL
            value: your_string
```
