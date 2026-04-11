package com.bank.service;

import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.model.Wallet;
import com.bank.repository.TransactionRepository;
import com.bank.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private LiveExchangeRateStrategy exchangeRateStrategy;

    @InjectMocks
    private TransferService transferService;

    private User mockUser;
    private Wallet fromWallet;
    private Wallet toWallet;

    @BeforeEach
    void setUp() {
        mockUser = new User("pranav", "pranav@gmail.com");
        fromWallet = new Wallet("INR", new BigDecimal("10000.00"), mockUser);
        toWallet = new Wallet("USD", new BigDecimal("0.00"), mockUser);
    }

    @Test
    void transfer_success_currencyConversion() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(toWallet));
        when(exchangeRateStrategy.getRate("INR", "USD")).thenReturn(new BigDecimal("0.012"));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transferService.transfer(1L, 2L, new BigDecimal("5000"));

        assertNotNull(result);
        assertEquals("INR", result.getFromCurrency());
        assertEquals("USD", result.getToCurrency());
        assertEquals(new BigDecimal("0.012"), result.getExchangeRate());
        assertEquals(new BigDecimal("60.00"), result.getConvertedAmount());
        verify(walletRepository, times(2)).save(any(Wallet.class));
    }

    @Test
    void transfer_insufficientBalance_throwsException() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(fromWallet));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(toWallet));

        assertThrows(RuntimeException.class, () ->
            transferService.transfer(1L, 2L, new BigDecimal("99999"))
        );
    }

    @Test
    void transfer_sameCurrency_noConversion() {
        // Both wallets have USD with sufficient balance
        Wallet usdWallet1 = new Wallet("USD", new BigDecimal("500.00"), mockUser);
        Wallet usdWallet2 = new Wallet("USD", new BigDecimal("200.00"), mockUser);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(usdWallet1));
        when(walletRepository.findById(2L)).thenReturn(Optional.of(usdWallet2));
        when(exchangeRateStrategy.getRate("USD", "USD")).thenReturn(BigDecimal.ONE);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction result = transferService.transfer(1L, 2L, new BigDecimal("100"));

        assertEquals(new BigDecimal("100.00"), result.getConvertedAmount());
    }
}
