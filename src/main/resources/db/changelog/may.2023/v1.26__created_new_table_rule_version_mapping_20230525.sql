--liquibase formatted sql
--changeset ManhNV8:v1.26__created_new_table_rule_version_mapping_20230525 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'rule_version_mapping'
CREATE TABLE `rule_version_mapping`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `application_id` bigint(0) NOT NULL,
  `rule_code` varchar(20) NULL,
  `rule_version` int(0) NULL,
  `description` varchar(255) NULL,
  `created_at` datetime(0) NULL,
  `created_by` varchar(50) NULL,
  `updated_at` datetime(0) NULL,
  `updated_by` varchar(50) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `rule_version_mapping_ibfk_1` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
);