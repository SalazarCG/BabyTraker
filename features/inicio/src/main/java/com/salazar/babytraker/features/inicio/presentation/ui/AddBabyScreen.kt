package com.salazar.babytraker.features.inicio.presentation.ui

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.salazar.babytraker.features.inicio.presentation.mvi.AddBabyEffect
import com.salazar.babytraker.features.inicio.presentation.mvi.AddBabyIntent
import com.salazar.babytraker.features.inicio.presentation.viewmodel.AddBabyViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBabyScreen(
    viewModel: AddBabyViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    
    // URI temporal para la cámara
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para Galería
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.onIntent(AddBabyIntent.UpdateFotoUri(it.toString())) }
        }
    )

    // Launcher para Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempCameraUri?.let { viewModel.onIntent(AddBabyIntent.UpdateFotoUri(it.toString())) }
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                AddBabyEffect.NavigateBack -> onNavigateBack()
                is AddBabyEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.onIntent(AddBabyIntent.UpdateFechaNacimiento(it))
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showPhotoOptions) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoOptions = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Seleccionar Foto", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                
                ListItem(
                    headlineContent = { Text("Hacer Foto con Cámara") },
                    leadingContent = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                    modifier = Modifier.clickable {
                        val uri = createTempPictureUri(context)
                        tempCameraUri = uri
                        cameraLauncher.launch(uri)
                        showPhotoOptions = false
                    }
                )
                ListItem(
                    headlineContent = { Text("Elegir de la Galería") },
                    leadingContent = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
                    modifier = Modifier.clickable {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                        showPhotoOptions = false
                    }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Bebé", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onIntent(AddBabyIntent.SaveBaby) },
                icon = { Icon(Icons.Default.Save, contentDescription = null) },
                text = { Text("Guardar") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Selector de Foto (Avatar)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { showPhotoOptions = true },
                contentAlignment = Alignment.Center
            ) {
                if (state.fotoUri != null) {
                    AsyncImage(
                        model = state.fotoUri,
                        contentDescription = "Foto del bebé",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Añadir foto",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = state.nombre,
                onValueChange = { viewModel.onIntent(AddBabyIntent.UpdateNombre(it)) },
                label = { Text("Nombre del Bebé") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.ChildCare, contentDescription = null) },
                singleLine = true
            )

            val dateText = state.fechaNacimiento?.let {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
            } ?: "Seleccionar fecha"

            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Fecha de Nacimiento",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                }
            }

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/**
 * Helper para crear un URI seguro para la cámara usando FileProvider
 */
private fun createTempPictureUri(context: Context): Uri {
    val tempFile = File.createTempFile(
        "baby_capture_", ".jpg", 
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    ).apply {
        createNewFile()
        deleteOnExit()
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}
