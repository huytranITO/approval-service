--liquibase formatted sql
--changeset TuanHA13:v1.8__alter_table_application_20230418-001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'created_full_name';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'created_phone_number';
ALTER TABLE application ADD created_full_name varchar(255) DEFAULT NULL COMMENT 'Họ tên người khởi tạo';
ALTER TABLE application ADD created_phone_number varchar(20) DEFAULT NULL COMMENT 'SĐT người khởi tạo';

--changeset TuanHA13:v1.8__alter_table_application_20230418-002 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'assignee_full_name';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'assignee_phone_number';
ALTER TABLE application ADD assignee_full_name varchar(255) DEFAULT NULL COMMENT 'Họ tên người phê duyệt';
ALTER TABLE application ADD assignee_phone_number varchar(20) DEFAULT NULL COMMENT 'SĐT người phê duyệt';