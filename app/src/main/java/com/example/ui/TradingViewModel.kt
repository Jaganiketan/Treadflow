package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Order
import com.example.data.TradingRepository
import com.example.data.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TradingViewModel(private val repository: TradingRepository) : ViewModel() {

    // Auth States
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _registerSuccess = MutableStateFlow<Boolean>(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    // Form States (New Order)
    var stockSymbol = MutableStateFlow("")
    var stockName = MutableStateFlow("")
    var quantity = MutableStateFlow("")
    var targetPrice = MutableStateFlow("")
    var notes = MutableStateFlow("")
    var orderType = MutableStateFlow("BUY") // "BUY" or "SELL"

    private val _orderFormError = MutableStateFlow<String?>(null)
    val orderFormError: StateFlow<String?> = _orderFormError.asStateFlow()

    // Auto Execution Engine States
    val isAutoExecutionEnabled = MutableStateFlow(true)
    private val _engineActive = MutableStateFlow(true)
    val engineActive: StateFlow<Boolean> = _engineActive.asStateFlow()

    // Active Screen state: "login", "register", "dashboard", "admin"
    private val _currentScreen = MutableStateFlow("login")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Client lists observed reactively
    val currentUserOrders: StateFlow<List<Order>> = _currentUser
        .flatMapLatest { user ->
            if (user != null) {
                repository.getOrdersForUser(user.username)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin lists observed reactively
    val allUsers: StateFlow<List<User>> = repository.getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<Order>> = repository.getAllOrdersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined/Derived Metrics for current user dashboard
    val metrics = currentUserOrders.map { orders ->
        val total = orders.size
        val pending = orders.count { it.status == "Pending" }
        val executed = orders.count { it.status == "Executed" }
        val cancelled = orders.count { it.status == "Cancelled" }
        DashboardMetrics(total, pending, executed, cancelled)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardMetrics(0, 0, 0, 0)
    )

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    // Login Flow
    fun login(username: String) {
        viewModelScope.launch {
            _loginError.value = null
            val trimUser = username.trim().lowercase()
            if (trimUser.isEmpty()) {
                _loginError.value = "Username cannot be empty."
                return@launch
            }
            val user = repository.getUser(trimUser)
            if (user != null) {
                _currentUser.value = user
                _currentScreen.value = "dashboard"
            } else {
                _loginError.value = "User not found. Try 'owner' or register a new account."
            }
        }
    }

    // Register Flow
    fun register(
        username: String,
        email: String,
        phone: String,
        identityType: String,
        identityValue: String,
        brokerName: String,
        brokerClientId: String,
        brokerApiKey: String
    ) {
        viewModelScope.launch {
            _loginError.value = null
            _registerSuccess.value = false
            val trimUser = username.trim().lowercase()
            
            if (trimUser.isEmpty() || email.trim().isEmpty() || phone.trim().isEmpty() || identityValue.trim().isEmpty()) {
                _loginError.value = "Please fill in all mandatory fields."
                return@launch
            }

            // Check if username exists
            val existing = repository.getUser(trimUser)
            if (existing != null) {
                _loginError.value = "Username already exists."
                return@launch
            }

            val newUser = User(
                username = trimUser,
                email = email.trim(),
                phone = phone.trim(),
                identityProofType = identityType,
                identityProofValue = identityValue.trim(),
                brokerName = brokerName,
                brokerClientId = brokerClientId.trim(),
                brokerApiKey = brokerApiKey.trim(),
                isOwner = false
            )

            repository.insertUser(newUser)
            _registerSuccess.value = true
            // Auto login after registration
            _currentUser.value = newUser
            _currentScreen.value = "dashboard"
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = "login"
        _loginError.value = null
        _registerSuccess.value = false
        clearOrderForm()
    }

    // Order Operations
    fun selectOrderType(type: String) {
        orderType.value = type
    }

    fun placeOrder() {
        val user = _currentUser.value ?: return
        _orderFormError.value = null

        val symbol = stockSymbol.value.trim().uppercase()
        val name = stockName.value.trim()
        val qtyStr = quantity.value.trim()
        val priceStr = targetPrice.value.trim()

        if (symbol.isEmpty() || name.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            _orderFormError.value = "All fields marked with * are required."
            return
        }

        val qty = qtyStr.toIntOrNull()
        if (qty == null || qty <= 0) {
            _orderFormError.value = "Quantity must be a positive number."
            return
        }

        val price = priceStr.toDoubleOrNull()
        if (price == null || price <= 0) {
            _orderFormError.value = "Price must be a positive number."
            return
        }

        val displayName = if (user.isOwner) "Owner (Admin)" else user.username.replaceFirstChar { it.uppercase() }

        val newOrder = Order(
            userId = user.username,
            userDisplayName = displayName,
            stockSymbol = symbol,
            stockName = name,
            quantity = qty,
            targetPrice = price,
            notes = notes.value.trim(),
            orderType = orderType.value,
            status = "Pending" // Starts as Pending (After-Market Order Automation)
        )

        viewModelScope.launch {
            repository.insertOrder(newOrder)
            clearOrderForm()
        }
    }

    private fun clearOrderForm() {
        stockSymbol.value = ""
        stockName.value = ""
        quantity.value = ""
        targetPrice.value = ""
        notes.value = ""
        _orderFormError.value = null
    }

    // Manual execution trigger
    fun executeAllPendingOrders() {
        viewModelScope.launch {
            repository.executePendingOrders()
        }
    }

    fun cancelAllPendingOrders() {
        viewModelScope.launch {
            repository.cancelPendingOrders()
        }
    }

    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            repository.deleteOrderById(orderId)
        }
    }

    fun toggleAutoExecution(enabled: Boolean) {
        isAutoExecutionEnabled.value = enabled
        _engineActive.value = enabled
    }
}

data class DashboardMetrics(
    val total: Int,
    val pending: Int,
    val executed: Int,
    val cancelled: Int
)
