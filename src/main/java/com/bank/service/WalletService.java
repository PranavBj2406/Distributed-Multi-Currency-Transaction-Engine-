package com.bank.service;

import com.bank.exception.ResourceNotFoundException;
import com.bank.model.User;
import com.bank.model.Wallet;
import com.bank.repository.UserRepository;
import com.bank.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    // Create wallet for a user in a given currency
    public Wallet createWallet(Long userId, String currency) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (walletRepository.existsByUserIdAndCurrency(userId, currency.toUpperCase())) {
            throw new RuntimeException("Wallet already exists for currency: " + currency);
        }

        Wallet wallet = new Wallet(currency.toUpperCase(), BigDecimal.ZERO, user);
        return walletRepository.save(wallet);
    }

    // Get all wallets for a user
    public List<Wallet> getUserWallets(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return walletRepository.findByUserId(userId);
    }

    // Get single wallet by id
    public Wallet getWalletById(Long walletId) {
        return walletRepository.findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found: " + walletId));
    }

    // Deposit money into wallet
    public Wallet deposit(Long walletId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero");
        }
        Wallet wallet = getWalletById(walletId);
        wallet.setBalance(wallet.getBalance().add(amount));
        return walletRepository.save(wallet);
    }
}
