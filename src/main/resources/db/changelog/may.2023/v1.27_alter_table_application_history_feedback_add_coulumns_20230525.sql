--liquibase formatted sql
--changeset haimd:v1.27_alter_table_application_history_feedback_add_coulumns_20230525 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_history_feedback' AND COLUMN_NAME = 'created_phone_number';
ALTER TABLE application_history_feedback ADD created_phone_number varchar(15) NULL;