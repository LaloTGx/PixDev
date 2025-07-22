package com.lalo.pixdev.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lalo.pixdev.ui.screens.*
import com.lalo.pixdev.viewmodel.ColorViewModel
import com.lalo.pixdev.viewmodel.ProjectViewModel


@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            val projectViewModel: ProjectViewModel = hiltViewModel()
            HomeScreen(
                viewModel = projectViewModel,
                navController = navController
            )
        }

        composable(
            route = Routes.REQUIREMENTS,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectIdString = backStackEntry.arguments?.getString("projectId")
            if (projectIdString != null) {
                val projectViewModel: ProjectViewModel = hiltViewModel()
                val project by projectViewModel.getProjectById(projectIdString).collectAsState(initial = null)
                if (project != null){
                    RequirementScreen(project = project!!, navController = navController)
                }else{
                    Text("Cargando requisitos o Proyecto no encontrado", modifier = Modifier.padding(16.dp))
                }
            } else {
                Text("ID de proyecto inválido", modifier = Modifier.padding(16.dp))
            }
        }

        composable(
            route = Routes.COLORS,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectIdString = backStackEntry.arguments?.getString("projectId")
            if (projectIdString != null) {
                val projectViewModel: ProjectViewModel = hiltViewModel()
                val project by projectViewModel.getProjectById(projectIdString).collectAsState(initial = null)
                if (project != null){
                    val colorViewModel: ColorViewModel = hiltViewModel()
                    ColorsScreen(project = project!!, navController = navController, colorViewModel = colorViewModel)
                }else{
                    Text("Cargando colores o Proyecto no encontrado", modifier = Modifier.padding(16.dp))
                }
            } else {
                Text("ID de proyecto inválido", modifier = Modifier.padding(16.dp))
            }
        }

        composable(Routes.SETTINGS) {
            SettingScreen(navController = navController)
        }
    }
}