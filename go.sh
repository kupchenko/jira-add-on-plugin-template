#!/usr/bin/env bash
atlas-debug \
    --product jira \
    --container tomcat7x \
    -DskipTests | tee log.log
