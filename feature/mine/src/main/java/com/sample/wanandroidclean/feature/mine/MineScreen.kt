package com.sample.wanandroidclean.feature.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.wanandroidclean.domain.entity.UserInfo
import org.koin.androidx.compose.koinViewModel

// 定义一些符合图中的颜色
val BrandPurple = Color(0xFF6200EE)
val LightPurple = Color(0xFFF2E7FE)

@Composable
fun MineScreen(
    onLoginClick: () -> Unit,
    onCollectionClick: () -> Unit,
    viewModel: MineViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)) // 浅灰色底
    ) {
        // 1. 顶部紫色弧形背景
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BrandPurple, BrandPurple.copy(alpha = 0.8f))
                    )
                )
        )

        // 2. 可滚动内容区
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 用户信息头部
            MineHeader(
                userInfo = uiState.userInfo,
                onLoginClick = onLoginClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. 悬浮快捷功能卡片
            QuickActionCard(
                coinCount = uiState.userInfo?.coinCount ?: 0,
                onCollectionClick = onCollectionClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. 搜索框预览 (装饰用)
            SearchSection()

            Spacer(modifier = Modifier.height(24.dp))

            // 5. 积分/等级大卡片
            LevelCard(uiState.userInfo)

            Spacer(modifier = Modifier.height(24.dp))

            // 6. 功能网格菜单
            MenuGridSection(onCollectionClick)

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun MineHeader(userInfo: UserInfo?, onLoginClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Hello,",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = userInfo?.nickname ?: "点击登录",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = if (userInfo == null) Modifier.clickable { onLoginClick() } else Modifier
            )
        }

        // 头像
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun QuickActionCard(coinCount: Int, onCollectionClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            QuickActionItem(Icons.Default.Stars, "我的积分", "$coinCount")
            QuickActionItem(Icons.Default.Favorite, "我的收藏", onClick = onCollectionClick)
            QuickActionItem(Icons.Default.Notifications, "消息通知")
        }
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String, value: String? = null, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(LightPurple),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BrandPurple)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        if (value != null) {
            Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun SearchSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Searching for...", color = Color.LightGray, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun LevelCard(userInfo: UserInfo?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF8E24AA), Color(0xFFE91E63))
                    )
                )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "等级 & 排名", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "Rank: ${userInfo?.rank ?: 0}",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Level: ${userInfo?.level ?: 0}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MenuGridSection(onCollectionClick: () -> Unit) {
    val menuItems = listOf(
        Pair(Icons.Outlined.Share, "我的分享"),
        Pair(Icons.Outlined.History, "浏览记录"),
        Pair(Icons.Outlined.Code, "开源项目"),
        Pair(Icons.Outlined.Info, "关于作者"),
        Pair(Icons.Outlined.Article, "稍后阅读"),
        Pair(Icons.Outlined.BugReport, "问题反馈"),
        Pair(Icons.Outlined.Settings, "系统设置"),
        Pair(Icons.Outlined.PowerSettingsNew, "退出登录")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        for (i in 0 until menuItems.size step 4) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (j in 0 until 4) {
                    if (i + j < menuItems.size) {
                        val item = menuItems[i + j]
                        GridMenuItem(item.first, item.second, Modifier.weight(1f))
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GridMenuItem(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.DarkGray,
            maxLines = 1
        )
    }
}
