DROP USER IF EXISTS 'MYSQLUSERNAME';
CREATE USER 'MYSQLUSERNAME'@'%';
CREATE DATABASE IF NOT EXISTS feedback_service;
GRANT ALL ON feedback_service.* TO 'MYSQLUSERNAME'@'%' IDENTIFIED BY 'MYSQLPASSWORD';

