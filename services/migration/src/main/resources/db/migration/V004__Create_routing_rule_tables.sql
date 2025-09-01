-- Migration: V012__Create_routing_rule_tables.sql
-- Description: Create routing rules tables (routing_rules, psps) with versioning
-- Service: shared

-- Create routing_rules table with versioning
CREATE TABLE routing_rules (
    id TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    name TEXT NOT NULL,
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    psp_selection_mode psp_selection_mode NOT NULL,
    condition_json JSONB NOT NULL,
    is_default BOOLEAN NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,

    CONSTRAINT routing_rules_pk PRIMARY KEY (id, version),
    CONSTRAINT fk_routing_rules_brand_id FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_routing_rules_environment_id FOREIGN KEY (environment_id) REFERENCES environments(id)
);

-- Create routing_rule_psps table
CREATE TABLE routing_rule_psps (
    routing_rule_id TEXT NOT NULL,
    routing_rule_version INTEGER NOT NULL,
    psp_id TEXT NOT NULL,
    psp_value INTEGER,

    CONSTRAINT routing_rule_psp_pk PRIMARY KEY (routing_rule_id, routing_rule_version, psp_id),
    CONSTRAINT fk_routing_rule_psps_rule FOREIGN KEY (routing_rule_id, routing_rule_version) REFERENCES routing_rules(id, version),
    CONSTRAINT fk_routing_rule_psps_psp FOREIGN KEY (psp_id) REFERENCES psps(id)
);

-- Create indexes for better query performance
CREATE INDEX idx_routing_rules_brand_id ON routing_rules(brand_id);
CREATE INDEX idx_routing_rules_environment_id ON routing_rules(environment_id);
CREATE INDEX idx_routing_rules_status ON routing_rules(status);
CREATE INDEX idx_routing_rules_psp_selection_mode ON routing_rules(psp_selection_mode);

CREATE INDEX idx_routing_rule_psps_rule_id ON routing_rule_psps(routing_rule_id, routing_rule_version);
CREATE INDEX idx_routing_rule_psps_psp_id ON routing_rule_psps(psp_id);