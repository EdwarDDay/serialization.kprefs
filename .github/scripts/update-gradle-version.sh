#!/bin/bash

set -e

latestVersion=$(curl https://services.gradle.org/versions/current | jq --raw-output '.version')

echo "Latest gradle version: $latestVersion" >&2

echo "::set-output name=gradleVersion::$latestVersion"

# update gradle properties
./gradlew wrapper --gradle-version "${latestVersion}"

# update gradle wrapper
./gradlew wrapper
