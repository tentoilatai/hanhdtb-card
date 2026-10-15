package com.kaiz.quanlythe

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Worker chạy ngầm mỗi ngày 1 lần bằng WorkManager để quét các thẻ sắp đến hạn thanh toán
 * và bắn thông báo (3 ngày trước hạn, 1 ngày trước hạn, và ngày đến hạn).
 */
class ReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            createNotificationChannel(context)
            val db = AppDatabase.getDatabase(context)
            val cards = db.cardDao().getAllCardsSync()

            for (card in cards) {
                val daysLeft = FormatUtils.daysUntilNextDue(card.dueDay)

                // Chỉ gửi thông báo khi còn 3 ngày, 1 ngày hoặc hôm nay
                if (daysLeft == 3 || daysLeft == 1 || daysLeft == 0) {
                    sendDueNotification(context, card, daysLeft)
                }
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        const val CHANNEL_ID = "channel_card_due_reminder"
        const val CHANNEL_NAME = "Nhắc hạn thanh toán thẻ"
        const val WORK_NAME = "DAILY_CARD_REMINDER_WORK"

        /**
         * Tạo Notification Channel cho Android 8.0 (API 26+)
         */
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Thông báo nhắc nhở ngày đến hạn thanh toán thẻ tín dụng"
                    enableVibration(true)
                }
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        /**
         * Lên lịch chạy Worker mỗi 24 giờ một lần bằng WorkManager
         */
        fun scheduleDailyReminder(context: Context) {
            createNotificationChannel(context)

            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            val dailyWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                repeatInterval = 24,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setInitialDelay(15, TimeUnit.MINUTES) // Khởi chạy nhẹ nhàng sau 15 phút
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWorkRequest
            )
        }

        /**
         * Bắn thông báo nhắc hạn cho một thẻ cụ thể
         */
        fun sendDueNotification(context: Context, card: CreditCard, daysLeft: Int) {
            val title = when (daysLeft) {
                0 -> "🚨 HÔM NAY ĐẾN HẠN: ${card.cardName}"
                1 -> "⚠️ KHẨN CẤP: ${card.cardName} đến hạn ngày mai!"
                3 -> "🔔 NHẮC NHỞ: ${card.cardName} còn 3 ngày nữa đến hạn!"
                else -> "📅 Nhắc hạn thẻ: ${card.cardName}"
            }

            val message = when (daysLeft) {
                0 -> "Hôm nay là ngày đến hạn thanh toán thẻ ${card.cardName} (${card.bankName} - đuôi ${card.last4Digits}). Hãy thanh toán để tránh phát sinh lãi!"
                1 -> "Thẻ ${card.cardName} (${card.bankName}) sẽ đến hạn vào ngày mai. Hãy kiểm tra sao kê và chuẩn bị thanh toán nhé."
                3 -> "Thẻ ${card.cardName} (${card.bankName}) còn 3 ngày nữa đến hạn thanh toán (ngày ${card.dueDay})."
                else -> "Thẻ ${card.cardName} còn $daysLeft ngày nữa đến hạn."
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent = PendingIntent.getActivity(
                context,
                card.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val notificationManager = NotificationManagerCompat.from(context)
            try {
                notificationManager.notify((card.id * 100 + daysLeft).toInt(), notification)
            } catch (e: SecurityException) {
                // Người dùng chưa cấp quyền POST_NOTIFICATIONS
                e.printStackTrace()
            }
        }

        /**
         * Gửi thông báo mẫu thử nghiệm ngay lập tức (dùng khi bấm nút trong Cài đặt)
         */
        fun triggerTestNotification(context: Context) {
            createNotificationChannel(context)
            val demoCard = CreditCard(
                id = 9999,
                userId = 1,
                cardName = "Visa Platinum",
                bankName = "Techcombank",
                last4Digits = "8899",
                creditLimit = 50000000.0,
                statementDay = 20,
                dueDay = 5
            )
            sendDueNotification(context, demoCard, 1)
        }
    }
}
