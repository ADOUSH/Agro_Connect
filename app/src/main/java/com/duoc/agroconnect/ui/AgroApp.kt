package com.duoc.agroconnect.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.agroconnect.AppContainer
import com.duoc.agroconnect.R
import com.duoc.agroconnect.model.EstadoDron
import com.duoc.agroconnect.model.EstadoPedido
import com.duoc.agroconnect.model.Pedido
import com.duoc.agroconnect.viewmodel.EstadoViewModel
import com.duoc.agroconnect.viewmodel.LoginViewModel
import com.duoc.agroconnect.viewmodel.MapaViewModel
import com.duoc.agroconnect.viewmodel.PedidosViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AgroApp() {
    // La sesión se guarda en el teléfono: si ya había una iniciada, entramos directo al Home.
    var logueado by remember { mutableStateOf(AppContainer.authRepository.usuarioActual != null) }

    if (!logueado) {
        LoginScreen(onLoginOk = { logueado = true })
    } else {
        HomeScreen(onLogout = {
            AppContainer.authRepository.cerrarSesion()
            logueado = false
        })
    }
}

/* ----------------------------- LOGIN ----------------------------- */

@Composable
fun LoginScreen(onLoginOk: () -> Unit, vm: LoginViewModel = viewModel()) {
    var email by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    val error by vm.error.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.dron_campo),
                contentDescription = "Dron fumigando un campo de cultivo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "AgroConnect",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Control de drones agrícolas", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = clave,
                    onValueChange = { clave = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { if (vm.iniciarSesion(email, clave)) onLoginOk() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Iniciar sesión") }

                Spacer(Modifier.height(24.dp))
                Text(
                    "Usuario de prueba:\nagricultor@tecmundo.cl / agro1234",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/* ------------------------------ HOME ----------------------------- */

private enum class Tab(val titulo: String, val icono: ImageVector) {
    MAPA("Mapa", Icons.Default.Place),
    ESTADO("Estado", Icons.Default.Info),
    PEDIDOS("Pedidos", Icons.Default.ShoppingCart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onLogout: () -> Unit) {
    var tabIndex by rememberSaveable { mutableStateOf(0) }
    val tab = Tab.entries[tabIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AgroConnect · ${tab.titulo}") },
                actions = { TextButton(onClick = onLogout) { Text("Salir") } }
            )
        },
        bottomBar = {
            NavigationBar {
                Tab.entries.forEachIndexed { i, t ->
                    NavigationBarItem(
                        selected = tabIndex == i,
                        onClick = { tabIndex = i },
                        icon = { Icon(t.icono, contentDescription = t.titulo) },
                        label = { Text(t.titulo) }
                    )
                }
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            when (tab) {
                Tab.MAPA -> MapaScreen()
                Tab.ESTADO -> EstadoScreen()
                Tab.PEDIDOS -> PedidosScreen()
            }
        }
    }
}

/* ------------------------------ MAPA ----------------------------- */

private val VerdeOscuro = Color(0xFF558B2F)
private val Naranja = Color(0xFFF57C00)
private const val ZOOM_MAPA = 16f

@Composable
fun MapaScreen(vm: MapaViewModel = viewModel()) {
    val dron by vm.dron.collectAsState()
    val posicion = LatLng(dron.latitud, dron.longitud)

    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(posicion, ZOOM_MAPA)
    }
    val marcador = rememberMarkerState(position = posicion)

    // Mueve el marcador y hace que el mapa siga al dron cada vez que cambia su posición.
    LaunchedEffect(posicion) {
        marcador.position = posicion
        camara.animate(CameraUpdateFactory.newLatLng(posicion), 1500)
    }

    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .clip(RoundedCornerShape(24.dp)),
            cameraPositionState = camara
        ) {
            Marker(
                state = marcador,
                title = dron.nombre,
                snippet = "Batería: ${dron.bateria}%",
                icon = BitmapDescriptorFactory.defaultMarker(
                    if (dron.estado == EstadoDron.EN_VUELO) {
                        BitmapDescriptorFactory.HUE_AZURE
                    } else {
                        BitmapDescriptorFactory.HUE_ORANGE
                    }
                )
            )
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(dron.nombre, style = MaterialTheme.typography.titleMedium)
                Text("Estado: ${dron.estado.texto}")
                Text("Lat: ${"%.5f".format(dron.latitud)}   Lon: ${"%.5f".format(dron.longitud)}")
                Text("Batería: ${dron.bateria}%")
            }
        }
    }
}

/* ----------------------------- ESTADO ---------------------------- */

@Composable
fun EstadoScreen(vm: EstadoViewModel = viewModel()) {
    val dron by vm.dron.collectAsState()
    val alertas by vm.alertas.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(dron.nombre, style = MaterialTheme.typography.titleMedium)
                    Text("Estado: ${dron.estado.texto}")
                    Text("Batería: ${dron.bateria}%")
                    LinearProgressIndicator(
                        progress = { dron.bateria / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item { Text("Alertas", style = MaterialTheme.typography.titleMedium) }
        if (alertas.isEmpty()) {
            item { Text("Sin alertas") }
        } else {
            items(alertas) { a ->
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(a.titulo, fontWeight = FontWeight.Bold)
                        Text(a.detalle)
                    }
                }
            }
        }

        item { Text("Misiones recientes", style = MaterialTheme.typography.titleMedium) }
        items(vm.misiones) { m ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("${m.tipo} · ${m.hectareas} ha", fontWeight = FontWeight.Bold)
                    Text(m.descripcion)
                    Text(m.fecha, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

/* ----------------------------- PEDIDOS --------------------------- */

@Composable
fun PedidosScreen(vm: PedidosViewModel = viewModel()) {
    val pedidos by vm.pedidos.collectAsState()
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    var pedidoSeleccionadoId by rememberSaveable { mutableStateOf<Int?>(null) }
    val pedidoSeleccionado = pedidos.firstOrNull { it.id == pedidoSeleccionadoId }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo pedido")
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(pedidos, key = { it.id }) { p ->
                Card(
                    onClick = { pedidoSeleccionadoId = p.id },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("${p.producto} ×${p.cantidad}", fontWeight = FontWeight.Bold)
                            if (p.notas.isNotBlank()) Text(p.notas)
                        }
                        Text(
                            p.estado.texto,
                            color = when (p.estado) {
                                EstadoPedido.PENDIENTE -> Naranja
                                EstadoPedido.EN_CAMINO -> Color(0xFF1565C0)
                                EstadoPedido.ENTREGADO -> VerdeOscuro
                            }
                        )
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
                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun DetallePedidoDialog(pedido: Pedido, onCerrar: () -> Unit) {
    val llegada = pedido.fechaEntrega?.let { formatearFecha(it) }
        ?: if (pedido.estado == EstadoPedido.EN_CAMINO) "Aún en camino" else "Pendiente de envío"
    val unidades = if (pedido.cantidad == 1) "1 unidad" else "${pedido.cantidad} unidades"

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Pedido N° ${pedido.id}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SeccionDetalle("Artículo")
                FilaDetalle("Producto", pedido.producto)
                FilaDetalle("Cantidad", unidades)
                FilaDetalle("Notas", pedido.notas.ifBlank { "Sin notas" })

                SeccionDetalle("Seguimiento")
                FilaDetalle("Estado", pedido.estado.texto)
                FilaDetalle("Hora del pedido", formatearFecha(pedido.fechaPedido))
                FilaDetalle("Hora de llegada", llegada)
            }
        },
        confirmButton = { TextButton(onClick = onCerrar) { Text("Cerrar") } }
    )
}

@Composable
private fun SeccionDetalle(titulo: String) {
    Text(
        titulo,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun FilaDetalle(titulo: String, valor: String) {
    Column {
        Text(titulo, style = MaterialTheme.typography.labelMedium)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
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
        title = { Text("Nuevo pedido") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = producto, onValueChange = { producto = it },
                    label = { Text("Producto") }, singleLine = true
                )
                OutlinedTextField(
                    value = cantidad, onValueChange = { cantidad = it },
                    label = { Text("Cantidad") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = notas, onValueChange = { notas = it },
                    label = { Text("Notas (opcional)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = valido,
                onClick = { onCrear(producto.trim(), cantidadNum ?: 1, notas.trim()) }
            ) { Text("Crear") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
