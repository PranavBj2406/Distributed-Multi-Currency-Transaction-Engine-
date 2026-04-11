package com.bank.controller;

import com.bank.dto.TransferRequest;
import com.bank.model.Transaction;
import com.bank.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @PostMapping
    public ResponseEntity<Transaction> transfer(@Valid @RequestBody TransferRequest request) {
        Transaction transaction = transferService.transfer(
            request.getFromWalletId(),
            request.getToWalletId(),
            request.getAmount()
        );
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping("/history/{walletId}")
    public ResponseEntity<List<Transaction>> getHistory(@PathVariable Long walletId) {
        return ResponseEntity.ok(transferService.getTransactionHistory(walletId));
    }
}
