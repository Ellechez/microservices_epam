-- Create the SONG table
create table song (
        id serial not null,
        album varchar(255),
        artist varchar(255),
        length varchar(255),
        name varchar(255),
        year varchar(255),
        primary key (id)
    )