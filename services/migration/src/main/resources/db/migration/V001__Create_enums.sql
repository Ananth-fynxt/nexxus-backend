-- Migration: V001__Create_enums.sql
-- Description: Create all PostgreSQL enum types for the application
-- Service: shared

-- Create scope enum for system vs brand level permissions
CREATE TYPE scope AS ENUM ('SYSTEM', 'BRAND');

-- Create status enum for enabled/disabled states
CREATE TYPE status AS ENUM ('ENABLED', 'DISABLED');

-- Create token_type enum for different token purposes
CREATE TYPE token_type AS ENUM ('INVITATION', 'RESET_PASSWORD', 'ACCESS', 'REFRESH');

-- Create user_status enum for user account states
CREATE TYPE user_status AS ENUM ('INVITED', 'ACTIVE');

-- Create charge_fee_type enum for how fees are charged
CREATE TYPE charge_fee_type AS ENUM ('INCLUSIVE', 'EXCLUSIVE');

-- Create fee_component_type enum for component types
CREATE TYPE fee_component_type AS ENUM ('FIXED', 'FIXED_PER_UNIT', 'PERCENTAGE');

-- Create risk_type enum for different risk rule types
CREATE TYPE risk_type AS ENUM ('DEFAULT', 'CUSTOMER');

-- Create risk_action enum for risk rule actions
CREATE TYPE risk_action AS ENUM ('BLOCK', 'ALERT');

-- Create risk_duration enum for time periods
CREATE TYPE risk_duration AS ENUM ('HOUR', 'DAY', 'WEEK', 'MONTH');

-- Create risk_customer_criteria_type enum for customer criteria
CREATE TYPE risk_customer_criteria_type AS ENUM ('TAG', 'ACCOUNT_TYPE');

-- Create conversion_rate_source enum for FX rate sources
CREATE TYPE conversion_rate_source AS ENUM ('FIXER_API', 'MANUAL', 'CUSTOM_URL');

-- Create conversion_fetch_option enum for FX data fetching options
CREATE TYPE conversion_fetch_option AS ENUM ('REAL_TIME', 'PREVIOUS_DAY_CLOSING');

-- Create conversion_markup_option enum for FX markup options
CREATE TYPE conversion_markup_option AS ENUM ('FIXED_PER_UNIT', 'PERCENTAGE');

-- Create psp_selection_mode enum for PSP selection strategies
CREATE TYPE psp_selection_mode AS ENUM ('PRIORITY', 'RANDOM', 'WEIGHTED');

-- Create webhook_status_type enum for webhook event types
CREATE TYPE webhook_status_type AS ENUM ('SUCCESS', 'FAILURE', 'NOTIFICATION');

-- Create transaction_status enum for transaction state machine
CREATE TYPE transaction_status AS ENUM ('INITIATED', 'EVALUATED', 'PSP_OFFERED', 'PSP_SELECTED', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'ABANDONED');