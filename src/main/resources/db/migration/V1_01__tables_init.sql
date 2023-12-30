CREATE TABLE IF NOT EXISTS operator
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    name text,
    chat_id text,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS performance
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    name text,
    date timestamp,
    time time,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS visitor
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    first_name text,
    last_name text,
    phone_number text,
    chat_id text,
    state text,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS ticket
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    visitor_id integer,
    performance_id integer,
    visitor_first_name text,
    visitor_last_name text,
    visitor_phone_number text,
    PRIMARY KEY (id),
    FOREIGN KEY (visitor_id)
        REFERENCES circus_tg_chat.visitor (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

