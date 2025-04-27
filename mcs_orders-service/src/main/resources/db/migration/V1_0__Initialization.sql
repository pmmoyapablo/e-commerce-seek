create schema if not exists transaction_db;

CREATE SEQUENCE IF NOT EXISTS transaction_db.transactions_id_seq;

CREATE TABLE IF NOT EXISTS transaction_db.transactions
(
    id  BIGINT PRIMARY KEY DEFAULT nextval('transaction_db.transactions_id_seq'),
    from_account BIGINT NOT NULL,
    to_account  BIGINT NOT NULL,
    monto DOUBLE PRECISION NOT NULL,
    fecha TIMESTAMP NOT NULL
);

-- Asegurarse de que la secuencia esté asociada a la tabla
ALTER SEQUENCE transaction_db.transactions_id_seq OWNED BY transaction_db.transactions.id;