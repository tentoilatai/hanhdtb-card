package com.kaiz.quanlythe

import android.content.Context
import android.util.Base64
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ==========================================
// 1. ENTITIES (Thực thể Room Database)
// ==========================================

/**
 * Bảng Người dùng: lưu thông tin tài khoản local, mật khẩu băm SHA-256 kèm muối (salt)
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Bảng Thẻ tín dụng: gắn với từng người dùng qua userId
 */
@Entity(
    tableName = "credit_cards",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class CreditCard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val cardName: String,     // Ví dụ: Visa Cashback, Platinum Card
    val bankName: String,     // Ví dụ: VPBank, Techcombank, VIB
    val last4Digits: String,  // 4 số cuối: 1234
    val creditLimit: Double,  // Hạn mức: 50.000.000 ₫
    val statementDay: Int,    // Ngày sao kê: 1 - 31
    val dueDay: Int,          // Ngày đến hạn thanh toán: 1 - 31
    val cardColorHex: String = "#FF6FA5" // Màu nhấn giao diện thẻ
)

/**
 * Bảng Giao dịch: Ghi nhận chi tiêu hoặc thanh toán dư nợ
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CreditCard::class,
            parentColumns = ["id"],
            childColumns = ["cardId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cardId"), Index("userId")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val cardId: Long,
    val amount: Double,
    val type: String,        // "CHI_TIEU" (chi tiêu) hoặc "THANH_TOAN" (thanh toán dư nợ)
    val category: String,    // "Ăn uống", "Mua sắm", "Di chuyển", "Hoá đơn", "Khác", "Thanh toán thẻ"
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// ==========================================
// 2. DAOs (Data Access Objects)
// ==========================================

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserByIdFlow(id: Long): Flow<User?>
}

@Dao
interface CardDao {
    @Query("SELECT * FROM credit_cards WHERE userId = :userId ORDER BY id DESC")
    fun getCardsByUserId(userId: Long): Flow<List<CreditCard>>

    @Query("SELECT * FROM credit_cards WHERE id = :cardId LIMIT 1")
    suspend fun getCardById(cardId: Long): CreditCard?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CreditCard): Long

    @Update
    suspend fun updateCard(card: CreditCard)

    @Delete
    suspend fun deleteCard(card: CreditCard)

    // Truy vấn đồng bộ dùng cho WorkManager chạy nền
    @Query("SELECT * FROM credit_cards")
    fun getAllCardsSync(): List<CreditCard>
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getTransactionsByUserId(userId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE cardId = :cardId ORDER BY timestamp DESC")
    fun getTransactionsByCardId(cardId: Long): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}

// ==========================================
// 3. ROOM DATABASE (Local SQLite)
// ==========================================

@Database(
    entities = [User::class, CreditCard::class, Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cardDao(): CardDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quan_ly_the.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ==========================================
// 4. TIỆN ÍCH BẢO MẬT & ĐỊNH DẠNG
// ==========================================

object SecurityUtils {
    /**
     * Tạo chuỗi muối (Salt) ngẫu nhiên 16 bytes bằng SecureRandom
     */
    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return Base64.encodeToString(saltBytes, Base64.NO_WRAP)
    }

    /**
     * Băm mật khẩu bằng SHA-256 kết hợp chuỗi muối (Salt)
     */
    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt.toByteArray(Charsets.UTF_8))
        val digest = md.digest(password.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}

object FormatUtils {
    /**
     * Định dạng tiền tệ chuẩn Việt Nam: 1.500.000 ₫
     */
    fun formatCurrency(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }
        val formatter = DecimalFormat("#,##0", symbols)
        return "${formatter.format(amount)} ₫"
    }

    /**
     * Định dạng ngày dd/MM/yyyy
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("vi", "VN"))
        return sdf.format(Date(timestamp))
    }

    /**
     * Tính số ngày còn lại đến ngày đến hạn tiếp theo (dueDay: 1..31)
     */
    fun daysUntilNextDue(dueDay: Int): Int {
        val today = Calendar.getInstance()
        val todayMidnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val dueCal = Calendar.getInstance().apply {
            val maxDayThisMonth = getActualMaximum(Calendar.DAY_OF_MONTH)
            set(Calendar.DAY_OF_MONTH, dueDay.coerceAtMost(maxDayThisMonth))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Nếu ngày đến hạn trong tháng này đã qua, dời sang tháng sau
        if (dueCal.before(todayMidnight)) {
            dueCal.add(Calendar.MONTH, 1)
            val maxDayNextMonth = dueCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            dueCal.set(Calendar.DAY_OF_MONTH, dueDay.coerceAtMost(maxDayNextMonth))
        }

        val diffMillis = dueCal.timeInMillis - todayMidnight.timeInMillis
        val days = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        return days.coerceAtLeast(0)
    }

    /**
     * Mô tả tiếng Việt số ngày còn lại
     */
    fun getDueStatusText(daysLeft: Int): String {
        return when (daysLeft) {
            0 -> "Hôm nay đến hạn!"
            1 -> "Còn 1 ngày nữa!"
            2 -> "Còn 2 ngày nữa"
            3 -> "Còn 3 ngày nữa"
            else -> "Còn $daysLeft ngày"
        }
    }
}
