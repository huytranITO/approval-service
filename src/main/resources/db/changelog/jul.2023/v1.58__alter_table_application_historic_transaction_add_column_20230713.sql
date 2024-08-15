--liquibase formatted sql
--changeset BAONV@2:v1.58__alter_table_application_historic_transaction_add_column_20230713 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_historic_transaction' AND COLUMN_NAME = 'application_sub_card_credit_id';
ALTER TABLE application_historic_transaction ADD application_sub_card_credit_id bigint NULL;