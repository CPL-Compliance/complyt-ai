#!/bin/bash

option="$1"
salt="$2"

if [[ "$option" == "build" ]]; then
  mongorestore --uri mongodb+srv://testUser$salt:password$salt@integrationtesting.vg1kino.mongodb.net \
    --nsInclude=address_validation.* --nsFrom="address_validation.\$colname\$" --nsTo="address_validation$salt.\$colname\$" --archive=/address-validation.dump
else
  mongosh --eval "use address_validation$salt" --eval  "db.dropDatabase()" mongodb+srv://testUser$salt:password$salt@integrationtesting.vg1kino.mongodb.net
fi
