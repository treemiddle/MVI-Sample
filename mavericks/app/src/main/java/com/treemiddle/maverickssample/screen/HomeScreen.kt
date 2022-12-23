package com.treemiddle.maverickssample

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.treemiddle.maverickssample.ui.theme.MavericksSampleTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = mavericksViewModel()
    val state = viewModel.collectAsState()
    val effect = viewModel.homeEffect

    HomeContent(
        state = state.value,
        effect = effect,
        event = { viewModel.setEvent(it) }
    )
}

@Composable
fun HomeContent(
    state: HomeState,
    effect: Flow<HomeViewModel.HomeEffect>?,
    event: (HomeViewModel.HomeEvent) -> Unit
) {
    val scroll = rememberScrollState()
    val isShowDialog = remember { mutableStateOf(false) }
    val deviceIndex = remember { mutableStateOf(0) }
    val context = LocalContext.current

    LaunchedEffect("SIDE_EFFECTS_KEY") {
        effect?.onEach {
            when (it) {
                HomeViewModel.HomeEffect.ShowToast -> {
                    Toast.makeText(context, "toast", Toast.LENGTH_SHORT).show()
                }
                is HomeViewModel.HomeEffect.Dialog -> {
                    deviceIndex.value = it.deviceIndex
                    isShowDialog.value = true
                }
            }
        }?.collect()
    }


    Column(modifier = Modifier.verticalScroll(scroll)) {
        Header(event = event)
        state.deviceList.forEachIndexed { _, deviceModel ->
            DeviceInformation(
                name = deviceModel.name,
                serialNumber = deviceModel.serialNumber,
                devcieIndex = deviceModel.index,
                event = event
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
                        event(HomeViewModel.HomeEvent.DialogDeletedClicked(deviceIndex.value))
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
fun Header(event: (HomeViewModel.HomeEvent) -> Unit) {
    // NOTE : 필요에 따라 사용할 수도 있음
    // val viewModel: HomeViewModel = mavericksViewModel()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .height(56.dp)
            .clickable { event(HomeViewModel.HomeEvent.TitleSelected) },
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
    event: (HomeViewModel.HomeEvent) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier
                .weight(1f)
                .clickable { event(HomeViewModel.HomeEvent.DeviceInformationSelected) }
        ) {
            Text(text = "기종: $name")
            Text(text = "고유번호: $serialNumber")
        }

        Text(
            modifier = modifier.clickable {
                event(
                    HomeViewModel.HomeEvent.DeleteDevcieClicked(
                        devcieIndex
                    )
                )
            },
            text = "삭제하기"
        )
    }
}

@Preview
@Composable
fun HeaderPreview() {
    Surface {
        Header { }
    }
}

@Preview
@Composable
fun DeviceInformationPreview() {
    DeviceInformation(
        name = "test name",
        serialNumber = "test number",
        devcieIndex = 0
    ) { }
}

@Preview
@Composable
fun HomeScreenPreview() {
    MavericksSampleTheme {
        HomeScreen()
    }
}
