#!/bin/bash

wget https://freelog-s3-bucket.s3.amazonaws.com/image/whatap.zip
unzip  whatap.zip
chmod 755 whatap/whatap.agent-2.2.20.jar
chmod 755 whatap/whatap.agent.proxy-2.2.20.jar
java -jar -javaagent:whatap/whatap.agent-2.2.20.jar -Dwhatap.micro.enabled=true /freeblog/freeblog-1.0.war

