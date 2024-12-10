#!/bin/bash

# Specify the target directory and file
target_directory="$HOME/.config/atlascli"
config_file="$target_directory/config.toml"
config_text=$(
  cat <<EOF
[default]
output = 'json'
private_api_key = '$1'
project_id = '65098327b74ab34c5bb67716'
public_api_key = 'fdczmeqm' #secret exist in bitwarden
service = 'cloud'
EOF
)

# Create the target directory if it doesn't exist
if [ ! -d "$target_directory" ]; then
  mkdir -p "$target_directory"
fi

# Create the config file and insert the specified text
echo "$config_text" >"$config_file"

# Create a new database user to be used in tests
salt=$2
#temp=$(atlas dbusers create --username testUser$salt --scope=IntegrationTesting \
#  --password password$salt --role dbAdmin@addresses$salt,readWrite@addresses$salt \
#  --deleteAfter $(date -u -d "$(date -u) + 20 minutes" +"%Y-%m-%dT%H:%M:%SZ"))

temp=$(atlas dbusers create readWriteAnyDatabase --username testUser$salt --scope=IntegrationTesting \
  --password password$salt \
  --deleteAfter $(date -u -d "$(date -u) + 60 minutes" +"%Y-%m-%dT%H:%M:%SZ"))

# Number of seconds to wait between checks
check_interval=2

# Maximum number of retries
max_retries=8

# Counter for retries
retries=0

sleep 6

# Wait for the cluster to be idle after deploying a dbuser
while [ $retries -lt $max_retries ]; do
  result=$(atlas clusters describe IntegrationTesting | jq -r '.stateName')
  if [[ "$result" == "IDLE" ]]; then
    break
  fi
  sleep 1
done

if [ $retries -ge $max_retries ]; then
  echo "failed"
else
  echo "succeeded"
fi
