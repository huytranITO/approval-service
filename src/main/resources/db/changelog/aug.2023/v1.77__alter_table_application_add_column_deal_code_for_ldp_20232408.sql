--liquibase formatted sql
--changeset BAONV2:v1.77__alter_table_application_add_column_deal_code_for_ldp_20232408 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'deal_code';
ALTER TABLE application ADD deal_code varchar(100) DEFAULT NULL COMMENT 'Mã Cơ hội bán';