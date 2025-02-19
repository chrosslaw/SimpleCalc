package com.example.simplecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SimpleCalculator(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Calculator class with two operands and a compute method.
// The primary constructor initializes operand1 and operand2.
class Calculator(val operand1: Double, val operand2: Double) {
    // Compute the result based on the chosen operation using a when clause.
    fun compute(operation: Operation): Double {
        return when (operation) {
            Operation.ADD -> operand1 + operand2
            Operation.SUBTRACT -> operand1 - operand2
            Operation.MULTIPLY -> operand1 * operand2
            Operation.DIVIDE -> {
                if (operand2 == 0.0)
                    throw ArithmeticException("Division by zero")
                else
                    operand1 / operand2
            }
        }
    }
}
// enum class with related symbols
enum class Operation(val symbol: String) {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("x"),
    DIVIDE("/")
}

@Composable
fun Title() {
    Text(
        text = "Simple Calculator",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun InputBox(labelText: String, textState: MutableState<String>, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = textState.value,
        onValueChange = { textState.value = it },
        label = { Text(labelText) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun OperationSelect(selectedOperation: MutableState<Operation>) {
    Text(
        text = "Select Operation",
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
    // Put the buttons side-by-side in one row
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { selectedOperation.value = Operation.ADD }) {
            Text("Add")
        }
        Button(onClick = { selectedOperation.value = Operation.SUBTRACT }) {
            Text("Subtract")
        }
        Button(onClick = { selectedOperation.value = Operation.MULTIPLY }) {
            Text("Multiply")
        }
        Button(onClick = { selectedOperation.value = Operation.DIVIDE }) {
            Text("Divide")
        }
    }
}

@Composable
fun CalculateButton(
    operand1: String,
    operand2: String,
    selectedOperation: Operation,
    onResult: (String) -> Unit
) {
    Button(
        onClick = {
            // Attempt to convert input strings to numbers.
            val o1 = operand1.toDoubleOrNull()
            val o2 = operand2.toDoubleOrNull()
            if (o1 == null || o2 == null) {
                onResult("Please enter valid numbers.")
            } else {
                try {
                    // Create a Calculator object using the provided operands.
                    val calculator = Calculator(o1, o2)
                    // Compute the result based on the selected operation.
                    val result = calculator.compute(selectedOperation)
                    onResult("= $result")
                } catch (e: ArithmeticException) {
                    onResult(e.message ?: "Error")
                }
            }
        },
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text("Calculate", fontSize = 24.sp)
    }
}

@Composable
fun DisplayResult(
    resultText: String,
    selectedOperation: Operation,
    operand1: String,
    operand2: String
) {
    // Only display the operation if operand1 is not empty
    val displayOperation = if (operand1.isNotEmpty()) " ${selectedOperation.symbol} " else ""
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .requiredSizeIn(minWidth = 300.dp, minHeight = 200.dp)
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Use a Column to stack texts vertically.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$operand1$displayOperation$operand2",
                fontSize = 24.sp
            )
            Text(
                text = resultText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                // color them for divide by 0
                color = if (resultText.contains("Division by zero")) Color.Red else Color.Black
            )
        }
    }
}

@Composable
fun SimpleCalculator(modifier: Modifier = Modifier) {
    // Define state for operand inputs, selected operation, and result.
    val operand1State = remember { mutableStateOf("") }
    val operand2State = remember { mutableStateOf("") }
    val selectedOperation = remember { mutableStateOf(Operation.ADD) }
    var resultText by remember { mutableStateOf("") }

    // Reset resultText if both operand inputs are empty.
    LaunchedEffect(key1 = operand1State.value, key2 = operand2State.value) {
        if (operand1State.value.isEmpty() && operand2State.value.isEmpty()) {
            resultText = ""
        }
    }
    // Compile all my Composables
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title()
        DisplayResult(
            resultText = resultText,
            selectedOperation = selectedOperation.value,
            operand1 = operand1State.value,
            operand2 = operand2State.value
        )
        InputBox("Enter Operand 1", operand1State)
        InputBox("Enter Operand 2", operand2State)
        OperationSelect(selectedOperation)
        CalculateButton(
            operand1 = operand1State.value,
            operand2 = operand2State.value,
            selectedOperation = selectedOperation.value,
            onResult = { resultText = it }
        )
    }
}