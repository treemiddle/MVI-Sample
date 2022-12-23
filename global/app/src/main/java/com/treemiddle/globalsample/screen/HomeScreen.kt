package com.treemiddle.globalsample.screen

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.treemiddle.globalsample.HomeViewModel
import com.treemiddle.globalsample.contract.HomeContract
import com.treemiddle.globalsample.ui.theme.GlobalSampleTheme
import com.treemiddle.globalsample.ui.theme.musinsaColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeContent(
        state = viewModel.uiState.collectAsState().value,
        effect = viewModel.effect,
        event = { viewModel.setEvent(it) }
    )
}

@Composable
fun HomeContent(
    state: HomeContract.HomeState,
    effect: Flow<HomeContract.HomeEffect>?,
    event: (event: HomeContract.HomeEvent) -> Unit
) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val isShowDialog = remember { mutableStateOf(false) }
    val deviceIndex = remember { mutableStateOf(0) }

    LaunchedEffect("SIDE_EFFECTS_KEY") {
        effect?.onEach {
            when (it) {
                HomeContract.HomeEffect.ShowToast -> {
                    Toast.makeText(context, "toast", Toast.LENGTH_SHORT).show()
                }
                is HomeContract.HomeEffect.Dialog -> {
                    deviceIndex.value = it.deviceIndex
                    isShowDialog.value = true
                }
            }
        }?.collect()
    }

    Column(modifier = Modifier.verticalScroll(scroll)) {
        Header(event)
        state.deviceList.forEachIndexed { _, deviceModel ->
            DeviceInformation(
                name = deviceModel.name,
                serialNumber = deviceModel.serialNumber,
                event = event,
                devcieIndex = deviceModel.index
            )
        }
    }

    if (isShowDialog.value) {
        Dialog(
            onDismissRequest = { isShowDialog.value = false }
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(Color.Gray)
                    .clickable {
                        isShowDialog.value = false
                        event(HomeContract.HomeEvent.DialogDeletedClicked(deviceIndex.value))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "삭제")
            }
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.Green)
        }
    }
}

@Composable
fun Header(event: (HomeContract.HomeEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .height(56.dp)
            .clickable { event(HomeContract.HomeEvent.TitleSelected) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "자동 로그인 디바이스 관리",
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun DeviceInformation(
    modifier: Modifier = Modifier,
    name: String,
    serialNumber: String,
    devcieIndex: Int,
    event: (HomeContract.HomeEvent) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color((musinsaColor.indices).random()))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier
                .weight(1f)
                .clickable {
                    event(HomeContract.HomeEvent.DeviceInformationSelected)
                }
        ) {
            Text(text = "기종: $name")
            Text(text = "고유번호: $serialNumber")
        }

        Text(
            modifier = modifier.clickable { event(HomeContract.HomeEvent.DeleteDevcieClicked(devcieIndex)) },
            text = "삭제하기"
        )
    }
}

@Preview
@Composable
fun HeaderPreview() {
    Surface {
        Header {

        }
    }
}

@Preview
@Composable
fun DeviceInformationPreview() {
    DeviceInformation(
        name = "test name",
        serialNumber = "test number",
        event = { },
        devcieIndex = 0
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    GlobalSampleTheme {
        HomeScreen()
    }
}