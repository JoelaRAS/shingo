import { Direction, EntryType, Signal, SignalStatus, Trader } from './types';

export const MOCK_TRADERS: Trader[] = [
  // SUBSCRIBED TRADERS (For My Feed)
  {
    id: 't1',
    name: 'Cipher Alpha',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&h=200',
    bio: 'BTC Swing Specialist. Low frequency, institutional grade setups.',
    winRate: 87,
    roi: 420,
    totalSignals: 142,
    subscribers: 1250,
    isSubscribed: true,
    price: 49,
    rating: 4.9,
    reviewsCount: 312
  },
  {
    id: 't3',
    name: 'Quantum Whale',
    avatar: 'https://images.unsplash.com/photo-1599566150163-29194dcaad36?auto=format&fit=crop&w=200&h=200',
    bio: 'On-chain flow analysis. I see what whales do before you do.',
    winRate: 72,
    roi: 310,
    totalSignals: 310,
    subscribers: 890,
    isSubscribed: true,
    price: 99,
    rating: 4.8,
    reviewsCount: 156
  },
  {
    id: 't5',
    name: 'Altcoin Queen',
    avatar: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=200&h=200',
    bio: 'Hunting 100x gems in the micro-cap forest.',
    winRate: 55,
    roi: 1250,
    totalSignals: 420,
    subscribers: 5000,
    isSubscribed: true,
    price: 35,
    rating: 4.6,
    reviewsCount: 890
  },

  // UNSUBSCRIBED TRADERS (For Market)
  {
    id: 't2',
    name: 'Velocity FX',
    avatar: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&h=200',
    bio: 'Scalping ETH & Solana. High volatility hunter.',
    winRate: 64,
    roi: 185,
    totalSignals: 850,
    subscribers: 3400,
    isSubscribed: false,
    price: 29,
    rating: 4.5,
    reviewsCount: 1042
  },
  {
    id: 't4',
    name: 'Satoshi\'s Ghost',
    avatar: 'https://images.unsplash.com/photo-1527980965255-d3b416303d12?auto=format&fit=crop&w=200&h=200',
    bio: 'Macro trends and cycle analysis. Playing the long game.',
    winRate: 92,
    roi: 850,
    totalSignals: 45,
    subscribers: 5600,
    isSubscribed: false,
    price: 149,
    rating: 5.0,
    reviewsCount: 2400
  },
  {
    id: 't6',
    name: 'DeFi Degen',
    avatar: 'https://images.unsplash.com/photo-1633332755192-727a05c4013d?auto=format&fit=crop&w=200&h=200',
    bio: 'High risk, high reward. Yield farming and loops.',
    winRate: 45,
    roi: -12,
    totalSignals: 90,
    subscribers: 200,
    isSubscribed: false,
    price: 15,
    rating: 3.2,
    reviewsCount: 45
  },
  {
    id: 't7',
    name: 'Forex Converter',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=200&h=200',
    bio: 'Applying traditional forex strategies to crypto markets.',
    winRate: 68,
    roi: 140,
    totalSignals: 1200,
    subscribers: 850,
    isSubscribed: false,
    price: 59,
    rating: 4.1,
    reviewsCount: 120
  },
  {
    id: 't8',
    name: 'AI Signals Bot',
    avatar: 'https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=200&h=200',
    bio: 'Algorithmic trading based on sentiment analysis.',
    winRate: 75,
    roi: 300,
    totalSignals: 5000,
    subscribers: 10000,
    isSubscribed: false,
    price: 19,
    rating: 4.3,
    reviewsCount: 3000
  }
];

// Helper to generate realistic history
const generateHistory = (traderId: string, count: number, volatility: number, winBias: number): Signal[] => {
  const signals: Signal[] = [];
  let currentDate = new Date();
  const pairs = ['BTC/USDT', 'ETH/USDT', 'SOL/USDT', 'AVAX/USDT', 'BNB/USDT'];
  
  for (let i = 0; i < count; i++) {
    // Generate PnL based on volatility and bias
    // If Math.random() < winBias, it's a win
    const isWin = Math.random() < winBias;
    let pnl = 0;
    
    if (isWin) {
        pnl = (Math.random() * volatility) + (volatility * 0.2); // Win: 20% to 120% of volatility
    } else {
        pnl = -(Math.random() * (volatility * 0.6)); // Loss: 0% to 60% of volatility (risk management simulation)
    }

    // Go back in time progressively
    currentDate = new Date(currentDate.getTime() - 1000 * 60 * 60 * (Math.random() * 48 + 4)); 

    signals.push({
      id: `${traderId}_hist_${i}`,
      traderId: traderId,
      pair: pairs[Math.floor(Math.random() * pairs.length)],
      direction: Math.random() > 0.5 ? Direction.LONG : Direction.SHORT,
      entryType: EntryType.LIMIT,
      entryPrice: 100 + Math.random() * 60000,
      stopLoss: 90,
      takeProfits: [],
      timeframe: Math.random() > 0.5 ? 'H4' : 'H1',
      confidence: Math.floor(Math.random() * 5) + 5,
      createdAt: currentDate.toISOString(),
      expiresAt: new Date(currentDate.getTime() + 1000 * 60 * 60 * 4).toISOString(),
      status: SignalStatus.EXPIRED,
      resultPnl: parseFloat(pnl.toFixed(2))
    });
  }
  return signals.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
};

// --- Generate Unique Histories for Each Trader ---
// Trader 1: Cipher Alpha (Consistent, High Win Rate)
const T1_HISTORY = generateHistory('t1', 30, 8.0, 0.85); 

// Trader 2: Velocity FX (High Volatility, Scalper)
const T2_HISTORY = generateHistory('t2', 40, 5.0, 0.60);

// Trader 3: Quantum Whale (Big swings)
const T3_HISTORY = generateHistory('t3', 25, 12.0, 0.70);

// Trader 4: Satoshi's Ghost (Macro, Huge wins, some losses)
const T4_HISTORY = generateHistory('t4', 15, 25.0, 0.90);

// Trader 5: Altcoin Queen (Extreme volatility, 100x gems or bust)
const T5_HISTORY = generateHistory('t5', 45, 30.0, 0.55);

// Trader 6: DeFi Degen (Losing streak simulation)
const T6_HISTORY = generateHistory('t6', 20, 10.0, 0.40);

// Trader 7: Forex Converter (Tight ranges)
const T7_HISTORY = generateHistory('t7', 35, 3.0, 0.65);

// Trader 8: AI Bot (High frequency, small steady gains)
const T8_HISTORY = generateHistory('t8', 60, 2.5, 0.75);


export const MOCK_SIGNALS: Signal[] = [
  // --- Active Signals (Hardcoded for Demo) ---
  {
    id: 's1',
    traderId: 't1',
    pair: 'BTC/USDT',
    direction: Direction.LONG,
    entryType: EntryType.LIMIT,
    entryPrice: 64200,
    stopLoss: 63000,
    takeProfits: [
      { level: 1, price: 65500, percentage: 2.02 },
      { level: 2, price: 67000, percentage: 4.36 },
      { level: 3, price: 70000, percentage: 9.03 },
    ],
    timeframe: 'H4',
    confidence: 9,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(),
    expiresAt: new Date(Date.now() + 1000 * 60 * 60 * 24).toISOString(),
    status: SignalStatus.ACTIVE,
  },
  {
    id: 's2',
    traderId: 't1',
    pair: 'SOL/USDT',
    direction: Direction.SHORT,
    entryType: EntryType.MARKET,
    entryPrice: 145.50,
    stopLoss: 148.00,
    takeProfits: [
      { level: 1, price: 140.00, percentage: 3.78 },
      { level: 2, price: 135.00, percentage: 7.21 },
    ],
    timeframe: 'H1',
    confidence: 7,
    createdAt: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
    expiresAt: new Date(Date.now() + 1000 * 60 * 60 * 8).toISOString(),
    status: SignalStatus.ACTIVE,
  },
  {
    id: 's7',
    traderId: 't3',
    pair: 'LINK/USDT',
    direction: Direction.LONG,
    entryType: EntryType.LIMIT,
    entryPrice: 18.20,
    stopLoss: 17.50,
    takeProfits: [
      { level: 1, price: 19.50, percentage: 7.1 },
      { level: 2, price: 21.00, percentage: 15.3 },
    ],
    timeframe: 'D1',
    confidence: 9,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 5).toISOString(),
    expiresAt: new Date(Date.now() + 1000 * 60 * 60 * 48).toISOString(),
    status: SignalStatus.ACTIVE,
  },
  {
    id: 's8',
    traderId: 't5',
    pair: 'PEPE/USDT',
    direction: Direction.LONG,
    entryType: EntryType.MARKET,
    entryPrice: 0.00000850,
    stopLoss: 0.00000780,
    takeProfits: [
      { level: 1, price: 0.00001000, percentage: 17.6 },
      { level: 2, price: 0.00001500, percentage: 76.4 },
    ],
    timeframe: 'M15',
    confidence: 6,
    createdAt: new Date(Date.now() - 1000 * 60 * 10).toISOString(),
    expiresAt: new Date(Date.now() + 1000 * 60 * 60 * 4).toISOString(),
    status: SignalStatus.ACTIVE,
  },
   {
    id: 's9',
    traderId: 't5',
    pair: 'WIF/USDT',
    direction: Direction.SHORT,
    entryType: EntryType.MARKET,
    entryPrice: 3.20,
    stopLoss: 3.50,
    takeProfits: [
      { level: 1, price: 2.80, percentage: 12.5 },
    ],
    timeframe: 'H1',
    confidence: 5,
    createdAt: new Date(Date.now() - 1000 * 60 * 45).toISOString(),
    expiresAt: new Date(Date.now() + 1000 * 60 * 60 * 6).toISOString(),
    status: SignalStatus.ACTIVE,
  },
  {
    id: 's3',
    traderId: 't2',
    pair: 'ETH/USDT',
    direction: Direction.LONG,
    entryType: EntryType.MARKET,
    entryPrice: 3400,
    stopLoss: 3350,
    takeProfits: [
      { level: 1, price: 3450, percentage: 1.47 },
      { level: 2, price: 3500, percentage: 2.94 },
    ],
    timeframe: 'M15',
    confidence: 6,
    createdAt: new Date(Date.now() - 1000 * 60 * 15).toISOString(),
    expiresAt: new Date(Date.now() + 1000 * 60 * 60 * 1).toISOString(),
    status: SignalStatus.ACTIVE,
  },
  {
    id: 's10',
    traderId: 't2',
    pair: 'XRP/USDT',
    direction: Direction.SHORT,
    entryType: EntryType.LIMIT,
    entryPrice: 0.62,
    stopLoss: 0.63,
    takeProfits: [
      { level: 1, price: 0.60, percentage: 3.2 },
    ],
    timeframe: 'H1',
    confidence: 7,
    createdAt: new Date(Date.now() - 1000 * 60 * 20).toISOString(),
    expiresAt: new Date(Date.now() + 1000 * 60 * 60 * 12).toISOString(),
    status: SignalStatus.ACTIVE,
  },
  {
    id: 's11',
    traderId: 't4',
    pair: 'BTC/USDT',
    direction: Direction.LONG,
    entryType: EntryType.LIMIT,
    entryPrice: 58000,
    stopLoss: 55000,
    takeProfits: [
      { level: 1, price: 75000, percentage: 29.3 },
      { level: 2, price: 82000, percentage: 41.3 },
    ],
    timeframe: 'W1',
    confidence: 10,
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 100).toISOString(),
    expiresAt: new Date(Date.now() + 1000 * 60 * 60 * 24 * 7).toISOString(),
    status: SignalStatus.ACTIVE,
  },

  // --- MERGED GENERATED HISTORY ---
  ...T1_HISTORY,
  ...T2_HISTORY,
  ...T3_HISTORY,
  ...T4_HISTORY,
  ...T5_HISTORY,
  ...T6_HISTORY,
  ...T7_HISTORY,
  ...T8_HISTORY,
];