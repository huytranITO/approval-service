--liquibase formatted sql
--changeset TuanHA13:v1.49__alter_table_application_checklist_20230619 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_checklist' AND COLUMN_NAME = 'checklist_mapping_id';
ALTER TABLE application_checklist RENAME COLUMN request_code TO checklist_mapping_id;