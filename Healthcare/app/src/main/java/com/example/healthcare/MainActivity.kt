package com.example.healthcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.healthcare.ui.theme.HealthCareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthCareTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HealthcareApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthcareApp(modifier: Modifier = Modifier) {
    var selectedTask by remember { mutableStateOf("Digging Trench") }
    val tasks = listOf("Digging Trench", "Roofing", "Electrical Work", "Scaffolding")
    
    val gearStatus = remember {
        mutableStateMapOf(
            "Helmet" to false,
            "Gloves" to false,
            "Boots" to false,
            "Goggles" to false
        )
    }
    
    val riskMapping = mapOf(
        "Helmet" to "Head Injury",
        "Gloves" to "Hand Cuts/Burns",
        "Boots" to "Foot Crushing",
        "Goggles" to "Eye Damage"
    )

    var incidentLog by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Safety Dashboard", style = MaterialTheme.typography.headlineMedium)

        // 1. Task Selector
        Text(text = "Task Selector", style = MaterialTheme.typography.titleLarge)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedTask,
                onValueChange = {},
                readOnly = true,
                label = { Text("Choose the job for the day") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                tasks.forEach { task ->
                    DropdownMenuItem(
                        text = { Text(task) },
                        onClick = {
                            selectedTask = task
                            expanded = false
                        }
                    )
                }
            }
        }

        HorizontalDivider()

        // 2. Safety Gear Checklist
        Text(text = "Safety Gear Checklist", style = MaterialTheme.typography.titleLarge)
        gearStatus.keys.forEach { gear ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = gearStatus[gear] ?: false,
                    onCheckedChange = { gearStatus[gear] = it }
                )
                Text(text = gear, modifier = Modifier.padding(start = 8.dp))
            }
        }

        HorizontalDivider()

        // 3. Risk Meter
        val missingGear = gearStatus.filter { !it.value }.keys
        val riskLevel = missingGear.size.toFloat() / gearStatus.size
        val riskColor = when {
            riskLevel == 0f -> Color.Green
            riskLevel < 0.5f -> Color.Yellow
            else -> Color.Red
        }

        Text(text = "Risk Meter", style = MaterialTheme.typography.titleLarge)
        Text(text = "Current Risk Level: ${if (riskLevel == 0f) "Safe" else if (riskLevel < 0.5f) "Moderate" else "High"}")
        LinearProgressIndicator(
            progress = { riskLevel },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = riskColor,
        )
        
        if (missingGear.isNotEmpty()) {
            Text(
                text = "Likely Injury if gear is ignored:",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Red
            )
            missingGear.forEach { gear ->
                Text(text = "• ${riskMapping[gear]}", color = Color.Red)
            }
        } else {
            Text(text = "All gear equipped. Stay safe!", color = Color.Green)
        }

        HorizontalDivider()

        // 4. Incident Log
        Text(text = "Incident Log", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = incidentLog,
            onValueChange = { incidentLog = it },
            label = { Text("Report 'Near Misses'") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Button(
            onClick = { 
                // Handle log submission here
                incidentLog = "" 
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Report Incident")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun HealthcareAppPreview() {
    HealthCareTheme {
        HealthcareApp()
    }
}
