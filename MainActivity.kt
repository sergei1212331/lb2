package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BonusCalculatorScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun BonusCalculatorScreen(modifier: Modifier = Modifier) {
    var employeeCount by remember { mutableStateOf(0) }
    var inputCount by remember { mutableStateOf("") }
    var employeeIndex by remember { mutableStateOf(1) }

    var name by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var bonusRate by remember { mutableStateOf("") }

    var employees by remember { mutableStateOf(listOf<Pair<String, Double>>()) }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp)) {
        if (employeeIndex <= employeeCount && employeeCount > 0) {
            Text("Працівник №$employeeIndex")

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Ім’я") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = salary,
                onValueChange = { salary = it },
                label = { Text("Оклад") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bonusRate,
                onValueChange = { bonusRate = it },
                label = { Text("Коефіцієнт бонусу (0.1–2.0)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                try {
                    val s = salary.toDouble()
                    val r = bonusRate.toDouble()
                    if (s < 0) throw Exception("Оклад не може бути від’ємним")
                    if (r !in 0.1..2.0) throw Exception("Невірний коефіцієнт бонусу")

                    val bonus = s * r
                    employees = employees + (name.ifBlank { "Невідомий" } to bonus)

                    employeeIndex++
                    name = ""
                    salary = ""
                    bonusRate = ""
                    errorMessage = ""
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Помилка"
                }
            }) {
                Text("Зберегти")
            }

            if (errorMessage.isNotBlank()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        } else if (employeeCount == 0) {
            OutlinedTextField(
                value = inputCount,
                onValueChange = { inputCount = it },
                label = { Text("Кількість працівників") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                employeeCount = inputCount.toIntOrNull() ?: 0
                inputCount = ""
            }) {
                Text("Почати")
            }
        } else {
            // Аналіз результатів
            val maxBonus = employees.maxByOrNull { it.second }
            val totalBonus = employees.sumOf { it.second }
            val excellentCount = employees.count { it.second >= 10_000 }

            Text("\nРезультати:", style = MaterialTheme.typography.titleLarge)
            Text("Найбільший бонус: ${maxBonus?.first} — %.2f".format(maxBonus?.second ?: 0.0))
            Text("Загальна сума бонусів: %.2f".format(totalBonus))
            Text("Кількість «відмінних» (бонус ≥ 10 000): $excellentCount")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BonusCalculatorPreview() {
    MyApplicationTheme {
        BonusCalculatorScreen()
    }
}
