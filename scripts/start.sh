#!/bin/bash
cd /home/ec2-user/app

echo "Stopping existing application..."
sudo pkill -f 'java -jar' || true

echo "Starting new application..."
nohup java -jar app.jar > app.log 2>&1 &
