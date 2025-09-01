-- Migration: V005__Create_crm_tables.sql
-- Description: Create CRM customer table for managing customer data
-- Service: shared

-- Create crm_customer table
CREATE TABLE crm_customer (
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    crm_customer_id TEXT NOT NULL,
    name TEXT,
    email TEXT,
    tag TEXT,
    country TEXT,
    account_type TEXT,
    custom_attributes JSONB NOT NULL DEFAULT '{}',
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT crm_customer_pk PRIMARY KEY (brand_id, environment_id, crm_customer_id),
    CONSTRAINT fk_crm_customer_brand FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_crm_customer_environment FOREIGN KEY (environment_id) REFERENCES environments(id)
);

-- Create indexes for better query performance
CREATE INDEX idx_crm_customer_brand_id ON crm_customer(brand_id);
CREATE INDEX idx_crm_customer_environment_id ON crm_customer(environment_id);
CREATE INDEX idx_crm_customer_email ON crm_customer(email);
CREATE INDEX idx_crm_customer_country ON crm_customer(country);
CREATE INDEX idx_crm_customer_account_type ON crm_customer(account_type);