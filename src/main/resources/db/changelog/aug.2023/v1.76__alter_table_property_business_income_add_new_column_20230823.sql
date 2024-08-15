--liquibase formatted sql
--changeset ManhNV8:v1.76__alter_table_property_business_income_add_new_column_20230823 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'property_business_income' AND COLUMN_NAME = 'ldp_property_business_id';
ALTER TABLE property_business_income ADD ldp_property_business_id varchar(36) NULL COMMENT 'ID nguồn thu BĐS gom xây từ LDP/CMS';
ALTER TABLE property_business_income CHANGE ldp_property_business_id ldp_property_business_id varchar(36) NULL COMMENT 'ID nguồn thu BĐS gom xây tuwfLDP/CMS' AFTER id;
