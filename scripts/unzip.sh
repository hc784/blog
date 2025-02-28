#!/bin/bash

# 압축 해제
echo "Unzipping deploy.zip..."
cd /home/ec2-user
unzip -o deploy.zip -d /home/ec2-user/app

# 압축 해제 후 zip 삭제
rm -f /home/ec2-user/deploy.zip

echo "Unzip complete."
