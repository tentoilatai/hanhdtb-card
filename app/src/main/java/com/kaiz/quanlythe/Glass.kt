package com.kaiz.quanlythe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ripple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// ==========================================
// 1. MÀU SẮC & THEME HỒNG PASTEL + LIQUID GLASS
// ==========================================

object GlassThemeColors {
    // Gradient nền dọc
    val BackgroundTop = Color(0xFFFFE4EC)
    val BackgroundMiddle = Color(0xFFFFD1E3)
    val BackgroundBottom = Color(0xFFF8C8DC)

    // Màu chữ & màu nhấn theo yêu cầu
    val Accent = Color(0xFFFF6FA5)           // Màu nhấn chính
    val AccentSecondary = Color(0xFFFF8FB7)  // Hồng phấn phụ
    val AccentGradientEnd = Color(0xFFFF4D8F)
    val PrimaryText = Color(0xFF4A2C3A)      // Chữ chính (nâu hồng đậm)
    val SecondaryText = Color(0xFF8E6B7D)    // Chữ phụ (nâu tím nhạt)
    val CardBackground = Color.White.copy(alpha = 0.35f) // Kính trong suốt
    val CardBackgroundStrong = Color.White.copy(alpha = 0.55f)

    // Màu vòng tròn mờ trang trí phía sau (Liquid Blobs)
    val BlobPink = Color(0xFFFF9EB5)
    val BlobPurple = Color(0xFFD4A5FF)
    val BlobRose = Color(0xFFFFB6C1)
    val BlobWarm = Color(0xFFFFC6D9)

    // Trạng thái / Cảnh báo
    val Success = Color(0xFF2E7D32)
    val Warning = Color(0xFFE65100)
    val Danger = Color(0xFFD32F2F)

    // Viền kính gradient 1dp (alpha 0.7 -> 0.2)
    val GlassBorder = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.75f),
            Color.White.copy(alpha = 0.20f)
        )
    )

    // Viền kính ngang cho nút / thẻ đặc biệt
    val GlassBorderHorizontal = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.85f),
            Color.White.copy(alpha = 0.25f)
        )
    )
}

/**
 * Cung cấp ColorScheme sáng cố định (không bị đổi màu khi máy bật Dark Mode)
 */
@Composable
fun AppGlassTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = GlassThemeColors.Accent,
        onPrimary = Color.White,
        secondary = GlassThemeColors.SecondaryText,
        onSecondary = Color.White,
        background = GlassThemeColors.BackgroundTop,
        onBackground = GlassThemeColors.PrimaryText,
        surface = Color.White.copy(alpha = 0.85f),
        onSurface = GlassThemeColors.PrimaryText
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

// ==========================================
// 2. NỀN GRADIENT LIQUID GLASS KÈM CÁC VÒNG TRÒN MỜ PHÍA SAU
// ==========================================

@Composable
fun LiquidGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GlassThemeColors.BackgroundTop,
                        GlassThemeColors.BackgroundMiddle,
                        GlassThemeColors.BackgroundBottom
                    )
                )
            )
    ) {
        // Vòng tròn mờ 1: góc trên bên phải
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = 180.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GlassThemeColors.BlobPink.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Vòng tròn mờ 2: giữa bên trái
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = (-80).dp, y = 220.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GlassThemeColors.BlobPurple.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Vòng tròn mờ 3: dưới góc phải
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 90.dp, y = 80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GlassThemeColors.BlobRose.copy(alpha = 0.45f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Vòng tròn mờ 4: góc trên bên trái
        Box(
            modifier = Modifier
                .size(170.dp)
                .offset(x = (-30).dp, y = 50.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            GlassThemeColors.BlobWarm.copy(alpha = 0.40f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Nội dung chính hiển thị sắc nét phía trên các blob kính
        content()
    }
}

// ==========================================
// 3. THẺ KÍNH (GlassCard)
// ==========================================

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    backgroundColor: Color = GlassThemeColors.CardBackground,
    borderBrush: Brush = GlassThemeColors.GlassBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = GlassThemeColors.Accent.copy(alpha = 0.2f)),
            onClick = onClick
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = GlassThemeColors.Accent.copy(alpha = 0.12f),
                spotColor = GlassThemeColors.Accent.copy(alpha = 0.18f)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
            .then(clickableModifier)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

// ==========================================
// 4. Ô NHẬP LIỆU KÍNH (GlassTextField)
// ==========================================

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true
) {
    val shape = RoundedCornerShape(20.dp)

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = GlassThemeColors.PrimaryText,
            modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = shape,
                    ambientColor = GlassThemeColors.Accent.copy(alpha = 0.08f),
                    spotColor = GlassThemeColors.Accent.copy(alpha = 0.12f)
                )
                .background(Color.White.copy(alpha = 0.50f), shape),
            shape = shape,
            placeholder = {
                Text(
                    text = placeholder,
                    color = GlassThemeColors.SecondaryText.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            textStyle = TextStyle(
                color = GlassThemeColors.PrimaryText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GlassThemeColors.Accent,
                unfocusedBorderColor = Color.White.copy(alpha = 0.65f),
                errorBorderColor = GlassThemeColors.Danger,
                cursorColor = GlassThemeColors.Accent
            )
        )

        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = GlassThemeColors.Danger,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

// ==========================================
// 5. NÚT BẤM KÍNH (GlassButton)
// ==========================================

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    icon: @Composable (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(24.dp)

    if (isPrimary) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = shape,
            modifier = modifier
                .height(52.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = shape,
                    ambientColor = GlassThemeColors.Accent.copy(alpha = 0.35f),
                    spotColor = GlassThemeColors.Accent.copy(alpha = 0.45f)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = GlassThemeColors.Accent,
                contentColor = Color.White,
                disabledContainerColor = GlassThemeColors.Accent.copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            ),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    } else {
        // Nút phụ kính trong suốt
        Box(
            modifier = modifier
                .height(52.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = shape,
                    ambientColor = GlassThemeColors.Accent.copy(alpha = 0.1f),
                    spotColor = GlassThemeColors.Accent.copy(alpha = 0.15f)
                )
                .clip(shape)
                .background(Color.White.copy(alpha = 0.50f))
                .border(width = 1.dp, brush = GlassThemeColors.GlassBorder, shape = shape)
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = GlassThemeColors.Accent.copy(alpha = 0.2f)),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GlassThemeColors.PrimaryText
                )
            }
        }
    }
}

// ==========================================
// 6. HUY HIỆU / NHÃN KÍNH (GlassBadge)
// ==========================================

@Composable
fun GlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = GlassThemeColors.Accent.copy(alpha = 0.15f),
    textColor: Color = GlassThemeColors.Accent,
    icon: @Composable (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(width = 0.5.dp, color = textColor.copy(alpha = 0.35f), shape = shape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

// ==========================================
// 7. HỘP THOẠI KÍNH (GlassDialog)
// ==========================================

@Composable
fun GlassDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            cornerRadius = 28.dp,
            backgroundColor = Color.White.copy(alpha = 0.90f) // Trong mờ vừa đủ đọc trong Dialog
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GlassThemeColors.PrimaryText,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}
