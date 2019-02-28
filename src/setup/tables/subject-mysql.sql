create table subject (
  id int auto_increment primary key not null,
  name varchar(255) not null, 
  unique(name)
)
