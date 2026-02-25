package com.sample.wanandroidclean.feature.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.wanandroidclean.domain.entity.UserInfo
import org.koin.androidx.compose.koinViewModel

@Composable
fun MineScreen(
    onLoginClick: () -> Unit,
    viewModel: MineViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)) // 整体深色背景
        ) {
            if (uiState.userInfo != null) {
                UserInfoHeader(userInfo = uiState.userInfo!!)
            } else {
                // 未登录状态的占位头部
                GuestHeader(onLoginClick = onLoginClick)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.background(Color(0xFF1E1E1E))) {
                MineMenuItem(icon = Icons.Default.Star, title = "我的积分", value = uiState.userInfo?.coinCount?.toString())
                MineMenuItem(icon = Icons.Default.Share, title = "我的分享")
                MineMenuItem(icon = Icons.Default.Favorite, title = "我的收藏")
                MineMenuItem(icon = Icons.Default.Favorite, title = "稍后阅读")
                MineMenuItem(icon = Icons.Default.Favorite, title = "开源项目")
                MineMenuItem(icon = Icons.Default.Info, title = "关于作者")
                MineMenuItem(icon = Icons.Default.Settings, title = "系统设置")
            }
        }
    }
}

@Composable
fun UserInfoHeader(userInfo: UserInfo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Avatar
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Color.DarkGray
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = userInfo.username,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Text(
                text = "ID: ${userInfo.id}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "等级: ${userInfo.level}  排名: ${userInfo.rank}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun GuestHeader(onLoginClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .clickable { onLoginClick() },
                shape = CircleShape,
                color = Color.DarkGray
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "请先登录",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}

@Composable
fun MineMenuItem(
    icon: ImageVector, 
    title: String, 
    value: String? = null,
    onClick: () -> Unit = {}
) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = title, 
                tint = Color(0xFF64B5F6), // 浅蓝色图标
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title, 
                style = MaterialTheme.typography.bodyLarge, 
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            
            if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null, 
                modifier = Modifier.size(14.dp), 
                tint = Color.Gray
            )
        }
        Divider(
            modifier = Modifier.padding(start = 56.dp), 
            thickness = 0.5.dp, 
            color = Color(0xFF333333)
        )
    }
}
