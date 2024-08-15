--liquibase formatted sql
--changeset ManhNV8:v1.86__alter_table_application_add_new_column_20230912 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application ' AND COLUMN_NAME = 'has_asset';
ALTER TABLE application ADD has_asset BOOL NULL COMMENT 'Đánh dấu hồ sơ có tài sản';
ALTER TABLE application CHANGE has_asset has_asset BOOL NULL COMMENT 'Đánh dấu hồ sơ có tài sản' AFTER sale_code;
UPDATE application SET has_asset = 0;