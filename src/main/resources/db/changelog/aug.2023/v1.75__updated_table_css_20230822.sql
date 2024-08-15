--liquibase formatted sql
--changeset dungda5:v1.75__updated_table_css_20230822 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE application_credit_ratings_dtl MODIFY scoring_id BIGINT NULL;