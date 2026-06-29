import os
import time
from datetime import datetime
from typing import List, Optional
from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from jose import JWTError, jwt
from passlib.context import CryptContext
from pydantic import BaseModel, EmailStr
import httpx

# FastAPI App Configuration
app = FastAPI(
    title="LeadHunter AI API",
    description="Production-grade cold outreach automation & business discovery backend",
    version="1.0.0"
)

# Security config
SECRET_KEY = os.getenv("JWT_SECRET", "super_secret_signing_key_for_production_leadhunter_ai")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

# --- Schemas ---
class Token(BaseModel):
    access_token: str
    token_type: str

class UserCreate(BaseModel):
    name: str
    email: EmailStr
    password: str

class UserResponse(BaseModel):
    id: int
    name: str
    email: EmailStr
    created_at: datetime

class LeadCreate(BaseModel):
    company_name: str
    website: str
    public_contact_email: EmailStr
    phone: str
    industry: str
    city: str
    country: str
    company_description: str
    lead_source: str

class LeadResponse(BaseModel):
    lead_id: int
    company_name: str
    website: str
    public_contact_email: str
    phone: str
    industry: str
    city: str
    country: str
    company_description: str
    lead_source: str
    status: str
    score: int
    created_at: datetime

class AnalysisResponse(BaseModel):
    id: int
    lead_id: int
    summary: str
    score: int
    recommendations: str
    website_quality_score: int
    seo_score: int
    mobile_friendliness: str
    missing_contact_form: bool
    missing_cta: bool
    speed_issues: str
    content_issues: str

class CampaignCreate(BaseModel):
    campaign_name: str
    lead_ids: List[int]

class CampaignResponse(BaseModel):
    campaign_id: int
    campaign_name: str
    status: str
    leads_count: int
    created_at: datetime

class EmailLogResponse(BaseModel):
    id: int
    lead_id: int
    campaign_id: Optional[int]
    subject: str
    status: str
    sent_at: Optional[datetime]

# --- In-memory Database Mock for Sandbox execution ---
# (In production, SqlAlchemy + PostgreSQL is used as defined in PostgreSQL schema)
USERS_DB = {}
LEADS_DB = {}
ANALYSES_DB = {}
CAMPAIGNS_DB = {}
EMAILS_DB = []

# Helpers
def hash_password(password: str) -> str:
    return pwd_context.hash(password)

def verify_password(plain_password: str, hashed_password: str) -> bool:
    return pwd_context.verify(plain_password, hashed_password)

def create_access_token(data: dict) -> str:
    to_encode = data.copy()
    expire = time.time() + (ACCESS_TOKEN_EXPIRE_MINUTES * 60)
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

async def get_current_user(token: str = Depends(oauth2_scheme)):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        email: str = payload.get("sub")
        if email is None:
            raise credentials_exception
    except JWTError:
        raise credentials_exception
    user = USERS_DB.get(email)
    if user is None:
        raise credentials_exception
    return user

# --- AUTHENTICATION ROUTES ---
@app.post("/register", response_model=UserResponse, status_code=status.HTTP_201_CREATED)
def register(user: UserCreate):
    if user.email in USERS_DB:
        raise HTTPException(status_code=400, detail="Email already registered")
    
    hashed = hash_password(user.password)
    new_user = {
        "id": len(USERS_DB) + 1,
        "name": user.name,
        "email": user.email,
        "password_hash": hashed,
        "created_at": datetime.now()
    }
    USERS_DB[user.email] = new_user
    return new_user

@app.post("/token", response_model=Token)
def login(form_data: OAuth2PasswordRequestForm = Depends()):
    user = USERS_DB.get(form_data.username)
    if not user or not verify_password(form_data.password, user["password_hash"]):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token = create_access_token(data={"sub": user["email"]})
    return {"access_token": access_token, "token_type": "bearer"}

# --- LEAD DISCOVERY ROUTES ---
@app.post("/leads", response_model=LeadResponse)
def create_lead(lead: LeadCreate, current_user: dict = Depends(get_current_user)):
    lead_id = len(LEADS_DB) + 1
    new_lead = {
        "lead_id": lead_id,
        "company_name": lead.company_name,
        "website": lead.website,
        "public_contact_email": lead.public_contact_email,
        "phone": lead.phone,
        "industry": lead.industry,
        "city": lead.city,
        "country": lead.country,
        "company_description": lead.company_description,
        "lead_source": lead.lead_source,
        "status": "New",
        "score": 75,
        "created_at": datetime.now()
    }
    LEADS_DB[lead_id] = new_lead
    return new_lead

@app.get("/leads", response_model=List[LeadResponse])
def get_leads(current_user: dict = Depends(get_current_user)):
    return list(LEADS_DB.values())

# --- WEBSITE ANALYSIS ---
@app.post("/analysis/{lead_id}", response_model=AnalysisResponse)
async def analyze_website(lead_id: int, current_user: dict = Depends(get_current_user)):
    if lead_id not in LEADS_DB:
        raise HTTPException(status_code=404, detail="Lead not found")
    
    # Live analyzer simulation
    analysis = {
        "id": len(ANALYSES_DB) + 1,
        "lead_id": lead_id,
        "summary": "Website lacks responsive design on headers and misses primary contact forms.",
        "score": 68,
        "recommendations": "Upgrade contact flow\nOptimize images\nFix viewport settings",
        "website_quality_score": 65,
        "seo_score": 70,
        "mobile_friendliness": "Average",
        "missing_contact_form": True,
        "missing_cta": True,
        "speed_issues": "3.8s load delay",
        "content_issues": "Unstructured typography"
    }
    ANALYSES_DB[lead_id] = analysis
    LEADS_DB[lead_id]["status"] = "Analyzed"
    return analysis

# --- CAMPAIGN MANAGER ---
@app.post("/campaigns", response_model=CampaignResponse)
def create_campaign(campaign: CampaignCreate, current_user: dict = Depends(get_current_user)):
    camp_id = len(CAMPAIGNS_DB) + 1
    new_camp = {
        "campaign_id": camp_id,
        "campaign_name": campaign.campaign_name,
        "status": "Draft",
        "leads_count": len(campaign.lead_ids),
        "created_at": datetime.now()
    }
    CAMPAIGNS_DB[camp_id] = new_camp
    return new_camp

# --- OUTREACH GENERATION ---
@app.post("/generate-email/{lead_id}")
async def generate_outreach(lead_id: int, current_user: dict = Depends(get_current_user)):
    if lead_id not in LEADS_DB:
        raise HTTPException(status_code=404, detail="Lead not found")
    
    # Simulation outreach payload
    return {
        "subject": "Quick web feedback for Acme",
        "body": "Hi team, I noticed some loading lags on your landing page. Let's optimize it!",
        "follow_up": "Hi team, following up on our conversion upgrades discussion.",
        "linkedin": "Loved checking out your agency. Let's connect!"
    }
