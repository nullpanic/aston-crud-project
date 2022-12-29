#!/bin/bash

# Add environment variables
export DB_USER_LOCAL=$1
export DB_PASSWORD_LOCAL=$2

## Prepare War
mvn clean package

# Ensure, that docker-compose stopped
docker-compose stop

# Start new deployment
docker-compose up --build -d

