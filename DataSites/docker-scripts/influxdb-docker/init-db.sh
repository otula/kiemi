#!/bin/bash

username=USERNAME
password=PASSWORD
database=ruuvit

/usr/bin/curl -X POST http://localhost:44447/query --data-urlencode "q=CREATE USER $username WITH PASSWORD '$password' WITH ALL PRIVILEGES"
/usr/bin/curl -X POST -u $username:$password http://localhost:44447/query --data-urlencode "q=CREATE DATABASE $database"
/usr/bin/curl -X POST -u $username:$password http://localhost:44447/query --data-urlencode "q=CREATE RETENTION POLICY \"30min_mean_inf\" ON \"$database\" DURATION INF REPLICATION 1"
/usr/bin/curl -X POST -u $username:$password http://localhost:44447/query --data-urlencode "q=CREATE CONTINUOUS QUERY \"30min_mean\" ON \"$database\" BEGIN SELECT mean(*) INTO \"$database\".\"30min_mean_inf\".:MEASUREMENT FROM /.*/ GROUP BY time(30m),* END"
