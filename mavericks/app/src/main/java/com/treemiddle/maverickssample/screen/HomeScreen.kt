package com.treemiddle.maverickssample.screen

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
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.treemiddle.maverickssample.HomeViewModel
import com.treemiddle.maverickssample.ui.theme.MavericksSampleTheme

@Composable
fun HomeScreen() {
    HomeContent()
}

@Composable
fun HomeContent() {
    val viewModel: HomeViewModel = mavericksViewModel()
    val state = viewModel.collectAsState().value

    val scroll = rememberScrollState()
    val isShowDialog = remember { mutableStateOf(false) }
    val deviceIndex = remember { mutableStateOf(0) }
    val context = LocalContext.current

    if (state.fetchRequest is Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.Green)
        }
    }

    Column(modifier = Modifier.verticalScroll(scroll)) {
        Header(
            totalDeviceCount = state.totalDeviceCount,
            onTitleClicked = {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        )
        state.deviceList.forEachIndexed { _, deviceModel ->
            DeviceInformation(
                name = deviceModel.name,
                serialNumber = deviceModel.serialNumber,
                devcieIndex = deviceModel.index,
                isActivated = deviceModel.isActivated,
                onDeviceInformationClicked = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                },
                onDeleteClicked = {
                    deviceIndex.value = it
                    isShowDialog.value = true
                }
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
                        viewModel.removeDeviceModel(deviceIndex.value)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "삭제")
            }
        }
    }
}

@Composable
fun Header(
    totalDeviceCount: Int,
    onTitleClicked: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .height(56.dp)
            .clickable { onTitleClicked("타이틀 클릭") },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Mavericks 로그인 디바이스 관리: $totalDeviceCount",
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
    onDeviceInformationClicked: (String) -> Unit,
    onDeleteClicked: (Int) -> Unit
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
                .clickable { onDeviceInformationClicked(name) }
        ) {
            Text(text = "기종: $name")
            Text(text = "고유번호: $serialNumber")
        }

        Text(
            modifier = if (isActivated) {
                modifier
            } else {
                modifier.clickable { onDeleteClicked(devcieIndex) }
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
        Header(0, {})
    }
}

@Preview
@Composable
fun DeviceInformationPreview() {
    DeviceInformation(
        name = "test name",
        serialNumber = "test number",
        devcieIndex = 0,
        isActivated = false,
        onDeleteClicked = {},
        onDeviceInformationClicked = {}
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    MavericksSampleTheme {
        HomeScreen()
    }
}
