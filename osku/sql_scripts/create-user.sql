USE feedback_service;
INSERT INTO users (account_non_expired,account_non_locked,created,credentials_non_expired,enabled,password,updated,username) VALUES (1,1,now(),1,1,"$2y$10$r/w5or6a90pNoVGtM7YDrOEmEG/jXFzvUknZYUdlSoAcMAP8iWH3e",now(),"FEEDBACKUSERNAME");
INSERT INTO user_authorities (authority, user_id) VALUES ("ROLE_ADMIN",LAST_INSERT_ID());


