--liquibase formatted sql
--changeset BAONV2:v1.64_alter_table_application_historic_transaction_modify_column_20230724 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_historic_transaction ' AND COLUMN_NAME = 'json_response';
ALTER TABLE application_historic_transaction MODIFY COLUMN json_response text NULL;
