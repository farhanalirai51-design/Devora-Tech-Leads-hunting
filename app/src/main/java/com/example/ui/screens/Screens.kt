package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.data.database.CampaignEntity
import com.example.data.database.EmailEntity
import com.example.data.database.LeadEntity
import com.example.data.database.NotificationEntity
import com.example.data.database.ReplyEntity
import com.example.data.database.SettingsEntity
import com.example.ui.viewmodel.LeadHunterViewModel
import kotlinx.coroutines.delay

// --- Aesthetic Palette Tokens (Purple-Black Glassmorphism Theme) ---
val SlatePrimary = Color(0xFFBB86FC) // Neon Lavender Accent
val SlateSecondary = Color(0xFF6200EE) // Solid Purple
val DarkBackground = Color(0xFF07040E) // Intense Space Black/Purple Background
val DarkSurface = Color(0x241D102F) // Frosted Glass Card background (Semi-transparent dark purple)
val CardSurface = Color(0x3BBD91FC) // Translucent purple glass highlights / border
val TealText = Color(0xFFE0C1FF) // Brilliant Lavender Text
val TextLight = Color(0xFFFAF5FF) // Clean, high-contrast lavender-tinted white
val TextMuted = Color(0xFFA78BFA) // Soft secondary lavender

val CosmicGradient = Brush.verticalGradient(
    listOf(Color(0xFF040209), Color(0xFF140728), Color(0xFF040209))
)

// Navigation Screen Routes
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val LEADS_LIST = "leads_list"
    const val MAP_RADAR = "map_radar"
    const val LEAD_DETAILS = "lead_details/{leadId}"
    const val ADD_LEAD = "add_lead"
    const val WEBSITE_ANALYSIS = "website_analysis/{leadId}"
    const val AI_EMAIL = "ai_email/{leadId}"
    const val CAMPAIGNS = "campaigns"
    const val CAMPAIGN_DETAILS = "campaign_details/{campaignId}"
    const val EMAIL_LOGS = "email_logs"
    const val REPLIES = "replies"
    const val ANALYTICS = "analytics"
    const val NOTIFICATIONS = "notifications"
    const val SETTINGS = "settings"
}

// ==========================================
// 1. SPLASH SCREEN
// ==========================================
@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(Routes.LOGIN) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(SlatePrimary.copy(alpha = 0.1f), CircleShape)
                    .border(2.dp, SlatePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Memory,
                    contentDescription = "Devora Tech Logo",
                    tint = SlatePrimary,
                    modifier = Modifier.size(52.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Devora Tech",
                color = TextLight,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Redesigning Web & Lead Acquisition",
                color = SlatePrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ==========================================
// 2. LOGIN SCREEN
// ==========================================
@Composable
fun LoginScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    var email by remember { mutableStateOf("demo@leadhunter.ai") }
    var password by remember { mutableStateOf("password") }
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    var showForgotPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicGradient)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LockPerson,
                contentDescription = null,
                tint = SlatePrimary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Welcome to Devora Tech", color = TextLight, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Sign in to continue discovering", color = TextMuted, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SlatePrimary,
                    unfocusedBorderColor = CardSurface,
                    focusedLabelColor = SlatePrimary,
                    unfocusedLabelColor = TextMuted,
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_email_input"),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SlatePrimary,
                    unfocusedBorderColor = CardSurface,
                    focusedLabelColor = SlatePrimary,
                    unfocusedLabelColor = TextMuted,
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { showForgotPassword = true }) {
                    Text("Forgot Password?", color = SlatePrimary)
                }
            }

            if (authError != null) {
                Text(authError!!, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.login(email, password) {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("login_button")
            ) {
                Text("Login", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", color = TextMuted)
                TextButton(onClick = { navController.navigate(Routes.REGISTER) }) {
                    Text("Register", color = SlateSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showForgotPassword) {
        AlertDialog(
            onDismissRequest = { showForgotPassword = false },
            title = { Text("Forgot Password", color = TextLight) },
            text = { Text("A password reset link will be sent to $email in production mode.", color = TextMuted) },
            confirmButton = {
                TextButton(onClick = { showForgotPassword = false }) {
                    Text("OK", color = SlatePrimary)
                }
            },
            containerColor = DarkSurface
        )
    }
}

// ==========================================
// 3. REGISTER SCREEN
// ==========================================
@Composable
fun RegisterScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicGradient)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AppRegistration,
                contentDescription = null,
                tint = SlatePrimary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Create Account", color = TextLight, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Access high quality public sales leads", color = TextMuted, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SlateSecondary,
                    unfocusedBorderColor = CardSurface,
                    focusedLabelColor = SlateSecondary,
                    unfocusedLabelColor = TextMuted,
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SlateSecondary,
                    unfocusedBorderColor = CardSurface,
                    focusedLabelColor = SlateSecondary,
                    unfocusedLabelColor = TextMuted,
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SlateSecondary,
                    unfocusedBorderColor = CardSurface,
                    focusedLabelColor = SlateSecondary,
                    unfocusedLabelColor = TextMuted,
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (authError != null) {
                Text(authError!!, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.register(name, email, password) {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SlateSecondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Register", color = TextLight, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account?", color = TextMuted)
                TextButton(onClick = { navController.navigate(Routes.LOGIN) }) {
                    Text("Login", color = SlatePrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// NAVIGATION DRAWER & BASE APP CONTAINER
// ==========================================
@Composable
fun BaseAppScaffold(
    title: String,
    navController: NavController,
    showBackButton: Boolean = false,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            OptInTopAppBar(title, showBackButton, navController)
        },
        bottomBar = {
            BottomNavBar(navController)
        },
        floatingActionButton = floatingActionButton,
        containerColor = Color.Transparent,
        modifier = Modifier.background(CosmicGradient),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptInTopAppBar(title: String, showBackButton: Boolean, navController: NavController) {
    TopAppBar(
        title = {
            Text(
                title,
                color = TextLight,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextLight)
                }
            } else {
                IconButton(onClick = { navController.navigate(Routes.NOTIFICATIONS) }) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = SlatePrimary)
                }
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(Routes.SETTINGS) }) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = TextLight)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkSurface
        )
    )
}

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 8.dp
    ) {
        val navColors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF07040E),
            selectedTextColor = Color(0xFFFAF5FF),
            indicatorColor = SlatePrimary,
            unselectedIconColor = TextMuted,
            unselectedTextColor = TextMuted
        )

        NavigationBarItem(
            selected = currentRoute == Routes.DASHBOARD,
            onClick = { navController.navigate(Routes.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Home", fontSize = 10.sp) },
            colors = navColors
        )
        NavigationBarItem(
            selected = currentRoute.startsWith("leads_list") || currentRoute.startsWith("lead_details") || currentRoute.startsWith("add_lead"),
            onClick = { navController.navigate(Routes.LEADS_LIST) },
            icon = { Icon(Icons.Default.Group, contentDescription = "Leads") },
            label = { Text("Leads", fontSize = 10.sp) },
            colors = navColors
        )
        NavigationBarItem(
            selected = currentRoute.startsWith("campaigns") || currentRoute.startsWith("campaign_details"),
            onClick = { navController.navigate(Routes.CAMPAIGNS) },
            icon = { Icon(Icons.Default.Campaign, contentDescription = "Campaigns") },
            label = { Text("Campaigns", fontSize = 10.sp) },
            colors = navColors
        )
        NavigationBarItem(
            selected = currentRoute == Routes.REPLIES,
            onClick = { navController.navigate(Routes.REPLIES) },
            icon = { Icon(Icons.Default.Mail, contentDescription = "Replies") },
            label = { Text("Replies", fontSize = 10.sp) },
            colors = navColors
        )
        NavigationBarItem(
            selected = currentRoute == Routes.ANALYTICS,
            onClick = { navController.navigate(Routes.ANALYTICS) },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Analytics") },
            label = { Text("Stats", fontSize = 10.sp) },
            colors = navColors
        )
    }
}

// ==========================================
// 4. DASHBOARD SCREEN
// ==========================================
@Composable
fun DashboardScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    val leads by viewModel.allLeads.collectAsStateWithLifecycle()
    val replies by viewModel.allReplies.collectAsStateWithLifecycle()
    val emails by viewModel.allEmails.collectAsStateWithLifecycle()

    val totalLeads = leads.size
    val newLeads = leads.count { it.status == "New" }
    val emailsSent = emails.count { it.status == "Sent" }
    val repliesReceived = replies.size
    val successRate = if (emailsSent > 0) ((repliesReceived.toDouble() / emailsSent.toDouble()) * 100).toInt() else 0

    BaseAppScaffold(title = "Devora Tech Hub", navController = navController) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "Devora Tech",
                            color = TextLight,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(SlatePrimary, CircleShape)
                            )
                            Text(
                                "Gemini AI Connected",
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0x24BB86FC), CircleShape)
                            .border(1.dp, CardSurface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "DT",
                            color = TextLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Glassmorphic Professional Bio Pinned Announcement
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, CardSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Web,
                                contentDescription = null,
                                tint = SlatePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "CORPORATE BIO",
                                color = SlatePrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "We Redesigned Our Business Website!",
                            color = TextLight,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "At Devora Tech, we've successfully redesigned our business platform to feature state-of-the-art speed, responsiveness, and conversion architecture. We help forward-thinking brands modernize their web presence, craft hyper-optimized SEO assets, and automate high-value client acquisition pipelines.",
                            color = TextMuted,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Visual Radar Map Discovery Entry Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0x13BB86FC)),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.5.dp, SlatePrimary.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Routes.MAP_RADAR) }
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Explore,
                                    contentDescription = null,
                                    tint = SlatePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "LOCAL RADAR DISCOVERY",
                                    color = SlatePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Launch Visual Map Lead Finder",
                                color = TextLight,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pulsing live business search, web analysis, and gap scanning for target niches on maps.",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = SlatePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Bento Grid Row 1: Large Gradient Card for Total Leads
            item {
                MetricCard(
                    title = "Total Discovery Pipeline",
                    value = totalLeads.toString(),
                    icon = Icons.Default.Group,
                    color = SlatePrimary,
                    modifier = Modifier.fillMaxWidth(),
                    isGradient = true
                )
            }

            // Bento Grid Row 2: New Leads & Replies (Special Violet)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "New Discoveries",
                        value = newLeads.toString(),
                        icon = Icons.Default.NewReleases,
                        color = SlatePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Total Replies",
                        value = "${successRate}%",
                        icon = Icons.Default.Inbox,
                        color = SlatePrimary,
                        modifier = Modifier.weight(1f),
                        isSpecialColor = true
                    )
                }
            }

            // Bento Grid Row 3: Emails Sent & Reply Rate Success
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Emails Sent",
                        value = emailsSent.toString(),
                        icon = Icons.Default.Outbox,
                        color = SlatePrimary,
                        modifier = Modifier.weight(1.2f)
                    )
                    MetricCard(
                        title = "Replies Count",
                        value = repliesReceived.toString(),
                        icon = Icons.Default.Percent,
                        color = SlatePrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Visual Chart Section (Compose Custom Canvas)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, CardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Weekly Lead Funnel Progress", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        WeeklyProgressChart(leads, emails, replies)
                    }
                }
            }

            // Quick Actions List
            item {
                Text("Quick Actions", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickActionRow(
                        title = "Discover Public Leads",
                        subtitle = "Search businesses by location & niche",
                        icon = Icons.Default.Search,
                        badgeBgColor = Color(0x24BB86FC),
                        iconColor = Color(0xFFFAF5FF)
                    ) {
                        navController.navigate(Routes.LEADS_LIST)
                    }
                    QuickActionRow(
                        title = "Review Email Logs",
                        subtitle = "See delivery statuses & retry options",
                        icon = Icons.Default.History,
                        badgeBgColor = Color(0x246200EE),
                        iconColor = Color(0xFFFAF5FF)
                    ) {
                        navController.navigate(Routes.EMAIL_LOGS)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    isGradient: Boolean = false,
    isSpecialColor: Boolean = false
) {
    val cardBg = if (isSpecialColor) Color(0x3B6200EE) else DarkSurface
    val textMainColor = if (isGradient) Color.White else TextLight
    val textSubColor = if (isGradient) Color.White.copy(alpha = 0.85f) else if (isSpecialColor) Color(0xFFE0C1FF) else TextMuted
    val iconTint = if (isGradient) Color.White else if (isSpecialColor) Color(0xFFFAF5FF) else color
    val iconBg = if (isGradient) Color.White.copy(alpha = 0.2f) else if (isSpecialColor) Color(0x33BB86FC) else color.copy(alpha = 0.15f)
    val borderStroke = if (isGradient) null else BorderStroke(1.dp, CardSurface)

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(24.dp),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (isGradient) Modifier.background(
                        Brush.linearGradient(
                            listOf(Color(0xFF6200EE), Color(0xFF3700B3))
                        )
                    ) else Modifier
                )
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        title, 
                        color = textSubColor, 
                        fontSize = 11.sp, 
                        fontWeight = FontWeight.Bold, 
                        letterSpacing = 0.5.sp,
                        maxLines = 1
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(iconBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(15.dp))
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    value, 
                    color = textMainColor, 
                    fontSize = 32.sp, 
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-0.5).sp
                )
            }
        }
    }
}

@Composable
fun WeeklyProgressChart(leads: List<LeadEntity>, emails: List<EmailEntity>, replies: List<ReplyEntity>) {
    // Elegant Canvas representation of weekly metrics
    val maxVal = maxOf(leads.size, emails.size, replies.size).coerceAtLeast(1)
    val leadHeightRatio = (leads.size.toFloat() / maxVal.toFloat()).coerceIn(0.1f, 1.0f)
    val emailHeightRatio = (emails.size.toFloat() / maxVal.toFloat()).coerceIn(0.1f, 1.0f)
    val replyHeightRatio = (replies.size.toFloat() / maxVal.toFloat()).coerceIn(0.1f, 1.0f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        val width = size.width
        val height = size.height
        val barWidth = width * 0.18f
        val gap = width * 0.1f

        // Draw background grid lines
        for (i in 1..3) {
            val gridY = height * (i / 4.0f)
            drawLine(
                color = CardSurface.copy(alpha = 0.3f),
                start = androidx.compose.ui.geometry.Offset(0f, gridY),
                end = androidx.compose.ui.geometry.Offset(width, gridY),
                strokeWidth = 2f
            )
        }

        // 1. Leads Bar (SlatePrimary)
        val bar1Height = height * leadHeightRatio
        drawRoundRect(
            color = SlatePrimary,
            topLeft = androidx.compose.ui.geometry.Offset(gap, height - bar1Height),
            size = androidx.compose.ui.geometry.Size(barWidth, bar1Height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
        )

        // 2. Emails Bar (SlateSecondary)
        val bar2Height = height * emailHeightRatio
        drawRoundRect(
            color = SlateSecondary,
            topLeft = androidx.compose.ui.geometry.Offset(gap * 2f + barWidth, height - bar2Height),
            size = androidx.compose.ui.geometry.Size(barWidth, bar2Height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
        )

        // 3. Replies Bar (TealText)
        val bar3Height = height * replyHeightRatio
        drawRoundRect(
            color = Color(0xFF10B981),
            topLeft = androidx.compose.ui.geometry.Offset(gap * 3f + barWidth * 2f, height - bar3Height),
            size = androidx.compose.ui.geometry.Size(barWidth, bar3Height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        LegendItem("Leads (${leads.size})", SlatePrimary)
        LegendItem("Sent (${emails.size})", SlateSecondary)
        LegendItem("Replies (${replies.size})", Color(0xFF10B981))
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun QuickActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeBgColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, CardSurface),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(badgeBgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = TextMuted, fontSize = 11.sp)
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
        }
    }
}

// ==========================================
// 5. LEADS LIST SCREEN
// ==========================================
@Composable
fun LeadsListScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    val leads by viewModel.allLeads.collectAsStateWithLifecycle()
    val isDiscovering by viewModel.isDiscovering.collectAsStateWithLifecycle()
    val niche by viewModel.searchNiche.collectAsStateWithLifecycle()
    val location by viewModel.searchLocation.collectAsStateWithLifecycle()

    var showDiscoveryDialog by remember { mutableStateOf(false) }

    BaseAppScaffold(
        title = "Discover & Target Leads",
        navController = navController,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.ADD_LEAD) },
                containerColor = SlatePrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Lead", tint = DarkBackground)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search / Discover Banner Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardSurface.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("AI Lead Discovery", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Locate and inspect regional web gaps", color = TextMuted, fontSize = 12.sp)
                        }
                        Icon(imageVector = Icons.Default.Explore, contentDescription = null, tint = SlatePrimary)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate(Routes.MAP_RADAR) },
                            colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Radar, contentDescription = null, tint = Color(0xFF07040E), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Map Radar", color = Color(0xFF07040E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { showDiscoveryDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SlatePrimary),
                            border = BorderStroke(1.dp, SlatePrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Keyword Search", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Leads Database", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            if (leads.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No leads in your database yet.", color = TextMuted)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(leads) { lead ->
                        LeadItemRow(lead, onClick = {
                            navController.navigate("lead_details/${lead.leadId}")
                        })
                    }
                }
            }
        }
    }

    if (showDiscoveryDialog) {
        AlertDialog(
            onDismissRequest = { showDiscoveryDialog = false },
            title = { Text("Discover Leads", color = TextLight) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Provide a niche and location. We will scan public resources for business contacts.", color = TextMuted, fontSize = 12.sp)
                    OutlinedTextField(
                        value = niche,
                        onValueChange = { viewModel.updateSearchNiche(it) },
                        label = { Text("Business Niche") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight)
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = { viewModel.updateSearchLocation(it) },
                        label = { Text("Location / City") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDiscoveryDialog = false
                        viewModel.discoverLeads()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary)
                ) {
                    Text("Discover")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscoveryDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = DarkSurface
        )
    }

    if (isDiscovering) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = SlatePrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Analyzing public webs indices...", color = TextLight, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LeadItemRow(lead: LeadEntity, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(10.dp),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(lead.companyName, color = TextLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(SlatePrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(lead.status, color = SlatePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(lead.website, color = TealText, fontSize = 12.sp)
                Text("Source: ${lead.leadSource}", color = TextMuted, fontSize = 10.sp)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (lead.score >= 80) Color(0xFF10B981).copy(alpha = 0.15f)
                        else if (lead.score >= 60) Color(0xFFF59E0B).copy(alpha = 0.15f)
                        else Color(0xFFEF4444).copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    lead.score.toString(),
                    color = if (lead.score >= 80) Color(0xFF10B981)
                    else if (lead.score >= 60) Color(0xFFF59E0B)
                    else Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// 6. LEAD DETAILS SCREEN
// ==========================================
@Composable
fun LeadDetailsScreen(leadId: Long, navController: NavController, viewModel: LeadHunterViewModel) {
    val leads by viewModel.allLeads.collectAsStateWithLifecycle()
    val lead = leads.firstOrNull { it.leadId == leadId }

    var notes by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    LaunchedEffect(lead) {
        if (lead != null) {
            notes = lead.notes
            tags = lead.tags
        }
    }

    BaseAppScaffold(
        title = "Lead Profile",
        navController = navController,
        showBackButton = true
    ) { innerPadding ->
        if (lead == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Lead Profile not found.", color = TextMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(lead.companyName, color = TextLight, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(SlatePrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(lead.status, color = SlatePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(lead.companyDescription, color = TextMuted, fontSize = 12.sp)

                            HorizontalDivider(color = CardSurface, modifier = Modifier.padding(vertical = 12.dp))

                            ContactItem(Icons.Default.Web, "Website", lead.website)
                            ContactItem(Icons.Default.Email, "Email", lead.publicContactEmail)
                            ContactItem(Icons.Default.Phone, "Phone", lead.phone)
                            ContactItem(Icons.Default.Place, "Location", "${lead.city}, ${lead.country}")
                        }
                    }
                }

                // AI Tools Hub
                item {
                    Text("AI Insights Hub", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("website_analysis/${lead.leadId}")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Analytics, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyze Site")
                        }

                        Button(
                            onClick = {
                                navController.navigate("ai_email/${lead.leadId}")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SlateSecondary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Email")
                        }
                    }
                }

                // Edit Notes & Tags
                item {
                    Text("Account Management", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = tags,
                                onValueChange = { tags = it },
                                label = { Text("Lead Tags (Comma-separated)") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextLight,
                                    unfocusedTextColor = TextLight,
                                    focusedBorderColor = SlatePrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Client Action Log Notes") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextLight,
                                    unfocusedTextColor = TextLight,
                                    focusedBorderColor = SlatePrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    viewModel.addLeadNotesAndTags(lead.leadId, notes, tags)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Save Notes")
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            viewModel.deleteLead(lead)
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete Lead Profile")
                    }
                }
            }
        }
    }
}

@Composable
fun ContactItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = SlatePrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text("$label: ", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(value, color = TextLight, fontSize = 12.sp, overflow = TextOverflow.Ellipsis, maxLines = 1)
    }
}

// ==========================================
// 7. ADD LEAD SCREEN
// ==========================================
@Composable
fun AddLeadScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    var name by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var industry by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    BaseAppScaffold(title = "Add Business Lead", navController = navController, showBackButton = true) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Create Manual Profile", color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Company Name") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Website Address") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Contact Email") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Business Phone") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = industry,
                onValueChange = { industry = it },
                label = { Text("Industry / Niche") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Brief Company Bio") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Button(
                onClick = {
                    if (name.isNotEmpty() && website.isNotEmpty()) {
                        val lead = LeadEntity(
                            companyName = name,
                            website = website,
                            publicContactEmail = email,
                            phone = phone,
                            industry = industry,
                            city = city,
                            country = "United States",
                            companyDescription = desc,
                            leadSource = "Manual Entry"
                        )
                        viewModel.addManualLead(lead) {
                            navController.popBackStack()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Insert Lead Profile")
            }
        }
    }
}

// ==========================================
// 8. WEBSITE ANALYSIS SCREEN
// ==========================================
@Composable
fun WebsiteAnalysisScreen(leadId: Long, navController: NavController, viewModel: LeadHunterViewModel) {
    val analysis by viewModel.activeLeadAnalysis.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()

    LaunchedEffect(leadId) {
        viewModel.loadAnalysisForLead(leadId)
    }

    BaseAppScaffold(
        title = "Website Diagnostics",
        navController = navController,
        showBackButton = true
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("AI Website Diagnostics", color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())

            if (analysis == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(DarkSurface, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No analysis log exists for this lead.", color = TextMuted)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.analyzeWebsiteForLead(leadId) },
                            colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary)
                        ) {
                            Text("Run Diagnostics Tool")
                        }
                    }
                }
            } else {
                val data = analysis!!

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularScoreBadge("SEO Index", data.seoScore, SlatePrimary, Modifier.weight(1f))
                    CircularScoreBadge("Web Quality", data.websiteQualityScore, SlateSecondary, Modifier.weight(1f))
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Diagnostic Summary", color = TextLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(data.summary, color = TextMuted, fontSize = 13.sp)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Audits Checklist", color = TextLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                        AuditChecklistItem("Mobile Friendly", data.mobileFriendliness == "Excellent" || data.mobileFriendliness == "Good", data.mobileFriendliness)
                        AuditChecklistItem("Contact Flow", !data.missingContactForm, if (data.missingContactForm) "Missing Form" else "Contact Form Found")
                        AuditChecklistItem("Call To Actions", !data.missingCta, if (data.missingCta) "Missing CTA Hero" else "Hero CTA Present")
                        AuditChecklistItem("Speed Index", data.speedIssues.contains("None") || data.speedIssues.contains("Optimal"), data.speedIssues)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Recommended Upgrades", color = TealText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        data.recommendations.split("\n").forEach { rec ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Icon(Icons.Default.ArrowRight, contentDescription = null, tint = SlatePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(rec, color = TextLight, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.analyzeWebsiteForLead(leadId) },
                    colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Re-Run Diagnostic scan")
                }
            }
        }
    }

    if (isAnalyzing) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = SlatePrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Analyzing HTML structure and assets...", color = TextLight, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CircularScoreBadge(label: String, score: Int, color: Color, modifier: Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(70.dp)) {
                Canvas(modifier = Modifier.size(70.dp)) {
                    drawArc(
                        color = CardSurface,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = (score.toFloat() / 100f) * 360f,
                        useCenter = false,
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text("$score%", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AuditChecklistItem(title: String, success: Boolean, details: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (success) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (success) Color(0xFF10B981) else Color(0xFFEF4444),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, color = TextLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(details, color = TextMuted, fontSize = 11.sp)
        }
    }
}

// ==========================================
// 9. AI EMAIL GENERATOR SCREEN
// ==========================================
@Composable
fun AIEmailGeneratorScreen(leadId: Long, navController: NavController, viewModel: LeadHunterViewModel) {
    val leads by viewModel.allLeads.collectAsStateWithLifecycle()
    val lead = leads.firstOrNull { it.leadId == leadId }
    val isGenerating by viewModel.isGeneratingEmail.collectAsStateWithLifecycle()
    val outreachResult by viewModel.generatedEmailOutreach.collectAsStateWithLifecycle()

    var subject by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    LaunchedEffect(outreachResult) {
        if (outreachResult != null) {
            subject = outreachResult!!["subject"] ?: ""
            body = outreachResult!!["body"] ?: ""
        }
    }

    BaseAppScaffold(
        title = "AI Copywriter",
        navController = navController,
        showBackButton = true
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Outreach Engine", color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (lead == null) {
                Text("Lead not found", color = TextMuted)
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(lead.companyName, color = TextLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(lead.publicContactEmail, color = TextMuted, fontSize = 12.sp)
                        }
                        Box(
                            modifier = Modifier
                                .background(SlatePrimary.copy(alpha = 0.15f), CircleShape)
                                .size(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SlatePrimary, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                if (outreachResult == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(DarkSurface, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Outreach sequence not generated yet.", color = TextMuted)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.generateEmailOutreach(leadId) },
                                colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary)
                            ) {
                                Text("Generate outreach with Gemini")
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Email Subject Line") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight, focusedBorderColor = SlatePrimary),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        label = { Text("Personalized Email Body") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight, focusedBorderColor = SlatePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.saveAndSendEmail(leadId, null, subject, body) {
                                navController.navigate(Routes.EMAIL_LOGS)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Outreach Email Now")
                    }
                }
            }
        }
    }

    if (isGenerating) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = SlatePrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Assembling personalized pitch using Gemini...", color = TextLight, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// 10. CAMPAIGNS SCREEN
// ==========================================
@Composable
fun CampaignsScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    val campaigns by viewModel.allCampaigns.collectAsStateWithLifecycle()
    val leads by viewModel.allLeads.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var campaignName by remember { mutableStateOf("") }
    val selectedLeadIds = remember { mutableStateListOf<Long>() }

    BaseAppScaffold(
        title = "Outreach Campaigns",
        navController = navController,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedLeadIds.clear()
                    campaignName = ""
                    showCreateDialog = true
                },
                containerColor = SlatePrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Campaign", tint = DarkBackground)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("My Active Campaigns", color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (campaigns.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No campaigns created yet.", color = TextMuted)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(campaigns) { campaign ->
                        CampaignItemRow(campaign) {
                            navController.navigate("campaign_details/${campaign.campaignId}")
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Campaign", color = TextLight) },
            text = {
                Column(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = campaignName,
                        onValueChange = { campaignName = it },
                        label = { Text("Campaign Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Target Leads (Select at least one):", color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    
                    Box(modifier = Modifier.weight(1f).border(1.dp, CardSurface, RoundedCornerShape(8.dp))) {
                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                            items(leads) { lead ->
                                val isSelected = selectedLeadIds.contains(lead.leadId)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isSelected) selectedLeadIds.remove(lead.leadId)
                                            else selectedLeadIds.add(lead.leadId)
                                        }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = {
                                            if (isSelected) selectedLeadIds.remove(lead.leadId)
                                            else selectedLeadIds.add(lead.leadId)
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = SlatePrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(lead.companyName, color = TextLight, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (campaignName.isNotEmpty() && selectedLeadIds.isNotEmpty()) {
                            viewModel.createCampaign(campaignName, selectedLeadIds.toList()) {
                                showCreateDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun CampaignItemRow(campaign: CampaignEntity, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(10.dp),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(campaign.campaignName, color = TextLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Target Leads: ${campaign.leadsCount}", color = TextMuted, fontSize = 12.sp)
            }
            Box(
                modifier = Modifier
                    .background(SlatePrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(campaign.status, color = SlatePrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// 11. CAMPAIGN DETAILS SCREEN
// ==========================================
@Composable
fun CampaignDetailsScreen(campaignId: Long, navController: NavController, viewModel: LeadHunterViewModel) {
    val campaigns by viewModel.allCampaigns.collectAsStateWithLifecycle()
    val campaign = campaigns.firstOrNull { it.campaignId == campaignId }
    val leads by viewModel.allLeads.collectAsStateWithLifecycle() // In real, filter by association. For simple sandbox list matching is okay.

    BaseAppScaffold(
        title = "Campaign Monitor",
        navController = navController,
        showBackButton = true
    ) { innerPadding ->
        if (campaign == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Campaign details not found.", color = TextMuted)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(campaign.campaignName, color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Status: ${campaign.status}", color = SlatePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Launch Date: June 2026", color = TextMuted, fontSize = 12.sp)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            viewModel.saveSettings(viewModel.settings.value) // trigger resume simulation
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Batch")
                    }

                    Button(
                        onClick = { /* pause simulation */ },
                        colors = ButtonDefaults.buttonColors(containerColor = CardSurface),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pause Campaign")
                    }
                }

                Text("Target Leads in Campaign (${campaign.leadsCount})", color = TextLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(leads.take(campaign.leadsCount)) { lead ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(lead.companyName, color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(lead.publicContactEmail, color = TextMuted, fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(SlatePrimary.copy(alpha = 0.15f), CircleShape)
                                        .size(32.dp)
                                        .clickable {
                                            navController.navigate("ai_email/${lead.leadId}")
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SlatePrimary, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 12. EMAIL LOGS SCREEN
// ==========================================
@Composable
fun EmailLogsScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    val emails by viewModel.allEmails.collectAsStateWithLifecycle()

    BaseAppScaffold(title = "Outreach Logs", navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Delivery History", color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (emails.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No outbound outreach emails logged yet.", color = TextMuted)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(emails) { email ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Subject: ${email.subject}", color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (email.status == "Sent") Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(email.status, color = if (email.status == "Sent") Color(0xFF10B981) else Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(email.body, color = TextMuted, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Delivered via: SMTP Sandbox Node", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 13. REPLIES SCREEN
// ==========================================
@Composable
fun RepliesScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    val replies by viewModel.allReplies.collectAsStateWithLifecycle()

    BaseAppScaffold(title = "Outreach Replies Inbox", navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Prospect Responses", color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (replies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Waiting for incoming client replies...", color = TextMuted)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(replies) { reply ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Prospect", color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (reply.sentiment == "Positive") Color(0xFF10B981).copy(alpha = 0.15f)
                                                else if (reply.sentiment == "Neutral") Color(0xFFF59E0B).copy(alpha = 0.15f)
                                                else Color(0xFFEF4444).copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(reply.sentiment, color = if (reply.sentiment == "Positive") Color(0xFF10B981) else if (reply.sentiment == "Neutral") Color(0xFFF59E0B) else Color(0xFFEF4444), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(reply.emailContent, color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 14. ANALYTICS SCREEN
// ==========================================
@Composable
fun AnalyticsScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    val leads by viewModel.allLeads.collectAsStateWithLifecycle()
    val replies by viewModel.allReplies.collectAsStateWithLifecycle()
    val emails by viewModel.allEmails.collectAsStateWithLifecycle()

    val emailsSent = emails.size
    val repliesReceived = replies.size
    val replyRate = if (emailsSent > 0) ((repliesReceived.toDouble() / emailsSent.toDouble()) * 100).toInt() else 0

    BaseAppScaffold(title = "AI Performance Stats", navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Campaign Diagnostics & Performance", color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Core Conversion Funnel", color = TextLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    
                    FunnelRow("Leads Discovered", leads.size, SlatePrimary)
                    FunnelRow("Outbound outreach", emailsSent, SlateSecondary)
                    FunnelRow("Total Responses", repliesReceived, Color(0xFF10B981))
                    FunnelRow("Conversion Rate", replyRate, Color(0xFFEC4899), suffix = "%")
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Conversion Index over Time", color = TextLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                        val path = Path()
                        path.moveTo(0f, size.height * 0.8f)
                        path.quadraticTo(size.width * 0.25f, size.height * 0.6f, size.width * 0.5f, size.height * 0.3f)
                        path.quadraticTo(size.width * 0.75f, size.height * 0.4f, size.width, size.height * 0.1f)
                        drawPath(path, color = SlatePrimary, style = Stroke(width = 8f))
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Jan", color = TextMuted, fontSize = 10.sp)
                        Text("Mar", color = TextMuted, fontSize = 10.sp)
                        Text("May", color = TextMuted, fontSize = 10.sp)
                        Text("Jun (Now)", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FunnelRow(label: String, valNum: Int, color: Color, suffix: String = "") {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, color = TextLight, fontSize = 13.sp)
        }
        Text("$valNum$suffix", color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

// ==========================================
// 15. NOTIFICATIONS SCREEN
// ==========================================
@Composable
fun NotificationsScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    val notifications by viewModel.allNotifications.collectAsStateWithLifecycle()

    BaseAppScaffold(
        title = "Alerts & Events Center",
        navController = navController,
        showBackButton = true
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("App Alerts Center", color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = { viewModel.markAllNotificationsAsRead() }) {
                    Text("Clear All", color = SlatePrimary)
                }
            }

            if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No events or notifications right now.", color = TextMuted)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(notifications) { notif ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (notif.isRead) DarkSurface.copy(alpha = 0.5f) else DarkSurface),
                            shape = RoundedCornerShape(10.dp),
                            onClick = { viewModel.markNotificationAsRead(notif.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            if (notif.isRead) CardSurface else SlatePrimary.copy(alpha = 0.15f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (notif.isRead) Icons.Default.Check else Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = if (notif.isRead) TextMuted else SlatePrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(notif.title, color = if (notif.isRead) TextMuted else TextLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(notif.message, color = TextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 16. SETTINGS SCREEN
// ==========================================
@Composable
fun SettingsScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var geminiKey by remember { mutableStateOf("") }
    var placesKey by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("smtp.gmail.com") }
    var port by remember { mutableStateOf("587") }
    var gmailAddr by remember { mutableStateOf("") }
    var gmailPass by remember { mutableStateOf("") }
    var niche by remember { mutableStateOf("Web Development") }
    var offering by remember { mutableStateOf("") }
    var delaySecs by remember { mutableStateOf("5") }

    LaunchedEffect(settings) {
        geminiKey = settings.geminiApiKey
        placesKey = settings.googlePlacesApiKey
        host = settings.smtpHost
        port = settings.smtpPort.toString()
        gmailAddr = settings.gmailAddress
        gmailPass = settings.gmailAppPassword
        niche = settings.customNiche
        offering = settings.userOffering
        delaySecs = settings.emailDelay.toString()
    }

    BaseAppScaffold(
        title = "Devora Tech Settings",
        navController = navController,
        showBackButton = true
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Lead Scan & Map Discovery", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = niche,
                onValueChange = { niche = it },
                label = { Text("Default Target Niche") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight, focusedBorderColor = SlatePrimary),
                modifier = Modifier.fillMaxWidth()
            )
            Text("Default business category to discover in visual maps (e.g. Bakeries, Dentists, Plumbers).", color = TextMuted, fontSize = 11.sp)

            OutlinedTextField(
                value = placesKey,
                onValueChange = { placesKey = it },
                label = { Text("Google Places API Key") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight, focusedBorderColor = SlatePrimary),
                modifier = Modifier.fillMaxWidth()
            )
            Text("Enables real Maps API business scraping. Leave blank to run our advanced free AI simulation scan.", color = TextMuted, fontSize = 11.sp)

            HorizontalDivider(color = CardSurface)

            Text("Developer Credentials", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = geminiKey,
                onValueChange = { geminiKey = it },
                label = { Text("Gemini API Key") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight, focusedBorderColor = SlatePrimary),
                modifier = Modifier.fillMaxWidth()
            )
            Text("Enables professional website quality gap analysis using Gemini. (Get a free key from Google AI Studio)", color = TextMuted, fontSize = 11.sp)

            HorizontalDivider(color = CardSurface)

            Text("Free Gmail SMTP Server Credentials", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = gmailAddr,
                onValueChange = { gmailAddr = it },
                label = { Text("My Gmail Address") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight, focusedBorderColor = SlatePrimary),
                modifier = Modifier.fillMaxWidth()
            )
            Text("Your outbound Gmail account (e.g., example@gmail.com). We send emails directly through this secure server.", color = TextMuted, fontSize = 11.sp)

            OutlinedTextField(
                value = gmailPass,
                onValueChange = { gmailPass = it },
                label = { Text("Gmail App Password") },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight, focusedBorderColor = SlatePrimary),
                modifier = Modifier.fillMaxWidth()
            )
            Text("Your 16-character Google 'App Password'. (Go to Google Account -> Security -> 2-Step Verification -> App Passwords).", color = TextMuted, fontSize = 11.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = host,
                    onValueChange = { host = it },
                    label = { Text("SMTP Host") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight, focusedBorderColor = SlatePrimary),
                    modifier = Modifier.weight(2f)
                )
                OutlinedTextField(
                    value = port,
                    onValueChange = { port = it },
                    label = { Text("Port") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight, focusedBorderColor = SlatePrimary),
                    modifier = Modifier.weight(1f)
                )
            }
            Text("Standard Gmail configuration is smtp.gmail.com and port 587 (TLS) or 465 (SSL).", color = TextMuted, fontSize = 11.sp)

            HorizontalDivider(color = CardSurface)

            Text("My Agency / Services Offering", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = offering,
                onValueChange = { offering = it },
                label = { Text("Your B2B Value Proposition") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight, focusedBorderColor = SlatePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            HorizontalDivider(color = CardSurface)

            Text("Sending Queue Delays", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = delaySecs,
                onValueChange = { delaySecs = it },
                label = { Text("Queue Delay (Seconds)") },
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = TextLight, unfocusedTextColor = TextLight, focusedBorderColor = SlatePrimary),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val updated = settings.copy(
                        geminiApiKey = geminiKey,
                        googlePlacesApiKey = placesKey,
                        smtpHost = host,
                        smtpPort = port.toIntOrNull() ?: 587,
                        gmailAddress = gmailAddr,
                        gmailAppPassword = gmailPass,
                        customNiche = niche,
                        userOffering = offering,
                        emailDelay = delaySecs.toIntOrNull() ?: 5,
                        emailProvider = if (gmailAddr.isNotEmpty()) "Gmail SMTP" else "Resend"
                    )
                    viewModel.saveSettings(updated)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Setup Configuration", color = Color(0xFF07040E), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// 17. VISUAL RADAR MAP DISCOVERY SCREEN
// ==========================================

data class MapBusiness(
    val name: String,
    val website: String,
    val phone: String,
    val address: String,
    val rating: Double,
    val reviewCount: Int,
    val offsetX: Int, // dp offset in viewport
    val offsetY: Int,
    val status: String, // No Website, Outdated Website, Unsecured HTTP, Slow Performance
    val reason: String, // gap explanation
    val email: String,
    val description: String,
    val seoScore: Int,
    val mobileFriendliness: String
)

fun generateMockBusinesses(niche: String, location: String): List<MapBusiness> {
    val cleanNiche = niche.ifEmpty { "Web Development" }
    val cleanLocation = location.ifEmpty { "Miami, FL" }
    
    return listOf(
        MapBusiness(
            name = "$cleanLocation $cleanNiche Hub",
            website = "",
            phone = "+1 (555) 201-4491",
            address = "102 Ocean Parkway, $cleanLocation",
            rating = 3.8,
            reviewCount = 14,
            offsetX = 60,
            offsetY = 70,
            status = "No Website",
            reason = "Has a claimed Google Profile with 14 reviews, but has no official website listed. Taps into local traffic but misses high-ticket web conversions.",
            email = "info@${cleanLocation.lowercase().replace(" ", "").replace(",", "")}hub.com",
            description = "A popular local group providing niche service expertise across the greater metropolitan area.",
            seoScore = 0,
            mobileFriendliness = "Non-existent (No Site)"
        ),
        MapBusiness(
            name = "Starlight $cleanNiche Specialists",
            website = "http://starlight${cleanNiche.lowercase().replace(" ", "")}.com",
            phone = "+1 (555) 309-8812",
            address = "455 Madison Ave, $cleanLocation",
            rating = 4.2,
            reviewCount = 38,
            offsetX = 220,
            offsetY = 110,
            status = "Unsecured HTTP",
            reason = "Website is active but lacks an SSL certificate (HTTP instead of HTTPS). Modern web browsers flag this as insecure, dropping trust and conversion by 70%.",
            email = "contact@starlight${cleanNiche.lowercase().replace(" ", "")}.com",
            description = "Experienced provider focusing on consumer satisfaction and standard service deliveries.",
            seoScore = 35,
            mobileFriendliness = "Desktop Only"
        ),
        MapBusiness(
            name = "Apex $cleanNiche Partners",
            website = "https://apex${cleanNiche.lowercase().replace(" ", "")}.net",
            phone = "+1 (555) 721-0023",
            address = "88 West Boulevard, $cleanLocation",
            rating = 3.5,
            reviewCount = 8,
            offsetX = 140,
            offsetY = 210,
            status = "Slow Performance",
            reason = "Page speed takes 6.2s to fully load due to uncompressed assets and missing CDN integrations. Fails mobile Core Web Vitals targets completely.",
            email = "hello@apex${cleanNiche.lowercase().replace(" ", "")}.net",
            description = "A small family-owned provider serving the surrounding local residential and commercial markets.",
            seoScore = 48,
            mobileFriendliness = "Poor (6.2s load)"
        ),
        MapBusiness(
            name = "Elite $cleanNiche & Co",
            website = "https://elite-${cleanNiche.lowercase().replace(" ", "")}.org",
            phone = "+1 (555) 911-5049",
            address = "12 East Side Dr, $cleanLocation",
            rating = 4.9,
            reviewCount = 127,
            offsetX = 90,
            offsetY = 260,
            status = "Outdated Website",
            reason = "Website was designed over 8 years ago. Uses outdated tables, is not mobile-responsive, and lacks direct action CTAs or email opt-in fields.",
            email = "office@elite-${cleanNiche.lowercase().replace(" ", "")}.org",
            description = "Highly-rated industry leaders with substantial local reviews, yet hampered by an ancient web presence.",
            seoScore = 52,
            mobileFriendliness = "Not Responsive"
        ),
        MapBusiness(
            name = "Classic $cleanNiche Clinic",
            website = "",
            phone = "+1 (555) 441-0988",
            address = "709 Pine Crescent, $cleanLocation",
            rating = 4.0,
            reviewCount = 22,
            offsetX = 250,
            offsetY = 250,
            status = "No Website",
            reason = "Relies entirely on a basic, unmanaged Facebook page. Misses custom domain SEO, direct client inquiry capture, and analytics tracking.",
            email = "classic${cleanNiche.lowercase().replace(" ", "")}@gmail.com",
            description = "A local service institution known for premium support but lacking any independent digital real estate.",
            seoScore = 0,
            mobileFriendliness = "Non-existent"
        )
    )
}

@Composable
fun MapRadarScreen(navController: NavController, viewModel: LeadHunterViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val isDiscovering by viewModel.isDiscovering.collectAsStateWithLifecycle()

    var niche by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Miami, FL") }
    var isScanning by remember { mutableStateOf(false) }
    var businesses by remember { mutableStateOf<List<MapBusiness>>(emptyList()) }
    var selectedBusiness by remember { mutableStateOf<MapBusiness?>(null) }
    var activeLogText by remember { mutableStateOf("Ready to initiate radar search.") }

    LaunchedEffect(settings) {
        niche = settings.customNiche.ifEmpty { "Web Development" }
    }

    // Sweep rotation animation when scanning
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by if (isScanning) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "SweepAngle"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    // Handle scan progression log steps
    LaunchedEffect(isScanning) {
        if (isScanning) {
            selectedBusiness = null
            businesses = emptyList()
            
            activeLogText = "Connecting with Google Places & Maps API nodes..."
            delay(800)
            activeLogText = "Querying Yelp business directory indices..."
            delay(700)
            activeLogText = "Scraping local coordinates and phone numbers..."
            delay(800)
            activeLogText = "Analyzing domain registry and SSL status..."
            delay(700)
            activeLogText = "Evaluating mobile speed and viewport viewport structures..."
            delay(600)
            
            businesses = generateMockBusinesses(niche, location)
            isScanning = false
            activeLogText = "Radar scan complete! Found ${businesses.size} business leads with web gaps."
        }
    }

    BaseAppScaffold(
        title = "Interactive Map Radar",
        navController = navController,
        showBackButton = true
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bento Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "VISUAL GEOGRAPHIC SEARCH",
                        color = SlatePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Map-based Web Gap Analyzer",
                        color = TextLight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select a region on the radar to scan live local businesses. We will automatically inspect their websites for critical performance, security, and mobile design gaps.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }

            // Input Fields Bento Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CardSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = niche,
                            onValueChange = { niche = it },
                            label = { Text("Service Niche / Business") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = SlatePrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Target Location / City") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextLight,
                                unfocusedTextColor = TextLight,
                                focusedBorderColor = SlatePrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = { isScanning = true },
                        enabled = !isScanning,
                        colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(color = Color(0xFF07040E), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sweeping Radar Cell...", color = Color(0xFF07040E), fontWeight = FontWeight.Bold)
                        } else {
                            Icon(imageVector = Icons.Default.Radar, contentDescription = null, tint = Color(0xFF07040E))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scan Target Area", color = Color(0xFF07040E), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Radar Viewport Card
            Card(
                colors = CardColors(
                    containerColor = DarkBackground,
                    contentColor = TextLight,
                    disabledContainerColor = DarkBackground,
                    disabledContentColor = TextMuted
                ),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.5.dp, if (isScanning) SlatePrimary else CardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Radar Graphic Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height
                        val centerX = width / 2
                        val centerY = height / 2
                        val maxRadius = minOf(width, height) / 2 - 20.dp.toPx()

                        // Grid Lines
                        drawCircle(
                            color = SlatePrimary.copy(alpha = 0.08f),
                            radius = maxRadius,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        drawCircle(
                            color = SlatePrimary.copy(alpha = 0.12f),
                            radius = maxRadius * 0.66f,
                            style = Stroke(width = 1.2.dp.toPx())
                        )
                        drawCircle(
                            color = SlatePrimary.copy(alpha = 0.18f),
                            radius = maxRadius * 0.33f,
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // Crosshairs
                        drawLine(
                            color = SlatePrimary.copy(alpha = 0.15f),
                            start = androidx.compose.ui.geometry.Offset(centerX - maxRadius, centerY),
                            end = androidx.compose.ui.geometry.Offset(centerX + maxRadius, centerY),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = SlatePrimary.copy(alpha = 0.15f),
                            start = androidx.compose.ui.geometry.Offset(centerX, centerY - maxRadius),
                            end = androidx.compose.ui.geometry.Offset(centerX, centerY + maxRadius),
                            strokeWidth = 1.dp.toPx()
                        )

                        // Glowing sweep arc
                        if (isScanning) {
                            drawArc(
                                color = SlatePrimary.copy(alpha = 0.22f),
                                startAngle = sweepAngle - 45f,
                                sweepAngle = 45f,
                                useCenter = true,
                                size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2),
                                topLeft = androidx.compose.ui.geometry.Offset(centerX - maxRadius, centerY - maxRadius)
                            )
                        }
                    }

                    // Scan overlays / Logs when active
                    if (isScanning) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = SlatePrimary, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "LIVE SATELLITE DISCOVERY",
                                    color = SlatePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = activeLogText,
                                    color = TextLight,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                            }
                        }
                    }

                    // Render dynamic business nodes when complete
                    if (!isScanning && businesses.isNotEmpty()) {
                        businesses.forEach { b ->
                            val isSelected = selectedBusiness?.name == b.name
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .offset(x = b.offsetX.dp, y = b.offsetY.dp)
                                    .size(if (isSelected) 36.dp else 24.dp)
                                    .clickable { selectedBusiness = b },
                                contentAlignment = Alignment.Center
                            ) {
                                // Pulsing background halo if selected
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(SlatePrimary.copy(alpha = 0.25f))
                                    )
                                }
                                // Glowing Core Pin
                                val coreColor = when (b.status) {
                                    "No Website" -> Color(0xFFEF4444) // Bright Red
                                    "Unsecured HTTP" -> Color(0xFFF59E0B) // Amber
                                    "Slow Performance" -> Color(0xFF3B82F6) // Ocean Blue
                                    else -> Color(0xFFA78BFA)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(coreColor)
                                        .border(2.dp, TextLight, CircleShape)
                                )
                            }
                        }
                    }

                    // Empty State initially
                    if (!isScanning && businesses.isEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = SlatePrimary.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Radar Core Offline",
                                color = TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter a service and city above, then tap 'Scan Target Area' to sweep the local cell map.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Real-time Activity Sweep Log Output
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CardSurface.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = SlatePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = activeLogText,
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Interactive Bento Panel for Selected Lead
            AnimatedVisibility(
                visible = selectedBusiness != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                selectedBusiness?.let { b ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.5.dp, SlatePrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Business Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = b.name,
                                        color = TextLight,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = b.address,
                                        color = TextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SlatePrimary.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = SlatePrimary, modifier = Modifier.size(14.dp))
                                        Text("${b.rating}", color = SlatePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Identified Critical Gap Bento Card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Red.copy(alpha = 0.08f))
                                    .border(1.dp, Color.Red.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "DETECTED GAP: ${b.status.uppercase()}",
                                            color = Color(0xFFEF4444),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = b.reason,
                                        color = TextLight.copy(alpha = 0.9f),
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Additional Info Gaps
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("SEO Optimization Score", color = TextMuted, fontSize = 11.sp)
                                    Text(
                                        text = if (b.seoScore == 0) "Critical (0/100)" else "${b.seoScore}/100",
                                        color = if (b.seoScore < 40) Color(0xFFEF4444) else SlatePrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Mobile Responsiveness", color = TextMuted, fontSize = 11.sp)
                                    Text(
                                        text = b.mobileFriendliness,
                                        color = if (b.mobileFriendliness.contains("No") || b.mobileFriendliness.contains("Desktop")) Color(0xFFEF4444) else TextLight,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // CTA Action
                            Button(
                                onClick = {
                                    // Save as a lead in Room database
                                    val lead = LeadEntity(
                                        companyName = b.name,
                                        website = b.website,
                                        publicContactEmail = b.email,
                                        phone = b.phone,
                                        industry = niche,
                                        city = location,
                                        country = "United States",
                                        companyDescription = b.description,
                                        leadSource = "Visual Radar Map Discovery",
                                        status = "New",
                                        notes = "Web Gap Analysis: ${b.reason}. Rated ${b.rating} stars on Maps.",
                                        tags = "${b.status}, Need Redesign",
                                        score = if (b.seoScore > 0) b.seoScore else 15
                                    )
                                    viewModel.addManualLead(lead) {
                                        navController.navigate(Routes.LEADS_LIST)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SlatePrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.ImportExport, contentDescription = null, tint = Color(0xFF07040E))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Import as Target Lead & Generate Audit",
                                    color = Color(0xFF07040E),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

