--liquibase formatted sql
--changeset ManhNV8:v1.4__alter_table_customer_address_20230413 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'customer_address'
ALTER TABLE customer_address MODIFY COLUMN can_delete tinyint(1) DEFAULT 0 NULL;
