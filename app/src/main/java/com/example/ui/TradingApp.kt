package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Order
import com.example.data.User
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradingApp(viewModel: TradingViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundGray
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "login" -> LoginScreen(viewModel)
                "register" -> RegisterScreen(viewModel)
                "dashboard" -> DashboardScreen(viewModel)
                "admin" -> AdminScreen(viewModel)
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: TradingViewModel) {
    var usernameInput by remember { mutableStateOf("") }
    val loginError by viewModel.loginError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.TrendingUp,
                contentDescription = "Logo",
                tint = TradeGreen,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "TradeFlow",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                fontFamily = FontFamily.SansSerif
            )
        }
        Text(
            text = "After-Market Order Automation",
            fontSize = 14.sp,
            color = LightText,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome Back",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = "Enter your user ID to access your brokerage account and automate orders.",
                    fontSize = 12.sp,
                    color = LightText,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp, bottom = 20.dp)
                )

                OutlinedTextField(
                    value = usernameInput,
                    onValueChange = { usernameInput = it },
                    label = { Text("User ID / Username") },
                    placeholder = { Text("e.g. rahul_sharma") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = TradeGreen,
                        focusedLabelColor = TradeGreen
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "User Icon", tint = LightText)
                    }
                )

                if (loginError != null) {
                    Text(
                        text = loginError ?: "",
                        color = TradeRed,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.login(usernameInput) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TradeGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Login Now", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("New client?", color = LightText, fontSize = 14.sp)
                    TextButton(onClick = { viewModel.setScreen("register") }) {
                        Text("Register here", color = TradeGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Demo Quick Switcher Card for testing
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TradeBlueLight.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, TradeBlue.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = "Info", tint = TradeBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quick Access Demo Accounts:", fontWeight = FontWeight.Bold, color = TradeBlue, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("Tap on any pre-configured demo account to instantly log in:", fontSize = 12.sp, color = DarkText)
                Spacer(modifier = Modifier.height(8.dp))

                // Owner Account
                Button(
                    onClick = { viewModel.login("owner") },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkText),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👨‍💼 Owner (Full Admin view)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("User ID: owner", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Clients accounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.login("rahul_sharma") },
                        colors = ButtonDefaults.buttonColors(containerColor = TradeGreenLight),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Rahul (Zerodha)", color = TradeGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.login("priya_patel") },
                        colors = ButtonDefaults.buttonColors(containerColor = TradeGreenLight),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Priya (Groww)", color = TradeGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.login("vikram_singh") },
                        colors = ButtonDefaults.buttonColors(containerColor = TradeGreenLight),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Vikram (Upstox)", color = TradeGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: TradingViewModel) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var identityType by remember { mutableStateOf("PAN Card") }
    var identityValue by remember { mutableStateOf("") }
    var brokerName by remember { mutableStateOf("Zerodha (Kite)") }
    var brokerClientId by remember { mutableStateOf("") }
    var brokerApiKey by remember { mutableStateOf("") }

    val brokers = listOf("Zerodha (Kite)", "AngelOne", "Groww", "Upstox", "Kotak Securities", "HDFC Securities")
    val identityTypes = listOf("PAN Card", "Aadhaar Card", "Driving License", "Voter ID")
    
    var brokerDropdownExpanded by remember { mutableStateOf(false) }
    var identityDropdownExpanded by remember { mutableStateOf(false) }

    val loginError by viewModel.loginError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = { viewModel.setScreen("login") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkText)
            }
            Text("Register New Client", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkText)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Personal & Identity Details", fontWeight = FontWeight.Bold, color = DarkText, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Choose User ID *") },
                    placeholder = { Text("e.g. suresh_kumar") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address *") },
                    placeholder = { Text("e.g. suresh@gmail.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number *") },
                    placeholder = { Text("e.g. +91 98765 43210") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Identity Proof Dropdown
                ExposedDropdownMenuBox(
                    expanded = identityDropdownExpanded,
                    onExpandedChange = { identityDropdownExpanded = !identityDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = identityType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Identity Proof Type *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = identityDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = identityDropdownExpanded,
                        onDismissRequest = { identityDropdownExpanded = false }
                    ) {
                        identityTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    identityType = type
                                    identityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = identityValue,
                    onValueChange = { identityValue = it },
                    label = { Text("$identityType Number *") },
                    placeholder = { Text("Enter ID details") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Text("Broker Account Integration", fontWeight = FontWeight.Bold, color = DarkText, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                // Broker Dropdown
                ExposedDropdownMenuBox(
                    expanded = brokerDropdownExpanded,
                    onExpandedChange = { brokerDropdownExpanded = !brokerDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = brokerName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Your Broker *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brokerDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = brokerDropdownExpanded,
                        onDismissRequest = { brokerDropdownExpanded = false }
                    ) {
                        brokers.forEach { name ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                onClick = {
                                    brokerName = name
                                    brokerDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = brokerClientId,
                    onValueChange = { brokerClientId = it },
                    label = { Text("Broker Client ID") },
                    placeholder = { Text("e.g. BRK8290") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = brokerApiKey,
                    onValueChange = { brokerApiKey = it },
                    label = { Text("Broker API Key / Token") },
                    placeholder = { Text("e.g. api_key_xxxx") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                if (loginError != null) {
                    Text(
                        text = loginError ?: "",
                        color = TradeRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.register(
                            username, email, phone, identityType, identityValue,
                            brokerName, brokerClientId, brokerApiKey
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TradeGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Register & Connect Broker", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: TradingViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()
    val orders by viewModel.currentUserOrders.collectAsStateWithLifecycle()

    val stockSymbol by viewModel.stockSymbol.collectAsStateWithLifecycle()
    val stockName by viewModel.stockName.collectAsStateWithLifecycle()
    val quantity by viewModel.quantity.collectAsStateWithLifecycle()
    val targetPrice by viewModel.targetPrice.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val orderType by viewModel.orderType.collectAsStateWithLifecycle()
    val orderError by viewModel.orderFormError.collectAsStateWithLifecycle()

    val isAutoExecutionEnabled by viewModel.isAutoExecutionEnabled.collectAsStateWithLifecycle()
    val engineActive by viewModel.engineActive.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Premium Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Customized Logo
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(DarkText, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.TrendingUp,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "TradeFlow",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Text(
                        text = "After-Market Order Automation",
                        fontSize = 11.sp,
                        color = LightText
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Secure badge & UTC Time info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(TradeGreenLight, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure",
                        tint = TradeGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Secure",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TradeGreen
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Logout button
                IconButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = LightText)
                }
            }
        }

        // Welcome back Client Identity details (and button for Owner Admin Panel)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BorderGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, ${currentUser?.username?.replaceFirstChar { it.uppercase() } ?: "User"}!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = DarkText
                        )
                        Text(
                            text = "Connected via ${currentUser?.brokerName ?: "N/A"} (${currentUser?.brokerClientId ?: "N/A"})",
                            fontSize = 12.sp,
                            color = LightText
                        )
                    }

                    if (currentUser?.isOwner == true) {
                        Button(
                            onClick = { viewModel.setScreen("admin") },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkText),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Owner Panel", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Market Status Alert (matches photo pink warning banner)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TradeRedLight),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Clock",
                        tint = TradeRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Market Closed  NSE/BSE",
                        fontWeight = FontWeight.Bold,
                        color = TradeRed,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Market is closed. Opens at 9:15 AM IST",
                        color = TradeRed,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Metrics Grid (2 columns x 2 rows)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Orders",
                count = metrics.total,
                icon = Icons.Default.FormatListBulleted,
                iconColor = TradeBlue,
                bgColor = TradeBlueLight,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Pending",
                count = metrics.pending,
                icon = Icons.Default.Schedule,
                iconColor = TradeAmber,
                bgColor = TradeAmberLight,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Executed",
                count = metrics.executed,
                icon = Icons.Default.CheckCircle,
                iconColor = TradeGreen,
                bgColor = TradeGreenLight,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Cancelled",
                count = metrics.cancelled, // In ViewModel metrics, cancelled count is `cancelled`
                icon = Icons.Default.Cancel,
                iconColor = TradeRed,
                bgColor = TradeRedLight,
                modifier = Modifier.weight(1f)
            )
        }

        // "Place New Order" Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderGray),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = DarkText, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Place New Order",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkText
                    )
                }

                // Buy / Sell toggle bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BorderGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    Button(
                        onClick = { viewModel.selectOrderType("BUY") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (orderType == "BUY") TradeGreen else Color.Transparent
                        ),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        elevation = null,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Buy",
                            tint = if (orderType == "BUY") Color.White else LightText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "BUY",
                            color = if (orderType == "BUY") Color.White else LightText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.selectOrderType("SELL") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (orderType == "SELL") TradeRed else Color.Transparent
                        ),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        elevation = null,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Sell",
                            tint = if (orderType == "SELL") Color.White else LightText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SELL",
                            color = if (orderType == "SELL") Color.White else LightText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Inputs
                Column {
                    Text("Stock Symbol *", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkText, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = stockSymbol,
                        onValueChange = { viewModel.stockSymbol.value = it },
                        placeholder = { Text("E.G. RELIANCE") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }

                Column {
                    Text("Stock Name *", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkText, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = stockName,
                        onValueChange = { viewModel.stockName.value = it },
                        placeholder = { Text("e.g. Reliance Industries") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Quantity *", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkText, modifier = Modifier.padding(bottom = 4.dp))
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { viewModel.quantity.value = it },
                            placeholder = { Text("e.g. 10") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Target Price (Rs) *", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkText, modifier = Modifier.padding(bottom = 4.dp))
                        OutlinedTextField(
                            value = targetPrice,
                            onValueChange = { viewModel.targetPrice.value = it },
                            placeholder = { Text("e.g. 2500.50") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }
                }

                Column {
                    Text("Notes (Optional)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkText, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { viewModel.notes.value = it },
                        placeholder = { Text("Any additional notes...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }

                if (orderError != null) {
                    Text(orderError ?: "", color = TradeRed, fontSize = 12.sp)
                }

                Button(
                    onClick = { viewModel.placeOrder() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (orderType == "BUY") TradeGreen else TradeRed
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (orderType == "BUY") Icons.Default.AddCircle else Icons.Default.RemoveCircle,
                        contentDescription = "Place",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Place $orderType Order",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // "Auto-Execution Engine" Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderGray),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Engine",
                        tint = DarkText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Auto-Execution Engine",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkText
                    )
                }

                // Switch Block
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundGray, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(TradeGreenLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Shield",
                                tint = TradeGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Auto-Execution Mode",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = DarkText
                            )
                            Text(
                                text = "Orders will auto-execute when market opens",
                                fontSize = 11.sp,
                                color = LightText
                            )
                        }
                    }

                    Switch(
                        checked = isAutoExecutionEnabled,
                        onCheckedChange = { viewModel.toggleAutoExecution(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TradeGreen
                        )
                    )
                }

                // Engine status and Manual Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Engine Status", fontSize = 12.sp, color = LightText, fontWeight = FontWeight.Medium)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (engineActive) TradeGreen else TradeRed,
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (engineActive) "Active" else "Inactive",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (engineActive) TradeGreen else TradeRed
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.executeAllPendingOrders() },
                        colors = ButtonDefaults.buttonColors(containerColor = LightText),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Execute Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // "Your Orders" list Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderGray),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatListBulleted,
                        contentDescription = "Orders",
                        tint = DarkText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Your Orders",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkText
                    )
                }

                if (orders.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatListBulleted,
                            contentDescription = "No orders",
                            tint = LightText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No orders yet",
                            fontWeight = FontWeight.Bold,
                            color = DarkText,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Add your first order using the form above",
                            color = LightText,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        orders.forEach { order ->
                            OrderItemRow(order = order, onDelete = { viewModel.deleteOrder(order.id) })
                        }
                    }
                }
            }
        }

        // Informational Footer (matches bottom guidelines details)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Divider()

            FooterItem(
                title = "NSE/BSE Support",
                description = "All orders are placed for National Stock Exchange & Bombay Stock Exchange.",
                icon = Icons.Outlined.Business
            )

            FooterItem(
                title = "Market Hours (IST)",
                description = "Pre-market: 9:00-9:15 AM | Normal: 9:15 AM-3:30 PM | Post-market: 3:40-4:00 PM",
                icon = Icons.Outlined.Schedule
            )

            FooterItem(
                title = "Auto-Execution",
                description = "Orders placed after hours are automatically executed when the market opens next trading day.",
                icon = Icons.Outlined.Security
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(bgColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = count.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Text(text = title, fontSize = 12.sp, color = LightText, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun OrderItemRow(order: Order, onDelete: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(order.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BackgroundGray.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, BorderGray),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // BUY/SELL badge
                    Box(
                        modifier = Modifier
                            .background(
                                if (order.orderType == "BUY") TradeGreenLight else TradeRedLight,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = order.orderType,
                            color = if (order.orderType == "BUY") TradeGreen else TradeRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.stockSymbol,
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = order.stockName,
                    fontSize = 11.sp,
                    color = LightText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Qty: ${order.quantity}", fontSize = 11.sp, color = DarkText, fontWeight = FontWeight.Medium)
                    Text(text = "Price: ₹${order.targetPrice}", fontSize = 11.sp, color = DarkText, fontWeight = FontWeight.Medium)
                }

                if (order.notes.isNotEmpty()) {
                    Text(
                        text = "Notes: ${order.notes}",
                        fontSize = 10.sp,
                        color = LightText,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Text(
                    text = formattedDate,
                    fontSize = 9.sp,
                    color = LightText,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                // Status badge
                Box(
                    modifier = Modifier
                        .background(
                            when (order.status) {
                                "Executed" -> TradeGreenLight
                                "Pending" -> TradeAmberLight
                                else -> TradeRedLight
                            },
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.status,
                        color = when (order.status) {
                            "Executed" -> TradeGreen
                            "Pending" -> TradeAmber
                            else -> TradeRed
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (order.status == "Pending") {
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Cancel order", tint = TradeRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FooterItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = LightText,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Text(text = description, fontSize = 11.sp, color = LightText, lineHeight = 14.sp)
        }
    }
}

@Composable
fun AdminScreen(viewModel: TradingViewModel) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    
    var selectedTab by remember { mutableStateOf(0) } // 0 for Clients, 1 for Orders Log

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Back Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.setScreen("dashboard") }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DarkText)
                }
                Text("Owner Admin Panel", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
            }

            // Secure private marker
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(TradeRedLight, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Private Data",
                    tint = TradeRed,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Owner Only",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TradeRed
                )
            }
        }

        // Tab switcher
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = TradeGreen,
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Clients Data (${allUsers.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("All Orders Log (${allOrders.size})", fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTab == 0) {
            // Clients Data List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(allUsers) { user ->
                    ClientDataCard(user = user)
                }
            }
        } else {
            // Date-wise complete System Orders Log
            Column(modifier = Modifier.fillMaxSize()) {
                // Run engine buttons in Admin
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.executeAllPendingOrders() },
                        colors = ButtonDefaults.buttonColors(containerColor = TradeGreen),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Simulate Execution", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.cancelAllPendingOrders() },
                        colors = ButtonDefaults.buttonColors(containerColor = TradeRed),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Simulate Cancel", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (allOrders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No orders placed in system yet", color = LightText)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Display system-wide orders date-wise
                        items(allOrders) { order ->
                            SystemOrderCard(order = order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientDataCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (user.isOwner) TradeRedLight else TradeGreenLight,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (user.isOwner) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                            contentDescription = "User",
                            tint = if (user.isOwner) TradeRed else TradeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = user.username.replaceFirstChar { it.uppercase() } + if (user.isOwner) " (Owner)" else "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkText
                    )
                }

                Box(
                    modifier = Modifier
                        .background(BackgroundGray, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = user.brokerName, fontSize = 10.sp, color = DarkText, fontWeight = FontWeight.Bold)
                }
            }

            Divider()

            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Email ID", fontSize = 10.sp, color = LightText)
                    Text(user.email, fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.Medium)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Phone Number", fontSize = 10.sp, color = LightText)
                    Text(user.phone, fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.Medium)
                }
            }

            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Identity Proof (${user.identityProofType})", fontSize = 10.sp, color = LightText)
                    Text(user.identityProofValue, fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Broker Client ID", fontSize = 10.sp, color = LightText)
                    Text(user.brokerClientId.ifEmpty { "None Connected" }, fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.Medium)
                }
            }

            if (user.brokerApiKey.isNotEmpty()) {
                Column {
                    Text("Secret API Key (Encrypted Private Data)", fontSize = 10.sp, color = LightText)
                    Text(
                        text = "••••••••••••••••" + user.brokerApiKey.takeLast(4),
                        fontSize = 11.sp,
                        color = LightText,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun SystemOrderCard(order: Order) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm:ss a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(order.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "CLIENT / PLACER", fontSize = 9.sp, color = LightText, fontWeight = FontWeight.Bold)
                    Text(
                        text = order.userDisplayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DarkText
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            when (order.status) {
                                "Executed" -> TradeGreenLight
                                "Pending" -> TradeAmberLight
                                else -> TradeRedLight
                            },
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = order.status,
                        color = when (order.status) {
                            "Executed" -> TradeGreen
                            "Pending" -> TradeAmber
                            else -> TradeRed
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Divider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(
                            if (order.orderType == "BUY") TradeGreenLight else TradeRedLight,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = order.orderType,
                        color = if (order.orderType == "BUY") TradeGreen else TradeRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${order.stockSymbol} (${order.stockName})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = DarkText
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("QUANTITY", fontSize = 9.sp, color = LightText)
                    Text("${order.quantity} units", fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("TARGET PRICE", fontSize = 9.sp, color = LightText)
                    Text("₹${order.targetPrice}", fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("TOTAL AMOUNT", fontSize = 9.sp, color = LightText)
                    Text("₹${String.format("%.2f", order.quantity * order.targetPrice)}", fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.Bold)
                }
            }

            if (order.notes.isNotEmpty()) {
                Column(modifier = Modifier.background(BackgroundGray, RoundedCornerShape(4.dp)).padding(6.dp).fillMaxWidth()) {
                    Text("CLIENT NOTE", fontSize = 8.sp, color = LightText, fontWeight = FontWeight.Bold)
                    Text(order.notes, fontSize = 11.sp, color = DarkText)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Placed ID: #${order.id}",
                    fontSize = 9.sp,
                    color = LightText,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = LightText
                )
            }
        }
    }
}
