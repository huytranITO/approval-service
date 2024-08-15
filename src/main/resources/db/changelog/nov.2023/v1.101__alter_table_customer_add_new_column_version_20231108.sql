--liquibase formatted sql
--changeset ManhNV8:v1.101__alter_table_customer_add_new_column_version_20231108 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer' AND COLUMN_NAME = 'version';
ALTER TABLE customer ADD version INT NULL COMMENT 'Số phiên bản khách hàng từ customer-additional';
ALTER TABLE customer CHANGE version version INT NULL COMMENT 'Số phiên bản khách hàng từ customer-additional' AFTER customer_type;
