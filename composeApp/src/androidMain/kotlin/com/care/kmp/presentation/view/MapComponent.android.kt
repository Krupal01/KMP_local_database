package com.care.kmp.presentation.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.care.kmp.domain.model.LatLng
import com.care.kmp.domain.model.MapMarker
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng as GMSLatLng

@Composable
actual fun MapComponent(
    modifier: Modifier,
    cameraPosition: LatLng,
    zoom: Float,
    markers: List<MapMarker>
) {
    val gmsCenter = GMSLatLng(cameraPosition.latitude, cameraPosition.longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(gmsCenter, zoom)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
    ) {
        markers.forEach { marker ->
            Marker(
                state = MarkerState(
                    position = GMSLatLng(
                        marker.position.latitude,
                        marker.position.longitude
                    )
                ),
                title = marker.title,
            )
        }
    }
}