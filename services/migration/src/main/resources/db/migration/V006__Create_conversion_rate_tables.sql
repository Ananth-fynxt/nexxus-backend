-- Migration: V006__Create_conversion_rate_tables.sql
-- Description: Create FX rate tables (conversion_rate, conversion_rate_markup_values) with versioning
-- Service: shared

-- Create conversion_rate table with versioning
CREATE TABLE conversion_rate (
    id TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    source_type conversion_rate_source NOT NULL DEFAULT 'FIXER_API',
    custom_url TEXT,
    fetch_option conversion_fetch_option,
    brand_id TEXT NOT NULL,
    environment_id TEXT NOT NULL,
    status status NOT NULL DEFAULT 'ENABLED',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by TEXT NOT NULL,
    updated_by TEXT NOT NULL,

    CONSTRAINT conversion_rate_config_version_pk PRIMARY KEY (id, version),
    CONSTRAINT conversion_rate_config_unique UNIQUE (source_type, fetch_option, version),
    CONSTRAINT fk_conversion_rate_brand_id FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_conversion_rate_environment_id FOREIGN KEY (environment_id) REFERENCES environments(id)
);

-- Create conversion_rate_markup_values table
CREATE TABLE conversion_rate_markup_values (
    conversion_rate_config_id TEXT NOT NULL,
    conversion_rate_config_version INTEGER NOT NULL,
    markup_option conversion_markup_option NOT NULL,
    source_currency TEXT NOT NULL,
    target_currency TEXT NOT NULL,
    amount NUMERIC(18,6) NOT NULL,

    CONSTRAINT conversion_rate_markup_pk PRIMARY KEY (conversion_rate_config_id, conversion_rate_config_version, markup_option, source_currency, target_currency),
    CONSTRAINT fk_conversion_rate_markup_values_config FOREIGN KEY (conversion_rate_config_id, conversion_rate_config_version) REFERENCES conversion_rate(id, version)
);

-- Create indexes for better performance
CREATE INDEX idx_conversion_rate_brand_id ON conversion_rate(brand_id);
CREATE INDEX idx_conversion_rate_environment_id ON conversion_rate(environment_id);
CREATE INDEX idx_conversion_rate_status ON conversion_rate(status);
CREATE INDEX idx_conversion_rate_source_type ON conversion_rate(source_type);
CREATE INDEX idx_conversion_rate_fetch_option ON conversion_rate(fetch_option);

CREATE INDEX idx_conversion_rate_markup_values_config ON conversion_rate_markup_values(conversion_rate_config_id, conversion_rate_config_version);
CREATE INDEX idx_conversion_rate_markup_values_markup_option ON conversion_rate_markup_values(markup_option);
CREATE INDEX idx_conversion_rate_markup_values_source_currency ON conversion_rate_markup_values(source_currency);
CREATE INDEX idx_conversion_rate_markup_values_target_currency ON conversion_rate_markup_values(target_currency);