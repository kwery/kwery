alter table job_sql_query alter column ui_order not null;
alter table job_sql_query add constraint uc_job_sql_query_sql_query_id_fk_job_id_fk_ui_order unique (sql_query_id_fk, job_id_fk, ui_order);
