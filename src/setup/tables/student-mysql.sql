create table student (
  id integer auto_increment primary key not null,
  name varchar(255) not null, 
  enrolled date not null,
  unique(name)
)
