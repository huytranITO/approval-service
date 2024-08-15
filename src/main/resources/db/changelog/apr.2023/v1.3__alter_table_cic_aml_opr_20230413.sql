--liquibase formatted sql
--changeset ManhNV8:v1.3__alter_table_cic_aml_opr_20230413-001 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'aml_opr'
ALTER TABLE aml_opr ADD identifier_code varchar(45) NULL;
ALTER TABLE aml_opr ADD subject varchar(45) NULL;

--liquibase formatted sql
--changeset ManhNV8:v1.3__alter_table_cic_aml_opr_20230413-002 dbms:mysql
--preconditions onFail:HALT onError:HALT
--precondition-sql-check expectedResult:1 SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = (SELECT DATABASE()) AND TABLE_NAME = 'cic'
ALTER TABLE cic ADD subject varchar(45) NULL;
ALTER TABLE cic ADD identifier_code varchar(45) NULL;