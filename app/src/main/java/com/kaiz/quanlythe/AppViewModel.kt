package com.kaiz.quanlythe

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class UpcomingDueInfo(
    val card: CreditCard,
    val daysLeft: Int,
    val balance: Double
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val prefs = application.getSharedPreferences("app_user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "logged_in_user_id"
    }

    // ID người dùng đang đăng nhập
    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId = _currentUserId.asStateFlow()

    // Người dùng hiện tại
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    // Trạng thái đã đăng nhập hay chưa
    val isLoggedIn: StateFlow<Boolean> = _currentUserId
        .map { it != null && it > 0 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        // Tải trạng thái đăng nhập từ SharedPreferences
        val savedUserId = prefs.getLong(KEY_USER_ID, -1L)
        if (savedUserId > 0) {
            _currentUserId.value = savedUserId
            loadUserDetails(savedUserId)
        }
    }

    private fun loadUserDetails(userId: Long) {
        viewModelScope.launch {
            val user = db.userDao().getUserById(userId)
            _currentUser.value = user
        }
    }

    // ==========================================
    // DỮ LIỆU CỦA NGƯỜI DÙNG ĐANG ĐĂNG NHẬP
    // ==========================================

    @OptIn(ExperimentalCoroutinesApi::class)
    val cards: StateFlow<List<CreditCard>> = _currentUserId
        .flatMapLatest { uid ->
            if (uid != null && uid > 0) {
                db.cardDao().getCardsByUserId(uid)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: StateFlow<List<Transaction>> = _currentUserId
        .flatMapLatest { uid ->
            if (uid != null && uid > 0) {
                db.transactionDao().getTransactionsByUserId(uid)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dư nợ của từng thẻ: Map<CardId, DưNợ> (Dư nợ = Tổng chi tiêu - Tổng thanh toán)
    val cardBalances: StateFlow<Map<Long, Double>> = combine(cards, transactions) { cardList, txnList ->
        val balances = mutableMapOf<Long, Double>()
        for (card in cardList) {
            val cardTxns = txnList.filter { it.cardId == card.id }
            val totalExpense = cardTxns.filter { it.type == "CHI_TIEU" }.sumOf { it.amount }
            val totalPayment = cardTxns.filter { it.type == "THANH_TOAN" }.sumOf { it.amount }
            val balance = (totalExpense - totalPayment).coerceAtLeast(0.0)
            balances[card.id] = balance
        }
        balances
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Hạn mức còn lại của từng thẻ
    val cardRemainingLimits: StateFlow<Map<Long, Double>> = combine(cards, cardBalances) { cardList, balances ->
        cardList.associate { card ->
            val debt = balances[card.id] ?: 0.0
            card.id to (card.creditLimit - debt).coerceAtLeast(0.0)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Tổng dư nợ tất cả các thẻ
    val totalDebt: StateFlow<Double> = cardBalances.map { map ->
        map.values.sum()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Tổng hạn mức tín dụng được cấp
    val totalLimit: StateFlow<Double> = cards.map { list ->
        list.sumOf { it.creditLimit }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Tổng hạn mức còn lại
    val totalRemainingLimit: StateFlow<Double> = combine(totalLimit, totalDebt) { limit, debt ->
        (limit - debt).coerceAtLeast(0.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Danh sách thẻ sắp đến hạn (trong vòng 7 ngày)
    val upcomingDueCards: StateFlow<List<UpcomingDueInfo>> = combine(cards, cardBalances) { cardList, balances ->
        cardList.map { card ->
            val days = FormatUtils.daysUntilNextDue(card.dueDay)
            val balance = balances[card.id] ?: 0.0
            UpcomingDueInfo(card, days, balance)
        }
            .filter { it.daysLeft <= 7 }
            .sortedBy { it.daysLeft }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Thống kê chi tiêu tháng hiện tại theo danh mục
    val monthlyCategoryExpenses: StateFlow<Map<String, Double>> = transactions.map { list ->
        val currentCalendar = Calendar.getInstance()
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        val currentYear = currentCalendar.get(Calendar.YEAR)

        val monthlyExpenses = list.filter { txn ->
            if (txn.type != "CHI_TIEU") return@filter false
            val cal = Calendar.getInstance().apply { timeInMillis = txn.timestamp }
            cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
        }

        val map = mutableMapOf<String, Double>()
        // Các danh mục chuẩn
        listOf("Ăn uống", "Mua sắm", "Di chuyển", "Hoá đơn", "Khác").forEach {
            map[it] = 0.0
        }

        for (txn in monthlyExpenses) {
            val cat = if (map.containsKey(txn.category)) txn.category else "Khác"
            map[cat] = (map[cat] ?: 0.0) + txn.amount
        }
        map
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val currentMonthTotalExpense: StateFlow<Double> = monthlyCategoryExpenses.map { it.values.sum() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val currentMonthTotalPayment: StateFlow<Double> = transactions.map { list ->
        val currentCalendar = Calendar.getInstance()
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        val currentYear = currentCalendar.get(Calendar.YEAR)

        list.filter { txn ->
            if (txn.type != "THANH_TOAN") return@filter false
            val cal = Calendar.getInstance().apply { timeInMillis = txn.timestamp }
            cal.get(Calendar.MONTH) == currentMonth && cal.get(Calendar.YEAR) == currentYear
        }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // ==========================================
    // 1. XÁC THỰC: ĐĂNG KÝ / ĐĂNG NHẬP / ĐĂNG XUẤT
    // ==========================================

    fun register(
        fullName: String,
        email: String,
        pass: String,
        confirmPass: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase()

        if (cleanName.isBlank()) {
            onResult(false, "Vui lòng nhập họ và tên!")
            return
        }
        if (cleanEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            onResult(false, "Email không hợp lệ!")
            return
        }
        if (pass.length < 6) {
            onResult(false, "Mật khẩu phải có tối thiểu 6 ký tự!")
            return
        }
        if (pass != confirmPass) {
            onResult(false, "Mật khẩu xác nhận không khớp!")
            return
        }

        viewModelScope.launch {
            try {
                val existing = db.userDao().getUserByEmail(cleanEmail)
                if (existing != null) {
                    onResult(false, "Email này đã được đăng ký tài khoản!")
                    return@launch
                }

                val salt = SecurityUtils.generateSalt()
                val passwordHash = SecurityUtils.hashPassword(pass, salt)
                val newUser = User(
                    fullName = cleanName,
                    email = cleanEmail,
                    passwordHash = passwordHash,
                    salt = salt
                )
                val newId = db.userDao().insertUser(newUser)

                // Lưu đăng nhập
                prefs.edit().putLong(KEY_USER_ID, newId).apply()
                _currentUserId.value = newId
                _currentUser.value = newUser.copy(id = newId)

                onResult(true, "Đăng ký thành công!")
            } catch (e: Exception) {
                onResult(false, "Đã có lỗi xảy ra: ${e.localizedMessage}")
            }
        }
    }

    fun login(
        email: String,
        pass: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) {
            onResult(false, "Vui lòng nhập email!")
            return
        }
        if (pass.isBlank()) {
            onResult(false, "Vui lòng nhập mật khẩu!")
            return
        }

        viewModelScope.launch {
            try {
                val user = db.userDao().getUserByEmail(cleanEmail)
                if (user == null) {
                    onResult(false, "Tài khoản không tồn tại trên máy này!")
                    return@launch
                }

                val enteredHash = SecurityUtils.hashPassword(pass, user.salt)
                if (enteredHash != user.passwordHash) {
                    onResult(false, "Mật khẩu không chính xác!")
                    return@launch
                }

                // Lưu trạng thái đăng nhập
                prefs.edit().putLong(KEY_USER_ID, user.id).apply()
                _currentUserId.value = user.id
                _currentUser.value = user

                onResult(true, "Đăng nhập thành công!")
            } catch (e: Exception) {
                onResult(false, "Lỗi đăng nhập: ${e.localizedMessage}")
            }
        }
    }

    fun logout() {
        prefs.edit().remove(KEY_USER_ID).apply()
        _currentUserId.value = null
        _currentUser.value = null
    }

    // ==========================================
    // 2. QUẢN LÝ THẺ TÍN DỤNG
    // ==========================================

    fun addCard(
        cardName: String,
        bankName: String,
        last4Digits: String,
        creditLimit: Double,
        statementDay: Int,
        dueDay: Int,
        cardColorHex: String = "#FF6FA5",
        onResult: (Boolean, String?) -> Unit
    ) {
        val uid = _currentUserId.value
        if (uid == null || uid <= 0) {
            onResult(false, "Chưa đăng nhập!")
            return
        }
        if (cardName.isBlank()) {
            onResult(false, "Vui lòng nhập tên thẻ!")
            return
        }
        if (bankName.isBlank()) {
            onResult(false, "Vui lòng nhập tên ngân hàng!")
            return
        }
        if (last4Digits.length != 4 || !last4Digits.all { it.isDigit() }) {
            onResult(false, "4 số cuối thẻ phải đúng 4 chữ số!")
            return
        }
        if (creditLimit <= 0) {
            onResult(false, "Hạn mức tín dụng phải lớn hơn 0 ₫!")
            return
        }
        if (statementDay !in 1..31) {
            onResult(false, "Ngày sao kê phải từ ngày 1 đến 31!")
            return
        }
        if (dueDay !in 1..31) {
            onResult(false, "Ngày đến hạn phải từ ngày 1 đến 31!")
            return
        }

        viewModelScope.launch {
            val card = CreditCard(
                userId = uid,
                cardName = cardName.trim(),
                bankName = bankName.trim(),
                last4Digits = last4Digits.trim(),
                creditLimit = creditLimit,
                statementDay = statementDay,
                dueDay = dueDay,
                cardColorHex = cardColorHex
            )
            db.cardDao().insertCard(card)
            onResult(true, "Đã thêm thẻ thành công!")
        }
    }

    fun updateCard(
        card: CreditCard,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (card.cardName.isBlank()) {
            onResult(false, "Tên thẻ không được để trống!")
            return
        }
        if (card.last4Digits.length != 4 || !card.last4Digits.all { it.isDigit() }) {
            onResult(false, "4 số cuối phải đúng 4 chữ số!")
            return
        }
        if (card.creditLimit <= 0) {
            onResult(false, "Hạn mức phải lớn hơn 0 ₫!")
            return
        }
        if (card.statementDay !in 1..31 || card.dueDay !in 1..31) {
            onResult(false, "Ngày phải nằm trong khoảng từ 1 đến 31!")
            return
        }

        viewModelScope.launch {
            db.cardDao().updateCard(card)
            onResult(true, "Đã cập nhật thông tin thẻ!")
        }
    }

    fun deleteCard(card: CreditCard) {
        viewModelScope.launch {
            db.cardDao().deleteCard(card)
        }
    }

    // ==========================================
    // 3. GHI NHẬN GIAO DỊCH (CHI TIÊU / THANH TOÁN)
    // ==========================================

    fun addTransaction(
        cardId: Long,
        amount: Double,
        type: String, // "CHI_TIEU" hoặc "THANH_TOAN"
        category: String,
        note: String,
        timestamp: Long = System.currentTimeMillis(),
        onResult: (Boolean, String?) -> Unit
    ) {
        val uid = _currentUserId.value
        if (uid == null || uid <= 0) {
            onResult(false, "Chưa đăng nhập!")
            return
        }
        if (cardId <= 0) {
            onResult(false, "Vui lòng chọn thẻ tín dụng!")
            return
        }
        if (amount <= 0) {
            onResult(false, "Số tiền phải lớn hơn 0 ₫!")
            return
        }

        viewModelScope.launch {
            val txn = Transaction(
                userId = uid,
                cardId = cardId,
                amount = amount,
                type = type,
                category = if (type == "THANH_TOAN") "Thanh toán thẻ" else category,
                note = note.trim(),
                timestamp = timestamp
            )
            db.transactionDao().insertTransaction(txn)
            onResult(true, "Đã ghi nhận giao dịch thành công!")
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            db.transactionDao().deleteTransaction(transaction)
        }
    }
}
