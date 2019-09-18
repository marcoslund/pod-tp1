#!/bin/bash

java -DserverAddress=localhost:1099 -Dstate=JUNGLE -DoutPath=./result.csv -cp 'lib/jars/*' "ar.edu.itba.pod.client.query.QueryClient" $*

