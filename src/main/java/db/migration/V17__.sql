alter table sql_query_email_setting add column ignore_label boolean default false;

alter table email_configuration alter column bcc set data type varchar(32672);