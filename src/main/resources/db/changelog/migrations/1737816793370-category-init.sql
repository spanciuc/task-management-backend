-- liquibase formatted sql

-- changeset spanciuc:1737816793370-1
CREATE TABLE category
(
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    CONSTRAINT pk_category PRIMARY KEY (id)
);

-- rollback DROP TABLE IF EXISTS category;

