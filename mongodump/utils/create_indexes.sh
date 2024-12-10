#!/bin/bash

salt="$1"

# Modify the config json file to contain the temporary DB name
sed -e "s/db_replace/address_validation$salt/" search_indexes.json >temp.json

# Create the search index in Atlas
indexID=$(atlas clusters search indexes create --file temp.json --clusterName IntegrationTesting | jq -r '.indexID')

# Function to check if the index is "steady"
check_active() {
  result="$(atlas clusters search indexes describe $indexID --clusterName IntegrationTesting | jq -r '.status')"
  if [[ "$result" == "STEADY" ]]; then
    return 0
  else
    return 1
  fi
}

# Number of seconds to wait between checks
check_interval=2

# Maximum number of retries
max_retries=20

# Counter for retries
retries=0

sleep 15

# Wait fot index to be STEADY until passed max retries
while [ $retries -lt $max_retries ]; do
  if check_active; then
    break
  else
    sleep $check_interval
    retries=$((retries + 1))
  fi
done

if [ $retries -ge $max_retries ]; then
  echo "failed"
else
  echo $indexID
fi
