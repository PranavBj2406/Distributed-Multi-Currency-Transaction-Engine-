package com.bank.service;

import com.bank.exception.ResourceNotFoundException;
import com.bank.model.User;
import com.bank.model.Wallet;
import com.bank.repository.UserRepository;
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
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WalletService walletService;

    private User mockUser;
    private Wallet mockWallet;

    @BeforeEach
    void setUp() {
        mockUser = new User("pranav", "pranav@gmail.com");
        mockWallet = new Wallet("USD", new BigDecimal("1000.00"), mockUser);
    }

    @Test
    void createWallet_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(walletRepository.existsByUserIdAndCurrency(1L, "USD")).thenReturn(false);
        when(walletRepository.save(any(Wallet.class))).thenReturn(mockWallet);

        Wallet result = walletService.createWallet(1L, "USD");

        assertNotNull(result);
        assertEquals("USD", result.getCurrency());
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void createWallet_userNotFound_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            walletService.createWallet(99L, "USD")
        );
    }

    @Test
    void createWallet_duplicateCurrency_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(walletRepository.existsByUserIdAndCurrency(1L, "USD")).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            walletService.createWallet(1L, "USD")
        );
    }

    @Test
    void deposit_success() {
        when(walletRepository.findById(1L)).thenReturn(Optional.of(mockWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(mockWallet);

        Wallet result = walletService.deposit(1L, new BigDecimal("500.00"));

        assertNotNull(result);
        verify(walletRepository, times(1)).save(any(Wallet.class));
    }

    @Test
    void deposit_negativeAmount_throwsException() {
        assertThrows(RuntimeException.class, () ->
            walletService.deposit(1L, new BigDecimal("-100"))
        );
    }
}
