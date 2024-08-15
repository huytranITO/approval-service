--liquibase formatted sql
--changeset TuanHA13:v1.18__alter_table_cic_20230427-001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'cic ' AND COLUMN_NAME = 'cic_code';
--precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = (SELECT DATABASE())AND TABLE_NAME = 'cic ' AND COLUMN_NAME = 'client_question_id';
ALTER TABLE cic ADD cic_code varchar(50) DEFAULT NULL COMMENT 'Mã đối tượng liên quan';
ALTER TABLE cic ADD client_question_id bigint DEFAULT NULL COMMENT 'Mã hệ thống hỏi tin tới CIC';