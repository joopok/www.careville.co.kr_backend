#!/bin/bash

# Java 17 설정 (Homebrew)
export JAVA_HOME=$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home
echo "Using JAVA_HOME: $JAVA_HOME"

# 로컬 프로파일로 애플리케이션 실행 (포트 8080)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
