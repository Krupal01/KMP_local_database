package com.care.kmp.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.care.kmp.domain.model.LatLng
import com.care.kmp.domain.model.MapMarker
import com.care.kmp.presentation.contract.TodoEvents
import com.care.kmp.presentation.navigation.BottomNavItem
import com.care.kmp.presentation.navigation.Route
import com.care.kmp.presentation.view.AppBottomBar
import com.care.kmp.presentation.view.MapComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navigateToTodoList: () -> Unit
){
    val mumbai = LatLng(latitude = 19.0760, longitude = 72.8777)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mumbai, India",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                clickOnTab = {
                    when(it){
                        BottomNavItem.Map -> {}
                        BottomNavItem.Todo -> {
                            navigateToTodoList()
                        }
                    }
                },
                currentRoute = Route.Map,
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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


}