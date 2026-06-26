package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen() {
    var amountText by remember { mutableStateOf("1000") }
    var isAddGst by remember { mutableStateOf(true) }
    var gstRate by remember { mutableStateOf(18) }

    val amount = amountText.toDoubleOrNull() ?: 0.0

    val (net, gst, total) = remember(amount, isAddGst, gstRate) {
        if (isAddGst) {
            val n = amount
            val g = (amount * gstRate) / 100
            val t = n + g
            Triple(n, g, t)
        } else {
            val t = amount
            val n = amount / (1 + gstRate / 100.0)
            val g = t - n
            Triple(n, g, t)
        }
    }

    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Initial Amount Card
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Enter Initial Amount",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("₹", fontSize = 36.sp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        textStyle = LocalTextStyle.current.copy(fontSize = 36.sp, fontWeight = FontWeight.Bold),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mode Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (isAddGst) MaterialTheme.colorScheme.primary else Color.Transparent,
                        RoundedCornerShape(50)
                    )
                    .clickable { isAddGst = true }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Add GST",
                    color = if (isAddGst) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (!isAddGst) MaterialTheme.colorScheme.primary else Color.Transparent,
                        RoundedCornerShape(50)
                    )
                    .clickable { isAddGst = false }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Remove GST",
                    color = if (!isAddGst) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // GST Rates
        Text("SELECT GST RATE", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5, 12, 18, 28).forEach { rate ->
                val isSelected = gstRate == rate
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { gstRate = rate }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$rate%",
                        fontFamily = FontFamily.Monospace,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Breakdown
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (isAddGst) "Net Amount" else "Gross Amount", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(currencyFormat.format(net), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("GST Amount ($gstRate%)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(currencyFormat.format(gst), color = MaterialTheme.colorScheme.primary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isAddGst) "Total Amount" else "Excl. GST", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(currencyFormat.format(total), color = MaterialTheme.colorScheme.primary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Actions
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(
                onClick = { /* Share */ },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { /* Save */ },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Invoice", fontWeight = FontWeight.Bold)
            }
        }
    }
}
