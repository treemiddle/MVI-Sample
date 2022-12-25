package com.treemiddle.orbitsample.screen

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
import com.treemiddle.orbitsample.HomeSideEffect
import com.treemiddle.orbitsample.HomeViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    HomeContent(viewModel)
}

@Composable
fun HomeContent(
    viewModel: HomeViewModel
) {
    val state = viewModel.collectAsState()
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val isShowDialog = remember { mutableStateOf(false) }
    val deviceIndex = remember { mutableStateOf(0) }

    viewModel.collectSideEffect {
        when (it) {
            HomeSideEffect.ShowToast -> {
                Toast.makeText(context, "toast", Toast.LENGTH_SHORT).show()
            }
            is HomeSideEffect.Dialog -> {
                deviceIndex.value = it.deviceIndex
                isShowDialog.value = true
            }
        }
    }

    Column(modifier = Modifier.verticalScroll(scroll)) {
        Header(viewModel::setEvent)
        state.value.deviceList.forEachIndexed { _, deviceModel ->
            DeviceInformation(
                name = deviceModel.name,
                serialNumber = deviceModel.serialNumber,
                event = viewModel::setEvent,
                devcieIndex = deviceModel.index,
                isActivated = deviceModel.isActivated
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
                        viewModel.setEvent(HomeViewModel.HomeEvent.DialogDeletedClicked(deviceIndex.value))
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "삭제")
            }
        }
    }

    if (state.value.isLoading) {
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
    isActivated: Boolean,
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
                .clickable {
                    event(HomeViewModel.HomeEvent.DeviceInformationSelected)
                }
        ) {
            Text(text = "기종: $name")
            Text(text = "고유번호: $serialNumber")
        }

        Text(
            modifier = if (isActivated) {
                modifier
            } else {
                modifier.clickable {
                    event(HomeViewModel.HomeEvent.DeleteDevcieClicked(devcieIndex))
                }
            },
            text = if (isActivated) {
                "현재 접속 기기"
            } else {
                "삭제하기"
            },
            color = if (isActivated) {
                Color.Blue
            } else {
                Color.Black
            }
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
        devcieIndex = 0,
        isActivated = false
    )
}
