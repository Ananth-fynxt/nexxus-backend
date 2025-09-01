-- Migration: V003__Create_psp_tables.sql
-- Description: Create PSP management tables (psps, psp_operations, supported_currencies, currency_limits)
-- Service: shared

-- Create psps table
CREATE TABLE psps (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    logo TEXT,
    credential JSONB NOT NULL,
    timeout INTEGER NOT NULL DEFAULT 300,
    block_vpn_access BOOLEAN DEFAULT FALSE,
    block_data_center_access BOOLEAN DEFAULT FALSE,
    failure_rate BOOLEAN DEFAULT FALSE,
    failure_rate_threshold INTEGER DEFAULT 0,
    failure_rate_duration_minutes INTEGER DEFAULT 60,
    ip_address TEXT[],
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    flow_target_id TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,

    CONSTRAINT fk_psp_brand FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_psp_environment FOREIGN KEY (environment_id) REFERENCES environments(id),
    CONSTRAINT fk_psp_flow_target FOREIGN KEY (flow_target_id) REFERENCES flow_targets(id),
    CONSTRAINT brand_target_unique_constraint UNIQUE (brand_id, environment_id, flow_target_id, name)
);

-- Create psp_operations table
CREATE TABLE psp_operations (
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    psp_id TEXT NOT NULL,
    flow_action_id TEXT NOT NULL,
    flow_definition_id TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'ENABLED',

    CONSTRAINT pk_psp_operations PRIMARY KEY (brand_id, environment_id, psp_id, flow_action_id, flow_definition_id),
    CONSTRAINT fk_psp_operations_brand FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_psp_operations_environment FOREIGN KEY (environment_id) REFERENCES environments(id),
    CONSTRAINT fk_psp_operations_psp FOREIGN KEY (psp_id) REFERENCES psps(id),
    CONSTRAINT fk_psp_operations_flow_action FOREIGN KEY (flow_action_id) REFERENCES flow_actions(id),
    CONSTRAINT fk_psp_operations_flow_definition FOREIGN KEY (flow_definition_id) REFERENCES flow_definitions(id),
    CONSTRAINT chk_psp_operations_status CHECK (status IN ('ENABLED', 'DISABLED'))
);

-- Create currency_limits table
CREATE TABLE currency_limits (
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    flow_action_id TEXT NOT NULL,
    psp_id TEXT NOT NULL,
    currency TEXT NOT NULL,
    min_value NUMERIC(20,8) NOT NULL,
    max_value NUMERIC(20,8),

    CONSTRAINT currency_limit_pk PRIMARY KEY (brand_id, environment_id, flow_action_id, psp_id, currency),
    CONSTRAINT fk_currency_limits_brand FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_currency_limits_environment FOREIGN KEY (environment_id) REFERENCES environments(id),
    CONSTRAINT fk_currency_limits_flow_action FOREIGN KEY (flow_action_id) REFERENCES flow_actions(id),
    CONSTRAINT fk_currency_limits_psp FOREIGN KEY (psp_id) REFERENCES psps(id)
);

-- Create maintenance_windows table with versioning
CREATE TABLE maintenance_windows (
    id TEXT NOT NULL,
    psp_id TEXT NOT NULL,
    flow_action_id TEXT NOT NULL,
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    status TEXT NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,

    CONSTRAINT maintenance_window_pk PRIMARY KEY (id),
    CONSTRAINT fk_maintenance_window_psp FOREIGN KEY (psp_id) REFERENCES psps(id),
    CONSTRAINT fk_maintenance_window_flow_action FOREIGN KEY (flow_action_id) REFERENCES flow_actions(id),
    CONSTRAINT chk_maintenance_window_status CHECK (status IN ('ENABLED', 'DISABLED'))
);

-- Create indexes for better query performance
CREATE INDEX idx_psp_brand_id ON psps(brand_id);
CREATE INDEX idx_psp_environment_id ON psps(environment_id);
CREATE INDEX idx_psp_flow_target_id ON psps(flow_target_id);
CREATE INDEX idx_psp_status ON psps(status);

CREATE INDEX idx_psp_operations_brand_env ON psp_operations(brand_id, environment_id);
CREATE INDEX idx_psp_operations_psp_id ON psp_operations(psp_id);
CREATE INDEX idx_psp_operations_flow_action_id ON psp_operations(flow_action_id);
CREATE INDEX idx_psp_operations_flow_definition_id ON psp_operations(flow_definition_id);
CREATE INDEX idx_psp_operations_status ON psp_operations(status);

CREATE INDEX idx_currency_limits_brand_env ON currency_limits(brand_id, environment_id);
CREATE INDEX idx_currency_limits_psp_id ON currency_limits(psp_id);
CREATE INDEX idx_currency_limits_flow_action_id ON currency_limits(flow_action_id);
CREATE INDEX idx_currency_limits_currency ON currency_limits(currency);
CREATE INDEX idx_currency_limits_min_value ON currency_limits(min_value);
CREATE INDEX idx_currency_limits_max_value ON currency_limits(max_value);

CREATE INDEX idx_maintenance_windows_psp_id ON maintenance_windows(psp_id);
CREATE INDEX idx_maintenance_windows_flow_action_id ON maintenance_windows(flow_action_id);
CREATE INDEX idx_maintenance_windows_status ON maintenance_windows(status);
CREATE INDEX idx_maintenance_windows_time_range ON maintenance_windows(start_at, end_at);