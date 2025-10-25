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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pokeshop.R // Asegúrate de que tu paquete R sea el correcto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    // Pasa los datos del usuario y un callback para el logout si lo necesitas
    username: String,
    email: String,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit
) {
    // 1. Estado para almacenar la URI de la imagen seleccionada
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // 2. API nativa para seleccionar contenido (la galería de imágenes)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            // Cuando el usuario selecciona una imagen, la URI se guarda en nuestro estado
            imageUri = uri
        }
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                // --- 2. AÑADE EL BOTÓN DE NAVEGACIÓN ---
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { // Llama a la nueva función
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            "Mi Perfil",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // 3. Contenedor de la imagen de perfil
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape) // Recorta la imagen en forma de círculo
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                    // Al hacer clic, se abre la galería
                    galleryLauncher.launch("image/*") // Filtramos para que solo muestre imágenes
                },
            contentAlignment = Alignment.Center
        ) {
            // Usamos Coil para cargar la imagen de forma asíncrona
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri) // La URI de la imagen seleccionada
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground), // Una imagen por defecto
                error = painterResource(id = R.drawable.ic_launcher_foreground), // Imagen si falla la carga
                contentDescription = "Imagen de perfil",
                contentScale = ContentScale.Crop, // Escala la imagen para que llene el círculo
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(
            text = "Toca la imagen para cambiarla",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Información del usuario (Nombre y Correo)
        ProfileInfoItem(
            icon = Icons.Default.Person,
            label = "Nombre de Usuario",
            value = username
        )

        ProfileInfoItem(
            icon = Icons.Default.Email,
            label = "Correo Electrónico",
            value = email
        )

        Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

        // Botón opcional para cerrar sesión
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión")
        }
        }
    }
}

@Composable
fun ProfileInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
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
            tint = MaterialTheme.colorScheme.primary
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
                fontSize = 18.sp
            )
        }
    }
}