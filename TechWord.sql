create database spill;

use spill;

CREATE TABLE Ord (
    ordID INTEGER PRIMARY KEY AUTO_INCREMENT,
    navn CHAR(5)
);

insert into Ord (navn) values
('linux'),
('react'),
('agile'),
('debug'),
('mysql'),
('maven'),
('kafka'),
('mongo'),
('xampp'),
('error'),
('input'),
('label'),
('array'),
('regex'),
('scope'),
('class'),
('shell'),
('route'),
('event'),
('merge');