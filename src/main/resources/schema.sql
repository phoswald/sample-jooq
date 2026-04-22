create table if not exists task_ (
  task_id_      varchar(255) primary key not null,
  user_id_      varchar(255),
  timestamp_    timestamp,
  title_        varchar(255),
  description_  varchar(255),
  done_         boolean
);
