package com.example.tp3_hci.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.tp3_hci.ExerciseCard
import com.example.tp3_hci.ExerciseCardStatus
import com.example.tp3_hci.R
import com.example.tp3_hci.components.navigation.TopNavigationBar
import com.example.tp3_hci.components.routine.DifficultyIcons
import com.example.tp3_hci.components.routine.RatingStars
import com.example.tp3_hci.components.routine.RoutineImage
import com.example.tp3_hci.components.routine.RoutineTag

import com.example.tp3_hci.state_holders.RoutineDetail.RoutineDetailViewModel
import com.example.tp3_hci.ui.theme.FitiWhiteText
import com.example.tp3_hci.ui.theme.Shapes
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tp3_hci.data.model.Cycle
import com.example.tp3_hci.util.getViewModelFactory
import com.example.tp3_hci.utilities.ErrorSnackBar
import com.example.tp3_hci.utilities.TopAppBarType
import com.example.tp3_hci.utilities.navigation.RoutineDetailNavigation




@Composable
private fun RoutineData(
    name:String,
    difficulty: Int,
    creator: String,
    rating: Int,
    votes: Int
    ){
        val votesText = if(votes>1000){"${votes/1000} K"}else{"$votes"}
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ){
            Text(text = name, style = MaterialTheme.typography.h1)
            Row{
                Text(text = stringResource(R.string.difficulty), style = MaterialTheme.typography.h4)
                DifficultyIcons(difficulty = difficulty)
            }
            Text(text = "${stringResource(R.string.created_by)}: $creator",style = MaterialTheme.typography.h4)
            Row{
                RatingStars(rating = rating)
                Text(text = "($votesText)")
            }

        }
}

@Composable
private fun RoutineTags(
    tags: List<String>,
    modifier:Modifier = Modifier
){
    LazyRow(modifier = modifier){
        items(tags){tag->
            RoutineTag(
                text = tag,
                modifier = Modifier
                    .padding(3.dp)
                    .clip(Shapes.medium)
            )
        }
    }
}


@Composable
fun RoutineCycle(
    cycle: Cycle,
    status: ExerciseCardStatus = ExerciseCardStatus.VIEW_ONLY
){
    Card(
        modifier = Modifier.padding(8.dp),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.secondary
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
            Row (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp, 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
                    ){
                Text(text = cycle.name, style = MaterialTheme.typography.h3)
                Row (
                    verticalAlignment = Alignment.CenterVertically
                        ){
                    Icon(Icons.Outlined.Repeat,contentDescription = "Repeat icon")
                    Text(text = cycle.repetitions.toString(), style = MaterialTheme.typography.h3)
                }
            }
            cycle.exercises.forEach{ exercise ->
                ExerciseCard(
                    modifier = Modifier.padding(8.dp,0.dp),
                    elevation = 4.dp,
                    background = MaterialTheme.colors.background,
                    exercise = exercise
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetail(
    routineDetailNavigation: RoutineDetailNavigation,
    setTopAppBar : ((TopAppBarType)->Unit),
    routineId: Int,
    scaffoldState: ScaffoldState,
    viewModel: RoutineDetailViewModel = viewModel(factory = getViewModelFactory() )
){
    val uiState = viewModel.uiState
    if(!uiState.isFetching && uiState.routine==null && uiState.message==null){
        viewModel.getRoutineDetails(routineId)
    }
    var returned by remember { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    if(returned) {
        returned = false
        setTopAppBar(
            TopAppBarType(
                topAppBar = {
                    TopAppBar(
                        routineId = routineId,
                        scrollBehavior = scrollBehavior,
                        routineDetailNavigation = routineDetailNavigation,
                        routineDetailViewModel = viewModel,
                        isFavourite = viewModel.uiState.isFavourite
                    )
                }
            )
        )
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(8.dp),
                text = {Text(stringResource(id = R.string.start), color = Color.White, style = MaterialTheme.typography.h4)},
                icon = {Icon(Icons.Outlined.PlayArrow,"Play arrow",tint = Color.White)},
                onClick = {
                    returned = true
                    routineDetailNavigation.getExecuteRoutineScreen().invoke("$routineId")
                },
                shape = MaterialTheme.shapes.medium,
                backgroundColor = MaterialTheme.colors.onPrimary
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        if (uiState.isFetching) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                CircularProgressIndicator()
            }
        } else {
            if (uiState.routine != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = it
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .padding(8.dp, 8.dp)
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            RoutineData(
                                name = uiState.routine.name,
                                difficulty = uiState.routine.difficulty,
                                creator = uiState.routine.creator,
                                rating = uiState.routine.rating,
                                votes = uiState.routine.votes
                            )
                            RoutineImage(
                                source = uiState.routine.imageUrl,
                                contentDescription = "Routine Image",
                                modifier = Modifier
                                    .clip(Shapes.medium)
                                    .size(140.dp)
                                    .aspectRatio(1f / 1f)
                            )
                        }
                    }
                    item {
                        RoutineTags(tags = uiState.routine.tags, modifier = Modifier.padding(8.dp, 0.dp))
                    }
                    items(uiState.routine.cycles) {
                        RoutineCycle(it, status = ExerciseCardStatus.EDITABLE)
                    }
                }
            }
            if (uiState.message!=null){
                ErrorSnackBar(
                    scaffoldState = scaffoldState,
                    message = stringResource(id = uiState.message),
                    onActionLabelClicked = {
                        viewModel.dismissMessage()
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    routineDetailNavigation: RoutineDetailNavigation,
    routineId: Int,
    routineDetailViewModel : RoutineDetailViewModel,
    isFavourite: MutableState<Boolean>
){
    val clipboardManager: androidx.compose.ui.platform.ClipboardManager =
        LocalClipboardManager.current

    val context = LocalContext.current

    val shareText = "Mira esta Fiti Rutina! Seguro te interesa: https://fiti.com/Routine/${routineId}"

    val routineDetailUiState = routineDetailViewModel.uiState

    TopNavigationBar(
        scrollBehavior = scrollBehavior,
        leftIcon = {
            IconButton(onClick = {
                routineDetailNavigation.getPreviousScreen().invoke()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.search),
                    tint = FitiWhiteText,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        centerComponent = {
            Text(
                text = stringResource(id = R.string.fiti),
                style = MaterialTheme.typography.h2,
                color = FitiWhiteText
            )
        },
        secondRightIcon = {
            if(routineDetailUiState.routine != null){
                IconButton(onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    //clipboardManager.setText(AnnotatedString( "https://fiti.com/Routine/${routineId}"))

                    startActivity(
                        context,
                        Intent.createChooser(sendIntent, "ShareWith"),
                        null
                    )
                }) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(id = R.string.search),
                        tint = FitiWhiteText,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        rightIcon = {
            if(routineDetailUiState.routine != null){
                IconButton(onClick = {
                    routineDetailViewModel.toggleRoutineFavorite()
                }) {
                    if(isFavourite.value){
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            contentDescription = stringResource(id = R.string.routine_is_favorite),
                            tint = FitiWhiteText,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.routine_is_not_favorite),
                            tint = FitiWhiteText,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    )
}

/*
@Preview(showBackground = true)
@Composable
fun RoutineDetailPreview() {
    TP3_HCITheme {
        RoutineDetail(RoutineDetailUiState("Futbol",3,"Jose",3,120000,listOf("Hola","Como","estas", "buenas","tardes","Futbol","Scaloneta","Messi"), cycles))
    }
}
*/
