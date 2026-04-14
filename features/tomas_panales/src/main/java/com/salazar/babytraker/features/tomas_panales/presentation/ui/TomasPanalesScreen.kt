@file:OptIn(ExperimentalMaterial3Api::class)

package com.salazar.babytraker.features.tomas_panales.presentation.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.salazar.babytraker.core.domain.model.TipoAlimentacion
import com.salazar.babytraker.core.domain.model.TipoPanal
import com.salazar.babytraker.core.ui.components.BabyAvatar
import com.salazar.babytraker.features.tomas_panales.presentation.mvi.TomasPanalesEffect
import com.salazar.babytraker.features.tomas_panales.presentation.mvi.TomasPanalesIntent
import com.salazar.babytraker.features.tomas_panales.presentation.viewmodel.TomasPanalesViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TomasPanalesScreen(
    viewModel: TomasPanalesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showPhotoOptions by remember { mutableStateOf(false) }
    
    // Estados para los TimePickers
    var showTimePickerToma by remember { mutableStateOf(false) }
    var showTimePickerPanal by remember { mutableStateOf(false) }
    
    // Persistimos el URI temporal para la cámara
    var tempCameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    
    val density = LocalDensity.current
    val scrollThresholdPx = with(density) { 120.dp.toPx() }
    val scrollProgress = (scrollState.value.toFloat() / scrollThresholdPx).coerceIn(0f, 1f)

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.onIntent(TomasPanalesIntent.UpdateFotoDelDia(it.toString())) }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempCameraUri != null) {
                viewModel.onIntent(TomasPanalesIntent.UpdateFotoDelDia(tempCameraUri.toString()))
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createTempPictureUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                TomasPanalesEffect.ShowSuccess -> {
                    Toast.makeText(context, "Registro guardado correctamente", Toast.LENGTH_SHORT).show()
                }
                is TomasPanalesEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Diálogos de Selección de Hora
    if (showTimePickerToma) {
        TimePickerDialogCustom(
            onDismiss = { showTimePickerToma = false },
            onTimeSelected = { hour, minute ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                viewModel.onIntent(TomasPanalesIntent.UpdateHoraToma(calendar.timeInMillis))
                showTimePickerToma = false
            }
        )
    }

    if (showTimePickerPanal) {
        TimePickerDialogCustom(
            onDismiss = { showTimePickerPanal = false },
            onTimeSelected = { hour, minute ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }
                viewModel.onIntent(TomasPanalesIntent.UpdateHoraPanal(calendar.timeInMillis))
                showTimePickerPanal = false
            }
        )
    }

    if (showPhotoOptions) {
        ModalBottomSheet(onDismissRequest = { showPhotoOptions = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Foto del Día", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                ListItem(
                    headlineContent = { Text("Cámara") },
                    leadingContent = { Icon(Icons.Default.CameraAlt, null) },
                    modifier = Modifier.clickable {
                        showPhotoOptions = false
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            val uri = createTempPictureUri(context)
                            tempCameraUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                )
                ListItem(
                    headlineContent = { Text("Galería") },
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, null) },
                    modifier = Modifier.clickable {
                        showPhotoOptions = false
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .padding(top = 120.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // SELECTOR DE BEBÉ
            if (state.babies.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Bebé Seleccionado",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Gray
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.babies) { baby ->
                            BabyAvatar(
                                baby = baby,
                                isSelected = state.selectedBaby?.id == baby.id,
                                onClick = { viewModel.onIntent(TomasPanalesIntent.SelectBaby(baby)) }
                            )
                        }
                    }
                }
                HorizontalDivider(color = Color(0xFFF5F5F5))
            }

            if (state.selectedBaby == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Por favor, selecciona un bebé para registrar datos.", color = Color.Red)
                }
            } else {
                // SECCIÓN FOTO DEL DÍA
                SectionCard(title = "Foto del Día", icon = Icons.Default.CameraAlt) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AsyncImage(
                            model = state.fotoDelDia ?: state.selectedBaby?.fotoUri ?: "https://via.placeholder.com/150",
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF5F5F5))
                                .clickable { showPhotoOptions = true },
                            contentScale = ContentScale.Crop
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (state.fotoDelDia != null) "Foto capturada hoy" else "Usando foto de perfil",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (state.fotoDelDia != null) Color(0xFF4CAF50) else Color.Gray
                            )
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { showPhotoOptions = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1F5FE), contentColor = Color(0xFF039BE5)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Cambiar Foto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // SECCIÓN TOMAS
                SectionCard(title = "Alimentación", icon = Icons.Default.Restaurant) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Hora de la toma:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                            AssistChip(
                                onClick = { showTimePickerToma = true },
                                label = { 
                                    Text(
                                        text = state.horaToma?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it)) } ?: "Ahora"
                                    ) 
                                },
                                leadingIcon = { Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(18.dp)) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (state.horaToma != null) Color(0xFFE1F5FE) else Color.Transparent,
                                    labelColor = if (state.horaToma != null) Color(0xFF0288D1) else Color.Gray
                                )
                            )
                        }

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            TipoAlimentacion.entries.forEachIndexed { index, tipo ->
                                SegmentedButton(
                                    selected = state.selectedTipoAlimentacion == tipo,
                                    onClick = { viewModel.onIntent(TomasPanalesIntent.UpdateTipoAlimentacion(tipo)) },
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = TipoAlimentacion.entries.size)
                                ) {
                                    Text(tipo.name)
                                }
                            }
                        }

                        if (state.selectedTipoAlimentacion == TipoAlimentacion.BIBERON) {
                            OutlinedTextField(
                                value = state.cantidadMl,
                                onValueChange = { viewModel.onIntent(TomasPanalesIntent.UpdateCantidad(it)) },
                                label = { Text("Cantidad (ml)") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }

                        var notaAlimentacionLocal by remember(state.notaAlimentacion) { mutableStateOf(state.notaAlimentacion) }

                        OutlinedTextField(
                            value = notaAlimentacionLocal,
                            onValueChange = { newValue ->
                                val capitalized = newValue.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase() else it.toString()
                                }
                                notaAlimentacionLocal = capitalized
                                viewModel.onIntent(TomasPanalesIntent.UpdateNotaAlimentacion(capitalized))
                            },
                            label = { Text("Notas de alimentación") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Next
                            )
                        )

                        Button(
                            onClick = { viewModel.onIntent(TomasPanalesIntent.SaveToma) },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Guardar Toma")
                        }
                    }
                }

                // SECCIÓN PAÑALES
                SectionCard(title = "Pañal e Higiene", icon = Icons.Default.BabyChangingStation) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Hora del pañal:", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                            AssistChip(
                                onClick = { showTimePickerPanal = true },
                                label = { 
                                    Text(
                                        text = state.horaPanal?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it)) } ?: "Ahora"
                                    ) 
                                },
                                leadingIcon = { Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(18.dp)) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (state.horaPanal != null) Color(0xFFE1F5FE) else Color.Transparent,
                                    labelColor = if (state.horaPanal != null) Color(0xFF0288D1) else Color.Gray
                                )
                            )
                        }

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            TipoPanal.entries.forEachIndexed { index, tipo ->
                                SegmentedButton(
                                    selected = state.selectedTipoPanal == tipo,
                                    onClick = { viewModel.onIntent(TomasPanalesIntent.UpdateTipoPanal(tipo)) },
                                    shape = SegmentedButtonDefaults.itemShape(index = index, count = TipoPanal.entries.size)
                                ) {
                                    Text(tipo.name)
                                }
                            }
                        }

                        var notaPanalLocal by remember(state.notaPanal) { mutableStateOf(state.notaPanal) }

                        OutlinedTextField(
                            value = notaPanalLocal,
                            onValueChange = { newValue ->
                                val capitalized = newValue.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase() else it.toString()
                                }
                                notaPanalLocal = capitalized
                                viewModel.onIntent(TomasPanalesIntent.UpdateNotaPanal(capitalized))
                            },
                            label = { Text("Notas del pañal") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            )
                        )

                        Button(
                            onClick = { viewModel.onIntent(TomasPanalesIntent.SavePanal) },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Guardar Pañal")
                        }
                    }
                }
            }
        }

        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = if (scrollProgress > 0.8f) 0.95f else 0f))
                .statusBarsPadding()
                .height(72.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = BiasAlignment(
                horizontalBias = -1f + scrollProgress,
                verticalBias = 0f
            )
        ) {
            Text(
                text = "Registrar Actividad",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = lerp(
                        MaterialTheme.typography.headlineMedium.fontSize,
                        MaterialTheme.typography.titleLarge.fontSize,
                        scrollProgress
                    )
                ),
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun TimePickerDialogCustom(
    onDismiss: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit
) {
    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onTimeSelected(timePickerState.hour, timePickerState.minute) }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Seleccionar Hora", style = MaterialTheme.typography.titleLarge) },
        text = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = timePickerState)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White
    )
}

@Composable
fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFFF8FDFF)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color(0xFF4FC3F7))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

private fun createTempPictureUri(context: Context): Uri {
    val tempFile = File(context.cacheDir, "temp_daily_${System.currentTimeMillis()}.jpg").apply {
        createNewFile()
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}
