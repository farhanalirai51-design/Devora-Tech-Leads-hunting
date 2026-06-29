-- PostgreSQL Database Schema for LeadHunter AI

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Leads Table
CREATE TABLE IF NOT EXISTS leads (
    lead_id SERIAL PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    website VARCHAR(255) NOT NULL,
    public_contact_email VARCHAR(255),
    phone VARCHAR(50),
    industry VARCHAR(100),
    city VARCHAR(100),
    country VARCHAR(100),
    company_description TEXT,
    lead_source VARCHAR(100),
    status VARCHAR(50) DEFAULT 'New', -- New, Analyzed, Email Prepared, Email Sent, Replied, Converted, Archived
    tags TEXT, -- Comma-separated list
    notes TEXT,
    score INT DEFAULT 50,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Website Analyses Table
CREATE TABLE IF NOT EXISTS analyses (
    id SERIAL PRIMARY KEY,
    lead_id INT UNIQUE REFERENCES leads(lead_id) ON DELETE CASCADE,
    summary TEXT,
    score INT NOT NULL,
    recommendations TEXT,
    website_quality_score INT NOT NULL,
    seo_score INT NOT NULL,
    mobile_friendliness VARCHAR(50),
    missing_contact_form BOOLEAN DEFAULT FALSE,
    missing_cta BOOLEAN DEFAULT FALSE,
    speed_issues TEXT,
    content_issues TEXT,
    analyzed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Campaigns Table
CREATE TABLE IF NOT EXISTS campaigns (
    campaign_id SERIAL PRIMARY KEY,
    campaign_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT 'Draft', -- Draft, Running, Paused, Completed
    leads_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Campaign Leads Junction Table
CREATE TABLE IF NOT EXISTS campaign_leads (
    id SERIAL PRIMARY KEY,
    campaign_id INT REFERENCES campaigns(campaign_id) ON DELETE CASCADE,
    lead_id INT REFERENCES leads(lead_id) ON DELETE CASCADE,
    UNIQUE(campaign_id, lead_id)
);

-- Email Logs Table
CREATE TABLE IF NOT EXISTS emails (
    id SERIAL PRIMARY KEY,
    lead_id INT REFERENCES leads(lead_id) ON DELETE CASCADE,
    campaign_id INT REFERENCES campaigns(campaign_id) ON DELETE SET NULL,
    subject VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'Pending', -- Pending, Sent, Failed, Replied
    sent_at TIMESTAMP WITH TIME ZONE,
    error_message TEXT
);

-- Replies Table
CREATE TABLE IF NOT EXISTS replies (
    reply_id SERIAL PRIMARY KEY,
    lead_id INT REFERENCES leads(lead_id) ON DELETE CASCADE,
    email_content TEXT NOT NULL,
    sentiment VARCHAR(50) DEFAULT 'Neutral', -- Positive, Neutral, Negative
    received_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(100) NOT NULL, -- New Lead, Campaign Completed, New Reply, Failed Email, Follow-Up Triggered
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE
);

-- Create indexes for hyper-performance
CREATE INDEX IF NOT EXISTS idx_leads_status ON leads(status);
CREATE INDEX IF NOT EXISTS idx_leads_industry ON leads(industry);
CREATE INDEX IF NOT EXISTS idx_emails_status ON emails(status);
CREATE INDEX IF NOT EXISTS idx_replies_sentiment ON replies(sentiment);
