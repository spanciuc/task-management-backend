-- liquibase formatted sql

-- changeset spanciuc:1737816793372-1
CREATE TABLE user_account
(
    id         UUID         NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    username   VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user_account PRIMARY KEY (id)
);

ALTER TABLE user_account
    ADD CONSTRAINT uc_user_account_email UNIQUE (email);

ALTER TABLE user_account
    ADD CONSTRAINT uc_user_account_username UNIQUE (username);

-- rollback DROP TABLE IF EXISTS user_account;

