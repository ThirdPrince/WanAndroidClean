package com.sample.wanandroidclean.feature.mine

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.wanandroidclean.domain.entity.UserInfo
import org.koin.androidx.compose.koinViewModel

// 定义符合图中质感的颜色
val BrandPurple = Color(0xFF6200EE)
val BrandGradient = Brush.verticalGradient(listOf(Color(0xFF7C4DFF), Color(0xFF6200EE)))
val CardBackground = Color.White
val SurfaceBg = Color(0xFFF6F7F9)

@Composable
fun MineScreen(
    onLoginClick: () -> Unit,
    onCollectionClick: () -> Unit,
    viewModel: MineViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(SurfaceBg)) {
        // 1. 顶部紫色弧形背景 (参考图中异形设计)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
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

            // 头部：Hello, Nickname + Avatar
            MineTopHeader(uiState.userInfo, onLoginClick)

            Spacer(modifier = Modifier.height(24.dp))

            // 3. 悬浮快捷功能卡片 (积分/收藏/通知)
            QuickStatCard(uiState.userInfo, onCollectionClick)

            Spacer(modifier = Modifier.height(24.dp))

            // 4. 搜索框预览 (装饰用)
            SimpleSearchBox()

            Spacer(modifier = Modifier.height(24.dp))

            // 5. “关于作者” 专属全干工程师名片 (图中大卡片风格)
            AuthorInfoCard(
                onGithubClick = { uriHandler.openUri("https://github.com/ThirdPrince") },
                onJuejinClick = {
                    val juejinUrl = "https://juejin.cn/user/2313028195058471"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(juejinUrl)).apply {
                        setPackage("com.daimajia.gold") 
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try { context.startActivity(intent) } catch (e: Exception) { uriHandler.openUri(juejinUrl) }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 6. 九宫格菜单
            Text(
                text = "常用功能",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )
            MenuGrid(viewModel)

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun MineTopHeader(userInfo: UserInfo?, onLoginClick: () -> Unit) {
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
                fontSize = 26.sp,
                modifier = if (userInfo == null) Modifier.clickable { onLoginClick() } else Modifier
            )
        }
        
        Surface(
            modifier = Modifier.size(68.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.2f),
            border = BorderStroke(2.dp, Color.White)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.padding(14.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun QuickStatCard(userInfo: UserInfo?, onCollectionClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatActionItem(Icons.Default.Stars, "积分", userInfo?.coinCount?.toString() ?: "--")
            StatActionItem(Icons.Default.Favorite, "收藏", onClick = onCollectionClick)
            StatActionItem(Icons.Default.Notifications, "消息")
        }
    }
}

@Composable
fun StatActionItem(icon: ImageVector, label: String, value: String? = null, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(52.dp).clip(CircleShape).background(BrandPurple.copy(alpha = 0.1f)),
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
fun SimpleSearchBox() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Searching for...", color = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.weight(1f))
            Icon(Icons.Default.Search, null, tint = Color.Gray)
        }
    }
}

@Composable
fun AuthorInfoCard(onGithubClick: () -> Unit, onJuejinClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandPurple)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(Icons.Default.Terminal, null, tint = Color.White, modifier = Modifier.padding(10.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "关于作者", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = "全干工程师 (Full-Stack)", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "技术栈: Android / Java / iOS / 公众号 / Ktor",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onGithubClick,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("GitHub", color = BrandPurple, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onJuejinClick,
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                ) {
                    Text("掘金 App", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MenuGrid(viewModel: MineViewModel) {
    val items = listOf(
        Pair(Icons.Outlined.Share, "我的分享"),
        Pair(Icons.Outlined.List, "最近阅读"),
        Pair(Icons.Outlined.Code, "开源项目"),
        Pair(Icons.Outlined.Info, "关于作者"),
        Pair(Icons.Outlined.BookmarkBorder, "稍后阅读"),
        Pair(Icons.Outlined.Feedback, "反馈建议"),
        Pair(Icons.Outlined.Settings, "系统设置"),
        Pair(Icons.Outlined.Logout, "退出登录")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        for (i in items.indices step 4) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (j in 0 until 4) {
                    if (i + j < items.size) {
                        val item = items[i + j]
                        Box(modifier = Modifier.weight(1f)) {
                            GridMenuIcon(
                                icon = item.first, 
                                label = item.second,
                                onClick = { if (item.second == "退出登录") viewModel.logout() }
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
fun GridMenuIcon(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 4.dp)
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
        Text(text = label, fontSize = 11.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
    }
}
