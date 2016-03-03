CREATE TABLE users
(
	username VARCHAR(15) NOT NULL,
	password VARCHAR(15) NOT NULL,
	PRIMARY KEY (username)
);

CREATE TABLE profs
(
	fname VARCHAR(15) NOT NULL,
	lname VARCHAR(15) NOT NULL, 
	school VARCHAR(30) NOT NULL,
	PRIMARY KEY (lname)
);

CREATE TABLE ratings
(
	fname VARCHAR(15) NOT NULL,
	lname VARCHAR(15) NOT NULL,
	rating INTEGER NOT NULL, 
	classname VARCHAR(5) NOT NULL,
	classnumber INTEGER NOT NULL,
	comments VARCHAR(300),
	FOREIGN KEY (lname) REFERENCES profs(lname)
);