--liquibase formatted sql
--changeset BAONV2:v1.78__alter_table_application_add_column_insuranc_for_ldp_20232508 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'insurance';
ALTER TABLE application ADD insurance INT NULL COMMENT 'Mã Cơ hội bán';