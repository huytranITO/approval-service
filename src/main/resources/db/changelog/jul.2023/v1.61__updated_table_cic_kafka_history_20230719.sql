--liquibase formatted sql
--changeset TuanTV10:v1.61__updated_table_cic_kafka_history_20230719 dbms:mysql
--preconditions onFail:HALT onError:HALT
ALTER TABLE cic_kafka_history ADD application_bpm_id varchar(16) NULL;
ALTER TABLE cic_kafka_history ADD application_id BIGINT NULL;
ALTER TABLE cic_kafka_history ADD client_question_ids varchar(256) NULL;
