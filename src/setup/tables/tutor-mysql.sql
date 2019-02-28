create table tutor (
  id int auto_increment primary key not null,
  name varchar(255) not null, 
  email varchar(255) not null, 
  subject_id int not null,
  foreign key(subject_id) references subject(id),
  unique(name)
)
