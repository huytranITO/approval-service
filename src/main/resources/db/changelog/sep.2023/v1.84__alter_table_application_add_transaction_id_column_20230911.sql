--liquibase formatted sql
--changeset BAONV2:v1.84__alter_table_application_add_transaction_id_column_20230911 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'transaction_id';
ALTER TABLE application ADD transaction_id varchar(50) DEFAULT NULL COMMENT 'Mã Cơ hội bán';