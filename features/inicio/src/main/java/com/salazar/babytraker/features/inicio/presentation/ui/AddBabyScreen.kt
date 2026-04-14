package com.salazar.babytraker.features.inicio.presentation.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.salazar.babytraker.core.data.local.preferences.BabyPreferences
import com.salazar.babytraker.features.inicio.presentation.mvi.AddBabyEffect
import com.salazar.babytraker.features.inicio.presentation.mvi.AddBabyIntent
import com.salazar.babytraker.features.inicio.presentation.viewmodel.AddBabyViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBabyScreen(
    viewModel: AddBabyViewModel = hiltViewModel(),
    babyPreferences: BabyPreferences, // Pasado desde el grafo de navegación o inyectado
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    
    // Solo mostramos si no se ha visto antes
    var showWelcomeMessage by remember { 
        mutableStateOf(!babyPreferences.hasSeenWelcomeMessage) 
    }
    
    val blurIntensity by animateFloatAsState(
        targetValue = if (showWelcomeMessage) 10f else 0f,
        animationSpec = tween(800),
        label = "blurAnimation"
    )

    // Persistimos el URI temporal
    var tempCameraUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { viewModel.onIntent(AddBabyIntent.UpdateFotoUri(it.toString())) }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && tempCameraUri != null) {
                viewModel.onIntent(AddBabyIntent.UpdateFotoUri(tempCameraUri.toString()))
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
                AddBabyEffect.NavigateBack -> onNavigateBack()
                is AddBabyEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Efecto para ocultar el mensaje tras 8 segundos y marcar como visto
    LaunchedEffect(showWelcomeMessage) {
        if (showWelcomeMessage) {
            delay(8000)
            showWelcomeMessage = false
            babyPreferences.hasSeenWelcomeMessage = true
        }
    }

    // Bloquear el botón atrás del sistema mientras se muestra el mensaje
    if (showWelcomeMessage) {
        BackHandler(enabled = true) { /* No hace nada, bloquea el atrás */ }
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
        ModalBottomSheet(onDismissRequest = { showPhotoOptions = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Foto del Bebé", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurIntensity.dp),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Nuevo Perfil", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            enabled = !showWelcomeMessage // Bloquear navegación atrás
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Avatar Selector
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable(enabled = !showWelcomeMessage) { showPhotoOptions = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.fotoUri != null) {
                        AsyncImage(
                            model = state.fotoUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, null, modifier = Modifier.size(32.dp))
                            Text("Añadir Foto", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = { viewModel.onIntent(AddBabyIntent.UpdateNombre(it)) },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !showWelcomeMessage,
                    leadingIcon = { Icon(Icons.Default.ChildCare, null) },
                    shape = RoundedCornerShape(12.dp)
                )

                val dateText = state.fechaNacimiento?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                } ?: "Seleccionar fecha"

                OutlinedCard(
                    onClick = { if (!showWelcomeMessage) showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !showWelcomeMessage,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Fecha de Nacimiento", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(dateText, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        }
                        Icon(Icons.Default.CalendarToday, null)
                    }
                }

                Spacer(Modifier.weight(1f))

                if (state.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                Button(
                    onClick = { viewModel.onIntent(AddBabyIntent.SaveBaby) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !state.isLoading && !showWelcomeMessage
                ) {
                    Icon(Icons.Default.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Bebé", fontWeight = FontWeight.Bold)
                }
            }
        }

        // CAPA DE BLOQUEO Y DIFUMINADO
        AnimatedVisibility(
            visible = showWelcomeMessage,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
                    // Este clickable consume los eventos y bloquea todo lo de abajo
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} 
                    )
            )
        }

        // MENSAJE DE BIENVENIDA
        AnimatedVisibility(
            visible = showWelcomeMessage,
            enter = fadeIn(animationSpec = tween(500)) + scaleIn(initialScale = 0.8f),
            exit = fadeOut(animationSpec = tween(500)) + scaleOut(targetScale = 0.8f),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                modifier = Modifier
                    .padding(32.dp)
                    .wrapContentSize(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 12.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChildFriendly,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "¡Hola! 👋\nAquí podrás añadir a tu bebé o niño/a",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Crea un perfil para empezar a registrar su crecimiento y momentos especiales.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

private fun createTempPictureUri(context: Context): Uri {
    val tempFile = File(context.cacheDir, "temp_baby_${System.currentTimeMillis()}.jpg").apply {
        createNewFile()
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}
