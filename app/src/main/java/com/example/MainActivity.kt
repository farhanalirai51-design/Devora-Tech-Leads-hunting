package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.LeadHunterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get repository from application
        val app = application as LeadHunterApp
        val factory = LeadHunterViewModel.Factory(app.repository)
        val viewModel = factory.create(LeadHunterViewModel::class.java)

        setContent {
            MyApplicationTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: LeadHunterViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController, viewModel)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController, viewModel)
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController, viewModel)
        }
        composable(Routes.LEADS_LIST) {
            LeadsListScreen(navController, viewModel)
        }
        composable(Routes.MAP_RADAR) {
            MapRadarScreen(navController, viewModel)
        }
        composable(
            route = Routes.LEAD_DETAILS,
            arguments = listOf(navArgument("leadId") { type = NavType.LongType })
        ) { backStackEntry ->
            val leadId = backStackEntry.arguments?.getLong("leadId") ?: 0L
            LeadDetailsScreen(leadId, navController, viewModel)
        }
        composable(Routes.ADD_LEAD) {
            AddLeadScreen(navController, viewModel)
        }
        composable(
            route = Routes.WEBSITE_ANALYSIS,
            arguments = listOf(navArgument("leadId") { type = NavType.LongType })
        ) { backStackEntry ->
            val leadId = backStackEntry.arguments?.getLong("leadId") ?: 0L
            WebsiteAnalysisScreen(leadId, navController, viewModel)
        }
        composable(
            route = Routes.AI_EMAIL,
            arguments = listOf(navArgument("leadId") { type = NavType.LongType })
        ) { backStackEntry ->
            val leadId = backStackEntry.arguments?.getLong("leadId") ?: 0L
            AIEmailGeneratorScreen(leadId, navController, viewModel)
        }
        composable(Routes.CAMPAIGNS) {
            CampaignsScreen(navController, viewModel)
        }
        composable(
            route = Routes.CAMPAIGN_DETAILS,
            arguments = listOf(navArgument("campaignId") { type = NavType.LongType })
        ) { backStackEntry ->
            val campaignId = backStackEntry.arguments?.getLong("campaignId") ?: 0L
            CampaignDetailsScreen(campaignId, navController, viewModel)
        }
        composable(Routes.EMAIL_LOGS) {
            EmailLogsScreen(navController, viewModel)
        }
        composable(Routes.REPLIES) {
            RepliesScreen(navController, viewModel)
        }
        composable(Routes.ANALYTICS) {
            AnalyticsScreen(navController, viewModel)
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(navController, viewModel)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController, viewModel)
        }
    }
}
