--liquibase formatted sql
--changeset TuanTV10:v1.69__updated_table_cic_20230731 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE cic ADD product_code varchar(6) NULL;
