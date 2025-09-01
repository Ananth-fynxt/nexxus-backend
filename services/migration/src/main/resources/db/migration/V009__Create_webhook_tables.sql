-- Migration: V011__Create_webhook_tables.sql
-- Description: Create webhook tables (webhooks, webhook_logs) for managing webhooks and their execution logs
-- Service: shared

-- Create webhooks table
CREATE TABLE webhooks (
    id TEXT PRIMARY KEY NOT NULL,
    status_type webhook_status_type NOT NULL,
    url TEXT NOT NULL,
    retry INTEGER NOT NULL DEFAULT 3,
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,

    CONSTRAINT fk_webhooks_brand_id FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_webhooks_environment_id FOREIGN KEY (environment_id) REFERENCES environments(id),
    CONSTRAINT uq_webhook_brand_status UNIQUE (brand_id, environment_id, status_type)
);

-- Create webhook_logs table
CREATE TABLE webhook_logs (
    id TEXT PRIMARY KEY,
    webhook_id TEXT NOT NULL,
    response_status INTEGER,
    is_success BOOLEAN NOT NULL,
    request_payload JSONB NOT NULL,
    response_payload JSONB NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_webhook_logs_webhook_id FOREIGN KEY (webhook_id) REFERENCES webhooks(id)
);

-- Create indexes for better query performance
CREATE INDEX idx_webhooks_brand_id ON webhooks(brand_id);
CREATE INDEX idx_webhooks_environment_id ON webhooks(environment_id);
CREATE INDEX idx_webhooks_status ON webhooks(status);
CREATE INDEX idx_webhooks_status_type ON webhooks(status_type);

CREATE INDEX idx_webhook_logs_webhook_id ON webhook_logs(webhook_id);
CREATE INDEX idx_webhook_logs_created_at ON webhook_logs(created_at);
CREATE INDEX idx_webhook_logs_is_success ON webhook_logs(is_success);