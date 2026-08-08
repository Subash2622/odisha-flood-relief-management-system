-- Odisha Flood Relief & NGO Management System
-- Sample Database Schema (reference - JPA auto-creates with ddl-auto=update)

CREATE DATABASE IF NOT EXISTS flood_relief_ngo;
USE flood_relief_ngo;

-- Default CEO credentials (created by DataInitializer on first run):
-- Username: ceo
-- Password: ceo123

-- Roles: CEO, ADMIN, VOLUNTEER, MEMBER, USER

-- Core tables (auto-managed by Hibernate):
-- users, roles, user_roles
-- members, volunteers, campaigns, donations
-- payments, payment_transactions, membership_cards
-- flood_reports, relief_distribution, inventory
-- notifications, audit_logs, announcements
-- districts, villages, organization_details

-- Sample district data is loaded by DataInitializer (30 Odisha districts)
