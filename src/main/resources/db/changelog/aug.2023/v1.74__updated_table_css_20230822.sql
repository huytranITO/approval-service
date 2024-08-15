--liquibase formatted sql
--changeset dungda5:v1.74__updated_table_css_20230822 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE application_credit_ratings_dtl ADD scoring_id varchar(64) NULL,
ADD identity_no varchar(64) NULL,
ADD type_of_model varchar(64) NULL,
ADD scoring_source varchar(64) NULL,
ADD recommendation varchar(255) NULL,
ADD scoring_time varchar(64) NULL;