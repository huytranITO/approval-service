--liquibase formatted sql
--changeset TuanHA13:v1.36__alter_table_rule_version_mapping_20230608-001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rule_version_mapping ' AND COLUMN_NAME = 'group_code';
ALTER TABLE rule_version_mapping ADD group_code varchar(20) DEFAULT NULL COMMENT 'Mã nhóm đối tượng';