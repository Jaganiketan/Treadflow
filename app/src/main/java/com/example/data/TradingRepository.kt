package com.example.data

import kotlinx.coroutines.flow.Flow

class TradingRepository(
    private val userDao: UserDao,
    private val orderDao: OrderDao
) {
    suspend fun getUser(username: String): User? {
        return userDao.getUser(username)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    fun getAllUsersFlow(): Flow<List<User>> {
        return userDao.getAllUsersFlow()
    }

    fun getOrdersForUser(userId: String): Flow<List<Order>> {
        return orderDao.getOrdersForUser(userId)
    }

    fun getAllOrdersFlow(): Flow<List<Order>> {
        return orderDao.getAllOrders()
    }

    suspend fun insertOrder(order: Order): Long {
        return orderDao.insertOrder(order)
    }

    suspend fun updateOrder(order: Order) {
        orderDao.updateOrder(order)
    }

    suspend fun deleteOrderById(id: Int) {
        orderDao.deleteOrderById(id)
    }

    suspend fun executePendingOrders() {
        val pending = orderDao.getPendingOrders()
        for (order in pending) {
            // Update to Executed
            val executedOrder = order.copy(status = "Executed")
            orderDao.updateOrder(executedOrder)
        }
    }

    suspend fun cancelPendingOrders() {
        val pending = orderDao.getPendingOrders()
        for (order in pending) {
            // Update to Cancelled
            val cancelledOrder = order.copy(status = "Cancelled")
            orderDao.updateOrder(cancelledOrder)
        }
    }
}
