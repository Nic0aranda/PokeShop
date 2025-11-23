package com.example.pokeshop.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokeshop.R
import com.example.pokeshop.viewmodel.PokeShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: PokeShopViewModel,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val userState by viewModel.userState.collectAsState()
    val snackbarHostState = viewModel.snackbarHostState
    val isEditing = viewModel.isEditingProfile

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editando Perfil" else "Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) viewModel.cancelEditingProfile() else onNavigateBack()
                    }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        // ELIMINADO: floatingActionButton (Ya no lo usamos aquí)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // --- IMAGEN ---
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable(enabled = isEditing) {
                        if (isEditing) galleryLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.ic_launcher_background),
                    error = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Imagen de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (isEditing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_camera),
                            contentDescription = "Cambiar foto",
                            tint = Color.White
                        )
                    }
                }
            }

            if (isEditing) {
                Text(
                    text = "Toca para cambiar foto",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- CAMPOS ---

            if (isEditing) {
                OutlinedTextField(
                    value = viewModel.editUsername,
                    onValueChange = { viewModel.onEditUsernameChange(it) },
                    label = { Text("Nombre de Usuario") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                ProfileInfoItem(
                    icon = Icons.Default.Person,
                    label = "Nombre de Usuario",
                    value = userState.username
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileInfoItem(
                icon = Icons.Default.Email,
                label = "Correo Electrónico",
                value = userState.email,
                isReadOnly = true
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- FILA DE BOTONES (NUEVO DISEÑO) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre botones
            ) {

                // BOTÓN 1: Editar / Guardar (Peso 1)
                Button(
                    onClick = {
                        if (isEditing) {
                            viewModel.saveProfileChanges()
                        } else {
                            viewModel.startEditingProfile()
                        }
                    },
                    modifier = Modifier.weight(1f), // Ocupa el 50%
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isEditing) "Guardar" else "Editar")
                }

                // BOTÓN 2: Cerrar Sesión (Peso 1)
                // Si estamos editando, podríamos ocultarlo o dejarlo. Lo dejaré visible.
                Button(
                    onClick = onLogout,
                    modifier = Modifier.weight(1f), // Ocupa el otro 50%
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Salir")
                }
            }
        }
    }
}

// (Mantén la función auxiliar ProfileInfoItem igual que antes)
@Composable
fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String,
    isReadOnly: Boolean = false
) {
    val contentColor = if (isReadOnly) Color.Gray else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = if (isReadOnly) Color.Gray else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                color = contentColor
            )
        }
    }
}