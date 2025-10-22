package com.example.cpayments.sipay.domain;

public enum TransactionType {
    SALE,    // Tek seferde satış/para çekme
    AUTH,    // Otorizasyon (Provizyon alma)
    CAPTURE, // Otorizasyonu paraya çevirme
    VOID,    // Otorizasyon iptali
    REFUND   // İade
}