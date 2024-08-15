--liquibase formatted sql
--changeset BAONV2:v1.79__alter_table_application_update_column_insuranc_for_ldp_20232508 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'insurance';
ALTER TABLE application MODIFY COLUMN insurance TINYINT(1) NULL DEFAULT 0 COMMENT 'KH có quan tâm tới sản phẩm bảo hiểm';

UPDATE application SET insurance = 0;