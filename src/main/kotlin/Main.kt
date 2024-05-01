import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.material.icons.filled.Delete
import kotlinx.coroutines.delay
import java.io.File

fun loadStudents(filePath: String): List<String> {
    return File(filePath).readLines()
}

fun saveStudents(filePath: String, students: List<String>) {
    File(filePath).writeText(students.joinToString("\n"))
}

@Composable
fun Toast(message: String, onDismiss: () -> Unit) {
    Dialog(onCloseRequest = onDismiss) {
        Surface {
            Text(message, style = MaterialTheme.typography.h6, modifier = Modifier.padding(16.dp))
        }
    }
    LaunchedEffect(key1 = true) {
        delay(2000)
        onDismiss()
    }
}

@Composable
fun StudentScreen(filePath: String) {
    var newStudent by remember { mutableStateOf("") }
    var showToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("") }
    var students by remember { mutableStateOf(loadStudents(filePath)) }

    if (showToast) {
        Toast(message = toastMessage) {
            showToast = false
        }
    }

    Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Students: ${students.size}",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
            Box(modifier = Modifier.weight(1f).fillMaxHeight().border(BorderStroke(2.dp, Color.Black))) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(students) { student ->
                        val interactionSource = remember { MutableInteractionSource() }
                        val isHovered = interactionSource.collectIsHoveredAsState()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .heightIn(min = 70.dp)
                                .background(if (isHovered.value) Color.Cyan else Color.Transparent)
                                .hoverable(interactionSource),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = student,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.subtitle1.copy(fontSize = 20.sp)
                            )
                            IconButton(
                                onClick = {
                                    students = students.filter { it != student }
                                    toastMessage = "Student removed!"
                                    showToast = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete student",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
                IconButton(
                    onClick = { /* Implement scroll logic here, if applicable */ },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Icon(
                        Icons.Filled.KeyboardDoubleArrowUp,
                        contentDescription = "Scroll Up Quickly",
                        tint = Color.Gray
                    )
                }
                IconButton(
                    onClick = { /* Implement scroll logic here, if applicable */ },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                ) {
                    Icon(
                        Icons.Filled.KeyboardDoubleArrowDown,
                        contentDescription = "Scroll Down Quickly",
                        tint = Color.Gray
                    )
                }
            }
        }

        // Columna para añadir nuevos estudiantes y botones de acción
        Column(
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            TextField(
                value = newStudent,
                onValueChange = { if (it.length <= 10) newStudent = it },
                label = { Text("New student name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = androidx.compose.ui.text.input.ImeAction.Done)
            )
            Button(
                onClick = {
                    if (newStudent.isNotBlank()) {
                        students = students + newStudent  // Añade un nuevo estudiante a la lista
                        newStudent = ""
                        toastMessage = "Student added!"
                        showToast = true
                    }
                }
            ) {
                Text("Add new student")
            }
            Button(
                onClick = {
                    students = listOf()  // Borra todos los estudiantes de la lista
                    toastMessage = "All students cleared!"
                    showToast = true
                }
            ) {
                Text("Clear all")
            }
            Button(
                onClick = {
                    saveStudents(filePath, students)  // Guarda todos los cambios
                    toastMessage = "All changes saved!"
                    showToast = true
                }
            ) {
                Text("Save changes")
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "My students",
        state = rememberWindowState(width = 800.dp, height = 600.dp)
    ) {
        Surface(modifier = Modifier.padding(10.dp).background(Color.White)) {
            StudentScreen("students.txt")
        }
    }
}