package com.care.kmp.presentation.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.care.kmp.domain.model.LatLng
import com.care.kmp.domain.model.MapMarker

@Composable
expect fun MapComponent(
    modifier: Modifier = Modifier,
    cameraPosition: LatLng,
    zoom: Float = 12f,
    markers: List<MapMarker> = emptyList(),
)