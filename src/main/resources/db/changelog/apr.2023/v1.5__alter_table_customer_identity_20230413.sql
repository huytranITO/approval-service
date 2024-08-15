--liquibase formatted sql
--changeset ManhNV8:v1.5__alter_table_customer_identity_20230413 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'customer_identity'
ALTER TABLE `customer_identity`
    ADD COLUMN `priority` TINYINT(1) NULL DEFAULT 0 AFTER `updated_at`;