USE sites_service;
INSERT INTO users (account_non_expired,account_non_locked,created,credentials_non_expired,enabled,password,updated,username) VALUES (1,1,now(),1,1,"$2y$10$UJt5/j4fq7ziSIlUUfyNqOKAt78VG5H2LvY8fqGW3fABTWr8UD1jS",now(),"USERNAME");
INSERT INTO user_authorities (authority, user_id) VALUES ("ROLE_USER",LAST_INSERT_ID());

USE sites_data_service;
INSERT INTO users (account_non_expired,account_non_locked,created,credentials_non_expired,enabled,password,updated,username) VALUES (1,1,now(),1,1,"$2y$10$UJt5/j4fq7ziSIlUUfyNqOKAt78VG5H2LvY8fqGW3fABTWr8UD1jS",now(),"USERNAME");
INSERT INTO user_authorities (authority, user_id) VALUES ("ROLE_USER",LAST_INSERT_ID());

