-- Migration: V011__Create_transaction_tables.sql
-- Description: Create transaction tables (transactions, transaction_logs) for managing payment transactions and their logs
-- Service: core

-- Create transactions table
CREATE TABLE transactions (
    id TEXT PRIMARY KEY NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    currency TEXT NOT NULL,
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    flow_action_id TEXT NOT NULL,
    routing_rule_id TEXT,
    user_attribute JSONB NOT NULL,
    status transaction_status NOT NULL DEFAULT 'INITIATED',
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_transactions_flow_action FOREIGN KEY (flow_action_id) REFERENCES flow_actions(id),
    CONSTRAINT fk_transactions_brand_id FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_transactions_environment_id FOREIGN KEY (environment_id) REFERENCES environments(id)
);

-- Create transaction_logs table
CREATE TABLE transaction_logs (
    id TEXT PRIMARY KEY NOT NULL,
    transaction_id TEXT NOT NULL,
    psp_id TEXT,
    webhook_id TEXT,
    log JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_transaction_logs_transaction_id FOREIGN KEY (transaction_id) REFERENCES transactions(id),
    CONSTRAINT fk_transaction_logs_psp_id FOREIGN KEY (psp_id) REFERENCES psps(id),
    CONSTRAINT fk_transaction_logs_webhook_id FOREIGN KEY (webhook_id) REFERENCES webhooks(id)
);

-- Create indexes for better query performance
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_currency ON transactions(currency);
CREATE INDEX idx_transactions_flow_action_id ON transactions(flow_action_id);
CREATE INDEX idx_transactions_brand_id ON transactions(brand_id);
CREATE INDEX idx_transactions_environment_id ON transactions(environment_id);
CREATE INDEX idx_transactions_routing_rule_id ON transactions(routing_rule_id);

CREATE INDEX idx_transaction_logs_transaction_id ON transaction_logs(transaction_id);
CREATE INDEX idx_transaction_logs_psp_id ON transaction_logs(psp_id);
CREATE INDEX idx_transaction_logs_webhook_id ON transaction_logs(webhook_id);
CREATE INDEX idx_transaction_logs_created_at ON transaction_logs(created_at);
