--liquibase formatted sql
--changeset ManhNV8:v1.59__alter_table_application_phone_expertise_change_data_type_called_at_20230714 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application_phone_expertise' AND COLUMN_NAME = 'called_at';
ALTER TABLE application_phone_expertise MODIFY COLUMN called_at varchar(256) NULL COMMENT 'Thời gian gọi';
