package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class, Order::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tradeflow_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.userDao(), database.orderDao())
                }
            }
        }

        suspend fun populateDatabase(userDao: UserDao, orderDao: OrderDao) {
            // Seed Owner
            val owner = User(
                username = "owner",
                email = "owner@tradeflow.com",
                phone = "+91 98765 43210",
                identityProofType = "PAN Card",
                identityProofValue = "AMKPW4820L",
                brokerName = "AngelOne",
                brokerClientId = "ANGEL_OWNER_1",
                brokerApiKey = "api_sec_angel_999",
                isOwner = true
            )
            userDao.insertUser(owner)

            // Seed Client 1
            val client1 = User(
                username = "rahul_sharma",
                email = "rahul.sharma@gmail.com",
                phone = "+91 99221 13344",
                identityProofType = "Aadhaar Card",
                identityProofValue = "4321-8765-9012",
                brokerName = "Zerodha (Kite)",
                brokerClientId = "ZR9421",
                brokerApiKey = "zrd_api_key_sharma_98",
                isOwner = false
            )
            userDao.insertUser(client1)

            // Seed Client 2
            val client2 = User(
                username = "priya_patel",
                email = "priya.patel@groww.com",
                phone = "+91 98112 23344",
                identityProofType = "PAN Card",
                identityProofValue = "BPAXP9012K",
                brokerName = "Groww",
                brokerClientId = "GRW_PRIYA_88",
                brokerApiKey = "grw_api_key_patel_71",
                isOwner = false
            )
            userDao.insertUser(client2)

            // Seed Client 3
            val client3 = User(
                username = "vikram_singh",
                email = "vikram.singh@yahoo.com",
                phone = "+91 95432 10987",
                identityProofType = "Driving License",
                identityProofValue = "DL-14202300482",
                brokerName = "Upstox",
                brokerClientId = "UPS_VIKRAM_44",
                brokerApiKey = "ups_api_key_singh_30",
                isOwner = false
            )
            userDao.insertUser(client3)

            // Seed Orders
            val now = System.currentTimeMillis()
            val dayInMillis = 24 * 60 * 60 * 1000L

            // Rahul's orders
            orderDao.insertOrder(
                Order(
                    userId = "rahul_sharma",
                    userDisplayName = "Rahul Sharma",
                    stockSymbol = "RELIANCE",
                    stockName = "Reliance Industries Ltd.",
                    quantity = 15,
                    targetPrice = 2450.50,
                    notes = "Buy on support level",
                    orderType = "BUY",
                    status = "Executed",
                    timestamp = now - dayInMillis // Yesterday
                )
            )

            orderDao.insertOrder(
                Order(
                    userId = "rahul_sharma",
                    userDisplayName = "Rahul Sharma",
                    stockSymbol = "TCS",
                    stockName = "Tata Consultancy Services Ltd.",
                    quantity = 10,
                    targetPrice = 3850.00,
                    notes = "Long term investment",
                    orderType = "BUY",
                    status = "Pending",
                    timestamp = now - 2 * 60 * 60 * 1000L // 2 hours ago
                )
            )

            // Priya's orders
            orderDao.insertOrder(
                Order(
                    userId = "priya_patel",
                    userDisplayName = "Priya Patel",
                    stockSymbol = "TATAMOTORS",
                    stockName = "Tata Motors Ltd.",
                    quantity = 50,
                    targetPrice = 625.40,
                    notes = "Breakout trade",
                    orderType = "BUY",
                    status = "Pending",
                    timestamp = now - 5 * 60 * 60 * 1000L // 5 hours ago
                )
            )

            orderDao.insertOrder(
                Order(
                    userId = "priya_patel",
                    userDisplayName = "Priya Patel",
                    stockSymbol = "INFY",
                    stockName = "Infosys Ltd.",
                    quantity = 30,
                    targetPrice = 1480.00,
                    notes = "Earnings play",
                    orderType = "SELL",
                    status = "Cancelled",
                    timestamp = now - dayInMillis * 2 // 2 days ago
                )
            )

            // Vikram's orders
            orderDao.insertOrder(
                Order(
                    userId = "vikram_singh",
                    userDisplayName = "Vikram Singh",
                    stockSymbol = "HDFCBANK",
                    stockName = "HDFC Bank Ltd.",
                    quantity = 100,
                    targetPrice = 1620.00,
                    notes = "Accumulate near support",
                    orderType = "BUY",
                    status = "Executed",
                    timestamp = now - 4 * 60 * 60 * 1000L // 4 hours ago
                )
            )

            // Owner's own orders
            orderDao.insertOrder(
                Order(
                    userId = "owner",
                    userDisplayName = "Owner (Admin)",
                    stockSymbol = "SBIN",
                    stockName = "State Bank of India",
                    quantity = 200,
                    targetPrice = 582.10,
                    notes = "High volume trade",
                    orderType = "BUY",
                    status = "Executed",
                    timestamp = now - 1 * 60 * 60 * 1000L // 1 hour ago
                )
            )
            
            orderDao.insertOrder(
                Order(
                    userId = "owner",
                    userDisplayName = "Owner (Admin)",
                    stockSymbol = "ITC",
                    stockName = "ITC Ltd.",
                    quantity = 500,
                    targetPrice = 430.00,
                    notes = "Dividend portfolio",
                    orderType = "BUY",
                    status = "Pending",
                    timestamp = now - 30 * 60 * 1000L // 30 mins ago
                )
            )
        }
    }
}
