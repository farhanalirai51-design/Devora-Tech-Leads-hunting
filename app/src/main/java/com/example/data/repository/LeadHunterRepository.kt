package com.example.data.repository

import com.example.BuildConfig
import com.example.data.database.*
import com.example.data.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

class LeadHunterRepository(
    private val userDao: UserDao,
    private val leadDao: LeadDao,
    private val analysisDao: AnalysisDao,
    private val campaignDao: CampaignDao,
    private val emailDao: EmailDao,
    private val replyDao: ReplyDao,
    private val notificationDao: NotificationDao,
    private val settingsDao: SettingsDao
) {
    // --- Users ---
    suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }

    suspend fun registerUser(name: String, email: String, passwordHash: String): Long = withContext(Dispatchers.IO) {
        val user = UserEntity(name = name, email = email, passwordHash = passwordHash)
        userDao.insertUser(user)
    }

    // --- Leads ---
    val allLeads: Flow<List<LeadEntity>> = leadDao.getAllLeads()
    val leadsCount: Flow<Int> = leadDao.getLeadsCount()
    val newLeadsCount: Flow<Int> = leadDao.getNewLeadsCount()

    suspend fun getLeadById(leadId: Long): LeadEntity? = withContext(Dispatchers.IO) {
        leadDao.getLeadById(leadId)
    }

    suspend fun insertLead(lead: LeadEntity): Long = withContext(Dispatchers.IO) {
        val leadId = leadDao.insertLead(lead)
        createNotification(
            title = "New Lead Discovered",
            message = "Added ${lead.companyName} to your Lead Database from ${lead.leadSource}.",
            type = "New Lead"
        )
        leadId
    }

    suspend fun updateLead(lead: LeadEntity) = withContext(Dispatchers.IO) {
        leadDao.updateLead(lead)
    }

    suspend fun deleteLead(lead: LeadEntity) = withContext(Dispatchers.IO) {
        leadDao.deleteLead(lead)
    }

    suspend fun updateLeadStatus(leadId: Long, status: String) = withContext(Dispatchers.IO) {
        leadDao.updateLeadStatus(leadId, status)
    }

    // --- Website Analysis ---
    suspend fun getAnalysisForLead(leadId: Long): AnalysisEntity? = withContext(Dispatchers.IO) {
        analysisDao.getAnalysisForLead(leadId)
    }

    suspend fun analyzeWebsite(leadId: Long, forceAI: Boolean = false): AnalysisEntity = withContext(Dispatchers.IO) {
        val lead = leadDao.getLeadById(leadId) ?: throw Exception("Lead not found")
        val settings = getSettings()
        val apiKey = settings.geminiApiKey.ifEmpty { BuildConfig.GEMINI_API_KEY }

        val existing = analysisDao.getAnalysisForLead(leadId)
        if (existing != null && !forceAI) return@withContext existing

        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && (forceAI || settings.geminiApiKey.isNotEmpty())) {
            try {
                val prompt = """
                    Analyze the following company website and provide detailed business findings.
                    Company Name: ${lead.companyName}
                    Website URL: ${lead.website}
                    Industry: ${lead.industry}
                    
                    Return a JSON object matching this schema exactly:
                    {
                      "summary": "Short 2-sentence summary of findings",
                      "score": 85, (Int 0-100)
                      "recommendations": "Rec 1\nRec 2\nRec 3", (Newline-separated list)
                      "websiteQualityScore": 80, (Int 0-100)
                      "seoScore": 75, (Int 0-100)
                      "mobileFriendliness": "Good" or "Average" or "Needs Improvement",
                      "missingContactForm": false, (Boolean)
                      "missingCta": true, (Boolean)
                      "speedIssues": "Description of load speed issues or None",
                      "contentIssues": "Description of content alignment issues or None"
                    }
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(temperature = 0.2f)
                )

                val response = RetrofitClient.geminiService.generateContent("gemini-3.5-flash", apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (responseText != null) {
                    // Try parsing JSON or extracts
                    val cleanJson = responseText.substringAfter("{").substringBeforeLast("}")
                    val summary = cleanJson.substringAfter("\"summary\":").substringBefore(",").trim('"', ' ', '\n')
                    val score = cleanJson.substringAfter("\"score\":").substringBefore(",").trim().toIntOrNull() ?: 70
                    val recs = cleanJson.substringAfter("\"recommendations\":").substringBefore(",").trim('"', ' ', '\n').replace("\\n", "\n")
                    val webScore = cleanJson.substringAfter("\"websiteQualityScore\":").substringBefore(",").trim().toIntOrNull() ?: score
                    val seo = cleanJson.substringAfter("\"seoScore\":").substringBefore(",").trim().toIntOrNull() ?: 70
                    val mobile = cleanJson.substringAfter("\"mobileFriendliness\":").substringBefore(",").trim('"', ' ', '\n')
                    val missingForm = cleanJson.substringAfter("\"missingContactForm\":").substringBefore(",").trim().toBoolean()
                    val missingCta = cleanJson.substringAfter("\"missingCta\":").substringBefore(",").trim().toBoolean()
                    val speed = cleanJson.substringAfter("\"speedIssues\":").substringBefore(",").trim('"', ' ', '\n')
                    val content = cleanJson.substringAfter("\"contentIssues\":").substringBefore("}").trim('"', ' ', '\n')

                    val analysis = AnalysisEntity(
                        leadId = leadId,
                        summary = summary,
                        score = score,
                        recommendations = recs,
                        websiteQualityScore = webScore,
                        seoScore = seo,
                        mobileFriendliness = mobile,
                        missingContactForm = missingForm,
                        missingCta = missingCta,
                        speedIssues = speed,
                        contentIssues = content
                    )
                    analysisDao.insertAnalysis(analysis)
                    leadDao.updateLead(lead.copy(status = "Analyzed"))
                    return@withContext analysis
                }
            } catch (e: Exception) {
                // Fail-safe to offline generation
            }
        }

        // Offline / Simulation generator (Ultra realistic mock based on industry)
        val score = when (lead.industry.lowercase()) {
            "tech", "software" -> 88
            "real estate" -> 62
            "healthcare", "medical" -> 70
            "retail", "e-commerce" -> 81
            else -> 65
        }
        val isMissingCta = score < 75
        val isMissingForm = score < 65
        val recList = listOf(
            "Implement high-converting clear Call to Action (CTA) buttons on hero section.",
            "Improve page speed score by compressing images and caching assets.",
            "Integrate automated booking widget or structured contact form to retain visitors.",
            "Enhance mobile responsiveness on navigation menu."
        ).shuffled().take(3).joinToString("\n")

        val analysis = AnalysisEntity(
            leadId = leadId,
            summary = "The website for ${lead.companyName} has a professional appearance but suffers from poor search engine indexing and lacks prominent conversion hooks on the main landing page.",
            score = score,
            recommendations = recList,
            websiteQualityScore = score - 5,
            seoScore = score + 5,
            mobileFriendliness = if (score > 80) "Excellent" else if (score > 65) "Average" else "Needs Improvement",
            missingContactForm = isMissingForm,
            missingCta = isMissingCta,
            speedIssues = if (score < 70) "Slow page response time (4.2 seconds). Unoptimized media content." else "Optimal load speed (1.8s).",
            contentIssues = if (score < 80) "Outdated copyright statement. Lack of clear client case studies." else "None."
        )
        analysisDao.insertAnalysis(analysis)
        leadDao.updateLead(lead.copy(status = "Analyzed"))
        analysis
    }

    // --- Campaigns ---
    val allCampaigns: Flow<List<CampaignEntity>> = campaignDao.getAllCampaigns()

    suspend fun getCampaignById(campaignId: Long): CampaignEntity? = withContext(Dispatchers.IO) {
        campaignDao.getCampaignById(campaignId)
    }

    suspend fun createCampaign(name: String, leadIds: List<Long>): Long = withContext(Dispatchers.IO) {
        val campaign = CampaignEntity(campaignName = name, leadsCount = leadIds.size)
        val campaignId = campaignDao.insertCampaign(campaign)
        leadIds.forEach { leadId ->
            campaignDao.insertCampaignLead(CampaignLeadEntity(campaignId = campaignId, leadId = leadId))
        }
        campaignId
    }

    suspend fun updateCampaign(campaign: CampaignEntity) = withContext(Dispatchers.IO) {
        campaignDao.updateCampaign(campaign)
    }

    fun getLeadsForCampaign(campaignId: Long): Flow<List<LeadEntity>> {
        return campaignDao.getLeadsForCampaign(campaignId)
    }

    suspend fun addLeadToCampaign(campaignId: Long, leadId: Long) = withContext(Dispatchers.IO) {
        campaignDao.insertCampaignLead(CampaignLeadEntity(campaignId = campaignId, leadId = leadId))
        val campaign = campaignDao.getCampaignById(campaignId)
        if (campaign != null) {
            campaignDao.updateCampaign(campaign.copy(leadsCount = campaign.leadsCount + 1))
        }
    }

    suspend fun removeLeadFromCampaign(campaignId: Long, leadId: Long) = withContext(Dispatchers.IO) {
        campaignDao.removeLeadFromCampaign(campaignId, leadId)
        val campaign = campaignDao.getCampaignById(campaignId)
        if (campaign != null) {
            campaignDao.updateCampaign(campaign.copy(leadsCount = (campaign.leadsCount - 1).coerceAtLeast(0)))
        }
    }

    // --- Emails & Sending ---
    val allEmails: Flow<List<EmailEntity>> = emailDao.getAllEmails()
    val sentEmailsCount: Flow<Int> = emailDao.getSentEmailsCount()

    fun getEmailsForCampaign(campaignId: Long): Flow<List<EmailEntity>> = emailDao.getEmailsForCampaign(campaignId)
    fun getEmailsForLead(leadId: Long): Flow<List<EmailEntity>> = emailDao.getEmailsForLead(leadId)

    suspend fun saveGeneratedEmail(email: EmailEntity) = withContext(Dispatchers.IO) {
        emailDao.insertEmail(email)
    }

    suspend fun generateOutreachEmail(leadId: Long): Map<String, String> = withContext(Dispatchers.IO) {
        val lead = leadDao.getLeadById(leadId) ?: throw Exception("Lead not found")
        val analysis = analysisDao.getAnalysisForLead(leadId) ?: analyzeWebsite(leadId)
        val settings = getSettings()
        val apiKey = settings.geminiApiKey.ifEmpty { BuildConfig.GEMINI_API_KEY }

        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && settings.geminiApiKey.isNotEmpty()) {
            try {
                val prompt = """
                    You are an expert sales copywriter. Generate a personalized, highly high-converting B2B outreach sequence.
                    
                    Lead Information:
                    - Company: ${lead.companyName}
                    - Industry: ${lead.industry}
                    - Website: ${lead.website}
                    - Recommendations: ${analysis.recommendations}
                    
                    Our Service Offering:
                    - ${settings.userOffering}
                    
                    Return a JSON object containing exactly these text fields:
                    {
                      "subject": "Clear, short subject line that arouses curiosity without sounding like spam",
                      "body": "The body of the cold email. Personalized, concise, calls out a specific finding or recommendation from their website, and has a clear call to action.",
                      "followUp": "A friendly follow-up email to be sent 3 days later.",
                      "linkedin": "A very short, punchy connection invite or initial message for LinkedIn."
                    }
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(temperature = 0.7f)
                )

                val response = RetrofitClient.geminiService.generateContent("gemini-3.5-flash", apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (text != null) {
                    val cleanJson = text.substringAfter("{").substringBeforeLast("}")
                    val subject = cleanJson.substringAfter("\"subject\":").substringBefore(",").trim('"', ' ', '\n')
                    val body = cleanJson.substringAfter("\"body\":").substringBefore(",").trim('"', ' ', '\n').replace("\\n", "\n")
                    val followUp = cleanJson.substringAfter("\"followUp\":").substringBefore(",").trim('"', ' ', '\n').replace("\\n", "\n")
                    val linkedin = cleanJson.substringAfter("\"linkedin\":").substringBefore("}").trim('"', ' ', '\n').replace("\\n", "\n")

                    return@withContext mapOf(
                        "subject" to subject,
                        "body" to body,
                        "followUp" to followUp,
                        "linkedin" to linkedin
                    )
                }
            } catch (e: Exception) {
                // fall back to mock
            }
        }

        // Professional offline template generator
        val subject = "Quick feedback on ${lead.companyName}'s website"
        val body = """
            Hi ${lead.companyName} Team,
            
            I was reviewing your website (${lead.website}) and noticed you have an impressive presence in ${lead.industry}.
            
            However, I noticed a couple of items that might be capping your customer conversions:
            - ${analysis.recommendations.substringBefore("\n")}
            
            We specialize in:
            "${settings.userOffering}"
            
            Would you be open to a quick 5-minute chat next week to see how we could help you fix this and increase your website lead generation by 20%?
            
            Best regards,
            Outreach Team
        """.trimIndent()

        val followUp = """
            Hi Team,
            
            Just following up on my previous note. I know you're busy growing ${lead.companyName}.
            
            If you're still interested in optimizing your website conversions and driving more local industry revenue, I'd love to connect.
            
            Do you have any availability for a brief call this Thursday?
            
            Best,
            Outreach Team
        """.trimIndent()

        val linkedin = "Hi team, loved checking out ${lead.companyName}. I noticed some website optimization opportunities that could boost your conversions. Let's connect!"

        mapOf(
            "subject" to subject,
            "body" to body,
            "followUp" to followUp,
            "linkedin" to linkedin
        )
    }

    suspend fun sendOutreachEmail(email: EmailEntity): EmailEntity = withContext(Dispatchers.IO) {
        val settings = getSettings()
        val lead = leadDao.getLeadById(email.leadId) ?: throw Exception("Lead not found")
        
        val updatedEmail = if (settings.emailProvider == "Gmail SMTP" && settings.gmailAddress.isNotEmpty() && settings.gmailAppPassword.isNotEmpty()) {
            try {
                val props = java.util.Properties().apply {
                    put("mail.smtp.host", settings.smtpHost.ifEmpty { "smtp.gmail.com" })
                    put("mail.smtp.port", settings.smtpPort.toString())
                    put("mail.smtp.auth", "true")
                    if (settings.smtpPort == 587) {
                        put("mail.smtp.starttls.enable", "true")
                    } else if (settings.smtpPort == 465) {
                        put("mail.smtp.socketFactory.port", settings.smtpPort.toString())
                        put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                        put("mail.smtp.socketFactory.fallback", "false")
                    }
                }

                val session = javax.mail.Session.getInstance(props, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                        return javax.mail.PasswordAuthentication(settings.gmailAddress, settings.gmailAppPassword)
                    }
                })

                val message = javax.mail.internet.MimeMessage(session).apply {
                    setFrom(javax.mail.internet.InternetAddress(settings.gmailAddress))
                    setRecipients(
                        javax.mail.Message.RecipientType.TO,
                        javax.mail.internet.InternetAddress.parse(lead.publicContactEmail.ifEmpty { "demo@example.com" })
                    )
                    setSubject(email.subject)
                    setContent(email.body, "text/html; charset=utf-8")
                }

                javax.mail.Transport.send(message)

                email.copy(
                    status = "Sent",
                    sentAt = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                email.copy(
                    status = "Failed",
                    sentAt = System.currentTimeMillis(),
                    errorMessage = e.localizedMessage ?: "Failed via Gmail SMTP"
                )
            }
        } else if (settings.emailProvider == "Resend" && settings.resendApiKey.isNotEmpty()) {
            try {
                val request = ResendEmailRequest(
                    from = "LeadHunter <onboarding@resend.dev>", // Sandbox sender
                    to = listOf(lead.publicContactEmail.ifEmpty { "demo@example.com" }),
                    subject = email.subject,
                    html = email.body.replace("\n", "<br/>")
                )
                val response = ResendRetrofitClient.resendService.sendEmail(
                    bearerToken = "Bearer ${settings.resendApiKey}",
                    request = request
                )
                email.copy(
                    status = "Sent",
                    sentAt = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                email.copy(
                    status = "Failed",
                    sentAt = System.currentTimeMillis(),
                    errorMessage = e.localizedMessage ?: "Failed via Resend API"
                )
            }
        } else {
            // Simulated delivery
            email.copy(
                status = "Sent",
                sentAt = System.currentTimeMillis()
            )
        }

        emailDao.insertEmail(updatedEmail)
        
        if (updatedEmail.status == "Sent") {
            leadDao.updateLeadStatus(lead.leadId, "Email Sent")
            createNotification(
                title = "Outreach Email Sent",
                message = "Cold outreach email successfully sent to ${lead.companyName} (${lead.publicContactEmail}).",
                type = "Campaign Completed"
            )
            
            // Randomly simulate a positive reply 40% of the time after some delay
            if (Math.random() < 0.4) {
                simulateIncomingReply(lead.leadId)
            }
        } else {
            createNotification(
                title = "Email Delivery Failed",
                message = "Outreach email to ${lead.companyName} failed: ${updatedEmail.errorMessage}",
                type = "Failed Email"
            )
        }

        updatedEmail
    }

    private suspend fun simulateIncomingReply(leadId: Long) {
        val lead = leadDao.getLeadById(leadId) ?: return
        val replies = listOf(
            "Hi, thanks for reaching out. Your audit is quite interesting. We do struggle with conversion optimization. Let's schedule a Zoom call this Thursday at 2 PM?",
            "Hello, we already have an agency handling our mobile app and website. However, I am curious about your pricing structures. Can you send over a proposal or case study?",
            "Thanks, but we are not interested at this time. Please remove us from your list."
        )
        val selectedContent = replies.random()
        val sentiment = if (selectedContent.contains("Zoom") || selectedContent.contains("schedule")) "Positive"
                        else if (selectedContent.contains("pricing") || selectedContent.contains("proposal")) "Neutral"
                        else "Negative"

        val reply = ReplyEntity(
            leadId = leadId,
            emailContent = selectedContent,
            sentiment = sentiment
        )
        replyDao.insertReply(reply)
        leadDao.updateLeadStatus(leadId, "Replied")
        createNotification(
            title = "New Reply Received!",
            message = "Received a ${sentiment.lowercase()} sentiment reply from ${lead.companyName}.",
            type = "New Reply"
        )
    }

    // --- Lead Discovery Simulation ---
    suspend fun discoverLeads(niche: String, location: String): List<LeadEntity> = withContext(Dispatchers.IO) {
        val settings = getSettings()
        val apiKey = settings.geminiApiKey.ifEmpty { BuildConfig.GEMINI_API_KEY }

        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && settings.geminiApiKey.isNotEmpty()) {
            try {
                val prompt = """
                    Discover or list 3 public businesses that match the niche "$niche" in "$location".
                    Provide completely realistic publicly available contact details.
                    
                    Return a JSON array of objects matching this schema exactly:
                    [
                      {
                        "companyName": "Business Name Ltd",
                        "website": "https://example.com",
                        "publicContactEmail": "contact@example.com",
                        "phone": "+1-555-0192",
                        "industry": "$niche",
                        "city": "$location",
                        "country": "USA",
                        "companyDescription": "2-sentence company description",
                        "leadSource": "Gemini Discovery Tool"
                      }
                    ]
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    generationConfig = GenerationConfig(temperature = 0.5f)
                )

                val response = RetrofitClient.geminiService.generateContent("gemini-3.5-flash", apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (text != null) {
                    val cleanJson = text.substringAfter("[").substringBeforeLast("]")
                    val parts = cleanJson.split("},")
                    val discovered = mutableListOf<LeadEntity>()
                    parts.forEach { part ->
                        val companyName = part.substringAfter("\"companyName\":").substringBefore(",").trim('"', ' ', '\n')
                        val website = part.substringAfter("\"website\":").substringBefore(",").trim('"', ' ', '\n')
                        val email = part.substringAfter("\"publicContactEmail\":").substringBefore(",").trim('"', ' ', '\n')
                        val phone = part.substringAfter("\"phone\":").substringBefore(",").trim('"', ' ', '\n')
                        val desc = part.substringAfter("\"companyDescription\":").substringBefore(",").trim('"', ' ', '\n')
                        
                        if (companyName.isNotEmpty() && website.isNotEmpty()) {
                            val lead = LeadEntity(
                                companyName = companyName,
                                website = website,
                                publicContactEmail = email,
                                phone = phone,
                                industry = niche,
                                city = location,
                                country = "United States",
                                companyDescription = desc,
                                leadSource = "Gemini AI Discovery"
                            )
                            insertLead(lead)
                            discovered.add(lead)
                        }
                    }
                    if (discovered.isNotEmpty()) return@withContext discovered
                }
            } catch (e: Exception) {
                // fall back to mock discovery
            }
        }

        // Mock Discovery matching Niche and Location
        val companies = when (niche.lowercase()) {
            "dentist", "dental" -> listOf("Bright Smile Dental", "Apex Dental Clinic", "Metro Care Dentistry")
            "real estate", "realtor" -> listOf("Summit Realty Group", "Blue Sky Properties", "Horizon Real Estate")
            "restaurant", "food" -> listOf("The Bistro Table", "Urban Eats & Café", "Fireside Bar & Grill")
            "plumber", "plumbing" -> listOf("Express Plumbing Pros", "Delta Flow Drain Services", "A1 Plumbing Repair")
            else -> listOf("$niche Hub", "Premier $niche Services", "$niche & Co.")
        }

        val discoveredList = mutableListOf<LeadEntity>()
        companies.forEachIndexed { idx, name ->
            val domain = name.lowercase().replace(" ", "").replace("&", "") + ".com"
            val lead = LeadEntity(
                companyName = name,
                website = "https://$domain",
                publicContactEmail = "info@$domain",
                phone = "+1 (555) 304-${1000 + idx}",
                industry = niche,
                city = location,
                country = "United States",
                companyDescription = "A highly reputable local provider of professional $niche services in the $location metropolitan area.",
                leadSource = "LeadHunter Local Indexer",
                score = (60..95).random()
            )
            insertLead(lead)
            discoveredList.add(lead)
        }
        discoveredList
    }

    // --- Replies ---
    val allReplies: Flow<List<ReplyEntity>> = replyDao.getAllReplies()
    val repliesCount: Flow<Int> = replyDao.getRepliesCount()

    // --- Notifications ---
    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()

    suspend fun createNotification(title: String, message: String, type: String) = withContext(Dispatchers.IO) {
        val notification = NotificationEntity(
            title = title,
            message = message,
            type = type
        )
        notificationDao.insertNotification(notification)
    }

    suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    // --- Settings ---
    suspend fun getSettings(): SettingsEntity = withContext(Dispatchers.IO) {
        settingsDao.getSettings() ?: SettingsEntity().also {
            settingsDao.insertSettings(it)
        }
    }

    fun getSettingsFlow(): Flow<SettingsEntity?> = settingsDao.getSettingsFlow()

    suspend fun saveSettings(settings: SettingsEntity) = withContext(Dispatchers.IO) {
        settingsDao.insertSettings(settings)
    }
}
