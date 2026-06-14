package com.example.appdev_project_fitnessapp.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.appdev_project_fitnessapp.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController){
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) //Menu-Drawer
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Navigation", modifier = Modifier.padding(16.dp))
                NavigationDrawerItem(
                    label = { Text("${stringResource(id = R.string.home)}") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text("${stringResource(id = R.string.strength_training)}") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("strengthTraining")
                    },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                //TODO: add other Items (settings etc.)
            }
        }
    ){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Fitness App") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            //TODO: add content
            Column(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
            ) {
                //Text("${stringResource(id = R.string.home)}")
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                ) {
                    item{
                        homePageNavigationItem(stringResource(id = R.string.strength_training), Icons.Default.FitnessCenter, onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("strengthTraining")
                        })
                    }
                    //TODO: add other items
                }

            }

        }

    }

}


@Composable
fun homePageNavigationItem(title:String, icon: ImageVector, onClick: () -> Unit = {}){
    Box(
        //TODO: make clickable and add navigation on click
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp )
            .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))
            .padding(10.dp)
            .height(30.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ){
        Row(){
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(5.dp))
            Text(title)
        }
    }
}