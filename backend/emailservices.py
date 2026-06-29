import os
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from typing import Dict, Any, Optional

# SMTP configuration constants for Gmail
SMTP_SERVER = "smtp.gmail.com"
SMTP_PORT = 587
DEFAULT_SENDER = "farhanalirai51@gmail.com"

# Responsive HTML Email template for businesses without a website
NO_WEBSITE_TEMPLATE = """<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Launch Your Business Online</title>
    <style>
        body {{
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            background-color: #07040E;
            color: #E2E8F0;
            margin: 0;
            padding: 0;
            -webkit-font-smoothing: antialiased;
        }}
        .container {{
            max-width: 600px;
            margin: 40px auto;
            background-color: #0D091A;
            border: 1px solid #2A243E;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
        }}
        .header {{
            background: linear-gradient(135deg, #1B0F3A, #0D091A);
            padding: 32px 24px;
            text-align: center;
            border-bottom: 1px solid #2A243E;
        }}
        .logo {{
            color: #BB86FC;
            font-size: 11px;
            font-weight: bold;
            letter-spacing: 1.5px;
            text-transform: uppercase;
            margin-bottom: 8px;
        }}
        .title {{
            color: #FFFFFF;
            font-size: 24px;
            font-weight: 800;
            margin: 0;
        }}
        .content {{
            padding: 32px 24px;
            line-height: 1.6;
        }}
        .greeting {{
            font-size: 18px;
            font-weight: bold;
            color: #FFFFFF;
            margin-bottom: 16px;
        }}
        .lead-text {{
            color: #94A3B8;
            font-size: 15px;
            margin-bottom: 24px;
        }}
        .bento-card {{
            background-color: #130E24;
            border: 1px solid #EF4444;
            border-radius: 12px;
            padding: 18px;
            margin-bottom: 24px;
        }}
        .bento-title {{
            color: #EF4444;
            font-size: 12px;
            font-weight: 800;
            letter-spacing: 0.5px;
            text-transform: uppercase;
            margin: 0 0 8px 0;
        }}
        .bento-body {{
            color: #E2E8F0;
            font-size: 14px;
            margin: 0;
        }}
        .feature-list {{
            margin: 24px 0;
            padding: 0;
            list-style: none;
        }}
        .feature-item {{
            margin-bottom: 12px;
            display: flex;
            align-items: flex-start;
            color: #E2E8F0;
            font-size: 14px;
        }}
        .feature-icon {{
            color: #BB86FC;
            margin-right: 12px;
            font-weight: bold;
        }}
        .cta-container {{
            text-align: center;
            margin: 32px 0 16px 0;
        }}
        .cta-btn {{
            background-color: #BB86FC;
            color: #07040E;
            text-decoration: none;
            padding: 12px 32px;
            font-size: 15px;
            font-weight: bold;
            border-radius: 8px;
            display: inline-block;
            transition: opacity 0.2s ease;
        }}
        .footer {{
            background-color: #07040E;
            padding: 24px;
            text-align: center;
            border-top: 1px solid #2A243E;
            font-size: 12px;
            color: #64748B;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="logo">Devora Tech • Web Discovery</div>
            <h1 class="title">New Client Growth Opportunity</h1>
        </div>
        <div class="content">
            <div class="greeting">Hi Team at {company_name},</div>
            <p class="lead-text">
                I was checking local directories in {city} and noticed your Google Business Profile is highly active with local customers. However, your business currently misses an independent digital website address.
            </p>
            
            <div class="bento-card">
                <h4 class="bento-title">Critical Gap Identified: No Website</h4>
                <p class="bento-body">
                    Relying purely on directory profiles prevents you from establishing direct SEO visibility, running high-performance B2B web conversions, and maintaining custom professional email credentials.
                </p>
            </div>

            <p class="lead-text">
                We design and build bespoke, ultra-fast, mobile-responsive custom websites specifically tailored to secure direct client bookings and automated lead inquiries. Here's what we include:
            </p>

            <ul class="feature-list">
                <li class="feature-item">
                    <span class="feature-icon">✓</span>
                    <span><strong>100/100 Google Lighthouse Speed</strong> to minimize mobile bounce rates completely.</span>
                </li>
                <li class="feature-item">
                    <span class="feature-icon">✓</span>
                    <span><strong>Custom SEO Strategy</strong> to pull organic target search queries directly onto your domain.</span>
                </li>
                <li class="feature-item">
                    <span class="feature-icon">✓</span>
                    <span><strong>Lead Conversion Funnels</strong> with interactive forms and secure booking workflows.</span>
                </li>
            </ul>

            <div class="cta-container">
                <a href="mailto:{sender_email}?subject=Re: Website Redesign Proposal for {company_name}" class="cta-btn">Get Free Web Wireframe mock</a>
            </div>
        </div>
        <div class="footer">
            Sent securely via Devora Tech • {sender_email}<br>
            If you wish to opt-out, reply with "STOP"
        </div>
    </div>
</body>
</html>
"""

# Responsive HTML Email template for businesses whose website needs improvement
WEBSITE_NEEDS_IMPROVEMENT_TEMPLATE = """<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Website Optimization & Redesign Report</title>
    <style>
        body {{
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            background-color: #07040E;
            color: #E2E8F0;
            margin: 0;
            padding: 0;
            -webkit-font-smoothing: antialiased;
        }}
        .container {{
            max-width: 600px;
            margin: 40px auto;
            background-color: #0D091A;
            border: 1px solid #2A243E;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
        }}
        .header {{
            background: linear-gradient(135deg, #1B0F3A, #0D091A);
            padding: 32px 24px;
            text-align: center;
            border-bottom: 1px solid #2A243E;
        }}
        .logo {{
            color: #BB86FC;
            font-size: 11px;
            font-weight: bold;
            letter-spacing: 1.5px;
            text-transform: uppercase;
            margin-bottom: 8px;
        }}
        .title {{
            color: #FFFFFF;
            font-size: 24px;
            font-weight: 800;
            margin: 0;
        }}
        .content {{
            padding: 32px 24px;
            line-height: 1.6;
        }}
        .greeting {{
            font-size: 18px;
            font-weight: bold;
            color: #FFFFFF;
            margin-bottom: 16px;
        }}
        .lead-text {{
            color: #94A3B8;
            font-size: 15px;
            margin-bottom: 24px;
        }}
        .bento-card {{
            background-color: #130E24;
            border: 1px solid #F59E0B;
            border-radius: 12px;
            padding: 18px;
            margin-bottom: 24px;
        }}
        .bento-title {{
            color: #F59E0B;
            font-size: 12px;
            font-weight: 800;
            letter-spacing: 0.5px;
            text-transform: uppercase;
            margin: 0 0 8px 0;
        }}
        .bento-body {{
            color: #E2E8F0;
            font-size: 14px;
            margin: 0;
        }}
        .metric-row {{
            display: flex;
            justify-content: space-between;
            margin: 20px 0;
            padding: 12px;
            background-color: #07040E;
            border-radius: 8px;
            border: 1px solid #2A243E;
        }}
        .metric-col {{
            text-align: center;
            width: 50%;
        }}
        .metric-label {{
            font-size: 11px;
            color: #64748B;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }}
        .metric-val {{
            font-size: 18px;
            font-weight: bold;
            color: #EF4444;
            margin-top: 4px;
        }}
        .cta-container {{
            text-align: center;
            margin: 32px 0 16px 0;
        }}
        .cta-btn {{
            background-color: #BB86FC;
            color: #07040E;
            text-decoration: none;
            padding: 12px 32px;
            font-size: 15px;
            font-weight: bold;
            border-radius: 8px;
            display: inline-block;
            transition: opacity 0.2s ease;
        }}
        .footer {{
            background-color: #07040E;
            padding: 24px;
            text-align: center;
            border-top: 1px solid #2A243E;
            font-size: 12px;
            color: #64748B;
        }}
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="logo">Devora Tech • Web Discovery</div>
            <h1 class="title">Website Performance & Security Alert</h1>
        </div>
        <div class="content">
            <div class="greeting">Hi Team at {company_name},</div>
            <p class="lead-text">
                I was conducting standard digital health checks on businesses in the {industry} industry in {city} and evaluated your official website at <a href="{website}" style="color: #BB86FC; text-decoration: underline;">{website}</a>. 
            </p>
            
            <div class="bento-card">
                <h4 class="bento-title">Identified Performance Gap: {status}</h4>
                <p class="bento-body">
                    <strong>Details:</strong> {reason}
                </p>
            </div>

            <div class="metric-row">
                <div class="metric-col" style="border-right: 1px solid #2A243E;">
                    <div class="metric-label">SEO optimization</div>
                    <div class="metric-val">{seo_score}/100</div>
                </div>
                <div class="metric-col">
                    <div class="metric-label">Mobile Experience</div>
                    <div class="metric-val" style="color: #F59E0B;">{mobile_friendliness}</div>
                </div>
            </div>

            <p class="lead-text">
                Modern browsers drop consumer trust and organic Google Search ranking heavily when websites suffer from speed lags or missing SSL setups. We redesign and speed-optimize B2B web experiences. We can patch all these gaps in under 72 hours.
            </p>

            <div class="cta-container">
                <a href="mailto:{sender_email}?subject=Re: Website Audit & Optimization for {company_name}" class="cta-btn">Book a free speed review</a>
            </div>
        </div>
        <div class="footer">
            Sent securely via Devora Tech • {sender_email}<br>
            If you wish to opt-out, reply with "STOP"
        </div>
    </div>
</body>
</html>
"""

def generate_email_content(lead: Dict[str, Any], sender_email: str = DEFAULT_SENDER) -> Dict[str, str]:
    """
    Generates highly custom email content (Subject + HTML Body) based on the lead's website condition.
    """
    company_name = lead.get("company_name", "your business")
    website = lead.get("website", "").strip()
    status = lead.get("status", "No Website")
    reason = lead.get("reason", "No official website detected on claimed profiles.")
    city = lead.get("city", "your area")
    industry = lead.get("industry", "local business services")
    
    # Extract numerical scores or use defaults
    seo_score = lead.get("seo_score", 45)
    mobile_friendliness = lead.get("mobile_friendliness", "Unresponsive")
    
    # Normalize if it's missing or marked empty
    has_no_website = (
        not website or 
        website.lower() == "none" or 
        status.lower() == "no website" or 
        "no website" in reason.lower()
    )

    if has_no_website:
        subject = f"Growth opportunity for {company_name} in {city} (No Website listed)"
        body = NO_WEBSITE_TEMPLATE.format(
            company_name=company_name,
            city=city,
            sender_email=sender_email
        )
    else:
        subject = f"Performance Alert for {company_name} website ({status})"
        body = WEBSITE_NEEDS_IMPROVEMENT_TEMPLATE.format(
            company_name=company_name,
            website=website,
            industry=industry,
            city=city,
            status=status,
            reason=reason,
            seo_score=seo_score,
            mobile_friendliness=mobile_friendliness,
            sender_email=sender_email
        )
        
    return {
        "subject": subject,
        "body": body
    }

def send_cold_email(
    to_email: str,
    subject: str,
    html_body: str,
    gmail_address: str,
    gmail_app_password: str,
    smtp_host: str = SMTP_SERVER,
    smtp_port: int = SMTP_PORT
) -> bool:
    """
    Connects to the SMTP server and securely sends the HTML email using TLS/SSL.
    """
    if not gmail_address or not gmail_app_password:
        print("Error: SMTP credentials (gmail_address, gmail_app_password) must not be empty.")
        return False
        
    msg = MIMEMultipart("alternative")
    msg["Subject"] = subject
    msg["From"] = gmail_address
    msg["To"] = to_email

    # Attach HTML payload
    msg.attach(MIMEText(html_body, "html"))

    try:
        print(f"Connecting to SMTP server {smtp_host}:{smtp_port}...")
        # Check port to decide security protocol
        if smtp_port == 465:
            server = smtplib.SMTP_SSL(smtp_host, smtp_port)
        else:
            server = smtplib.SMTP(smtp_host, smtp_port)
            server.ehlo()
            server.starttls() # Secure the connection with STARTTLS
            server.ehlo()
            
        print("Authenticating credentials...")
        server.login(gmail_address, gmail_app_password)
        
        print(f"Sending email payload to {to_email}...")
        server.sendmail(gmail_address, [to_email], msg.as_string())
        server.quit()
        print("Email sent successfully!")
        return True
    except Exception as e:
        print(f"Failed to deliver email through SMTP server: {str(e)}")
        return False

# --- Quick Command Line Interface to run or test the service ---
if __name__ == "__main__":
    print("--------------------------------------------------")
    print("LeadHunter AI: Dedicated Outreach Email Service")
    print("--------------------------------------------------")
    
    # 1. Prepare sample leads representing different website conditions
    sample_leads = [
        {
            "company_name": "Sunset Harbor Bakery",
            "website": "",
            "status": "No Website",
            "reason": "Google Profile is claimed and active, but lacks a linked website. Misses premium local search optimization.",
            "city": "Miami, FL",
            "industry": "Bakery & Cafe",
            "seo_score": 0,
            "mobile_friendliness": "Non-existent"
        },
        {
            "company_name": "Lakeside Medical Clinic",
            "website": "http://lakesidemedclinic.com",
            "status": "Unsecured HTTP",
            "reason": "Website lacks SSL configuration. Browsers display 'Not Secure' warning banners, destroying customer trust.",
            "city": "Orlando, FL",
            "industry": "Healthcare Clinic",
            "seo_score": 38,
            "mobile_friendliness": "Responsive but insecure"
        }
    ]

    # Generate custom email content
    print("\n[Preview 1] Business with No Website:")
    out1 = generate_email_content(sample_leads[0])
    print(f"Subject: {out1['subject']}")
    print(f"Body length: {len(out1['body'])} chars (HTML template ready)")

    print("\n[Preview 2] Business with Outdated/Unsecured Website:")
    out2 = generate_email_content(sample_leads[1])
    print(f"Subject: {out2['subject']}")
    print(f"Body length: {len(out2['body'])} chars (HTML template ready)")
    
    print("\nTo send active emails, import this script and run:")
    print("from emailservices import send_cold_email, generate_email_content")
    print("email_payload = generate_email_content(lead, 'farhanalirai51@gmail.com')")
    print("send_cold_email(lead['public_contact_email'], email_payload['subject'], email_payload['body'], 'farhanalirai51@gmail.com', 'your_gmail_app_password')")
    print("--------------------------------------------------")
