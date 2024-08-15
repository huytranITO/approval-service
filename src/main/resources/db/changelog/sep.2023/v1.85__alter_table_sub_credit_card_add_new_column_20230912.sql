--liquibase formatted sql
--changeset ManhNV8:v1.85__alter_table_sub_credit_card_add_new_column_20230912 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'sub_credit_card ' AND COLUMN_NAME = 'ldp_sub_id';
ALTER TABLE sub_credit_card ADD ldp_sub_id varchar(36) NULL COMMENT 'ID thẻ phụ từ LandingPage/CMS';
ALTER TABLE sub_credit_card CHANGE created_date created_date date NULL COMMENT 'Ngày phát hành' AFTER contract_number;
ALTER TABLE sub_credit_card CHANGE contract_number contract_number varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Số thẻ' AFTER order_display;
ALTER TABLE sub_credit_card CHANGE id id bigint auto_increment NOT NULL COMMENT 'PK' FIRST;

