package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.LeadHunterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class LeadHunterViewModel(private val repository: LeadHunterRepository) : ViewModel() {

    // --- Authentication ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // --- Leads ---
    val allLeads: StateFlow<List<LeadEntity>> = repository.allLeads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leadsCount: StateFlow<Int> = repository.leadsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val newLeadsCount: StateFlow<Int> = repository.newLeadsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Campaigns ---
    val allCampaigns: StateFlow<List<CampaignEntity>> = repository.allCampaigns
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Emails & Sending ---
    val allEmails: StateFlow<List<EmailEntity>> = repository.allEmails
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sentEmailsCount: StateFlow<Int> = repository.sentEmailsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Replies ---
    val allReplies: StateFlow<List<ReplyEntity>> = repository.allReplies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val repliesCount: StateFlow<Int> = repository.repliesCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Notifications ---
    val allNotifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Settings ---
    private val _settings = MutableStateFlow(SettingsEntity())
    val settings: StateFlow<SettingsEntity> = _settings.asStateFlow()

    // --- Interactive UI Screens States ---
    private val _searchNiche = MutableStateFlow("Software Agency")
    val searchNiche: StateFlow<String> = _searchNiche.asStateFlow()

    private val _searchLocation = MutableStateFlow("San Francisco")
    val searchLocation: StateFlow<String> = _searchLocation.asStateFlow()

    private val _isDiscovering = MutableStateFlow(false)
    val isDiscovering: StateFlow<Boolean> = _isDiscovering.asStateFlow()

    private val _discoveredLeads = MutableStateFlow<List<LeadEntity>>(emptyList())
    val discoveredLeads: StateFlow<List<LeadEntity>> = _discoveredLeads.asStateFlow()

    private val _activeLeadAnalysis = MutableStateFlow<AnalysisEntity?>(null)
    val activeLeadAnalysis: StateFlow<AnalysisEntity?> = _activeLeadAnalysis.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _generatedEmailOutreach = MutableStateFlow<Map<String, String>?>(null)
    val generatedEmailOutreach: StateFlow<Map<String, String>?> = _generatedEmailOutreach.asStateFlow()

    private val _isGeneratingEmail = MutableStateFlow(false)
    val isGeneratingEmail: StateFlow<Boolean> = _isGeneratingEmail.asStateFlow()

    init {
        // Auto Login first mock user if database is completely empty
        viewModelScope.launch {
            repository.getSettingsFlow().collect { loadedSettings ->
                if (loadedSettings != null) {
                    _settings.value = loadedSettings
                }
            }
        }
        viewModelScope.launch {
            // Check if there is already a user, if not register one to allow simple auto-login or login
            val defaultEmail = "demo@leadhunter.ai"
            var user = repository.getUserByEmail(defaultEmail)
            if (user == null) {
                repository.registerUser("Outreach Manager", defaultEmail, "password")
                user = repository.getUserByEmail(defaultEmail)
            }
            _currentUser.value = user

            // Check if we need to seed initial leads to make the app look stunning immediately
            val existingLeads = repository.allLeads.first()
            if (existingLeads.isEmpty()) {
                seedInitialData()
            }
        }
    }

    private suspend fun seedInitialData() {
        val seedLeads = listOf(
            LeadEntity(
                companyName = "Acme Dev Solutions",
                website = "https://acmedevsolutions.io",
                publicContactEmail = "contact@acmedevsolutions.io",
                phone = "+1 (555) 019-2831",
                industry = "Software",
                city = "New York",
                country = "United States",
                companyDescription = "High-scale custom software integration and infrastructure consulting firm.",
                leadSource = "Acme Indexer",
                status = "New",
                score = 82
            ),
            LeadEntity(
                companyName = "Elite Dental Care",
                website = "https://elitedentalcare.com",
                publicContactEmail = "dr.smith@elitedentalcare.com",
                phone = "+1 (555) 402-1100",
                industry = "Dentist",
                city = "Los Angeles",
                country = "United States",
                companyDescription = "Modern clinic specializing in painless cosmetic dentistry and implants.",
                leadSource = "Google Maps Scraper",
                status = "New",
                score = 58
            ),
            LeadEntity(
                companyName = "Prime Realty Agency",
                website = "https://primerealty.net",
                publicContactEmail = "leads@primerealty.net",
                phone = "+1 (555) 880-9922",
                industry = "Real Estate",
                city = "Chicago",
                country = "United States",
                companyDescription = "Local real estate firm managing luxury properties and corporate leasing portfolios.",
                leadSource = "Real Estate Web Index",
                status = "New",
                score = 65
            )
        )
        seedLeads.forEach { repository.insertLead(it) }

        // Seed settings
        repository.saveSettings(SettingsEntity())
    }

    // --- Actions ---

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null && password.isNotEmpty()) {
                _currentUser.value = user
                _authError.value = null
                onSuccess()
            } else {
                _authError.value = "Invalid email or password"
            }
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                _authError.value = "Please fill in all fields"
                return@launch
            }
            val existing = repository.getUserByEmail(email)
            if (existing != null) {
                _authError.value = "User with this email already exists"
                return@launch
            }
            repository.registerUser(name, email, password)
            val user = repository.getUserByEmail(email)
            _currentUser.value = user
            _authError.value = null
            onSuccess()
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateSearchNiche(value: String) { _searchNiche.value = value }
    fun updateSearchLocation(value: String) { _searchLocation.value = value }

    fun discoverLeads() {
        viewModelScope.launch {
            _isDiscovering.value = true
            try {
                val results = repository.discoverLeads(_searchNiche.value, _searchLocation.value)
                _discoveredLeads.value = results
            } catch (e: Exception) {
                _authError.value = "Discovery error: ${e.localizedMessage}"
            } finally {
                _isDiscovering.value = false
            }
        }
    }

    fun addManualLead(lead: LeadEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertLead(lead)
            onComplete()
        }
    }

    fun deleteLead(lead: LeadEntity) {
        viewModelScope.launch {
            repository.deleteLead(lead)
        }
    }

    fun addLeadNotesAndTags(leadId: Long, notes: String, tags: String) {
        viewModelScope.launch {
            val lead = repository.getLeadById(leadId)
            if (lead != null) {
                repository.updateLead(lead.copy(notes = notes, tags = tags))
            }
        }
    }

    fun analyzeWebsiteForLead(leadId: Long) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                val result = repository.analyzeWebsite(leadId, forceAI = true)
                _activeLeadAnalysis.value = result
            } catch (e: Exception) {
                _authError.value = "Analysis error: ${e.localizedMessage}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun loadAnalysisForLead(leadId: Long) {
        viewModelScope.launch {
            val result = repository.getAnalysisForLead(leadId)
            _activeLeadAnalysis.value = result
        }
    }

    fun generateEmailOutreach(leadId: Long) {
        viewModelScope.launch {
            _isGeneratingEmail.value = true
            try {
                val output = repository.generateOutreachEmail(leadId)
                _generatedEmailOutreach.value = output
            } catch (e: Exception) {
                _authError.value = "Email generation error: ${e.localizedMessage}"
            } finally {
                _isGeneratingEmail.value = false
            }
        }
    }

    fun saveAndSendEmail(leadId: Long, campaignId: Long?, subject: String, body: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val email = EmailEntity(
                leadId = leadId,
                campaignId = campaignId,
                subject = subject,
                body = body,
                status = "Pending"
            )
            val savedId = repository.saveGeneratedEmail(email)
            val savedEmail = email.copy(id = savedId)
            repository.sendOutreachEmail(savedEmail)
            onComplete()
        }
    }

    fun createCampaign(name: String, leadIds: List<Long>, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (name.isEmpty() || leadIds.isEmpty()) return@launch
            repository.createCampaign(name, leadIds)
            onComplete()
        }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun saveSettings(updated: SettingsEntity) {
        viewModelScope.launch {
            repository.saveSettings(updated)
            _settings.value = updated
        }
    }

    class Factory(private val repository: LeadHunterRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LeadHunterViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LeadHunterViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
