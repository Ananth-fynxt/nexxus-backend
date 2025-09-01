-- Migration: V010__Seed_sample_data.sql
-- Description: Seed sample data for development and testing
-- Service: shared

-- ===========================================
-- SAMPLE DATA SEED FILE
-- ===========================================

-- ===========================================
-- 1. CREATE SAMPLE BRAND
-- ===========================================

INSERT INTO brands (id, name, created_at, updated_at) VALUES
('brn_sample_001', 'Sample Financial Services', NOW(), NOW());

-- ===========================================
-- 2. CREATE BRAND-BASED ENVIRONMENTS
-- ===========================================

INSERT INTO environments (id, name, secret, token, origin, brand_id, created_at, updated_at, created_by, updated_by) VALUES
('env_sample_dev_001', 'Development Environment', 'sec_sample_secret_dev', 'sample_token_dev_123', 'https://dev.sample.com', 'brn_sample_001', NOW(), NOW(), 'system', 'system'),
('env_sample_stg_001', 'Staging Environment', 'sec_sample_secret_stg', 'sample_token_stg_456', 'https://staging.sample.com', 'brn_sample_001', NOW(), NOW(), 'system', 'system'),
('env_sample_prd_001', 'Production Environment', 'sec_sample_secret_prd', 'sample_token_prd_789', 'https://api.sample.com', 'brn_sample_001', NOW(), NOW(), 'system', 'system');

-- ===========================================
-- 3. CREATE FLOW TYPE
-- ===========================================

INSERT INTO flow_types (id, name, created_at, updated_at, created_by, updated_by) VALUES
('ftp_payment_001', 'Payment Processing', NOW(), NOW(), 'system', 'system');

-- ===========================================
-- 4. CREATE FLOW ACTIONS BASED ON FLOW TYPE
-- ===========================================

INSERT INTO flow_actions (id, name, steps, flow_type_id, input_schema, output_schema, created_at, updated_at, created_by, updated_by) VALUES
('fat_deposit_001', 'Deposit', ARRAY['validate_amount', 'validate_currency', 'check_limits'], 'ftp_payment_001',
 '{
   "type": "object",
   "properties": {
     "amount": {"type": "number", "minimum": 0.01},
     "currency": {"type": "string", "enum": ["USD", "EUR", "GBP"]},
     "customerId": {"type": "string"}
   },
   "required": ["amount", "currency", "customerId"]
 }',
 '{
   "type": "object",
   "properties": {
     "valid": {"type": "boolean"},
     "validationErrors": {"type": "array", "items": {"type": "string"}}
   },
   "required": ["valid"]
 }',
 NOW(), NOW(), 'system', 'system'),

('fat_withdraw_001', 'Withdraw', ARRAY['initiate_transaction', 'process_fees', 'update_balance'], 'ftp_payment_001',
 '{
   "type": "object",
   "properties": {
     "amount": {"type": "number", "minimum": 0.01},
     "currency": {"type": "string", "enum": ["USD", "EUR", "GBP"]},
     "customerId": {"type": "string"},
     "validated": {"type": "boolean"}
   },
   "required": ["amount", "currency", "customerId", "validated"]
 }',
 '{
   "type": "object",
   "properties": {
     "transactionId": {"type": "string"},
     "status": {"type": "string", "enum": ["SUCCESS", "FAILED", "PENDING"]},
     "processedAmount": {"type": "number"},
     "feeAmount": {"type": "number"}
   },
   "required": ["transactionId", "status"]
 }',
 NOW(), NOW(), 'system', 'system'),

('fat_refund_001', 'Refund', ARRAY['send_notification', 'update_status', 'cleanup'], 'ftp_payment_001',
 '{
   "type": "object",
   "properties": {
     "transactionId": {"type": "string"},
     "status": {"type": "string"},
     "notificationRequired": {"type": "boolean"}
   },
   "required": ["transactionId", "status"]
 }',
 '{
   "type": "object",
   "properties": {
     "completed": {"type": "boolean"},
     "notificationSent": {"type": "boolean"}
   },
   "required": ["completed"]
 }',
 NOW(), NOW(), 'system', 'system');

-- ===========================================
-- 5. CREATE FLOW TARGETS BASED ON FLOW TYPE
-- ===========================================

INSERT INTO flow_targets (id, name, currencies, logo, status, credential_schema, input_schema, flow_type_id, created_at, updated_at, created_by, updated_by) VALUES
('ftg_stripe_payment_001', 'Stripe Payment Gateway', '{USD,EUR}', 'https://sample-logos.s3.amazonaws.com/stripe-logo.png', 'ENABLED',
 '{
   "type": "object",
   "properties": {
     "apiKey": {"type": "string"},
     "webhookSecret": {"type": "string"},
     "accountId": {"type": "string"}
   },
   "required": ["apiKey", "webhookSecret"]
 }',
 '{
   "type": "object",
   "properties": {
     "paymentMethod": {"type": "string"},
     "metadata": {"type": "object"}
   }
 }',
 'ftp_payment_001', NOW(), NOW(), 'system', 'system'),

('ftg_paypal_payment_001', 'PayPal Payment Gateway', '{AED,INR}','https://sample-logos.s3.amazonaws.com/paypal-logo.png', 'ENABLED',
 '{
   "type": "object",
   "properties": {
     "clientId": {"type": "string"},
     "clientSecret": {"type": "string"},
     "environment": {"type": "string"}
   },
   "required": ["clientId", "clientSecret"]
 }',
 '{
   "type": "object",
   "properties": {
     "paymentType": {"type": "string"},
     "experienceProfileId": {"type": "string"}
   },
   "required": ["paymentType", "experienceProfileId"]
 }',
 'ftp_payment_001', NOW(), NOW(), 'system', 'system'),

('ftg_bank_transfer_001', 'Bank Transfer Service', '{CHN,CAD}', 'https://sample-logos.s3.amazonaws.com/bank-logo.png', 'ENABLED',
 '{
   "type": "object",
   "properties": {
     "routingNumber": {"type": "string"},
     "accountNumber": {"type": "string"},
     "apiKey": {"type": "string"}
   },
   "required": ["routingNumber", "accountNumber", "apiKey"]
 }',
 '{
   "type": "object",
   "properties": {
     "transferType": {"type": "string"},
     "priority": {"type": "string"}
   },
   "required": ["transferType", "priority"]
 }',
 'ftp_payment_001', NOW(), NOW(), 'system', 'system');

-- ===========================================
-- 6. CREATE FLOW DEFINITIONS BASED ON FLOW TARGETS AND ACTIONS
-- ===========================================

INSERT INTO flow_definitions (id, flow_action_id, flow_target_id, description, code, brand_id, created_at, updated_at, created_by, updated_by) VALUES
('fld_deposit_001', 'fat_deposit_001', 'ftg_stripe_payment_001', 'Deposit flow for Stripe gateway', 'DEPOSIT_STRIPE', 'brn_sample_001', NOW(), NOW(), 'system', 'system'),
('fld_withdraw_001', 'fat_withdraw_001', 'ftg_stripe_payment_001', 'Withdraw flow for Stripe gateway', 'WITHDRAW_STRIPE', 'brn_sample_001', NOW(), NOW(), 'system', 'system'),
('fld_refund_001', 'fat_refund_001', 'ftg_stripe_payment_001', 'Refund flow for Stripe gateway', 'REFUND_STRIPE', 'brn_sample_001', NOW(), NOW(), 'system', 'system'),

('fld_deposit_002', 'fat_deposit_001', 'ftg_paypal_payment_001', 'Deposit flow for PayPal gateway', 'DEPOSIT_PAYPAL', 'brn_sample_001', NOW(), NOW(), 'system', 'system'),
('fld_withdraw_002', 'fat_withdraw_001', 'ftg_paypal_payment_001', 'Withdraw flow for PayPal gateway', 'WITHDRAW_PAYPAL', 'brn_sample_001', NOW(), NOW(), 'system', 'system'),
('fld_refund_002', 'fat_refund_001', 'ftg_paypal_payment_001', 'Refund flow for PayPal gateway', 'REFUND_PAYPAL', 'brn_sample_001', NOW(), NOW(), 'system', 'system'),

('fld_deposit_003', 'fat_deposit_001', 'ftg_bank_transfer_001', 'Deposit flow for bank transfer', 'DEPOSIT_BANK', 'brn_sample_001', NOW(), NOW(), 'system', 'system'),
('fld_withdraw_003', 'fat_withdraw_001', 'ftg_bank_transfer_001', 'Withdraw flow for bank transfer', 'WITHDRAW_BANK', 'brn_sample_001', NOW(), NOW(), 'system', 'system'),
('fld_refund_003', 'fat_refund_001', 'ftg_bank_transfer_001', 'Refund flow for bank transfer', 'REFUND_BANK', 'brn_sample_001', NOW(), NOW(), 'system', 'system');

-- ===========================================
-- SEED DATA SUMMARY
-- ===========================================
-- Created Records:
-- 1. Brands: 1 (Sample Financial Services)
-- 2. Environments: 3 (Dev, Staging, Production for the brand)
-- 3. Flow Types: 1 (Payment Processing)
-- 4. Flow Actions: 3 (Deposit, Withdraw, Refund)
-- 5. Flow Targets: 3 (Stripe, PayPal, Bank Transfer)
-- 6. Flow Definitions: 9 (All combinations of actions and targets)
--
-- Total Records: 17 sample records
-- All IDs follow the standard prefix format
-- All relationships properly established
-- ===========================================
