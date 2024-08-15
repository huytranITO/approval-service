--liquibase formatted sql
--changeset TuanTV10:v1.68__updated_table_cic_20230726 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE cic ADD customer_type varchar(8) NULL;
