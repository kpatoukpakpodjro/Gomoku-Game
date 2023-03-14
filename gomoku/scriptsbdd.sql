
CREATE DATABASE IF NOT EXISTS `mygame` DEFAULT CHARACTER SET utf8;
use mygame;

create table encours(
   nom varchar(25),
   adversaire varchar(25),
    idjeu varchar(50) unique primary key,
    contreia varchar(5),
    iacommence varchar(5),
    difficulte int ,
    nbaiderestant int default 0
);

create table stoneshistory(
	idp int primary key auto_increment ,
	pointx varchar(3),
    pointy varchar(3),
    proprio varchar(50) ,
    foreign key (proprio) REFERENCES encours (idjeu) on delete cascade
);

create table partiesgagnees(
	idpg int primary key auto_increment ,
	nom varchar(25),
	adversaire varchar(25),
    gagnant varchar(25),
    score1 float ,
    score2 float ,
    datejeu varchar(20) 
);

select * from partiesgagnees;

select * from stoneshistory;

select * from encours;