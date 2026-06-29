package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long
}

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY createdAt DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE leadId = :leadId LIMIT 1")
    suspend fun getLeadById(leadId: Long): LeadEntity?

    @Query("SELECT * FROM leads WHERE website = :website LIMIT 1")
    suspend fun getLeadByWebsite(website: String): LeadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity): Long

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Delete
    suspend fun deleteLead(lead: LeadEntity)

    @Query("UPDATE leads SET status = :status WHERE leadId = :leadId")
    suspend fun updateLeadStatus(leadId: Long, status: String)

    @Query("SELECT COUNT(*) FROM leads")
    fun getLeadsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM leads WHERE status = 'New'")
    fun getNewLeadsCount(): Flow<Int>
}

@Dao
interface AnalysisDao {
    @Query("SELECT * FROM analyses WHERE leadId = :leadId LIMIT 1")
    suspend fun getAnalysisForLead(leadId: Long): AnalysisEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalysis(analysis: AnalysisEntity): Long
}

@Dao
interface CampaignDao {
    @Query("SELECT * FROM campaigns ORDER BY createdAt DESC")
    fun getAllCampaigns(): Flow<List<CampaignEntity>>

    @Query("SELECT * FROM campaigns WHERE campaignId = :campaignId LIMIT 1")
    suspend fun getCampaignById(campaignId: Long): CampaignEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaign(campaign: CampaignEntity): Long

    @Update
    suspend fun updateCampaign(campaign: CampaignEntity)

    @Query("SELECT * FROM leads INNER JOIN campaign_leads ON leads.leadId = campaign_leads.leadId WHERE campaign_leads.campaignId = :campaignId")
    fun getLeadsForCampaign(campaignId: Long): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaignLead(campaignLead: CampaignLeadEntity): Long

    @Query("DELETE FROM campaign_leads WHERE campaignId = :campaignId AND leadId = :leadId")
    suspend fun removeLeadFromCampaign(campaignId: Long, leadId: Long)
}

@Dao
interface EmailDao {
    @Query("SELECT * FROM emails ORDER BY sentAt DESC, id DESC")
    fun getAllEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE campaignId = :campaignId")
    fun getEmailsForCampaign(campaignId: Long): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE leadId = :leadId")
    fun getEmailsForLead(leadId: Long): Flow<List<EmailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmail(email: EmailEntity): Long

    @Update
    suspend fun updateEmail(email: EmailEntity)

    @Query("SELECT COUNT(*) FROM emails WHERE status = 'Sent'")
    fun getSentEmailsCount(): Flow<Int>
}

@Dao
interface ReplyDao {
    @Query("SELECT * FROM replies ORDER BY receivedAt DESC")
    fun getAllReplies(): Flow<List<ReplyEntity>>

    @Query("SELECT * FROM replies WHERE leadId = :leadId")
    fun getRepliesForLead(leadId: Long): Flow<List<ReplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: ReplyEntity): Long

    @Query("SELECT COUNT(*) FROM replies")
    fun getRepliesCount(): Flow<Int>
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): SettingsEntity?

    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: SettingsEntity)
}
