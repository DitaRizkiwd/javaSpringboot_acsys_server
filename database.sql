CREATE TABLE users
(
    id               VARCHAR(100) NOT NULL,
    username         VARCHAR(100) NOT NULL,
    password         VARCHAR(100) NOT NULL,
    name             VARCHAR(100) NOT NULL,
    role             VARCHAR(100) NOT NULL,
    token            VARCHAR(100),
    status           VARCHAR(100),
    token_expired_at BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (id),
    UNIQUE (token)
) ENGINE InnoDB;