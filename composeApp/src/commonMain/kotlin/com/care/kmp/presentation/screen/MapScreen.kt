package com.care.kmp.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.care.kmp.domain.model.LatLng
import com.care.kmp.domain.model.MapMarker
import com.care.kmp.presentation.view.MapComponent

@Composable
fun MapScreen(){
    val mumbai = LatLng(latitude = 19.0760, longitude = 72.8777)

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Mumbai, India",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        MapComponent(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            cameraPosition = mumbai,
            zoom = 12f,
            markers = listOf(
                MapMarker(
                    position = mumbai,
                    title = "Mumbai"
                ),
                MapMarker(
                    position = LatLng(19.0330, 73.0297),
                    title = "Navi Mumbai"
                ),
            )
        )
    }
}