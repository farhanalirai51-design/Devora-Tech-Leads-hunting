package com.example

import android.app.Application
import com.example.data.database.AppDatabase
import com.example.data.repository.LeadHunterRepository

class LeadHunterApp : Application() {
    lateinit var database: AppDatabase
    lateinit var repository: LeadHunterRepository

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        repository = LeadHunterRepository(
            userDao = database.userDao(),
            leadDao = database.leadDao(),
            analysisDao = database.analysisDao(),
            campaignDao = database.campaignDao(),
            emailDao = database.emailDao(),
            replyDao = database.replyDao(),
            notificationDao = database.notificationDao(),
            settingsDao = database.settingsDao()
        )
    }
}
