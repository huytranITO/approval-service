--liquibase formatted sql
--changeset TuanTV10:v1.67__updated_table_cic_20230726 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE cic ADD pdf_data longtext NULL;
