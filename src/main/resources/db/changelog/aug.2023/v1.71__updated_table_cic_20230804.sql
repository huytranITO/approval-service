--liquibase formatted sql
--changeset TuanTV10:v1.71__updated_table_cic_20230804 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE cic ADD cic_indicator_status BOOL DEFAULT 0 NOT NULL,
ADD pdf_status BOOL DEFAULT 0 NOT NULL,
ADD cic_customer_name varchar(64) NULL;