package com.care.kmp.presentation.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.care.kmp.domain.model.LatLng
import com.care.kmp.domain.model.MapMarker
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.viewinterop.WebElementView   // Compose Multiplatform web API
import com.care.kmp.JsMap
import com.care.kmp.createMapOptions
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun MapComponent(
    modifier: Modifier,
    cameraPosition: LatLng,
    zoom: Float,
    markers: List<MapMarker>
) {
    WebElementView(
        factory = {
            val div = document.createElement("div") as HTMLDivElement
            div.style.width = "100%"
            div.style.height = "100%"

            // Initialize Google Map on this div
            val options = createMapOptions(cameraPosition.latitude, cameraPosition.longitude, zoom.toInt())
            JsMap(div, options)

            div
        },
        modifier = modifier,
    )
}