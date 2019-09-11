#!/bin/bash

java -DserverAddress=localhost:1099 -cp 'lib/jars/*' "ar.edu.itba.pod.client.query.QueryClient" $*

