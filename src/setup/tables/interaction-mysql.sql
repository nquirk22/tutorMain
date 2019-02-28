create table interaction (
  id int auto_increment primary key not null,
  tutor_id int not null,
  student_id int not null,
  report text,
  foreign key(student_id) references student(id),
  foreign key(tutor_id) references tutor(id),
  unique(student_id, tutor_id)
)
