#!/bin/bash

java -DserverAddress=x.x.x.x:x -cp 'lib/jars/*' "ar.edu.itba.pod.client.query.QueryClient" $*

