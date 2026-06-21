package com.sample.wanandroidclean.feature.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.wanandroidclean.domain.entity.UserInfo
import org.koin.androidx.compose.koinViewModel

@Composable
fun MineScreen(
    onLoginClick: () -> Unit,
    onCollectionClick: () -> Unit,
    viewModel: MineViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading && uiState.userInfo == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (uiState.userInfo != null) {
                UserInfoHeader(userInfo = uiState.userInfo!!)
            } else {
                GuestHeader(onLoginClick = onLoginClick)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                tonalElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    MineMenuItem(
                        icon = Icons.Default.Star, 
                        title = "我的积分", 
                        value = uiState.userInfo?.coinCount?.toString()
                    )
                    MineMenuItem(icon = Icons.Default.Share, title = "我的分享")
                    MineMenuItem(
                        icon = Icons.Default.Favorite, 
                        title = "我的收藏",
                        onClick = onCollectionClick
                    )
                    // 修正点：改用基础图标库图标，解决 Unresolved reference 报错
                    MineMenuItem(icon = Icons.Default.Schedule, title = "稍后阅读")
                    MineMenuItem(icon = Icons.Default.Build, title = "开源项目")
                    MineMenuItem(icon = Icons.Default.Info, title = "关于作者")
                    MineMenuItem(icon = Icons.Default.Settings, title = "系统设置")
                    
                    if (uiState.userInfo != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        MineMenuItem(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            title = "退出登录",
                            tint = MaterialTheme.colorScheme.error,
                            onClick = { viewModel.logout() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoHeader(userInfo: UserInfo) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = userInfo.nickname,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "ID: ${userInfo.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "等级: ${userInfo.level}  排名: ${userInfo.rank}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun GuestHeader(onLoginClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onLoginClick() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "点击登录",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
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
    tint: Color = MaterialTheme.colorScheme.primary,
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
                tint = tint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title, 
                style = MaterialTheme.typography.bodyLarge, 
                color = if (tint == MaterialTheme.colorScheme.error) tint else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            
            if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null, 
                modifier = Modifier.size(12.dp), 
                tint = MaterialTheme.colorScheme.outline
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 54.dp), 
            thickness = 0.5.dp, 
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}
