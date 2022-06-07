#!/bin/bash

sudo docker exec -i sites-docker_db_1 sh -c 'exec mysql -uUSERNAME -pPASSWORD' < ./create-user.sql
