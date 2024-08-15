--liquibase formatted sql
--changeset TuanTV10:v1.63__updated_table_cic_20230720 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE cic ADD total_debt_card_limit decimal(20,0) NULL;
