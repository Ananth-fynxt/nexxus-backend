-- Migration: V008__Create_fee_tables.sql
-- Description: Create fee tables (fee, fee_components, fee_countries, fee_psps) with versioning
-- Service: shared

-- Create fee table with versioning
CREATE TABLE fee (
    id TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    name TEXT NOT NULL,
    currency TEXT NOT NULL,
    charge_fee_type charge_fee_type NOT NULL,
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    flow_action_id TEXT NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,

    CONSTRAINT fee_version_pk PRIMARY KEY (id, version),
    CONSTRAINT fk_fee_brand FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_fee_environment FOREIGN KEY (environment_id) REFERENCES environments(id),
    CONSTRAINT fk_fee_flow_action FOREIGN KEY (flow_action_id) REFERENCES flow_actions(id)
);

-- Create fee_components table
CREATE TABLE fee_components (
    id TEXT NOT NULL,
    fee_id TEXT NOT NULL,
    fee_version INTEGER NOT NULL,
    fee_component_type fee_component_type NOT NULL,
    amount NUMERIC(18,6) NOT NULL,
    min_value NUMERIC(18,6),
    max_value NUMERIC(18,6),

    CONSTRAINT fee_component_pk PRIMARY KEY (id, fee_id, fee_version),
    CONSTRAINT fk_fee_components_fee FOREIGN KEY (fee_id, fee_version) REFERENCES fee(id, version)
);

-- Create fee_countries table
CREATE TABLE fee_countries (
    fee_id TEXT NOT NULL,
    fee_version INTEGER NOT NULL,
    country TEXT NOT NULL,

    CONSTRAINT fee_country_pk PRIMARY KEY (fee_id, fee_version, country),
    CONSTRAINT fk_fee_countries_fee FOREIGN KEY (fee_id, fee_version) REFERENCES fee(id, version)
);

-- Create fee_psps table
CREATE TABLE fee_psps (
    fee_id TEXT NOT NULL,
    fee_version INTEGER NOT NULL,
    psp_id TEXT NOT NULL,

    CONSTRAINT fee_psp_unique PRIMARY KEY (fee_id, fee_version, psp_id),
    CONSTRAINT fk_fee_psps_fee FOREIGN KEY (fee_id, fee_version) REFERENCES fee(id, version),
    CONSTRAINT fk_fee_psps_psp FOREIGN KEY (psp_id) REFERENCES psps(id)
);

-- Create indexes for better performance
CREATE INDEX idx_fee_brand_id ON fee(brand_id);
CREATE INDEX idx_fee_environment_id ON fee(environment_id);
CREATE INDEX idx_fee_flow_action_id ON fee(flow_action_id);
CREATE INDEX idx_fee_status ON fee(status);
CREATE INDEX idx_fee_charge_fee_type ON fee(charge_fee_type);
CREATE INDEX idx_fee_currency ON fee(currency);
CREATE INDEX idx_fee_name ON fee(name);

CREATE INDEX idx_fee_components_fee_id ON fee_components(fee_id, fee_version);
CREATE INDEX idx_fee_components_fee_component_type ON fee_components(fee_component_type);

CREATE INDEX idx_fee_countries_fee_id ON fee_countries(fee_id, fee_version);
CREATE INDEX idx_fee_countries_country ON fee_countries(country);

CREATE INDEX idx_fee_psps_fee_id ON fee_psps(fee_id, fee_version);
CREATE INDEX idx_fee_psps_psp_id ON fee_psps(psp_id);