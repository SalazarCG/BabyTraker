package com.salazar.babytraker.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.salazar.babytraker.core.domain.model.Baby

@Composable
fun BabyAvatar(
    baby: Baby,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Animamos la escala para que crezca sin empujar el layout de abajo
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.25f else 1.0f,
        label = "avatarScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        // El contenedor mantiene un tamaño fijo (75dp) para evitar el "rebote"
        Box(
            modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = baby.fotoUri ?: "https://via.placeholder.com/150",
                contentDescription = baby.nombre,
                modifier = Modifier
                    .size(60.dp) // Tamaño base
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) Color(0xFF4FC3F7) else Color.Transparent,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )
        }
        Text(
            text = baby.nombre,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF4FC3F7) else Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
