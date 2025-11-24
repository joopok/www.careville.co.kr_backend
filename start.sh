#!/bin/bash

# Java 17 설정 (Homebrew)
export JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home
echo "Using JAVA_HOME: $JAVA_HOME"

# 애플리케이션 실행
./mvnw spring-boot:run
