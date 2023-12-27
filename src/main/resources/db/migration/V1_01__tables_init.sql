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

CREATE TABLE IF NOT EXISTS customer
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    first_name text,
    last_name text,
    phone_number text,
    chat_id text,
    state text,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS visit
(
    id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 ),
    user_id integer,
    performance_id integer,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)
        REFERENCES circus_tg_chat.customer (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

