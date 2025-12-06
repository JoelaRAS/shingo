package com.crypticsignals.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Switch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crypticsignals.data.model.Direction
import com.crypticsignals.data.model.SignalStatus
import com.crypticsignals.ui.components.DirectionBadge
import com.crypticsignals.ui.components.GlassCard
import com.crypticsignals.ui.components.NeonBadge
import com.crypticsignals.ui.wallet.WalletConnectSection
import com.crypticsignals.ui.wallet.shortenAddress
import com.crypticsignals.wallet.WalletViewModel
import com.crypticsignals.viewmodel.ProfileUiState
import com.crypticsignals.viewmodel.SignalDraft

private data class TpInput(var price: String, var percentage: String)

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    walletAddress: String?,
    walletViewModel: WalletViewModel?,
    onCreateSignal: (SignalDraft) -> Unit,
    onRefresh: () -> Unit,
    onDisconnect: () -> Unit = {},
    onViewFollowing: () -> Unit = {},
    onViewFollowers: () -> Unit = {},
    onSignalClick: (String) -> Unit = {}
) {
    if (walletViewModel == null) {
        Text("Wallet non disponible dans ce contexte", color = Color.White, modifier = Modifier.padding(16.dp))
        return
    }
    val walletState by walletViewModel.uiState.collectAsStateWithLifecycle()
    val clipboard = LocalClipboardManager.current
    val baseAssets = listOf("BTC", "ETH", "SOL", "BNB", "XRP", "ADA", "AVAX", "LINK", "DOGE")
    val quoteAssets = listOf("USDT", "USDC", "USD", "EUR", "BTC", "ETH")
    val timeframes = listOf("M1", "M5", "M15", "M30", "H1", "H4", "D1", "W1", "MN1")

    var showCreateForm by remember { mutableStateOf(false) }
    val takeProfits = remember { mutableStateListOf(TpInput("", "100")) }
    var baseAsset by remember { mutableStateOf(baseAssets.first()) }
    var quoteAsset by remember { mutableStateOf(quoteAssets.first()) }
    var direction by remember { mutableStateOf(Direction.LONG) }
    var entry by remember { mutableStateOf("") }
    var stopLoss by remember { mutableStateOf("") }
    var timeframe by remember { mutableStateOf(timeframes[5]) }
    var confidence by remember { mutableStateOf(6f) }
    var formError by remember { mutableStateOf<String?>(null) }
    val displayName = "Sailor Zero" // mock alias instead of raw address
    val walletAddressDisplay = walletAddress ?: "Not connected"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(72.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text("W", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
            Column {
                Text(displayName, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = walletAddressDisplay,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.clickable(enabled = walletState.isConnected && walletAddress != null) {
                        walletAddress?.let { clipboard.setText(AnnotatedString(it)) }
                    }
                )
            }
        }

        if (!walletState.isConnected) {
            WalletConnectSection(walletViewModel = walletViewModel)
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                GlassCard(modifier = Modifier.weight(1f), onClick = onViewFollowers) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Followers", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        Text("1.2K", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
                GlassCard(modifier = Modifier.weight(1f), onClick = onViewFollowing) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Following", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        Text("12 traders", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    onClick = { showCreateForm = true }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        Text("Créer un signal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }
                GlassCard(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notifications", color = MaterialTheme.colorScheme.onSurface)
                        var notifEnabled by remember { mutableStateOf(true) }
                        Switch(checked = notifEnabled, onCheckedChange = { notifEnabled = it })
                    }
                }
            }

            Button(
                onClick = onDisconnect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFef4444))
            ) {
                Text("Déconnecter le wallet", fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Active signals block (scrollable area)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Mes signaux publiés", color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                val activeSignals = uiState.activeSignals.filter { it.status == SignalStatus.ACTIVE }
                if (activeSignals.isEmpty()) {
                    GlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.WifiOff, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                Text("Aucun signal publié", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            }
                            TextButton(onClick = onRefresh) {
                                Text("Refresh")
                            }
                        }
                    }
                } else {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 180.dp, max = 320.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            activeSignals.forEach { signal ->
                                GlassCard(onClick = { onSignalClick(signal.id) }) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(signal.pair, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                                                Text(signal.timeframe, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                            }
                                            NeonBadge(text = "LIVE", color = Color(0xFF10B981))
                                        }
                                        DirectionBadge(direction = signal.direction)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Entrée ${signal.entryPrice}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                            Text("SL ${signal.stopLoss}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateForm) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Créer un signal", color = Color.White, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showCreateForm = false }) {
                            Icon(Icons.Outlined.Close, contentDescription = null, tint = Color.White)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        DropdownField(
                            label = "Base",
                            options = baseAssets,
                            selected = baseAsset,
                            onSelected = { baseAsset = it },
                            modifier = Modifier.weight(1f)
                        )
                        DropdownField(
                            label = "Quote",
                            options = quoteAssets,
                            selected = quoteAsset,
                            onSelected = { quoteAsset = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Action", color = Color.White.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.clickable {
                            direction = if (direction == Direction.LONG) Direction.SHORT else Direction.LONG
                        }) {
                            DirectionBadge(direction = direction)
                        }
                    }

                    OutlinedTextField(
                        value = entry,
                        onValueChange = { entry = it },
                        label = { Text("Entry Price") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = stopLoss,
                        onValueChange = { stopLoss = it },
                        label = { Text("Stop Loss (required)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DropdownField(
                            label = "Timeframe",
                            options = timeframes,
                            selected = timeframe,
                            onSelected = { timeframe = it },
                            modifier = Modifier.weight(1f)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Confidence ${confidence.toInt()}/10", color = Color.White.copy(alpha = 0.7f))
                            Slider(
                                value = confidence,
                                onValueChange = { confidence = it },
                                valueRange = 1f..10f,
                                colors = androidx.compose.material3.SliderDefaults.colors(
                                    thumbColor = Color(0xFF6366F1),
                                    activeTrackColor = Color(0xFF6366F1)
                                )
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Take Profits", color = Color.White.copy(alpha = 0.7f))
                            TextButton(onClick = { takeProfits.add(TpInput("", "")) }) { Text("+ Add") }
                        }
                        takeProfits.forEachIndexed { index, tp ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = tp.price,
                                    onValueChange = { takeProfits[index] = tp.copy(price = it) },
                                    label = { Text("TP${index + 1} Price") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = tp.percentage,
                                    onValueChange = { takeProfits[index] = tp.copy(percentage = it) },
                                    label = { Text("%") },
                                    modifier = Modifier.width(90.dp),
                                    singleLine = true
                                )
                                if (takeProfits.size > 1) {
                                    IconButton(onClick = { takeProfits.removeAt(index) }) {
                                        Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color.White.copy(alpha = 0.7f))
                                    }
                                }
                            }
                        }
                    }

                    formError?.let { Text(it, color = Color(0xFFef4444)) }

                    Button(
                        onClick = {
                            val entryD = entry.toDoubleOrNull()
                            val stopD = stopLoss.toDoubleOrNull()
                            if (entryD == null || stopD == null) {
                                formError = "Entry and Stop Loss are required."
                                return@Button
                            }
                            val tps = takeProfits.mapNotNull { it.price.toDoubleOrNull() }
                            formError = null
                            onCreateSignal(
                                SignalDraft(
                                    pair = "$baseAsset/$quoteAsset",
                                    direction = direction,
                                    entryPrice = entryD,
                                    stopLoss = stopD,
                                    tpPrices = tps,
                                    timeframe = timeframe,
                                    confidence = confidence.toInt()
                                )
                            )
                            showCreateForm = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Text("Publier", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedTextField(
            value = selected,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
