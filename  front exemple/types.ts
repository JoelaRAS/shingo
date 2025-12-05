export enum Direction {
  LONG = 'LONG',
  SHORT = 'SHORT',
}

export enum SignalStatus {
  ACTIVE = 'ACTIVE',
  EXPIRED = 'EXPIRED',
}

export enum EntryType {
  LIMIT = 'LIMIT',
  MARKET = 'MARKET',
}

export interface TakeProfit {
  level: number;
  price: number;
  percentage: number;
}

export interface Trader {
  id: string;
  name: string;
  avatar: string;
  bio: string;
  winRate: number;
  roi: number; // Return on Investment (e.g., 340%)
  totalSignals: number;
  subscribers: number;
  isSubscribed: boolean;
  price: number; // Monthly subscription price in $
  rating: number; // 0-5
  reviewsCount: number;
}

export interface Signal {
  id: string;
  traderId: string;
  pair: string;
  direction: Direction;
  entryType: EntryType;
  entryPrice: number;
  stopLoss: number;
  takeProfits: TakeProfit[];
  timeframe: string;
  confidence: number; // 0-10
  createdAt: string; // ISO string
  expiresAt: string; // ISO string
  status: SignalStatus;
  resultPnl?: number; // For expired signals
}