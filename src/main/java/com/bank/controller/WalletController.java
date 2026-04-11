package com.bank.controller;

import com.bank.dto.CreateWalletRequest;
import com.bank.dto.DepositRequest;
import com.bank.model.Wallet;
import com.bank.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        Wallet wallet = walletService.createWallet(request.getUserId(), request.getCurrency());
        return new ResponseEntity<>(wallet, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Wallet>> getUserWallets(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getUserWallets(userId));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Wallet> getWalletById(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getWalletById(walletId));
    }

    @PostMapping("/{walletId}/deposit")
    public ResponseEntity<Wallet> deposit(@PathVariable Long walletId,
                                          @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(walletService.deposit(walletId, request.getAmount()));
    }
}
