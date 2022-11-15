package com.example.tp3_hci.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tp3_hci.screens.ExecuteRoutine
import com.example.tp3_hci.screens.LoginView
import com.example.tp3_hci.R
import com.example.tp3_hci.screens.RoutineDetail
import com.example.tp3_hci.components.navigation.BottomNavItem
import com.example.tp3_hci.components.navigation.BottomNavigationBar
import com.example.tp3_hci.screens.RatingView
import com.example.tp3_hci.screens.cycles
import com.example.tp3_hci.data.RoutineDetailUiState
import com.example.tp3_hci.screens.FavoritesScreen
import com.example.tp3_hci.screens.MainScreen
import com.example.tp3_hci.screens.Routines
import com.example.tp3_hci.screens.SearchResultsScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyNavHost(
    modifier: Modifier = Modifier,
    startDestination: String = "Login"
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val navigationUtilities by remember {
        mutableStateOf(NavigationUtilities(navController))
    }

    var topAppBar by remember {
        mutableStateOf(TopAppBarType(topAppBar = null))
    }
    val changeTopAppBarType = { newTopAppBar : TopAppBarType ->
        topAppBar = newTopAppBar
    }

    var showBottomBar by rememberSaveable { mutableStateOf(false) }
    showBottomBar = when (navBackStackEntry?.destination?.route) {
        "Login" -> false // on this screen bottom bar should be hidden
        "MakeRoutine/{name}" -> false
        "RatingRoutine/{name}" -> false
        "SearchResults" -> false
        else -> true // in all other cases show bottom bar
    }

    val bottomNavItems : List<BottomNavItem> = listOf(
        BottomNavItem(R.string.bottom_nav_favorites, { navController.navigate("Favorites") }, Icons.Filled.Favorite),
        BottomNavItem(R.string.bottom_nav_home, { navController.navigate("MainScreen") }, Icons.Filled.Home),
        BottomNavItem(R.string.bottom_nav_profile, { navController.navigate("Favorites") }, Icons.Filled.Person)
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = expandVertically(
                    expandFrom = Alignment.Bottom
                ),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Bottom
                )
            ) {
                BottomNavigationBar(items = bottomNavItems)
            }
        },
        topBar = {
            AnimatedVisibility(
                visible = (topAppBar.getTopAppBar() != null),
                enter = expandVertically(
                    expandFrom = Alignment.Bottom
                ),
                exit = shrinkVertically(
                    shrinkTowards = Alignment.Bottom
                )
            ) {
                if(topAppBar.getTopAppBar() != null){
                    topAppBar.getTopAppBar()!!.invoke()
                }
            }
        }
    ){innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(innerPadding)
        ) {
            composable("Login") {
                LoginView(
                    setTopAppBar = changeTopAppBarType,
                    navigationUtilities = navigationUtilities
                )
            }
            composable("MainScreen"){
                MainScreen(
                    navigationUtilities = navigationUtilities,
                    setTopAppBar = changeTopAppBarType,
                    lastRoutineDone = Routines,
                    createdRoutines = Routines
                )
            }
            composable("Favorites"){
                FavoritesScreen(
                    navigationUtilities = navigationUtilities,
                    setTopAppBar = changeTopAppBarType,
                    favoriteRoutines = Routines
                )
            }
            composable("RoutineDetails/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })){

                //Todos estos filters despues se sacan y se pone el manejo de la API
                val aux = Routines.filter { routine -> routine.name == (it.arguments?.getString("name") ?: "") }.first()
                RoutineDetail(
                    navigationUtilities = navigationUtilities,
                    setTopAppBar = changeTopAppBarType,
                    routine = RoutineDetailUiState(aux.name,3,"Jose",aux.score,120000,aux.tags!!, cycles),
                    srcImg = aux.imageUrl!!
                )
            }
            composable("MakeRoutine/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })){

                val aux = Routines.filter { routine -> routine.name == it.arguments?.getString("name")!! }.first()
                ExecuteRoutine(
                    navigationUtilities = navigationUtilities,
                    setTopAppBar = changeTopAppBarType,
                    routine = RoutineDetailUiState(aux.name,3,"Jose",aux.score,120000,aux.tags!!, cycles),
                )
            }
            composable("RatingRoutine/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })){

                val aux = Routines.filter { routine -> routine.name == it.arguments?.getString("name")!! }.first()
                RatingView(
                    navigationUtilities = navigationUtilities,
                    setTopAppBar = changeTopAppBarType,
                    routine = aux
                )
            }
            composable("SearchResults/{search}",
                arguments = listOf(
                    navArgument("search") { type = NavType.StringType }
                )
            ){

                SearchResultsScreen(
                    stringSearched = it.arguments?.getString("search")!! ,
                    routinesFound = Routines,
                    navigationUtilities = navigationUtilities,
                    setTopAppBar = changeTopAppBarType
                )
            }
        }
    }
}




