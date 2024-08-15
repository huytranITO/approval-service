--liquibase formatted sql
--changeset ManhNV8:v1.47__alter_table_application_add_new_columns_20230617 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'loan_approval_position_value';
ALTER TABLE application ADD loan_approval_position_value varchar(255) NULL COMMENT 'Tên thẩm quyền phê duyệt khoan vay';
ALTER TABLE application CHANGE loan_approval_position_value loan_approval_position_value varchar(255) NULL AFTER loan_approval_position;