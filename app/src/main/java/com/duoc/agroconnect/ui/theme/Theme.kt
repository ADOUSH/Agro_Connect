package com.duoc.agroconnect.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AgroLightColorScheme = lightColorScheme(
    primary = NegroIntenso,
    onPrimary = BlancoPuro,
    primaryContainer = GrisFondoSuave,
    onPrimaryContainer = NegroIntenso,
    secondary = AzulAcento,
    onSecondary = BlancoPuro,
    secondaryContainer = AzulSuave,
    onSecondaryContainer = AzulAcento,
    tertiary = RojoAcento,
    onTertiary = BlancoPuro,
    error = RojoAcento,
    onError = BlancoPuro,
    errorContainer = RojoSuave,
    onErrorContainer = RojoAcento,
    background = BlancoPuro,
    onBackground = NegroIntenso,
    surface = BlancoPuro,
    onSurface = NegroIntenso,
    surfaceVariant = GrisFondoSuave,
    onSurfaceVariant = GrisTextoSecundario,
    outline = GrisBorde
)

@Composable
fun AgroConnectTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AgroLightColorScheme,
        typography = Typography,
        content = content
    )
}