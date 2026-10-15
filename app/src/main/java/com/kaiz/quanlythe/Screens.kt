package com.kaiz.quanlythe

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==========================================
// MÀN HÌNH ĐĂNG NHẬP / ĐĂNG KÝ
// ==========================================

@Composable
fun AuthScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    var isRegisterMode by remember { mutableStateOf(false) }

    // Các trường form
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    LiquidGlassBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo / Biểu tượng ứng dụng
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
                    .border(1.5.dp, GlassThemeColors.GlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CreditCard,
                    contentDescription = null,
                    tint = GlassThemeColors.Accent,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Quản Lý Thẻ",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GlassThemeColors.PrimaryText
            )

            Text(
                text = if (isRegisterMode) "Tạo tài khoản quản lý thẻ cá nhân" else "Đăng nhập an toàn trên thiết bị",
                fontSize = 14.sp,
                color = GlassThemeColors.SecondaryText,
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            // Thẻ kính chứa Form đăng nhập / đăng ký
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 28.dp,
                backgroundColor = Color.White.copy(alpha = 0.42f)
            ) {
                // Tab chuyển đổi Đăng nhập / Đăng ký
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.4f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isRegisterMode) GlassThemeColors.Accent else Color.Transparent)
                            .clickable {
                                isRegisterMode = false
                                errorMessage = null
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Đăng nhập",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (!isRegisterMode) Color.White else GlassThemeColors.SecondaryText
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isRegisterMode) GlassThemeColors.Accent else Color.Transparent)
                            .clickable {
                                isRegisterMode = true
                                errorMessage = null
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Đăng ký",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isRegisterMode) Color.White else GlassThemeColors.SecondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Nếu là Đăng ký thì có thêm trường Họ và tên
                AnimatedVisibility(visible = isRegisterMode) {
                    Column {
                        GlassTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "Họ và tên",
                            placeholder = "Nguyễn Văn A",
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = GlassThemeColors.SecondaryText
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }

                // Email
                GlassTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Địa chỉ Email",
                    placeholder = "vi-du@email.com",
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Email,
                            contentDescription = null,
                            tint = GlassThemeColors.SecondaryText
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mật khẩu
                GlassTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Mật khẩu (tối thiểu 6 ký tự)",
                    placeholder = "••••••",
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = GlassThemeColors.SecondaryText
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = null,
                                tint = GlassThemeColors.SecondaryText
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                // Xác nhận mật khẩu khi Đăng ký
                AnimatedVisibility(visible = isRegisterMode) {
                    Column {
                        Spacer(modifier = Modifier.height(14.dp))
                        GlassTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Xác nhận mật khẩu",
                            placeholder = "••••••",
                            leadingIcon = {
                                Icon(
                                    Icons.Rounded.LockReset,
                                    contentDescription = null,
                                    tint = GlassThemeColors.SecondaryText
                                )
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Thông báo lỗi nếu có
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = GlassThemeColors.Danger,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nút Đăng nhập / Đăng ký
                GlassButton(
                    text = if (isRegisterMode) "Tạo tài khoản" else "Đăng nhập ngay",
                    onClick = {
                        errorMessage = null
                        if (isRegisterMode) {
                            viewModel.register(
                                fullName = fullName,
                                email = email,
                                pass = password,
                                confirmPass = confirmPassword
                            ) { success, msg ->
                                if (!success) {
                                    errorMessage = msg
                                } else {
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            viewModel.login(email = email, pass = password) { success, msg ->
                                if (!success) {
                                    errorMessage = msg
                                } else {
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "🔒 Dữ liệu lưu 100% trên máy của bạn, mã hoá SHA-256 an toàn",
                fontSize = 12.sp,
                color = GlassThemeColors.SecondaryText,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ==========================================
// 1. MÀN HÌNH TỔNG QUAN (OverviewScreen)
// ==========================================

@Composable
fun OverviewScreen(
    viewModel: AppViewModel,
    onNavigateToCards: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val totalDebt by viewModel.totalDebt.collectAsState()
    val totalLimit by viewModel.totalLimit.collectAsState()
    val totalRemaining by viewModel.totalRemainingLimit.collectAsState()
    val upcomingDueCards by viewModel.upcomingDueCards.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val cards by viewModel.cards.collectAsState()

    var showAddTxnDialog by remember { mutableStateOf(false) }

    LiquidGlassBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header chào hỏi
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Xin chào 👋",
                            fontSize = 14.sp,
                            color = GlassThemeColors.SecondaryText
                        )
                        Text(
                            text = currentUser?.fullName ?: "Bạn",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassThemeColors.PrimaryText
                        )
                    }

                    // Nút thêm giao dịch nhanh
                    IconButton(
                        onClick = { showAddTxnDialog = true },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GlassThemeColors.Accent)
                    ) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = "Thêm giao dịch",
                            tint = Color.White
                        )
                    }
                }
            }

            // Thẻ kính Master: Tổng dư nợ & Hạn mức
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 28.dp,
                    backgroundColor = Color.White.copy(alpha = 0.45f)
                ) {
                    Text(
                        text = "TỔNG DƯ NỢ CẦN THANH TOÁN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassThemeColors.SecondaryText,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = FormatUtils.formatCurrency(totalDebt),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GlassThemeColors.Accent
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.5f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Hạn mức còn lại",
                                fontSize = 12.sp,
                                color = GlassThemeColors.SecondaryText
                            )
                            Text(
                                text = FormatUtils.formatCurrency(totalRemaining),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlassThemeColors.Success
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Tổng hạn mức cấp",
                                fontSize = 12.sp,
                                color = GlassThemeColors.SecondaryText
                            )
                            Text(
                                text = FormatUtils.formatCurrency(totalLimit),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlassThemeColors.PrimaryText
                            )
                        }
                    }
                }
            }

            // PHẦN 4: THẺ SẮP ĐẾN HẠN THANH TOÁN
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sắp đến hạn thanh toán",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassThemeColors.PrimaryText
                    )

                    if (upcomingDueCards.isNotEmpty()) {
                        GlassBadge(
                            text = "${upcomingDueCards.size} thẻ cần chú ý",
                            backgroundColor = GlassThemeColors.Accent.copy(alpha = 0.2f),
                            textColor = GlassThemeColors.Accent
                        )
                    }
                }
            }

            if (upcomingDueCards.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        backgroundColor = Color.White.copy(alpha = 0.35f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = GlassThemeColors.Success,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Không có thẻ nào sắp đến hạn 🎉",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GlassThemeColors.PrimaryText
                                )
                                Text(
                                    text = "Tất cả các thẻ đều an toàn trong 7 ngày tới.",
                                    fontSize = 12.sp,
                                    color = GlassThemeColors.SecondaryText
                                )
                            }
                        }
                    }
                }
            } else {
                items(upcomingDueCards) { dueInfo ->
                    val badgeColor = when (dueInfo.daysLeft) {
                        0 -> GlassThemeColors.Danger
                        1 -> GlassThemeColors.Warning
                        else -> GlassThemeColors.Accent
                    }

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp,
                        backgroundColor = Color.White.copy(alpha = 0.45f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dueInfo.card.cardName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GlassThemeColors.PrimaryText
                                )
                                Text(
                                    text = "${dueInfo.card.bankName} •••• ${dueInfo.card.last4Digits}",
                                    fontSize = 12.sp,
                                    color = GlassThemeColors.SecondaryText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Dư nợ: ${FormatUtils.formatCurrency(dueInfo.balance)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GlassThemeColors.Accent
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                GlassBadge(
                                    text = FormatUtils.getDueStatusText(dueInfo.daysLeft),
                                    backgroundColor = badgeColor.copy(alpha = 0.18f),
                                    textColor = badgeColor
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Hạn: ngày ${dueInfo.card.dueDay}",
                                    fontSize = 12.sp,
                                    color = GlassThemeColors.SecondaryText
                                )
                            }
                        }
                    }
                }
            }

            // GIAO DỊCH GẦN ĐÂY
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Giao dịch gần đây",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassThemeColors.PrimaryText
                    )

                    Text(
                        text = "Xem tất cả",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GlassThemeColors.Accent,
                        modifier = Modifier.clickable { onNavigateToTransactions() }
                    )
                }
            }

            if (transactions.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 20.dp
                    ) {
                        Text(
                            text = "Chưa có giao dịch nào. Bấm nút '+' để ghi nhận chi tiêu đầu tiên!",
                            fontSize = 13.sp,
                            color = GlassThemeColors.SecondaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                items(transactions.take(4)) { txn ->
                    val card = cards.find { it.id == txn.cardId }
                    TransactionItemView(transaction = txn, card = card, onDelete = null)
                }
            }
        }

        // Dialog thêm giao dịch từ màn tổng quan
        if (showAddTxnDialog) {
            AddTransactionDialog(
                viewModel = viewModel,
                cards = cards,
                onDismiss = { showAddTxnDialog = false }
            )
        }
    }
}

// ==========================================
// 2. MÀN HÌNH QUẢN LÝ THẺ (CardsScreen)
// ==========================================

@Composable
fun CardsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val cards by viewModel.cards.collectAsState()
    val cardBalances by viewModel.cardBalances.collectAsState()
    val cardRemainingLimits by viewModel.cardRemainingLimits.collectAsState()

    var showAddCardDialog by remember { mutableStateOf(false) }
    var cardToEdit by remember { mutableStateOf<CreditCard?>(null) }
    var cardToDelete by remember { mutableStateOf<CreditCard?>(null) }

    LiquidGlassBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Thẻ tín dụng",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassThemeColors.PrimaryText
                        )
                        Text(
                            text = "Quản lý ${cards.size} thẻ cá nhân",
                            fontSize = 13.sp,
                            color = GlassThemeColors.SecondaryText
                        )
                    }

                    GlassButton(
                        text = "+ Thêm thẻ",
                        onClick = { showAddCardDialog = true },
                        isPrimary = true
                    )
                }
            }

            if (cards.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        cornerRadius = 24.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Rounded.CreditCard,
                                contentDescription = null,
                                tint = GlassThemeColors.Accent,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Bạn chưa thêm thẻ tín dụng nào",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlassThemeColors.PrimaryText
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Thêm thẻ để theo dõi hạn mức, dư nợ và nhận nhắc nhở hạn thanh toán nhé!",
                                fontSize = 13.sp,
                                color = GlassThemeColors.SecondaryText,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(cards) { card ->
                    val balance = cardBalances[card.id] ?: 0.0
                    val remaining = cardRemainingLimits[card.id] ?: card.creditLimit

                    CreditCardView(
                        card = card,
                        balance = balance,
                        remaining = remaining,
                        onEdit = { cardToEdit = card },
                        onDelete = { cardToDelete = card }
                    )
                }
            }
        }

        // Dialog thêm thẻ
        if (showAddCardDialog) {
            AddOrEditCardDialog(
                viewModel = viewModel,
                cardToEdit = null,
                onDismiss = { showAddCardDialog = false }
            )
        }

        // Dialog sửa thẻ
        if (cardToEdit != null) {
            AddOrEditCardDialog(
                viewModel = viewModel,
                cardToEdit = cardToEdit,
                onDismiss = { cardToEdit = null }
            )
        }

        // Dialog xác nhận xoá thẻ
        if (cardToDelete != null) {
            GlassDialog(
                onDismissRequest = { cardToDelete = null },
                title = "Xoá thẻ tín dụng?"
            ) {
                Text(
                    text = "Bạn có chắc chắn muốn xoá thẻ '${cardToDelete?.cardName}' (${cardToDelete?.bankName})? Tất cả giao dịch thuộc thẻ này cũng sẽ bị xoá.",
                    color = GlassThemeColors.SecondaryText,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    GlassButton(
                        text = "Huỷ",
                        onClick = { cardToDelete = null },
                        isPrimary = false
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    GlassButton(
                        text = "Xoá thẻ",
                        onClick = {
                            cardToDelete?.let { viewModel.deleteCard(it) }
                            cardToDelete = null
                        },
                        isPrimary = true
                    )
                }
            }
        }
    }
}

/**
 * Hiển thị thẻ tín dụng phong cách kính liquid glass tuyệt đẹp
 */
@Composable
fun CreditCardView(
    card: CreditCard,
    balance: Double,
    remaining: Double,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usagePercent = if (card.creditLimit > 0) {
        ((balance / card.creditLimit) * 100).coerceIn(0.0, 100.0).toFloat()
    } else 0f

    val daysLeft = FormatUtils.daysUntilNextDue(card.dueDay)

    val shape = RoundedCornerShape(26.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.55f),
                        Color.White.copy(alpha = 0.28f)
                    )
                )
            )
            .border(1.5.dp, GlassThemeColors.GlassBorder, shape)
            .padding(20.dp)
    ) {
        Column {
            // Hàng đầu: Tên ngân hàng & nút thao tác
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = card.bankName.uppercase(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = GlassThemeColors.Accent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Rounded.Contactless,
                        contentDescription = null,
                        tint = GlassThemeColors.SecondaryText,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = "Sửa thẻ",
                            tint = GlassThemeColors.SecondaryText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Rounded.Delete,
                            contentDescription = "Xoá thẻ",
                            tint = GlassThemeColors.Danger.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mô phỏng chip thẻ màu vàng ánh kim
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 30.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFD54F), Color(0xFFFFB300))
                        )
                    )
                    .border(0.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Số thẻ đuôi 4 số
            Text(
                text = "••••   ••••   ••••   ${card.last4Digits}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GlassThemeColors.PrimaryText,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Tên thẻ
            Text(
                text = card.cardName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GlassThemeColors.SecondaryText
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Thanh tiến trình hạn mức đã sử dụng
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Dư nợ: ${FormatUtils.formatCurrency(balance)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GlassThemeColors.Accent
                    )
                    Text(
                        text = "Hạn mức: ${FormatUtils.formatCurrency(card.creditLimit)}",
                        fontSize = 12.sp,
                        color = GlassThemeColors.SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.6f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (usagePercent / 100f).coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(GlassThemeColors.AccentSecondary, GlassThemeColors.Accent)
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hàng ngày sao kê và ngày đến hạn
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sao kê: ngày ${card.statementDay}",
                    fontSize = 12.sp,
                    color = GlassThemeColors.SecondaryText
                )

                GlassBadge(
                    text = "Hạn trả: ngày ${card.dueDay} (${FormatUtils.getDueStatusText(daysLeft)})",
                    backgroundColor = if (daysLeft <= 3) GlassThemeColors.Danger.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.5f),
                    textColor = if (daysLeft <= 3) GlassThemeColors.Danger else GlassThemeColors.PrimaryText
                )
            }
        }
    }
}

// ==========================================
// 3. MÀN HÌNH GIAO DỊCH (TransactionsScreen)
// ==========================================

@Composable
fun TransactionsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val cards by viewModel.cards.collectAsState()

    var filterType by remember { mutableStateOf("ALL") } // "ALL", "CHI_TIEU", "THANH_TOAN"
    var showAddTxnDialog by remember { mutableStateOf(false) }

    val filteredList = remember(transactions, filterType) {
        when (filterType) {
            "CHI_TIEU" -> transactions.filter { it.type == "CHI_TIEU" }
            "THANH_TOAN" -> transactions.filter { it.type == "THANH_TOAN" }
            else -> transactions
        }
    }

    LiquidGlassBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Lịch sử giao dịch",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassThemeColors.PrimaryText
                        )
                        Text(
                            text = "Ghi nhận chi tiêu & thanh toán dư nợ",
                            fontSize = 13.sp,
                            color = GlassThemeColors.SecondaryText
                        )
                    }

                    GlassButton(
                        text = "+ Ghi nhận",
                        onClick = { showAddTxnDialog = true },
                        isPrimary = true
                    )
                }
            }

            // Thanh lọc danh mục
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.4f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("ALL" to "Tất cả", "CHI_TIEU" to "Chi tiêu", "THANH_TOAN" to "Thanh toán").forEach { (key, label) ->
                        val isSelected = filterType == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) GlassThemeColors.Accent else Color.Transparent)
                                .clickable { filterType = key }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else GlassThemeColors.SecondaryText
                            )
                        }
                    }
                }
            }

            if (filteredList.isEmpty()) {
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp),
                        cornerRadius = 24.dp
                    ) {
                        Text(
                            text = "Không có giao dịch nào trong danh mục này.",
                            fontSize = 14.sp,
                            color = GlassThemeColors.SecondaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                        )
                    }
                }
            } else {
                items(filteredList) { txn ->
                    val card = cards.find { it.id == txn.cardId }
                    TransactionItemView(
                        transaction = txn,
                        card = card,
                        onDelete = { viewModel.deleteTransaction(txn) }
                    )
                }
            }
        }

        if (showAddTxnDialog) {
            AddTransactionDialog(
                viewModel = viewModel,
                cards = cards,
                onDismiss = { showAddTxnDialog = false }
            )
        }
    }
}

/**
 * Hiển thị một dòng giao dịch
 */
@Composable
fun TransactionItemView(
    transaction: Transaction,
    card: CreditCard?,
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val isExpense = transaction.type == "CHI_TIEU"

    val categoryIcon = when (transaction.category) {
        "Ăn uống" -> Icons.Rounded.Restaurant
        "Mua sắm" -> Icons.Rounded.ShoppingBag
        "Di chuyển" -> Icons.Rounded.DirectionsCar
        "Hoá đơn" -> Icons.Rounded.Receipt
        "Thanh toán thẻ" -> Icons.Rounded.Payment
        else -> Icons.Rounded.Category
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        backgroundColor = Color.White.copy(alpha = 0.40f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon danh mục tròn
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isExpense) GlassThemeColors.Accent.copy(alpha = 0.15f) else GlassThemeColors.Success.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = null,
                    tint = if (isExpense) GlassThemeColors.Accent else GlassThemeColors.Success,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (transaction.note.isNotBlank()) transaction.note else transaction.category,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlassThemeColors.PrimaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${card?.bankName ?: "Thẻ"} (•••• ${card?.last4Digits ?: "---"}) • ${FormatUtils.formatDate(transaction.timestamp)}",
                    fontSize = 12.sp,
                    color = GlassThemeColors.SecondaryText
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isExpense) "- ${FormatUtils.formatCurrency(transaction.amount)}" else "+ ${FormatUtils.formatCurrency(transaction.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isExpense) GlassThemeColors.Accent else GlassThemeColors.Success
                )

                if (onDelete != null) {
                    Text(
                        text = "Xoá",
                        fontSize = 11.sp,
                        color = GlassThemeColors.Danger.copy(alpha = 0.8f),
                        modifier = Modifier
                            .clickable { onDelete() }
                            .padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// 5. MÀN HÌNH THỐNG KÊ (StatisticsScreen)
// ==========================================

@Composable
fun StatisticsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val totalDebt by viewModel.totalDebt.collectAsState()
    val totalLimit by viewModel.totalLimit.collectAsState()
    val totalRemaining by viewModel.totalRemainingLimit.collectAsState()
    val monthlyExpenses by viewModel.monthlyCategoryExpenses.collectAsState()
    val totalMonthlyExpense by viewModel.currentMonthTotalExpense.collectAsState()
    val totalMonthlyPayment by viewModel.currentMonthTotalPayment.collectAsState()

    val currentMonthStr = remember {
        val sdf = SimpleDateFormat("MM/yyyy", Locale("vi", "VN"))
        sdf.format(Date())
    }

    val debtRatio = if (totalLimit > 0) ((totalDebt / totalLimit) * 100).toInt() else 0

    LiquidGlassBackground(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Tiêu đề
            item {
                Column {
                    Text(
                        text = "Thống kê chi tiêu",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassThemeColors.PrimaryText
                    )
                    Text(
                        text = "Báo cáo tài chính tháng $currentMonthStr",
                        fontSize = 13.sp,
                        color = GlassThemeColors.SecondaryText
                    )
                }
            }

            // Hộp tổng kết thu chi thẻ trong tháng
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    backgroundColor = Color.White.copy(alpha = 0.45f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Tổng chi tiêu tháng này",
                                fontSize = 12.sp,
                                color = GlassThemeColors.SecondaryText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = FormatUtils.formatCurrency(totalMonthlyExpense),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GlassThemeColors.Accent
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Đã thanh toán dư nợ",
                                fontSize = 12.sp,
                                color = GlassThemeColors.SecondaryText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = FormatUtils.formatCurrency(totalMonthlyPayment),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GlassThemeColors.Success
                            )
                        }
                    }
                }
            }

            // BIỂU ĐỒ CỘT TỰ VẼ BẰNG COMPOSE (Không dùng thư viện chart)
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp
                ) {
                    Text(
                        text = "BIỂU ĐỒ CHI TIÊU THEO DANH MỤC",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassThemeColors.PrimaryText,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val maxAmount = monthlyExpenses.values.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0

                    // Biểu đồ cột đứng tự vẽ bằng Jetpack Compose
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        monthlyExpenses.forEach { (cat, amount) ->
                            val heightRatio = (amount / maxAmount).toFloat().coerceIn(0.05f, 1f)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Cột màu
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight(fraction = heightRatio)
                                        .width(28.dp)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    GlassThemeColors.Accent,
                                                    GlassThemeColors.AccentSecondary.copy(alpha = 0.7f)
                                                )
                                            )
                                        )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Tên danh mục rút gọn
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = GlassThemeColors.SecondaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.5f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Danh sách chi tiết danh mục kèm thanh tiến trình ngang
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        monthlyExpenses.forEach { (category, amount) ->
                            val percent = if (totalMonthlyExpense > 0) {
                                ((amount / totalMonthlyExpense) * 100).toInt()
                            } else 0

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "$category ($percent%)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GlassThemeColors.PrimaryText
                                    )
                                    Text(
                                        text = FormatUtils.formatCurrency(amount),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GlassThemeColors.Accent
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.5f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction = (percent / 100f).coerceIn(0f, 1f))
                                            .fillMaxHeight()
                                            .clip(CircleShape)
                                            .background(GlassThemeColors.Accent)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // TỶ LỆ DƯ NỢ / HẠN MỨC
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp
                ) {
                    Text(
                        text = "TỶ LỆ SỬ DỤNG HẠN MỨC TÍN DỤNG",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlassThemeColors.PrimaryText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$debtRatio% đã dùng",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (debtRatio > 50) GlassThemeColors.Danger else GlassThemeColors.Success
                        )

                        GlassBadge(
                            text = if (debtRatio <= 30) "Mức an toàn (< 30%)" else "Cần chú ý trả bớt",
                            backgroundColor = if (debtRatio <= 30) GlassThemeColors.Success.copy(alpha = 0.15f) else GlassThemeColors.Danger.copy(alpha = 0.15f),
                            textColor = if (debtRatio <= 30) GlassThemeColors.Success else GlassThemeColors.Danger
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (debtRatio <= 30) {
                            "💡 Tỷ lệ sử dụng hạn mức dưới 30% giúp điểm tín dụng của bạn luôn ở mức xuất sắc!"
                        } else {
                            "⚠️ Dư nợ trên 30% hạn mức có thể ảnh hưởng đến điểm tín dụng. Hãy cân nhắc thanh toán sớm."
                        },
                        fontSize = 12.sp,
                        color = GlassThemeColors.SecondaryText
                    )
                }
            }
        }
    }
}

// ==========================================
// 6. MÀN HÌNH CÀI ĐẶT (SettingsScreen)
// ==========================================

@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current
    var showLogoutConfirm by remember { mutableStateOf(false) }

    LiquidGlassBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Cài đặt & Tài khoản",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = GlassThemeColors.PrimaryText
            )

            // Thẻ thông tin cá nhân
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(GlassThemeColors.Accent.copy(alpha = 0.2f))
                            .border(1.dp, GlassThemeColors.Accent.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.fullName?.take(1) ?: "U").uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassThemeColors.Accent
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = currentUser?.fullName ?: "Người dùng",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassThemeColors.PrimaryText
                        )
                        Text(
                            text = currentUser?.email ?: "email@domain.com",
                            fontSize = 13.sp,
                            color = GlassThemeColors.SecondaryText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tham gia: ${currentUser?.createdAt?.let { FormatUtils.formatDate(it) } ?: "Hôm nay"}",
                            fontSize = 11.sp,
                            color = GlassThemeColors.SecondaryText
                        )
                    }
                }
            }

            // Thẻ quản lý tính năng thông báo nhắc hạn (WorkManager)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Nhắc hạn thanh toán tự động",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlassThemeColors.PrimaryText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "WorkManager chạy ngầm mỗi ngày 1 lần. Tự động thông báo khi còn 3 ngày và 1 ngày đến hạn.",
                            fontSize = 12.sp,
                            color = GlassThemeColors.SecondaryText
                        )
                    }

                    GlassBadge(
                        text = "Đang bật",
                        backgroundColor = GlassThemeColors.Success.copy(alpha = 0.15f),
                        textColor = GlassThemeColors.Success
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                GlassButton(
                    text = "🔔 Thử bắn thông báo ngay",
                    onClick = {
                        ReminderWorker.triggerTestNotification(context)
                        Toast.makeText(context, "Đã gửi thông báo kiểm tra!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = false
                )
            }

            // Thẻ thông tin ứng dụng
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 24.dp
            ) {
                Text(
                    text = "ỨNG DỤNG QUẢN LÝ THẺ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlassThemeColors.SecondaryText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Phiên bản: 1.0.0 (Bảo mật Local)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GlassThemeColors.PrimaryText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Không cần Internet - Hoàn toàn không backend\n• Mật khẩu băm SHA-256 + Salt an toàn\n• Cơ sở dữ liệu SQLite Room lưu nội bộ trên máy",
                    fontSize = 12.sp,
                    color = GlassThemeColors.SecondaryText,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nút đăng xuất
            GlassButton(
                text = "Đăng xuất tài khoản",
                onClick = { showLogoutConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = true
            )

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Dialog xác nhận đăng xuất
        if (showLogoutConfirm) {
            GlassDialog(
                onDismissRequest = { showLogoutConfirm = false },
                title = "Đăng xuất tài khoản?"
            ) {
                Text(
                    text = "Bạn có chắc chắn muốn đăng xuất? Dữ liệu thẻ và giao dịch của bạn vẫn được lưu an toàn trên máy.",
                    fontSize = 14.sp,
                    color = GlassThemeColors.SecondaryText
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    GlassButton(
                        text = "Huỷ",
                        onClick = { showLogoutConfirm = false },
                        isPrimary = false
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    GlassButton(
                        text = "Đăng xuất",
                        onClick = {
                            showLogoutConfirm = false
                            viewModel.logout()
                        },
                        isPrimary = true
                    )
                }
            }
        }
    }
}

// ==========================================
// HỘP THOẠI THÊM / SỬA THẺ TÍN DỤNG
// ==========================================

@Composable
fun AddOrEditCardDialog(
    viewModel: AppViewModel,
    cardToEdit: CreditCard?,
    onDismiss: () -> Unit
) {
    val isEdit = cardToEdit != null
    var cardName by remember { mutableStateOf(cardToEdit?.cardName ?: "") }
    var bankName by remember { mutableStateOf(cardToEdit?.bankName ?: "") }
    var last4Digits by remember { mutableStateOf(cardToEdit?.last4Digits ?: "") }
    var creditLimitStr by remember { mutableStateOf(cardToEdit?.creditLimit?.toLong()?.toString() ?: "") }
    var statementDayStr by remember { mutableStateOf(cardToEdit?.statementDay?.toString() ?: "20") }
    var dueDayStr by remember { mutableStateOf(cardToEdit?.dueDay?.toString() ?: "5") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    GlassDialog(
        onDismissRequest = onDismiss,
        title = if (isEdit) "Chỉnh sửa thẻ tín dụng" else "Thêm thẻ tín dụng mới"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassTextField(
                value = cardName,
                onValueChange = { cardName = it },
                label = "Tên thẻ",
                placeholder = "Ví dụ: Visa Cashback, Platinum...",
                modifier = Modifier.fillMaxWidth()
            )

            GlassTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = "Ngân hàng",
                placeholder = "Ví dụ: VPBank, Techcombank, VIB...",
                modifier = Modifier.fillMaxWidth()
            )

            GlassTextField(
                value = last4Digits,
                onValueChange = { if (it.length <= 4) last4Digits = it },
                label = "4 số cuối in trên thẻ",
                placeholder = "Ví dụ: 8899",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            GlassTextField(
                value = creditLimitStr,
                onValueChange = { creditLimitStr = it },
                label = "Hạn mức được cấp (₫)",
                placeholder = "Ví dụ: 50000000",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassTextField(
                    value = statementDayStr,
                    onValueChange = { statementDayStr = it },
                    label = "Ngày sao kê (1-31)",
                    placeholder = "20",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                GlassTextField(
                    value = dueDayStr,
                    onValueChange = { dueDayStr = it },
                    label = "Ngày đến hạn (1-31)",
                    placeholder = "5",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage ?: "",
                    color = GlassThemeColors.Danger,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                GlassButton(
                    text = "Huỷ",
                    onClick = onDismiss,
                    isPrimary = false
                )
                Spacer(modifier = Modifier.width(12.dp))
                GlassButton(
                    text = if (isEdit) "Lưu thay đổi" else "Thêm thẻ",
                    onClick = {
                        val limit = creditLimitStr.toDoubleOrNull() ?: 0.0
                        val stDay = statementDayStr.toIntOrNull() ?: 0
                        val dDay = dueDayStr.toIntOrNull() ?: 0

                        if (isEdit && cardToEdit != null) {
                            val updated = cardToEdit.copy(
                                cardName = cardName,
                                bankName = bankName,
                                last4Digits = last4Digits,
                                creditLimit = limit,
                                statementDay = stDay,
                                dueDay = dDay
                            )
                            viewModel.updateCard(updated) { success, msg ->
                                if (success) {
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                } else {
                                    errorMessage = msg
                                }
                            }
                        } else {
                            viewModel.addCard(
                                cardName = cardName,
                                bankName = bankName,
                                last4Digits = last4Digits,
                                creditLimit = limit,
                                statementDay = stDay,
                                dueDay = dDay
                            ) { success, msg ->
                                if (success) {
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                } else {
                                    errorMessage = msg
                                }
                            }
                        }
                    },
                    isPrimary = true
                )
            }
        }
    }
}

// ==========================================
// HỘP THOẠI GHI NHẬN GIAO DỊCH
// ==========================================

@Composable
fun AddTransactionDialog(
    viewModel: AppViewModel,
    cards: List<CreditCard>,
    onDismiss: () -> Unit
) {
    if (cards.isEmpty()) {
        GlassDialog(onDismissRequest = onDismiss, title = "Chưa có thẻ tín dụng") {
            Text(
                text = "Bạn cần thêm ít nhất 1 thẻ tín dụng trước khi ghi nhận chi tiêu hoặc thanh toán.",
                fontSize = 14.sp,
                color = GlassThemeColors.SecondaryText
            )
            Spacer(modifier = Modifier.height(20.dp))
            GlassButton(text = "Đóng", onClick = onDismiss, modifier = Modifier.fillMaxWidth())
        }
        return
    }

    var selectedCardId by remember { mutableStateOf(cards.first().id) }
    var txnType by remember { mutableStateOf("CHI_TIEU") } // "CHI_TIEU" hoặc "THANH_TOAN"
    val categories = listOf("Ăn uống", "Mua sắm", "Di chuyển", "Hoá đơn", "Khác")
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var amountStr by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    GlassDialog(
        onDismissRequest = onDismiss,
        title = "Ghi nhận giao dịch mới"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Loại giao dịch: Chi tiêu vs Thanh toán
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.4f))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (txnType == "CHI_TIEU") GlassThemeColors.Accent else Color.Transparent)
                        .clickable { txnType = "CHI_TIEU" }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chi tiêu quẹt thẻ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (txnType == "CHI_TIEU") Color.White else GlassThemeColors.SecondaryText
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (txnType == "THANH_TOAN") GlassThemeColors.Success else Color.Transparent)
                        .clickable { txnType = "THANH_TOAN" }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Thanh toán dư nợ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (txnType == "THANH_TOAN") Color.White else GlassThemeColors.SecondaryText
                    )
                }
            }

            // Chọn thẻ
            Text(
                text = "Chọn thẻ áp dụng",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = GlassThemeColors.PrimaryText
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cards) { card ->
                    val isSelected = card.id == selectedCardId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) GlassThemeColors.Accent.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.5f))
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) GlassThemeColors.Accent else Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedCardId = card.id }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${card.bankName} (•••• ${card.last4Digits})",
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) GlassThemeColors.Accent else GlassThemeColors.PrimaryText
                        )
                    }
                }
            }

            // Nếu là Chi tiêu thì cho chọn danh mục
            if (txnType == "CHI_TIEU") {
                Text(
                    text = "Danh mục chi tiêu",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GlassThemeColors.PrimaryText
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = cat == selectedCategory
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) GlassThemeColors.Accent else Color.White.copy(alpha = 0.5f))
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cat,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else GlassThemeColors.PrimaryText
                            )
                        }
                    }
                }
            }

            // Số tiền
            GlassTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = "Số tiền (₫)",
                placeholder = "Ví dụ: 250000",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Ghi chú
            GlassTextField(
                value = note,
                onValueChange = { note = it },
                label = "Ghi chú (tuỳ chọn)",
                placeholder = if (txnType == "CHI_TIEU") "Ví dụ: Ăn tối cùng bạn bè" else "Ví dụ: Thanh toán dư nợ sao kê tháng 10",
                modifier = Modifier.fillMaxWidth()
            )

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage ?: "",
                    color = GlassThemeColors.Danger,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                GlassButton(text = "Huỷ", onClick = onDismiss, isPrimary = false)
                Spacer(modifier = Modifier.width(12.dp))
                GlassButton(
                    text = "Lưu giao dịch",
                    onClick = {
                        val amount = amountStr.toDoubleOrNull() ?: 0.0
                        viewModel.addTransaction(
                            cardId = selectedCardId,
                            amount = amount,
                            type = txnType,
                            category = selectedCategory,
                            note = note
                        ) { success, msg ->
                            if (success) {
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                onDismiss()
                            } else {
                                errorMessage = msg
                            }
                        }
                    },
                    isPrimary = true
                )
            }
        }
    }
}
