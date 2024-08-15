--liquibase formatted sql
--changeset BaoNV2:1.96__alter_table_retal_income_modify_column_ldpRetailId_20231004 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'rental_income' AND COLUMN_NAME = 'ldp_rental_id';
ALTER TABLE rental_income MODIFY COLUMN ldp_rental_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'ID nguồn thu cho thuê từ Landing Page';
