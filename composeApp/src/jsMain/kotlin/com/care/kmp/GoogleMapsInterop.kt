package com.care.kmp

// src/wasmJsMain/kotlin/GoogleMapsInterop.kt

@JsName("google.maps.Map")
external class JsMap(element: dynamic, options: dynamic) {
    fun setCenter(latLng: dynamic)
    fun setZoom(zoom: Int)
}

@JsName("google.maps.LatLng")
external class JsLatLng(lat: Double, lng: Double)

@JsName("google.maps.Marker")
external class JsMarker(options: dynamic)

fun createMapOptions(lat: Double, lng: Double, zoom: Int): dynamic {
    val opts = js("{}")
    opts.center = js("{}")
    opts.center.lat = lat
    opts.center.lng = lng
    opts.zoom = zoom
    return opts
}