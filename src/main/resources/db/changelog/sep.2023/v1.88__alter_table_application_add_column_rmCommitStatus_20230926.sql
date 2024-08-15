--liquibase formatted sql
--changeset TRILM9:v1.88__alter_table_application_add_column_rmCommitStatus_20230926 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'application' AND COLUMN_NAME = 'rm_commit_status';
ALTER TABLE application ADD rm_commit_status TINYINT(1) NULL DEFAULT 0 COMMENT 'Trạng thái RM cam kết đã đối chiếu bản gốc hồ sơ';