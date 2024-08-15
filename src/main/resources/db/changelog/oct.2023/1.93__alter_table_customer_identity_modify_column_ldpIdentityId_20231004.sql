--liquibase formatted sql
--changeset BaoNV2:1.93__alter_table_customer_identity_modify_column_ldpIdentityId_20231004 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'customer_identity' AND COLUMN_NAME = 'ldp_identity_id';
ALTER TABLE customer_identity MODIFY COLUMN ldp_identity_id varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'ID thông tin định danh từ Landing Page';
