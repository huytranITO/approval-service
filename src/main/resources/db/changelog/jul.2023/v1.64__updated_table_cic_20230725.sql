--liquibase formatted sql
--changeset TuanTV10:v1.64__updated_table_cic_20230725 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE cic ADD ref_customer_id bigint NULL,
ADD relationship varchar(16) NULL;
