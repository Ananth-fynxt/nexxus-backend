-- Migration: V013__Alter_conversion_rate_constraints.sql
-- Description: Add constraints and index for conversion_rate based on source_type rules

-- Enforce field requirements by source_type
ALTER TABLE conversion_rate
  ADD CONSTRAINT conversion_rate_source_field_requirements CHECK (
    (source_type = 'FIXER_API'::conversion_rate_source AND fetch_option IS NOT NULL AND custom_url IS NULL)
    OR
    (source_type = 'CUSTOM_URL'::conversion_rate_source AND custom_url IS NOT NULL AND fetch_option IS NULL)
    OR
    (source_type = 'MANUAL'::conversion_rate_source AND custom_url IS NULL AND fetch_option IS NULL)
  );

-- Helpful index when using custom URL source
CREATE INDEX IF NOT EXISTS idx_conversion_rate_custom_url ON conversion_rate(custom_url);

