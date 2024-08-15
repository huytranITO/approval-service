--liquibase formatted sql
--changeset TuanTV10:v1.62__updated_table_cic_20230720 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE cic ADD total_loan_collateral_usd decimal(20,0) NULL
               ,ADD total_unsecure_loan_usd decimal(20,0) NULL;
