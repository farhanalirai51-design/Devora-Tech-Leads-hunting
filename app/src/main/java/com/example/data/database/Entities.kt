package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true) val leadId: Long = 0,
    val companyName: String,
    val website: String,
    val publicContactEmail: String,
    val phone: String,
    val industry: String,
    val city: String,
    val country: String,
    val companyDescription: String,
    val leadSource: String,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "New", // New, Analyzed, Email Prepared, Email Sent, Replied, Converted, Archived
    val tags: String = "", // Comma-separated
    val notes: String = "",
    val score: Int = 50
)

@Entity(tableName = "analyses")
data class AnalysisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val leadId: Long,
    val summary: String,
    val score: Int, // Overall website quality score
    val recommendations: String, // Comma-separated or newline-separated
    val websiteQualityScore: Int,
    val seoScore: Int,
    val mobileFriendliness: String,
    val missingContactForm: Boolean,
    val missingCta: Boolean,
    val speedIssues: String,
    val contentIssues: String
)

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true) val campaignId: Long = 0,
    val campaignName: String,
    val status: String = "Draft", // Draft, Running, Paused, Completed
    val leadsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "campaign_leads")
data class CampaignLeadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val campaignId: Long,
    val leadId: Long
)

@Entity(tableName = "emails")
data class EmailEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val leadId: Long,
    val campaignId: Long?,
    val subject: String,
    val body: String,
    val status: String = "Pending", // Pending, Sent, Failed, Replied
    val sentAt: Long? = null,
    val errorMessage: String? = null
)

@Entity(tableName = "replies")
data class ReplyEntity(
    @PrimaryKey(autoGenerate = true) val replyId: Long = 0,
    val leadId: Long,
    val emailContent: String,
    val sentiment: String = "Neutral", // Positive, Neutral, Negative
    val receivedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // New Lead, Campaign Completed, New Reply, Failed Email, Follow-Up Triggered
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Long = 1, // Only 1 settings row
    val geminiApiKey: String = "",
    val emailProvider: String = "Gmail SMTP", // Resend, Gmail SMTP
    val emailDelay: Int = 5, // Delay in seconds between emails
    val notificationNewLead: Boolean = true,
    val notificationCampaignCompleted: Boolean = true,
    val notificationNewReply: Boolean = true,
    val notificationFailedEmail: Boolean = true,
    val notificationFollowUpTriggered: Boolean = true,
    val darkTheme: Boolean = true,
    val resendApiKey: String = "",
    val userOffering: String = "At Devora Tech, we recently redesigned our B2B business website into a high-performance web experience. Now, we are helping other companies scale by offering complete custom website redesigns, extreme speed and SEO optimization, and intelligent client acquisition workflows.",
    val smtpHost: String = "smtp.gmail.com",
    val smtpPort: Int = 587,
    val gmailAddress: String = "",
    val gmailAppPassword: String = "",
    val googlePlacesApiKey: String = "",
    val customNiche: String = "Web Development"
)
