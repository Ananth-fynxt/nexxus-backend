-- Migration: V012__Create_auth_tables.sql
-- Description: Create authentication/user tables for Auth0 user linkage and sessions

-- Users table linked to Auth0 subject (sub)
CREATE TABLE users (
    id TEXT PRIMARY KEY NOT NULL,
    auth0_sub TEXT NOT NULL UNIQUE,
    email TEXT NOT NULL,
    name TEXT,
    picture TEXT,
    status user_status NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- User roles (simple role storage; roles typically encoded in JWT but persisted for app logic)
CREATE TABLE user_roles (
    id TEXT PRIMARY KEY NOT NULL,
    user_id TEXT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE UNIQUE INDEX uq_user_role ON user_roles(user_id, role);

