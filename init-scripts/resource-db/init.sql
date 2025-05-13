-- Create the RESOURCE table
create table resource (
        id serial not null,
        data BYTEA,
        file_name varchar(255),
        type varchar(255),
        primary key (id)
    )