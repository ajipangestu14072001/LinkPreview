package com.example.linkpreview

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedTextFieldWithSendIcon(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit
) {
    val textFieldValue = remember { mutableStateOf(TextFieldValue(text)) }

    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        OutlinedTextField(
            value = textFieldValue.value,
            onValueChange = { value ->
                textFieldValue.value = value
                onTextChanged(value.text)
            },
            shape = RoundedCornerShape(16.dp),
            placeholder = { Text(text = "Masukkan Link") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.Gray,
                textColor = Color.Black,
                cursorColor = Color.Black,
            ),
            modifier = Modifier.weight(1f),
            trailingIcon = {
                IconButton(
                    onClick = {
                        textFieldValue.value = TextFieldValue("")
                        onSendClicked()
                    },
                    modifier = Modifier.padding(end = 8.dp),
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "Send",
                            tint = Color.Black,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                        )
                    }
                )
            }
        )
    }
}





