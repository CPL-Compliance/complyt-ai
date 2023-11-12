# Insert here the authentication provider url
auth_url="$1"

# Insert here the API domain to send requests to
api_domain="$2"

# Insert here the desired refresh date
refresh_date="$3"

#  insert a management key obtained by from the authenticator
jwt="$4"
states=("AL" "AK" "AZ" "AR" "CA" "CO" "CT" "DE" "FL" "GA" "HI" "ID" "IL" "IN" "IA" "KS" "KY" "LA" "ME" "MD" "MA" "MI" "MN" "MS" "MO" "MT" "NE" "NV" "NH" "NJ" "NM" "NY" "NC" "ND" "OH" "OK" "OR" "PA" "RI" "SC" "SD" "TN" "TX" "UT" "VT" "VA" "WA" "WV" "WI" "WY" "DC")

# Gets all client ids and secrects
clients=$(curl -s -L "$auth_url/api/v2/clients?fields=client_id%2Cclient_secret&include_fields=true" \
  -H 'Accept: application/json' \
  -H "Authorization: Bearer $jwt")

for obj in $(echo "$clients" | jq -c '.[]'); do

  client_id=$(echo $obj | jq -r '.client_id')
  client_secret=$(echo $obj | jq -r '.client_secret')

  # Gets a jwt for each client
  token=$(curl -s --location "$auth_url/oauth/token" \
    --header 'content-type: application/x-www-form-urlencoded' \
    --data-urlencode "client_id=$client_id" \
    --data-urlencode "client_secret=$client_secret" \
    --data-urlencode 'audience=https://sales-tax-service/' \
    --data-urlencode 'grant_type=client_credentials' | jq -c -r '.access_token')

  echo "------------------------------------------\nToken: $token"

  # If no jwt created for this client, skip
  if [ "$token" == "null" ]; then
    continue
  fi

  # Refresh all states in tenant
  for state in "${states[@]}"; do
    api_url="$api_domain/v1/nexus/refresh/state/$state?date=$refresh_date"

    response=$(curl -s -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $token" "$api_url")
    if [ $(echo $response | jq -c -r '.code') != "404" ]; then
      echo "Response for $state. Response: $response\n"
    else
      echo "Response for $state. Status: 404"
    fi
  done
done
