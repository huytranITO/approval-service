--liquibase formatted sql
--changeset ManhNV8:v1.70__alter_table_aml_opr_add_new_column_priority_20230801 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'aml_opr' AND COLUMN_NAME = 'priority';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'aml_opr' AND COLUMN_NAME = 'ref_customer_id';
ALTER TABLE aml_opr ADD priority BOOL NULL;
ALTER TABLE aml_opr CHANGE priority priority BOOL NULL AFTER order_display;
ALTER TABLE aml_opr MODIFY COLUMN priority tinyint(1) DEFAULT '0' NULL;
ALTER TABLE aml_opr ADD ref_customer_id BIGINT NULL;
ALTER TABLE aml_opr CHANGE ref_customer_id ref_customer_id BIGINT NULL AFTER customer_id;