#!/bin/bash

cd ./client/target/tpe1-client-1.0-SNAPSHOT
./run-admin-client.sh
./run-voting-client.sh
./run-table-query-client.sh
cat ./result.csv
cd ../../..
