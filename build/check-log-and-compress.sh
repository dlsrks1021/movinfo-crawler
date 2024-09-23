#!/bin/bash

LOG_FILE="/var/log/cron.log"
MAX_SIZE=104857600  # 100MB

log_size=$(stat -c%s "$LOG_FILE")

if [ "$log_size" -ge "$MAX_SIZE" ]; then
  TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
  ARCHIVE_NAME="/var/log/cronlog_$TIMESTAMP.tar.gz"
  
  tar -czf "$ARCHIVE_NAME" "$LOG_FILE"

  truncate -s 0 "$LOG_FILE"
fi