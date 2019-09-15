#!/bin/bash

mvn clean package
cd client/target/
tar xvzf tpe1-client-1.0-SNAPSHOT-bin.tar.gz
cd tpe1-client-1.0-SNAPSHOT
chmod u+x *.sh
cd ../../../server/target
tar xvzf tpe1-server-1.0-SNAPSHOT-bin.tar.gz
cd tpe1-server-1.0-SNAPSHOT
chmod u+x *.sh
cd ../../../
chmod u+x *.sh
