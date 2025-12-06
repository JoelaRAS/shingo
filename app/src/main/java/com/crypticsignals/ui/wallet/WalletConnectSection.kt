package com.crypticsignals.ui.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.crypticsignals.wallet.WalletViewModel
import kotlinx.coroutines.launch

@Composable
fun WalletConnectSection(
    walletViewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by walletViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (uiState.isConnected) "Wallet Solana connecté" else "Connecte ton wallet Solana",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = if (uiState.isConnected) {
                    "Ton wallet est prêt à exécuter des signaux sur Solana."
                } else {
                    "Nous allons ouvrir ton wallet compatible (Phantom, Backpack…) pour autoriser CrypticSignals."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            uiState.address?.let { WalletAddressRow(it) }

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!uiState.isConnected) {
                    Button(
                        onClick = { scope.launch { walletViewModel.connect() } },
                        enabled = !uiState.isConnecting,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (uiState.isConnecting) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text("Connexion…")
                        } else {
                            Text("Connecter un wallet")
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { scope.launch { walletViewModel.disconnect() } },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Déconnecter")
                    }
                }
            }

            if (!uiState.isConnected) {
                TextButton(
                    onClick = { /* TODO: ouvrir une page d'explication */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Qu’est-ce qu’un wallet ?")
                }
            }
        }
    }
}

@Composable
private fun WalletAddressRow(address: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Adresse :", style = MaterialTheme.typography.bodyMedium)
        Text(
            text = shortenAddress(address),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

fun shortenAddress(address: String, take: Int = 4): String {
    return if (address.length <= take * 2) address else "${address.take(take)}...${address.takeLast(take)}"
}
