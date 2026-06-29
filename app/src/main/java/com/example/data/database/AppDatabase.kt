package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        LeadEntity::class,
        AnalysisEntity::class,
        CampaignEntity::class,
        CampaignLeadEntity::class,
        EmailEntity::class,
        ReplyEntity::class,
        NotificationEntity::class,
        SettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun leadDao(): LeadDao
    abstract fun analysisDao(): AnalysisDao
    abstract fun campaignDao(): CampaignDao
    abstract fun emailDao(): EmailDao
    abstract fun replyDao(): ReplyDao
    abstract fun notificationDao(): NotificationDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "leadhunter_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
