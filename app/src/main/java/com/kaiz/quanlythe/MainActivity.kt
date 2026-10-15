package com.kaiz.quanlythe

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi động lịch nhắc hạn thanh toán mỗi ngày bằng WorkManager
        ReminderWorker.scheduleDailyReminder(this)

        setContent {
            AppGlassTheme {
                // Xin quyền hiển thị thông báo trên Android 13+ (API 33+)
                RequestNotificationPermission()

                val viewModel: AppViewModel = viewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()

                if (isLoggedIn) {
                    MainAppScaffold(viewModel = viewModel)
                } else {
                    AuthScreen(viewModel = viewModel)
                }
            }
        }
    }
}

/**
 * Tự động kiểm tra và xin quyền POST_NOTIFICATIONS trên Android 13+
 */
@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { /* Đã phản hồi cấp quyền */ }

        LaunchedEffect(Unit) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

// ==========================================
// ĐỊNH NGHĨA CÁC MỤC TRÊN BOTTOM BAR
// ==========================================

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Overview : BottomNavItem("overview", "Tổng quan", Icons.Rounded.Home)
    object Cards : BottomNavItem("cards", "Thẻ", Icons.Rounded.CreditCard)
    object Transactions : BottomNavItem("transactions", "Giao dịch", Icons.Rounded.ReceiptLong)
    object Statistics : BottomNavItem("statistics", "Thống kê", Icons.Rounded.BarChart)
    object Settings : BottomNavItem("settings", "Cài đặt", Icons.Rounded.Settings)
}

/**
 * Giao diện chính chứa Bottom Bar phong cách Liquid Glass và NavHost
 */
@Composable
fun MainAppScaffold(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItem.Overview,
        BottomNavItem.Cards,
        BottomNavItem.Transactions,
        BottomNavItem.Statistics,
        BottomNavItem.Settings
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Nội dung màn hình theo NavHost
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Overview.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(BottomNavItem.Overview.route) {
                OverviewScreen(
                    viewModel = viewModel,
                    onNavigateToCards = {
                        navController.navigate(BottomNavItem.Cards.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToTransactions = {
                        navController.navigate(BottomNavItem.Transactions.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(BottomNavItem.Cards.route) {
                CardsScreen(viewModel = viewModel)
            }
            composable(BottomNavItem.Transactions.route) {
                TransactionsScreen(viewModel = viewModel)
            }
            composable(BottomNavItem.Statistics.route) {
                StatisticsScreen(viewModel = viewModel)
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }
        }

        // Thanh điều hướng dưới đáy Liquid Glass (Glass Bottom Bar)
        val bottomBarShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = bottomBarShape,
                    ambientColor = GlassThemeColors.Accent.copy(alpha = 0.15f),
                    spotColor = GlassThemeColors.Accent.copy(alpha = 0.25f)
                )
                .clip(bottomBarShape)
                .background(Color.White.copy(alpha = 0.88f))
                .border(
                    width = 1.dp,
                    brush = GlassThemeColors.GlassBorder,
                    shape = bottomBarShape
                )
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val interactionSource = remember { MutableInteractionSource() }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = rememberRipple(bounded = false, radius = 24.dp)
                            ) {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                            .padding(vertical = 6.dp, horizontal = 10.dp)
                    ) {
                        // Icon với hiệu ứng viền & màu nhấn
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) GlassThemeColors.Accent.copy(alpha = 0.18f) else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) GlassThemeColors.Accent else GlassThemeColors.SecondaryText,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) GlassThemeColors.Accent else GlassThemeColors.SecondaryText
                        )
                    }
                }
            }
        }
    }
}
