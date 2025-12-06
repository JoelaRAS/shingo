package com.crypticsignals

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.crypticsignals.wallet.LocalActivityResultSender
import com.crypticsignals.wallet.SolanaWalletConnector
import com.crypticsignals.wallet.WalletUiState
import com.crypticsignals.wallet.WalletViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.crypticsignals.data.mock.MockData
import com.crypticsignals.data.mock.MockSignalRepository
import com.crypticsignals.data.mock.MockTraderRepository
import com.crypticsignals.ui.components.AuthRequiredScreen
import com.crypticsignals.ui.components.GlassCard
import com.crypticsignals.ui.components.TagChip
import com.crypticsignals.ui.screens.feed.ShingoGlyph
import com.crypticsignals.ui.screens.feed.MyFeedScreen
import com.crypticsignals.ui.screens.home.HomeScreen
import com.crypticsignals.ui.screens.profile.ProfileScreen
import com.crypticsignals.ui.screens.signaldetail.SignalDetailScreen
import com.crypticsignals.ui.screens.traders.TraderDetailScreen
import com.crypticsignals.viewmodel.ProfileViewModel
import com.crypticsignals.viewmodel.SignalsViewModel
import com.crypticsignals.viewmodel.TradersViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import coil.compose.AsyncImage

@Composable
fun CrypticSignalsApp() {
    val navController = rememberNavController()
    val traderRepository = remember { MockTraderRepository() }
    val signalRepository = remember { MockSignalRepository() }
    val mwaSender = LocalActivityResultSender.current
    val walletConnector = remember(mwaSender) { mwaSender?.let { SolanaWalletConnector(it) } }
    val walletViewModel: WalletViewModel? = walletConnector?.let { viewModel(factory = WalletViewModel.provideFactory(it)) }
    val walletState by (walletViewModel?.uiState ?: MutableStateFlow(WalletUiState())).collectAsStateWithLifecycle()

    val navItems = listOf(
        NavItem("home", "Market", Icons.Filled.Store),
        NavItem("feed", "My Signals", Icons.Filled.PhoneIphone),
        NavItem("profile", "Profile", Icons.Filled.Person)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { ShingoTopBar(navController) },
            bottomBar = {
                ShingoBottomBar(
                    navController = navController,
                    items = navItems
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home") {
                    val viewModel: TradersViewModel = viewModel(factory = TradersViewModel.provideFactory(traderRepository))
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    HomeScreen(
                        uiState = uiState,
                        onTraderClick = { traderId -> navController.navigate("trader/$traderId") }
                    )
                }
                composable("feed") {
                    val traderVm: TradersViewModel = viewModel(factory = TradersViewModel.provideFactory(traderRepository))
                    val tradersState by traderVm.uiState.collectAsStateWithLifecycle()
                    if (walletState.isConnected) {
                        MyFeedScreen(
                            traders = tradersState.traders,
                            signals = MockData.signals,
                            onSignalClick = { id -> navController.navigate("signal/$id") },
                            onMarketClick = { navController.navigate("home") },
                            onTraderClick = { navController.navigate("trader/$it") },
                            contentPadding = paddingValues
                        )
                    } else {
                        AuthRequiredScreen(
                            title = "Connect wallet to view signals",
                            subtitle = "Un wallet Solana (MWA) est requis pour accéder au feed.",
                            isConnecting = walletState.isConnecting,
                            error = walletState.errorMessage,
                            onConnect = { walletViewModel?.connect() }
                        )
                    }
                }
                composable(
                    route = "trader/{traderId}",
                    arguments = listOf(navArgument("traderId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val traderId = backStackEntry.arguments?.getString("traderId") ?: return@composable
                    val viewModel: SignalsViewModel = viewModel(factory = SignalsViewModel.provideFactory(signalRepository, traderRepository))
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    val trader by viewModel.selectedTrader.collectAsStateWithLifecycle()

                    LaunchedEffect(traderId) {
                        viewModel.loadSignals(traderId)
                    }

                    if (walletState.isConnected) {
                        TraderDetailScreen(
                            trader = trader,
                            signals = uiState.signals,
                            onSignalClick = { navController.navigate("signal/$it") },
                            onSubscribeClick = { /* mock */ }
                        )
                    } else {
                        AuthRequiredScreen(
                            title = "Connect wallet pour ce trader",
                            subtitle = "Connecte un wallet Solana pour voir les détails.",
                            isConnecting = walletState.isConnecting,
                            error = walletState.errorMessage,
                            onConnect = { walletViewModel?.connect() }
                        )
                    }
                }
                composable(
                    route = "signal/{signalId}",
                    arguments = listOf(navArgument("signalId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val signalId = backStackEntry.arguments?.getString("signalId") ?: return@composable
                    val viewModel: SignalsViewModel = viewModel(factory = SignalsViewModel.provideFactory(signalRepository, traderRepository))
                    val signal by viewModel.selectedSignal.collectAsStateWithLifecycle()
                    val trader by viewModel.selectedTrader.collectAsStateWithLifecycle()

                    LaunchedEffect(signalId) {
                        viewModel.getSignalById(signalId)
                    }

                    if (walletState.isConnected) {
                        SignalDetailScreen(signal = signal, trader = trader)
                    } else {
                        AuthRequiredScreen(
                            title = "Connect wallet pour ouvrir le signal",
                            subtitle = "Wallet Solana requis.",
                            isConnecting = walletState.isConnecting,
                            error = walletState.errorMessage,
                            onConnect = { walletViewModel?.connect() }
                        )
                    }
                }
                composable("profile") {
                    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.provideFactory(signalRepository, traderRepository))
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    if (walletState.isConnected) {
                        ProfileScreen(
                            uiState = uiState,
                            walletAddress = walletState.address,
                            walletViewModel = walletViewModel,
                            onCreateSignal = { draft -> viewModel.createSignal(draft) },
                            onRefresh = { viewModel.refreshActiveSignals() },
                            onDisconnect = { walletViewModel?.disconnect() },
                            onViewFollowing = {
                                navController.navigate("following") {
                                    launchSingleTop = true
                                }
                            },
                            onViewFollowers = {
                                navController.navigate("followers") {
                                    launchSingleTop = true
                                }
                            },
                            onSignalClick = { id -> navController.navigate("signal/$id") }
                        )
                    } else {
                        AuthRequiredScreen(
                            title = "Connect wallet pour ton profil",
                            subtitle = "Les infos profil et signaux sont accessibles après connexion.",
                            isConnecting = walletState.isConnecting,
                            error = walletState.errorMessage,
                            onConnect = { walletViewModel?.connect() }
                        )
                    }
                }
                composable("followers") {
                    val traderVm: TradersViewModel = viewModel(factory = TradersViewModel.provideFactory(traderRepository))
                    val tradersState by traderVm.uiState.collectAsStateWithLifecycle()
                    TraderListScreen(
                        title = "Followers",
                        traders = tradersState.traders,
                        onTraderClick = { navController.navigate("trader/$it") }
                    )
                }
                composable("following") {
                    val traderVm: TradersViewModel = viewModel(factory = TradersViewModel.provideFactory(traderRepository))
                    val tradersState by traderVm.uiState.collectAsStateWithLifecycle()
                    TraderListScreen(
                        title = "Following",
                        traders = tradersState.traders,
                        onTraderClick = { navController.navigate("trader/$it") }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShingoTopBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route.orEmpty()
    val isDetail = route.startsWith("trader/") || route.startsWith("signal/") || route == "followers" || route == "following"
    val detailTitle = when {
        route == "followers" -> "Followers"
        route == "following" -> "Following"
        else -> null
    }
    Surface(
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (isDetail) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    detailTitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    ShingoGlyph(size = 28.dp, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = "SHINGO",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

@Composable
private fun HexBadge() {
    val hexShape = GenericShape { size, _ ->
        val w = size.width
        val h = size.height
        moveTo(w * 0.5f, 0f)
        lineTo(w, h * 0.25f)
        lineTo(w, h * 0.75f)
        lineTo(w * 0.5f, h)
        lineTo(0f, h * 0.75f)
        lineTo(0f, h * 0.25f)
        close()
    }
    Box(
        modifier = Modifier
            .clip(hexShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
                    )
                )
            )
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), hexShape)
            .width(34.dp)
            .height(34.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.TrendingUp,
            contentDescription = null,
            tint = Color(0xFF01090F)
        )
    }
}

@Composable
private fun ShingoLogo(
    size: androidx.compose.ui.unit.Dp = 24.dp
) {
    val neon = MaterialTheme.colorScheme.onSurface
    Canvas(
        modifier = Modifier
            .height(size)
            .width(size)
    ) {
        val dim = size.toPx()
        val rOuter = dim * 0.083f
        val rCenter = dim * 0.138f
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f

        // four nodes
        drawCircle(color = neon.copy(alpha = 0.8f), radius = rOuter, center = androidx.compose.ui.geometry.Offset(cx, rOuter * 3f))
        drawCircle(color = neon.copy(alpha = 0.8f), radius = rOuter, center = androidx.compose.ui.geometry.Offset(this.size.width - rOuter * 3f, cy))
        drawCircle(color = neon.copy(alpha = 0.8f), radius = rOuter, center = androidx.compose.ui.geometry.Offset(cx, this.size.height - rOuter * 3f))
        drawCircle(color = neon.copy(alpha = 0.8f), radius = rOuter, center = androidx.compose.ui.geometry.Offset(rOuter * 3f, cy))

        // center
        drawCircle(color = neon, radius = rCenter, center = androidx.compose.ui.geometry.Offset(cx, cy))

        val stroke = 1.5.dp.toPx()
        // connecting lines
        drawLine(neon.copy(alpha = 0.4f), androidx.compose.ui.geometry.Offset(cx, rOuter * 3f), androidx.compose.ui.geometry.Offset(cx, cy), strokeWidth = stroke)
        drawLine(neon.copy(alpha = 0.4f), androidx.compose.ui.geometry.Offset(this.size.width - rOuter * 3f, cy), androidx.compose.ui.geometry.Offset(cx, cy), strokeWidth = stroke)
        drawLine(neon.copy(alpha = 0.4f), androidx.compose.ui.geometry.Offset(cx, this.size.height - rOuter * 3f), androidx.compose.ui.geometry.Offset(cx, cy), strokeWidth = stroke)
        drawLine(neon.copy(alpha = 0.4f), androidx.compose.ui.geometry.Offset(rOuter * 3f, cy), androidx.compose.ui.geometry.Offset(cx, cy), strokeWidth = stroke)
    }
}

@Composable
private fun ShingoBottomBar(
    navController: NavHostController,
    items: List<NavItem>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = navBackStackEntry?.destination
    Surface(
        tonalElevation = 0.dp,
        shadowElevation = 12.dp,
        color = Color(0xFF0A0A0C).copy(alpha = 0.95f),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = destination?.hierarchy?.any { it.route == item.route } == true
                val textColor = if (selected) Color.White else Color.White.copy(alpha = 0.5f)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(
                            if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            else Color.Transparent
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .clickable {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = textColor
                    )
                    Text(
                        text = item.label.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                }
            }
        }
    }
}

private data class NavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
private fun TraderListScreen(
    title: String,
    traders: List<com.crypticsignals.data.model.Trader>,
    onTraderClick: (String) -> Unit
) {
    if (traders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aucune entrée pour l'instant.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp + 80.dp)
        ) {
            items(traders) { trader ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onTraderClick(trader.id) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = trader.avatarUrl,
                            contentDescription = trader.name,
                            modifier = Modifier
                                .width(56.dp)
                                .height(56.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = trader.name,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = trader.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                maxLines = 2
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TagChip(text = "${trader.winRate}% WR")
                                TagChip(text = "${trader.totalSignals} calls")
                            }
                        }
                    }
                }
            }
        }
    }
}
