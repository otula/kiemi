--
-- Copyright 2019 Tampere University
-- 
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--   http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--


CREATE DATABASE IF NOT EXISTS `lorawan_gateway`;
USE `lorawan_gateway`;




CREATE TABLE IF NOT EXISTS `sensor_nodes` (
  `node_id` varchar(40) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `nwkskey` varchar(32) NOT NULL,
  `appskey` varchar(32) NOT NULL,
  `appkey` varchar(32) NOT NULL,
  `row_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `row_created` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




CREATE TABLE IF NOT EXISTS `sensor_data` (
  `data_id`  int(11) NOT NULL AUTO_INCREMENT,
  `node_id` varchar(40) NOT NULL,
  `eco2` bigint(21) DEFAULT NULL,
  `humidity` decimal(19,9) DEFAULT NULL,
  `pressure` decimal(19,9) DEFAULT NULL,
  `temperature` decimal(19,9) DEFAULT NULL,
  `timestamp` bigint(20) NOT NULL,
  `tvoc` bigint(21) DEFAULT NULL,
  `row_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `row_created` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`data_id`),
  KEY node_id_INDEX (`node_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

