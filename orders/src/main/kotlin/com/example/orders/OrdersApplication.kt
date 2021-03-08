package com.example.orders

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@SpringBootApplication
class OrdersApplication

fun main(args: Array<String>) {
    runApplication<OrdersApplication>(*args)
}

data class Order(var id: Int, var customerId: Int)

@Controller
class OrdersRSocketController {

    private val db = mutableMapOf<Int, Collection<Order>>()

    init {
        for (customerId in 0..3) {
            this.db[customerId] = randomOrdersFor(customerId)
        }
        this.db.forEach { (customerId, orders) -> println("the customerId is #${customerId} and the orders are ${orders}") }
    }

    private fun randomOrdersFor(id: Int): Collection<Order> {
        val listOfOrders = mutableListOf<Order>()
        val maxCount: Int = (Math.random() * 1000).toInt()
        for (count in 1..maxCount) {
            listOfOrders.add(Order(count, id))
        }
        return listOfOrders
    }

    @MessageMapping("orders.{cid}")
    fun getOrdersFor(@DestinationVariable cid: Int) = this.db[cid]?.toList()
}
