CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE wallets (
    id BIGSERIAL PRIMARY KEY,
    currency VARCHAR(10) NOT NULL,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    user_id BIGINT NOT NULL REFERENCES users(id)
);

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    from_wallet_id BIGINT NOT NULL,
    to_wallet_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    from_currency VARCHAR(10) NOT NULL,
    to_currency VARCHAR(10) NOT NULL,
    exchange_rate DECIMAL(19,6) NOT NULL,
    converted_amount DECIMAL(19,2) NOT NULL,
    timestamp TIMESTAMP NOT NULL
);
