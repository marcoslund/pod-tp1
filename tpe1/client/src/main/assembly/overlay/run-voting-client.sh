#!/bin/bash

java -DserverAddress=localhost:1099 -DvotesPath=./stv_votes.csv -cp 'lib/jars/*' "ar.edu.itba.pod.client.voting.VotingClient" $*

