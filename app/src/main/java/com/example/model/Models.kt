package com.example.model

data class InvoiceItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    var description: String = "",
    var qty: Int = 1,
    var price: Double = 0.0,
    var gstRate: Int = 18
)

data class Invoice(
    val id: String = "",
    val customerName: String = "",
    val gstin: String = "",
    val address: String = "",
    val items: List<InvoiceItem> = emptyList(),
    val subtotal: Double = 0.0,
    val cgst: Double = 0.0,
    val sgst: Double = 0.0,
    val totalAmount: Double = 0.0,
    val date: Long = System.currentTimeMillis()
)
