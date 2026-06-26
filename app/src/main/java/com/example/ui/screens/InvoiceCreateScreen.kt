package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Invoice
import com.example.model.InvoiceItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceCreateScreen(onNavigateToHistory: () -> Unit) {
    var customerName by remember { mutableStateOf("") }
    var gstin by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var items by remember { mutableStateOf(listOf(InvoiceItem())) }
    var isSaving by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val db = FirebaseFirestore.getInstance()

    val subtotal = items.sumOf { (it.qty * it.price) }
    val totalGst = items.sumOf { (it.qty * it.price * it.gstRate) / 100 }
    val cgst = totalGst / 2
    val sgst = totalGst / 2
    val totalAmount = subtotal + totalGst

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Customer Details Card
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Customer Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = gstin,
                    onValueChange = { gstin = it },
                    label = { Text("GSTIN (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Billing Address") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Items Card
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Invoice Items", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { items = items + InvoiceItem() }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Item")
                    }
                }

                items.forEachIndexed { index, item ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = item.description,
                                onValueChange = { desc ->
                                    items = items.toMutableList().apply { this[index] = item.copy(description = desc) }
                                },
                                label = { Text("Description") },
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                items = items.toMutableList().apply { removeAt(index) }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = item.qty.toString(),
                                onValueChange = { q ->
                                    items = items.toMutableList().apply { this[index] = item.copy(qty = q.toIntOrNull() ?: 0) }
                                },
                                label = { Text("Qty") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = item.price.toString(),
                                onValueChange = { p ->
                                    items = items.toMutableList().apply { this[index] = item.copy(price = p.toDoubleOrNull() ?: 0.0) }
                                },
                                label = { Text("Price") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Order Summary", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", color = Color.White.copy(alpha = 0.9f))
                    Text(currencyFormat.format(subtotal), color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("CGST (9%)", color = Color.White.copy(alpha = 0.9f))
                    Text(currencyFormat.format(cgst), color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("SGST (9%)", color = Color.White.copy(alpha = 0.9f))
                    Text(currencyFormat.format(sgst), color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Total Amount", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(currencyFormat.format(totalAmount), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        isSaving = true
                        val invoice = Invoice(
                            customerName = customerName,
                            gstin = gstin,
                            address = address,
                            items = items,
                            subtotal = subtotal,
                            cgst = cgst,
                            sgst = sgst,
                            totalAmount = totalAmount
                        )
                        db.collection("invoices").add(invoice).addOnSuccessListener {
                            isSaving = false
                            onNavigateToHistory()
                        }.addOnFailureListener {
                            isSaving = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save to History", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp)) // padding for bottom nav
    }
}
