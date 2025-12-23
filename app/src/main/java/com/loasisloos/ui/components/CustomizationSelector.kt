package com.loasisloos.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.loasisloos.ui.theme.RedPrimary

@Composable
fun RadioSelector(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    title: String
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        options.chunked(3).forEach { rowOptions -> 
            Row(modifier = Modifier.fillMaxWidth()) {
                rowOptions.forEach { option ->
                    Row(
                        Modifier
                            .weight(1f)
                            .height(56.dp)
                            .toggleable(
                                value = (option == selectedOption),
                                onValueChange = { onOptionSelected(option) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = null, // null recommended for accessibility with toggleable
                            colors = RadioButtonDefaults.colors(selectedColor = RedPrimary, unselectedColor = Color.Gray)
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                // Fill empty space if row is not full
                repeat(3 - rowOptions.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun MultiSelector(
    options: List<String>,
    selectedOptions: List<String>,
    onOptionToggle: (String) -> Unit,
    title: String,
    maxSelection: Int = Int.MAX_VALUE
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = "$title ${if (maxSelection < Int.MAX_VALUE) "(Max $maxSelection)" else ""}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        options.chunked(3).forEach { rowOptions ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowOptions.forEach { option ->
                    val isSelected = selectedOptions.contains(option)
                    val enabled = isSelected || selectedOptions.size < maxSelection
                    
                    Row(
                        Modifier
                            .weight(1f)
                            .height(56.dp)
                            .toggleable(
                                value = isSelected,
                                onValueChange = { if (enabled) onOptionToggle(option) },
                                role = Role.Checkbox,
                                enabled = enabled || isSelected
                            )
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(checkedColor = RedPrimary, uncheckedColor = Color.Gray, disabledUncheckedColor = Color.DarkGray),
                            enabled = enabled || isSelected
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (enabled) Color.White else Color.DarkGray,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                repeat(3 - rowOptions.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}
