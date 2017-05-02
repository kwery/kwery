alter table sql_query_email_setting add column single_result_styling boolean default false;

alter table email_configuration alter column bcc set data type varchar(32672);