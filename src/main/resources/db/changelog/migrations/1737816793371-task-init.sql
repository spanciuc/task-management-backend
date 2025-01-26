-- liquibase formatted sql

-- changeset spanciuc:1737816793371-1
CREATE TABLE task
(
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    priority    VARCHAR(255),
    status      VARCHAR(255),
    due_date    DATE,
    category_id UUID         NOT NULL,
    CONSTRAINT pk_task PRIMARY KEY (id)
);

ALTER TABLE task
    ADD CONSTRAINT FK_TASK_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);

-- rollback DROP TABLE IF EXISTS task;

