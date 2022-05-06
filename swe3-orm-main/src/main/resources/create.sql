CREATE TABLE IF NOT EXISTS teachers
(
    id        INT NOT NULL auto_increment,
    lastName      VARCHAR (255),
    firstName VARCHAR (255),
    enterDate  VARCHAR (255),
    leaveDate VARCHAR (255),
    gender    VARCHAR (255),
    salary    INT,
    PRIMARY KEY ( id )
    );


CREATE TABLE IF NOT EXISTS students
(
    id        INT NOT NULL auto_increment,
    lastname  VARCHAR (255),
    firstname VARCHAR (255),
    enterdate VARCHAR (255),
    leavedate VARCHAR (255),
    gender    VARCHAR (255),
    gpa       VARCHAR (255),
    PRIMARY KEY ( id )
    );



CREATE TABLE IF NOT EXISTS books
(
    b_id        INT NOT NULL auto_increment,
    titel      VARCHAR (255),
    author_fk INT,
    type_fk  INT,
    PRIMARY KEY ( b_id )
    );

CREATE TABLE IF NOT EXISTS authors
(
    a_id  INT NOT NULL auto_increment,
    fname VARCHAR (255),
    lname VARCHAR (255),
    PRIMARY KEY ( a_id )
    );

CREATE TABLE IF NOT EXISTS types
  (
     t_id  INT NOT NULL auto_increment,
     name VARCHAR (255),
     PRIMARY KEY ( t_id )
  );

CREATE TABLE IF NOT EXISTS borrows
(
    borrow_id   INT NOT NULL auto_increment,
    takendate   VARCHAR (255),
    broughtdate VARCHAR (255),
    id_fk       INT,
    b_id_fk     INT,
    PRIMARY KEY ( borrow_id )
    );