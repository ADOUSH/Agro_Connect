package com.duoc.agroconnect.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.agroconnect.AppContainer
import com.duoc.agroconnect.NotificationHelper
import com.duoc.agroconnect.model.EstadoDron
import com.duoc.agroconnect.model.EstadoPedido
import com.duoc.agroconnect.model.Mision
import com.duoc.agroconnect.model.Pedido
import com.duoc.agroconnect.model.Usuario
import com.duoc.agroconnect.ui.theme.AzulAcento
import com.duoc.agroconnect.ui.theme.AzulSuave
import com.duoc.agroconnect.ui.theme.BlancoPuro
import com.duoc.agroconnect.ui.theme.GrisBorde
import com.duoc.agroconnect.ui.theme.GrisFondoSuave
import com.duoc.agroconnect.ui.theme.GrisTextoSecundario
import com.duoc.agroconnect.ui.theme.NegroIntenso
import com.duoc.agroconnect.ui.theme.RojoAcento
import com.duoc.agroconnect.ui.theme.RojoSuave
import com.duoc.agroconnect.viewmodel.EstadoViewModel
import com.duoc.agroconnect.viewmodel.LoginViewModel
import com.duoc.agroconnect.viewmodel.MapaViewModel
import com.duoc.agroconnect.viewmodel.PedidosViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AgroApp() {
    var logueado by remember { mutableStateOf(AppContainer.authRepository.usuarioActual != null) }

    if (logueado) {
        HomeScreen(onLogout = {
            AppContainer.authRepository.cerrarSesion()
            logueado = false
        })
    } else {
        AuthScreen(onAuthSuccess = { logueado = true })
    }
}

/* --------------------- INICIO DE SESIÓN Y REGISTRO --------------------- */

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    var esModoRegistro by rememberSaveable { mutableStateOf(false) }

    var nombre by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var confirmarClave by rememberSaveable { mutableStateOf("") }
    var mostrarClave by rememberSaveable { mutableStateOf(false) }

    val error by vm.error.collectAsState()

    Scaffold(containerColor = BlancoPuro) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            // 1. Emblema minimalista de dron en negro y azul
            EmblemaDronTech()

            Spacer(Modifier.height(16.dp))

            // Etiqueta superior
            Surface(
                shape = CircleShape,
                color = AzulSuave
            ) {
                Text(
                    text = "GESTION INTELIGENTE DE SERVICIOS AGRICOLAS",
                    style = MaterialTheme.typography.labelMedium,
                    color = AzulAcento,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            // Título de marca bicolor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Agro",
                    style = MaterialTheme.typography.headlineLarge,
                    color = NegroIntenso
                )
                Text(
                    text = "Connect",
                    style = MaterialTheme.typography.headlineLarge,
                    color = AzulAcento
                )
            }

            Text(
                text = if (esModoRegistro) {
                    "Crea tu perfil de operador para gestionar vuelos"
                } else {
                    "Ingresa para monitorear tu flota en tiempo real"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTextoSecundario
            )

            Spacer(Modifier.height(24.dp))

            // 2. Selector tipo cápsula (Iniciar sesión | Crear cuenta)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GrisFondoSuave,
                border = BorderStroke(1.dp, GrisBorde),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    BotonTabAuth(
                        texto = "Iniciar sesión",
                        seleccionado = !esModoRegistro,
                        onClick = {
                            esModoRegistro = false
                            vm.limpiarError()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    BotonTabAuth(
                        texto = "Crear cuenta",
                        seleccionado = esModoRegistro,
                        onClick = {
                            esModoRegistro = true
                            vm.limpiarError()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 3. Tarjeta blanca con borde fino para el formulario
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = BlancoPuro,
                border = BorderStroke(1.dp, GrisBorde),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AnimatedVisibility(visible = esModoRegistro) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre completo") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = NegroIntenso)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = coloresCampoTexto(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = NegroIntenso)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = coloresCampoTexto(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = clave,
                        onValueChange = { clave = it },
                        label = {
                            Text(if (esModoRegistro) "Contraseña (mín. 6 caracteres)" else "Contraseña")
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = NegroIntenso)
                        },
                        trailingIcon = {
                            TextButton(onClick = { mostrarClave = !mostrarClave }) {
                                Text(
                                    text = if (mostrarClave) "Ocultar" else "Ver",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AzulAcento
                                )
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = coloresCampoTexto(),
                        visualTransformation = if (mostrarClave) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    AnimatedVisibility(visible = esModoRegistro) {
                        OutlinedTextField(
                            value = confirmarClave,
                            onValueChange = { confirmarClave = it },
                            label = { Text("Confirmar contraseña") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = NegroIntenso)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = coloresCampoTexto(),
                            visualTransformation = if (mostrarClave) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Banner de error en rojo si los datos fallan
                    if (error.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RojoSuave,
                            border = BorderStroke(1.dp, RojoAcento.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = RojoAcento,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = error,
                                    color = RojoAcento,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(2.dp))

                    // Botón principal negro
                    Button(
                        onClick = {
                            if (esModoRegistro) {
                                if (vm.registrar(nombre, email, clave, confirmarClave)) {
                                    NotificationHelper.mostrar(
                                        context = context,
                                        titulo = "¡Bienvenido a AgroConnect!",
                                        mensaje = "Hola ${nombre.trim()}, tu cuenta fue creada con éxito."
                                    )
                                    onAuthSuccess()
                                }
                            } else {
                                if (vm.iniciarSesion(email, clave)) {
                                    onAuthSuccess()
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NegroIntenso,
                            contentColor = BlancoPuro
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = if (esModoRegistro) "Registrarme y entrar" else "Iniciar sesión",
                            style = MaterialTheme.typography.titleMedium,
                            color = BlancoPuro
                        )
                    }
                }
            }

            // 4. Acceso rápido con 1 toque para pruebas (solo en modo Login)
            if (!esModoRegistro) {
                Spacer(Modifier.height(18.dp))
                Surface(
                    onClick = {
                        email = "agricultor@tecmundo.cl"
                        clave = "agro1234"
                        vm.limpiarError()
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = GrisFondoSuave,
                    border = BorderStroke(1.dp, GrisBorde),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Cuenta de demostración",
                                style = MaterialTheme.typography.labelMedium,
                                color = NegroIntenso
                            )
                            Text(
                                text = "agricultor@tecmundo.cl · agro1234",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrisTextoSecundario
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = AzulSuave
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AzulAcento,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Rellenar",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = AzulAcento
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BotonTabAuth(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (seleccionado) NegroIntenso else Color.Transparent,
        modifier = modifier.height(42.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = texto,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (seleccionado) BlancoPuro else GrisTextoSecundario
            )
        }
    }
}

@Composable
private fun EmblemaDronTech() {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(NegroIntenso),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(44.dp)) {
            val w = size.width
            val h = size.height
            val centro = Offset(w / 2f, h / 2f)
            val margen = w * 0.2f

            val supIzq = Offset(margen, margen)
            val supDer = Offset(w - margen, margen)
            val infIzq = Offset(margen, h - margen)
            val infDer = Offset(w - margen, h - margen)

            // Brazos cruzados en blanco
            drawLine(BlancoPuro, supIzq, infDer, strokeWidth = 5f, cap = StrokeCap.Round)
            drawLine(BlancoPuro, supDer, infIzq, strokeWidth = 5f, cap = StrokeCap.Round)

            // Rotores en azul eléctrico
            listOf(supIzq, supDer, infIzq, infDer).forEach { rotor ->
                drawCircle(
                    color = AzulAcento,
                    radius = w * 0.14f,
                    center = rotor,
                    style = Stroke(width = 4f)
                )
                drawCircle(
                    color = BlancoPuro,
                    radius = 3.5f,
                    center = rotor
                )
            }

            // Núcleo central del dron
            drawCircle(color = BlancoPuro, radius = w * 0.13f, center = centro)
            drawCircle(color = AzulAcento, radius = w * 0.06f, center = centro)
        }
    }
}

/* ------------------------------ HOME ----------------------------- */

private enum class Tab(val titulo: String, val icono: ImageVector) {
    MAPA("Mapa", Icons.Default.Place),
    ESTADO("Estado", Icons.Default.Info),
    PEDIDOS("Pedidos", Icons.Default.ShoppingCart),
    PERFIL("Perfil", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit) {
    var tabIndex by rememberSaveable { mutableStateOf(0) }
    val tab = Tab.entries[tabIndex]

    Scaffold(
        containerColor = BlancoPuro,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "AgroConnect",
                            fontWeight = FontWeight.ExtraBold,
                            color = NegroIntenso
                        )
                        Text(
                            " · ${tab.titulo}",
                            fontWeight = FontWeight.Medium,
                            color = AzulAcento
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlancoPuro,
                    titleContentColor = NegroIntenso
                )
            )
        },
        bottomBar = {
            BarraInferiorRedondeada(
                tabSeleccionado = tabIndex,
                onSeleccionarTab = { tabIndex = it }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlancoPuro)
                .padding(padding)
        ) {
            AnimatedContent(
                targetState = tab,
                transitionSpec = {
                    fadeIn(tween(200)) togetherWith fadeOut(tween(150))
                },
                label = "transicion_pestanas"
            ) { pestanaActual ->
                when (pestanaActual) {
                    Tab.MAPA -> MapaScreen()
                    Tab.ESTADO -> EstadoScreen()
                    Tab.PEDIDOS -> PedidosScreen()
                    Tab.PERFIL -> PerfilScreen(onLogout = onLogout)
                }
            }
        }
    }
}

@Composable
private fun BarraInferiorRedondeada(
    tabSeleccionado: Int,
    onSeleccionarTab: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = BlancoPuro,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, GrisBorde),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Tab.entries.forEachIndexed { index, tab ->
                    val activo = tabSeleccionado == index
                    val fondoPill = if (activo) NegroIntenso else Color.Transparent
                    val colorContenido = if (activo) BlancoPuro else GrisTextoSecundario

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(fondoPill)
                            .clickable { onSeleccionarTab(index) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = tab.icono,
                            contentDescription = tab.titulo,
                            tint = colorContenido,
                            modifier = Modifier.size(20.dp)
                        )
                        if (activo) {
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = tab.titulo,
                                color = colorContenido,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ------------------------------ MAPA ----------------------------- */

private const val ZOOM_MAPA = 16f

@Composable
fun MapaScreen(vm: MapaViewModel = viewModel()) {
    val dron by vm.dron.collectAsState()
    val posicion = LatLng(dron.latitud, dron.longitud)
    val scope = rememberCoroutineScope()

    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(posicion, ZOOM_MAPA)
    }
    val marcador = rememberMarkerState(position = posicion)

    LaunchedEffect(posicion) {
        marcador.position = posicion
        camara.animate(CameraUpdateFactory.newLatLng(posicion), 1500)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa a pantalla completa con bordes redondeados arriba
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            cameraPositionState = camara,
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            Marker(
                state = marcador,
                title = dron.nombre,
                snippet = "Batería: ${dron.bateria}%",
                icon = BitmapDescriptorFactory.defaultMarker(
                    if (dron.estado == EstadoDron.EN_VUELO) {
                        BitmapDescriptorFactory.HUE_AZURE
                    } else {
                        BitmapDescriptorFactory.HUE_RED
                    }
                )
            )
        }

        // Botón flotante blanco para re-centrar cámara en el dron
        SmallFloatingActionButton(
            onClick = {
                scope.launch {
                    camara.animate(CameraUpdateFactory.newLatLngZoom(posicion, ZOOM_MAPA), 800)
                }
            },
            shape = CircleShape,
            containerColor = BlancoPuro,
            contentColor = NegroIntenso,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "Centrar dron")
        }

        // Tarjeta flotante de telemetría en la parte inferior
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = BlancoPuro.copy(alpha = 0.96f),
            shadowElevation = 10.dp,
            border = BorderStroke(1.dp, GrisBorde),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TELEMETRÍA EN VIVO",
                            style = MaterialTheme.typography.labelMedium,
                            color = AzulAcento
                        )
                        Text(
                            text = dron.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            color = NegroIntenso
                        )
                    }
                    EtiquetaEstadoDron(dron.estado)
                }

                HorizontalDivider(color = GrisBorde)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "COORDENADAS GPS",
                            style = MaterialTheme.typography.labelMedium,
                            color = GrisTextoSecundario
                        )
                        Text(
                            text = "${"%.5f".format(dron.latitud)}, ${"%.5f".format(dron.longitud)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = NegroIntenso
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "BATERÍA",
                            style = MaterialTheme.typography.labelMedium,
                            color = GrisTextoSecundario
                        )
                        Text(
                            text = "${dron.bateria}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (dron.bateria <= 20) RojoAcento else AzulAcento
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EtiquetaEstadoDron(estado: EstadoDron) {
    val esVuelo = estado == EstadoDron.EN_VUELO
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (esVuelo) AzulSuave else RojoSuave
    ) {
        Text(
            text = estado.texto,
            color = if (esVuelo) AzulAcento else RojoAcento,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

/* ----------------------------- ESTADO ---------------------------- */

@Composable
fun EstadoScreen(vm: EstadoViewModel = viewModel()) {
    val context = LocalContext.current
    val dron by vm.dron.collectAsState()
    val alertas by vm.alertas.collectAsState()

    val bateriaAnimada by animateFloatAsState(
        targetValue = dron.bateria / 100f,
        animationSpec = tween(600),
        label = "bateria_dron"
    )
    val totalHectareas = remember(vm.misiones) {
        vm.misiones.sumOf { it.hectareas }
    }

    LaunchedEffect(alertas.size) {
        alertas.firstOrNull()?.let { alerta ->
            NotificationHelper.mostrar(
                context = context,
                titulo = "AgroConnect · ${alerta.titulo}",
                mensaje = alerta.detalle,
                id = 2001
            )
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Panel superior estilo controlador: Anillo de batería + métricas laterales
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tarjeta izquierda: Anillo circular de batería
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = GrisFondoSuave),
                    border = BorderStroke(1.dp, GrisBorde),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = dron.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            color = NegroIntenso
                        )
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { bateriaAnimada },
                                color = if (dron.bateria <= 20) RojoAcento else AzulAcento,
                                trackColor = GrisBorde,
                                strokeWidth = 9.dp,
                                strokeCap = StrokeCap.Round,
                                modifier = Modifier.size(92.dp)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${dron.bateria}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NegroIntenso
                                )
                                Text(
                                    text = "Batería",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GrisTextoSecundario
                                )
                            }
                        }
                        EtiquetaEstadoDron(dron.estado)
                    }
                }

                // Columna derecha: 2 mini-tarjetas de métricas
                Column(
                    modifier = Modifier.weight(0.9f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BlancoPuro),
                        border = BorderStroke(1.dp, GrisBorde),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                text = "HECTÁREAS",
                                style = MaterialTheme.typography.labelMedium,
                                color = GrisTextoSecundario
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${"%.1f".format(totalHectareas)} ha",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = AzulAcento
                            )
                            Text(
                                text = "${vm.misiones.size} misiones",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrisTextoSecundario
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = NegroIntenso),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                text = "ENLACE GPS",
                                style = MaterialTheme.typography.labelMedium,
                                color = AzulSuave
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Óptimo",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BlancoPuro
                            )
                            Text(
                                text = "Telemetría 2s",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrisBorde
                            )
                        }
                    }
                }
            }
        }

        // Alertas del sistema
        item {
            Text(
                "Alertas del sistema",
                style = MaterialTheme.typography.titleMedium,
                color = NegroIntenso
            )
        }
        if (alertas.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = GrisFondoSuave,
                    border = BorderStroke(1.dp, GrisBorde),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AzulAcento,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Todo operando con normalidad · Sin alertas",
                            color = GrisTextoSecundario,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            items(alertas) { a ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RojoSuave),
                    border = BorderStroke(1.dp, RojoAcento.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(a.titulo, fontWeight = FontWeight.Bold, color = RojoAcento)
                        Text(a.detalle, color = NegroIntenso)
                    }
                }
            }
        }

        // Misiones recientes con íconos por categoría
        item {
            Text(
                "Misiones recientes",
                style = MaterialTheme.typography.titleMedium,
                color = NegroIntenso
            )
        }
        items(vm.misiones) { m ->
            TarjetaMision(m)
        }
    }
}

@Composable
private fun TarjetaMision(mision: Mision) {
    val (icono, colorFondo, colorIcono) = when (mision.tipo.lowercase()) {
        "pulverización" -> Triple(Icons.Default.Refresh, AzulSuave, AzulAcento)
        "monitoreo" -> Triple(Icons.Default.LocationOn, GrisFondoSuave, NegroIntenso)
        "siembra" -> Triple(Icons.Default.Star, AzulSuave, AzulAcento)
        else -> Triple(Icons.Default.Build, GrisFondoSuave, NegroIntenso)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BlancoPuro),
        border = BorderStroke(1.dp, GrisBorde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorFondo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = mision.tipo,
                    tint = colorIcono,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(mision.tipo, fontWeight = FontWeight.Bold, color = NegroIntenso)
                    Text(
                        "${mision.hectareas} ha",
                        fontWeight = FontWeight.ExtraBold,
                        color = AzulAcento,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    mision.descripcion,
                    color = GrisTextoSecundario,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    mision.fecha,
                    style = MaterialTheme.typography.bodySmall,
                    color = GrisTextoSecundario
                )
            }
        }
    }
}

/* ----------------------------- PEDIDOS --------------------------- */

@Composable
fun PedidosScreen(vm: PedidosViewModel = viewModel()) {
    val context = LocalContext.current
    val pedidos by vm.pedidos.collectAsState()
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    var pedidoSeleccionadoId by rememberSaveable { mutableStateOf<Int?>(null) }
    val pedidoSeleccionado = pedidos.firstOrNull { it.id == pedidoSeleccionadoId }

    Scaffold(
        containerColor = BlancoPuro,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                shape = CircleShape,
                containerColor = NegroIntenso,
                contentColor = BlancoPuro
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo pedido")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(pedidos, key = { it.id }) { p ->
                Card(
                    onClick = { pedidoSeleccionadoId = p.id },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = BlancoPuro),
                    border = BorderStroke(1.dp, GrisBorde),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "${p.producto} ×${p.cantidad}",
                                fontWeight = FontWeight.Bold,
                                color = NegroIntenso
                            )
                            if (p.notas.isNotBlank()) {
                                Text(p.notas, color = GrisTextoSecundario)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Toca para ver seguimiento",
                                style = MaterialTheme.typography.bodySmall,
                                color = AzulAcento
                            )
                        }
                        val (fondoBadge, colorTexto) = when (p.estado) {
                            EstadoPedido.PENDIENTE -> RojoSuave to RojoAcento
                            EstadoPedido.EN_CAMINO -> AzulSuave to AzulAcento
                            EstadoPedido.ENTREGADO -> GrisFondoSuave to NegroIntenso
                        }
                        Surface(shape = RoundedCornerShape(20.dp), color = fondoBadge) {
                            Text(
                                text = p.estado.texto,
                                color = colorTexto,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (pedidoSeleccionado != null) {
        DetallePedidoDialog(
            pedido = pedidoSeleccionado,
            onCerrar = { pedidoSeleccionadoId = null }
        )
    }

    if (mostrarDialogo) {
        NuevoPedidoDialog(
            onCancelar = { mostrarDialogo = false },
            onCrear = { producto, cantidad, notas ->
                vm.crearPedido(producto, cantidad, notas)
                NotificationHelper.mostrar(
                    context = context,
                    titulo = "Pedido registrado",
                    mensaje = "Se solicitó $producto (×$cantidad) correctamente."
                )
                mostrarDialogo = false
            }
        )
    }
}

/* ----------------------------- PERFIL ---------------------------- */

@Composable
fun PerfilScreen(
    onLogout: () -> Unit,
    pedidosVm: PedidosViewModel = viewModel(),
    estadoVm: EstadoViewModel = viewModel()
) {
    val context = LocalContext.current
    val usuario = AppContainer.authRepository.usuarioActual
        ?: Usuario("agricultor@tecmundo.cl", "Agricultor")
    val pedidos by pedidosVm.pedidos.collectAsState()
    val dron by estadoVm.dron.collectAsState()

    val iniciales = usuario.nombre
        .trim()
        .split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "AC" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(NegroIntenso),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales,
                color = BlancoPuro,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = usuario.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NegroIntenso
            )
            Text(
                text = usuario.email,
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTextoSecundario
            )
            Spacer(Modifier.height(6.dp))
            Surface(shape = RoundedCornerShape(20.dp), color = AzulSuave) {
                Text(
                    text = "Operador Agrícola Verificado",
                    color = AzulAcento,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GrisFondoSuave),
            border = BorderStroke(1.dp, GrisBorde),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DatoPerfilResumen(valor = dron.nombre, etiqueta = "Dron asignado", color = NegroIntenso)
                DatoPerfilResumen(valor = "${pedidos.size}", etiqueta = "Pedidos", color = AzulAcento)
                DatoPerfilResumen(valor = "${dron.bateria}%", etiqueta = "Batería", color = NegroIntenso)
            }
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BlancoPuro),
            border = BorderStroke(1.dp, GrisBorde),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Detalles de la cuenta",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NegroIntenso
                )
                FilaDetalle("Nombre", usuario.nombre)
                HorizontalDivider(color = GrisBorde)
                FilaDetalle("Correo registrado", usuario.email)
                HorizontalDivider(color = GrisBorde)
                FilaDetalle("Empresa / Predio", "Tech Mundo Agro · Región Metropolitana")
            }
        }

        OutlinedButton(
            onClick = {
                NotificationHelper.mostrar(
                    context = context,
                    titulo = "AgroConnect · Notificación activa",
                    mensaje = "Las alertas de tu cuenta ${usuario.nombre} están funcionando."
                )
            },
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, AzulAcento),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulAcento),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Probar notificación en este teléfono", fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RojoAcento,
                contentColor = BlancoPuro
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Cerrar sesión", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DatoPerfilResumen(valor: String, etiqueta: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = GrisTextoSecundario
        )
    }
}

/* --------------------- DIÁLOGOS Y UTILIDADES --------------------- */

@Composable
private fun DetallePedidoDialog(pedido: Pedido, onCerrar: () -> Unit) {
    val llegada = pedido.fechaEntrega?.let { formatearFecha(it) }
        ?: if (pedido.estado == EstadoPedido.EN_CAMINO) "Aún en camino" else "Pendiente de envío"
    val unidades = if (pedido.cantidad == 1) "1 unidad" else "${pedido.cantidad} unidades"

    AlertDialog(
        onDismissRequest = onCerrar,
        containerColor = BlancoPuro,
        title = { Text("Pedido N° ${pedido.id}", fontWeight = FontWeight.Bold, color = NegroIntenso) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Línea de progreso visual de 3 pasos
                LineaProgresoPedido(estado = pedido.estado)

                SeccionDetalle("Artículo")
                FilaDetalle("Producto", pedido.producto)
                FilaDetalle("Cantidad", unidades)
                FilaDetalle("Notas", pedido.notas.ifBlank { "Sin notas" })

                SeccionDetalle("Seguimiento")
                FilaDetalle("Hora del pedido", formatearFecha(pedido.fechaPedido))
                FilaDetalle("Hora de llegada", llegada)
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) { Text("Cerrar", color = AzulAcento, fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
private fun LineaProgresoPedido(estado: EstadoPedido) {
    val pasoActual = when (estado) {
        EstadoPedido.PENDIENTE -> 0
        EstadoPedido.EN_CAMINO -> 1
        EstadoPedido.ENTREGADO -> 2
    }
    val etapas = listOf("Pendiente", "En camino", "Entregado")

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = GrisFondoSuave,
        border = BorderStroke(1.dp, GrisBorde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                etapas.forEachIndexed { index, _ ->
                    val completado = index <= pasoActual
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (completado) AzulAcento else GrisBorde)
                    )
                    if (index < etapas.lastIndex) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .background(if (index < pasoActual) AzulAcento else GrisBorde)
                        )
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                etapas.forEachIndexed { index, nombre ->
                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (index == pasoActual) FontWeight.Bold else FontWeight.Normal,
                        color = if (index <= pasoActual) AzulAcento else GrisTextoSecundario
                    )
                }
            }
        }
    }
}

@Composable
private fun SeccionDetalle(titulo: String) {
    Text(
        titulo,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = AzulAcento
    )
}

@Composable
private fun FilaDetalle(titulo: String, valor: String) {
    Column {
        Text(titulo, style = MaterialTheme.typography.labelMedium, color = GrisTextoSecundario)
        Text(valor, style = MaterialTheme.typography.bodyLarge, color = NegroIntenso)
    }
}

private fun formatearFecha(milisegundos: Long): String =
    SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.forLanguageTag("es-CL")).format(Date(milisegundos))

@Composable
private fun NuevoPedidoDialog(
    onCancelar: () -> Unit,
    onCrear: (String, Int, String) -> Unit
) {
    var producto by rememberSaveable { mutableStateOf("") }
    var cantidad by rememberSaveable { mutableStateOf("1") }
    var notas by rememberSaveable { mutableStateOf("") }
    val cantidadNum = cantidad.toIntOrNull()
    val valido = producto.isNotBlank() && cantidadNum != null && cantidadNum > 0

    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = BlancoPuro,
        title = { Text("Nuevo pedido", fontWeight = FontWeight.Bold, color = NegroIntenso) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = producto,
                    onValueChange = { producto = it },
                    label = { Text("Producto") },
                    singleLine = true,
                    colors = coloresCampoTexto()
                )
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad") },
                    singleLine = true,
                    colors = coloresCampoTexto(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas (opcional)") },
                    colors = coloresCampoTexto()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = valido,
                onClick = { onCrear(producto.trim(), cantidadNum ?: 1, notas.trim()) }
            ) { Text("Crear", color = AzulAcento, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar", color = RojoAcento) }
        }
    )
}

@Composable
private fun coloresCampoTexto() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NegroIntenso,
    unfocusedBorderColor = GrisBorde,
    focusedLabelColor = NegroIntenso,
    cursorColor = AzulAcento
)