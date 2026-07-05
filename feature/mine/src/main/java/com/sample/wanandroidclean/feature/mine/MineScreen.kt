package com.sample.wanandroidclean.feature.mine

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.wanandroidclean.domain.entity.UserInfo
import org.koin.androidx.compose.koinViewModel

// 高级感配色
val BrandPurple = Color(0xFF6200EE)
val BrandGradient = Brush.verticalGradient(listOf(Color(0xFF7C4DFF), Color(0xFF6200EE)))
val CardBackground = Color.White
val LightBg = Color(0xFFF8F9FA)

@Composable
fun MineScreen(
    onLoginClick: () -> Unit,
    onCollectionClick: () -> Unit,
    viewModel: MineViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current

    Box(modifier = Modifier.fillMaxSize().background(LightBg)) {
        // 1. 顶部紫色圆角背景 (参考图中异形设计)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
                .background(BrandGradient)
        )

        // 2. 滚动内容层
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // 头部：问候语 + 用户名 + 头像
            UserInfoSection(uiState.userInfo, onLoginClick)

            Spacer(modifier = Modifier.height(24.dp))

            // 3. 悬浮快捷卡片 (积分/收藏/消息)
            QuickActionSection(uiState.userInfo, onCollectionClick)

            Spacer(modifier = Modifier.height(24.dp))

            // 4. 搜索框预览 (装饰用)
            SimpleSearchBar()

            Spacer(modifier = Modifier.height(24.dp))

            // 5. “关于作者” 专属大卡片 (重点展示全干工程师身份)
            AuthorPromoCard(
                onGithubClick = { uriHandler.openUri("https://github.com/ThirdPrince") },
                onJuejinClick = { uriHandler.openUri("https://juejin.cn/user/2313028195058471") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 6. 网格功能菜单
            MenuGridSection(viewModel)

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun UserInfoSection(userInfo: UserInfo?, onLoginClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Hello,", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
            Text(
                text = userInfo?.nickname ?: "点击登录",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                modifier = if (userInfo == null) Modifier.clickable { onLoginClick() } else Modifier
            )
        }
        
        // 圆形头像
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.2f),
            border = BorderStroke(2.dp, Color.White)
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
fun QuickActionSection(userInfo: UserInfo?, onCollectionClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionItem(Icons.Default.Stars, "积分", userInfo?.coinCount?.toString() ?: "--")
            ActionItem(Icons.Default.Favorite, "收藏", onClick = onCollectionClick)
            ActionItem(Icons.Default.Notifications, "通知")
        }
    }
}

@Composable
fun ActionItem(icon: ImageVector, label: String, value: String? = null, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(50.dp).clip(CircleShape).background(BrandPurple.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BrandPurple)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        if (value != null) {
            Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun SimpleSearchBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Searching for...", color = Color.Gray.copy(alpha = 0.6f), modifier = Modifier.weight(1f))
            Icon(Icons.Default.Search, null, tint = Color.Gray)
        }
    }
}

@Composable
fun AuthorPromoCard(onGithubClick: () -> Unit, onJuejinClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandPurple)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // 背景装饰大图标
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                modifier = Modifier.size(150.dp).align(Alignment.BottomEnd).offset(x = 30.dp, y = 30.dp),
                tint = Color.White.copy(alpha = 0.1f)
            )
            
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "全干工程师 (Full-Stack)", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "关于作者", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "技术栈: Android / Java / iOS / 公众号 / Ktor",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row {
                    Button(
                        onClick = onGithubClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text("GitHub", color = BrandPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onJuejinClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("掘金", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MenuGridSection(viewModel: MineViewModel) {
    val menuItems = listOf(
        Pair(Icons.Outlined.Share, "分享项目"),
        Pair(Icons.Outlined.History, "最近阅读"),
        Pair(Icons.Outlined.Code, "开源项目"),
        Pair(Icons.Outlined.Info, "关于作者"),
        Pair(Icons.Outlined.BookmarkBorder, "稍后阅读"),
        Pair(Icons.Outlined.Feedback, "反馈建议"),
        Pair(Icons.Outlined.Settings, "系统设置"),
        Pair(Icons.Outlined.Logout, "退出登录")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        for (i in menuItems.indices step 4) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (j in 0 until 4) {
                    if (i + j < menuItems.size) {
                        val item = menuItems[i + j]
                        Box(modifier = Modifier.weight(1f)) {
                            GridMenuItem(
                                icon = item.first, 
                                label = item.second,
                                onClick = { if(item.second == "退出登录") viewModel.logout() }
                            )
                        }
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
fun GridMenuItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 8.dp)
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(16.dp),
            color = CardBackground,
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
    }
}
