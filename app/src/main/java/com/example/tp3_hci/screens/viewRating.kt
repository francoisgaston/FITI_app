package com.example.tp3_hci.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import com.example.tp3_hci.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tp3_hci.components.navigation.TopNavigationBar
import com.example.tp3_hci.components.review.RatingBar
import com.example.tp3_hci.data.view_model.RatingViewModel

import com.example.tp3_hci.ui.theme.FitiBlack
import com.example.tp3_hci.ui.theme.FitiBlue
import com.example.tp3_hci.ui.theme.FitiGreenButton
import com.example.tp3_hci.ui.theme.FitiWhiteText
import com.example.tp3_hci.util.getViewModelFactory
import com.example.tp3_hci.utilities.TopAppBarType
import com.example.tp3_hci.utilities.navigation.ViewRatingNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingView(
    viewRatingNavigation: ViewRatingNavigation,
    setTopAppBar : ((TopAppBarType)->Unit),
    routineId: Int,
    viewModel: RatingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = getViewModelFactory())
){
    val uiState = viewModel.uiState
    if(!uiState.isFetching && uiState.routine==null && uiState.message==null){
        viewModel.getRoutineOverview(routineId)
    }
    var returned by remember { mutableStateOf(true) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    if(returned) {
        returned = false
        setTopAppBar(
            TopAppBarType(
                topAppBar = {
                    TopAppBar(
                        scrollBehavior = scrollBehavior,
                        title = stringResource(id = R.string.routine_review),
                        viewRatingNavigation = viewRatingNavigation
                    )
                }
            )
        )
    }

    if(uiState.isFetching){
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            CircularProgressIndicator()
        }
    }else{
        if(uiState.routine!=null){
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Congratulations(
                    routineName = uiState.routine.name,
                    imageUrl = uiState.routine.imageUrl?:""
                )
                Spacer(modifier = Modifier.height(20.dp))
                Valoration()
                Spacer(modifier = Modifier.height(20.dp))
                ShareURL(routineId = routineId)
                Spacer(modifier = Modifier.height(20.dp))
                ButtonSide(
                    viewRatingNavigation = viewRatingNavigation,
                    routineId = routineId,
                    onClickSave = {
                        viewModel.ratingRoutine(routineId)
                        viewRatingNavigation.getHomeScreen().invoke() }
                )
            }
        }else if(uiState.message!=null){
            //TODO: cambiar
            Text(text = uiState.message)
        }
    }
    
}

@Composable
private fun ShareURL(
    routineId : Int
) {
    val clipboardManager: androidx.compose.ui.platform.ClipboardManager =
        LocalClipboardManager.current

    Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,) {

        Text(
            text = stringResource(id = R.string.share_this_routine),
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Card( border = BorderStroke(2.dp, FitiBlack),
            modifier = Modifier,
            backgroundColor = Color.White
        ) {
            Row( verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 10.dp) ) {
                val route = "https://fiti.com/Routine/${routineId}"
                Text(
                    text = route,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                IconButton(onClick = {
                    clipboardManager.setText(AnnotatedString(route))
                }){
                    Icon(painter = rememberVectorPainter(Icons.Rounded.CopyAll), contentDescription = "copy")
                }
            }
        }
    }
}


@Composable
private fun Congratulations(
    imageUrl: String,
    routineName: String
){
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Rutina",
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.congratulations),
            style = MaterialTheme.typography.h1.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        Text(
            text = "${stringResource(id = R.string.you_finished_the_routine)} $routineName",
            color = Color.Black
        )
    }
}


@Composable
private fun Valoration(){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.leave_your_review),
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Row() {
            RatingBar(Modifier, 0)
        }
    }
}


@Composable
private fun ButtonSide(
    viewRatingNavigation: ViewRatingNavigation,
    routineId : Int,
    onClickSave: ()->Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = FitiGreenButton,
            ),
            onClick = onClickSave,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .height(50.dp)
                .width(150.dp)
        ) {
            Text(
                text = stringResource(id = R.string.save),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h4,
                color = Color.White
            )
        }
        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = FitiBlue,
            ),
            onClick = {
                viewRatingNavigation.getHomeScreen().invoke()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .height(50.dp)
                .width(150.dp)
        ) {
            Text(
                text = stringResource(id = R.string.not_now),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h4,
                color = Color.White
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    viewRatingNavigation: ViewRatingNavigation,
    scrollBehavior: TopAppBarScrollBehavior,
    title: String
){

    TopNavigationBar(
        scrollBehavior = scrollBehavior,
        leftIcon = {
            IconButton(onClick = {
                viewRatingNavigation.getPreviousScreen().invoke()
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
                text = title,
                style = MaterialTheme.typography.h2,
                color = FitiWhiteText
            )
        }
    )
}

