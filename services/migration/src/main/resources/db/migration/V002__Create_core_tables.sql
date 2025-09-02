-- Migration: V002__Create_core_tables.sql
-- Description: Create core application tables (brands, environments, flow_types, flow_actions, flow_targets, flow_definitions)
-- Service: shared

-- Create brands table
CREATE TABLE brands (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Create environments table
CREATE TABLE environments (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    secret TEXT NOT NULL UNIQUE,
    token TEXT NOT NULL UNIQUE,
    origin TEXT NOT NULL,
    brand_id TEXT NOT NULL REFERENCES brands(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create flow_types table
CREATE TABLE flow_types (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create flow_actions table
CREATE TABLE flow_actions (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    steps TEXT[] NOT NULL,
    flow_type_id TEXT NOT NULL REFERENCES flow_types(id),
    input_schema JSONB NOT NULL,
    output_schema JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create flow_targets table
CREATE TABLE flow_targets (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    logo TEXT NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    credential_schema JSONB NOT NULL,
    input_schema JSONB NOT NULL DEFAULT '{}',
    currencies TEXT[] NOT NULL DEFAULT '{}',
    countries TEXT[] NOT NULL DEFAULT '{}',
    payment_methods TEXT[] NOT NULL DEFAULT '{}',
    flow_type_id TEXT NOT NULL REFERENCES flow_types(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create flow_definitions table
CREATE TABLE flow_definitions (
    id TEXT PRIMARY KEY NOT NULL,
    flow_action_id TEXT NOT NULL REFERENCES flow_actions(id),
    flow_target_id TEXT NOT NULL REFERENCES flow_targets(id),
    description TEXT,
    code TEXT NOT NULL,
    brand_id TEXT REFERENCES brands(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL
);

-- Create indexes for better query performance
CREATE INDEX idx_brands_name ON brands(name);

CREATE INDEX idx_environments_brand_id ON environments(brand_id);
CREATE INDEX idx_environments_name ON environments(name);
CREATE INDEX idx_environments_secret ON environments(secret);
CREATE INDEX idx_environments_token ON environments(token);

CREATE INDEX idx_flow_types_name ON flow_types(name);
CREATE INDEX idx_flow_types_created_by ON flow_types(created_by);

CREATE UNIQUE INDEX flow_action_unique_constraint ON flow_actions(flow_type_id, name);
CREATE INDEX idx_flow_actions_flow_type_id ON flow_actions(flow_type_id);
CREATE INDEX idx_flow_actions_name ON flow_actions(name);
CREATE INDEX idx_flow_actions_created_by ON flow_actions(created_by);

CREATE UNIQUE INDEX flow_target_type_name ON flow_targets(flow_type_id, name);
CREATE INDEX idx_flow_targets_flow_type_id ON flow_targets(flow_type_id);
CREATE INDEX idx_flow_targets_name ON flow_targets(name);
CREATE INDEX idx_flow_targets_status ON flow_targets(status);
CREATE INDEX idx_flow_targets_created_by ON flow_targets(created_by);

CREATE UNIQUE INDEX flow_definition_action_target ON flow_definitions(flow_action_id, flow_target_id);
CREATE UNIQUE INDEX flow_definition_code_brand ON flow_definitions(code, brand_id);
CREATE INDEX idx_flow_definitions_flow_action_id ON flow_definitions(flow_action_id);
CREATE INDEX idx_flow_definitions_flow_target_id ON flow_definitions(flow_target_id);
CREATE INDEX idx_flow_definitions_brand_id ON flow_definitions(brand_id);
CREATE INDEX idx_flow_definitions_code ON flow_definitions(code);
CREATE INDEX idx_flow_definitions_created_by ON flow_definitions(created_by);