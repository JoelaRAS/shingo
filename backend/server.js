/**
 * Backend provisoire pour simuler un endpoint Privy côté serveur.
 * Ne commit jamais un vrai secret ici. Utilise un fichier .env local ou des variables d'environnement.
 */

const express = require("express");
const cors = require("cors");
const bodyParser = require("body-parser");

const app = express();
app.use(cors());
app.use(bodyParser.json());

const APP_ID = process.env.PRIVY_APP_ID || "cmisd4q3v01dbl50cp96yaban";
const APP_SECRET = process.env.PRIVY_APP_SECRET || "";
const PORT = process.env.PORT || 8080;

if (!APP_SECRET) {
  console.warn("⚠️  PRIVY_APP_SECRET manquant (mode mock). Ajoute-le via .env ou env vars.");
}

app.post("/privy/session", (req, res) => {
  const now = Date.now();
  // Ici tu appellerais l'API Privy en utilisant APP_SECRET.
  // Pour le test on renvoie une session mock.
  return res.json({
    token: `privy_${APP_ID}_${Math.random().toString(16).slice(2)}`,
    user: {
      id: `user-${now}`,
      walletAddress: "0xA1C8...9F3D",
      chain: "Solana",
      label: "Privy Wallet"
    },
    expiresAt: now + 30 * 60 * 1000
  });
});

app.listen(PORT, () => {
  console.log(`Backend provisoire en écoute sur http://localhost:${PORT}`);
});
