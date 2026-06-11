package com.example.consoleBank.service;

import com.example.consoleBank.model.Account;
import com.example.consoleBank.model.Client;
import com.example.consoleBank.model.OperationType;
import com.example.consoleBank.model.Transaction;
import com.example.consoleBank.repository.AccountRepository;
import com.example.consoleBank.repository.ClientRepository;
import com.example.consoleBank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("Тестирование AccountService")
class AccountServiceTest {

    // Внедряем реальные сервисы и репозитории
    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Тестовые данные
    private Client testClient;
    private Account fromAccount;  // Счет отправителя
    private Account toAccount;    // Счет получателя

    @BeforeEach
    void setUp() {
        // Очищаем репозитории (хотя @Transactional и так откатит)
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        clientRepository.deleteAll();

        // Создаем тестового клиента
        testClient = new Client();
        testClient.setName("Тестовый Клиент");
        testClient.setTelNumber("81234567890");
        testClient.setEmail("test@example.com");
        testClient = clientService.create(testClient);

        // Создаем счет ОТПРАВИТЕЛЯ с балансом 1000
        fromAccount = new Account(testClient);
        fromAccount = accountService.create(fromAccount);
        fromAccount.setBalance(new BigDecimal("1000.00"));
        accountService.update(fromAccount);

        // Создаем счет ПОЛУЧАТЕЛЯ с балансом 500
        toAccount = new Account(testClient);
        toAccount = accountService.create(toAccount);
        toAccount.setBalance(new BigDecimal("500.00"));
        accountService.update(toAccount);

        // Выводим для отладки
        System.out.println("=== Подготовка к тесту ===");
        System.out.println("Счет отправителя: " + fromAccount.getNumber() +
                ", баланс: " + fromAccount.getBalance());
        System.out.println("Счет получателя: " + toAccount.getNumber() +
                ", баланс: " + toAccount.getBalance());
    }

    // ==================== ТЕСТЫ ====================

    @Nested
    @DisplayName("Тесты успешных переводов")
    class SuccessfulTransfers {

        @Test
        @DisplayName("Перевод 200 рублей - успешная операция")
        void testTransfer_Success_200Rub() {
            // GIVEN
            BigDecimal transferAmount = new BigDecimal("200.00");
            BigDecimal expectedFromBalance = new BigDecimal("800.00");
            BigDecimal expectedToBalance = new BigDecimal("700.00");

            // WHEN
            accountService.transfer(fromAccount, toAccount, transferAmount);

            // THEN
            // Получаем свежие данные из БД
            Account actualFromAccount = accountRepository.findById(fromAccount.getId()).get();
            Account actualToAccount = accountRepository.findById(toAccount.getId()).get();

            // Проверяем балансы
            assertThat(actualFromAccount.getBalance())
                    .as("Баланс отправителя должен уменьшиться на %s", transferAmount)
                    .isEqualByComparingTo(expectedFromBalance);

            assertThat(actualToAccount.getBalance())
                    .as("Баланс получателя должен увеличиться на %s", transferAmount)
                    .isEqualByComparingTo(expectedToBalance);
        }

        @Test
        @DisplayName("Перевод всей суммы счета (1000 рублей)")
        void testTransfer_EntireBalance() {
            // GIVEN
            BigDecimal transferAmount = new BigDecimal("1000.00");

            // WHEN
            accountService.transfer(fromAccount, toAccount, transferAmount);

            // THEN
            Account updatedFrom = accountRepository.findById(fromAccount.getId()).get();
            Account updatedTo = accountRepository.findById(toAccount.getId()).get();

            assertThat(updatedFrom.getBalance()).isEqualByComparingTo("0");
            assertThat(updatedTo.getBalance()).isEqualByComparingTo("1500");
        }

        @Test
        @DisplayName("Перевод копеек/дробной суммы")
        void testTransfer_CentsAmount() {
            // GIVEN
            BigDecimal transferAmount = new BigDecimal("0.50");

            // WHEN
            accountService.transfer(fromAccount, toAccount, transferAmount);

            // THEN
            Account updatedFrom = accountRepository.findById(fromAccount.getId()).get();
            Account updatedTo = accountRepository.findById(toAccount.getId()).get();

            assertThat(updatedFrom.getBalance()).isEqualByComparingTo("999.50");
            assertThat(updatedTo.getBalance()).isEqualByComparingTo("500.50");
        }
    }

    @Nested
    @DisplayName("Тесты ошибок и исключений")
    class ErrorTransfers {

        @Test
        @DisplayName("Перевод суммы, превышающей баланс - ошибка")
        void testTransfer_InsufficientFunds() {
            // GIVEN
            BigDecimal transferAmount = new BigDecimal("2000.00"); // Больше чем 1000

            // WHEN & THEN
            assertThatThrownBy(() -> accountService.transfer(fromAccount, toAccount, transferAmount))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Недостаточно средств");

            // Проверяем, что балансы НЕ изменились
            Account unchangedFrom = accountRepository.findById(fromAccount.getId()).get();
            Account unchangedTo = accountRepository.findById(toAccount.getId()).get();

            assertThat(unchangedFrom.getBalance()).isEqualByComparingTo("1000");
            assertThat(unchangedTo.getBalance()).isEqualByComparingTo("500");
        }

        @Test
        @DisplayName("Перевод нулевой суммы - ошибка")
        void testTransfer_ZeroAmount() {
            // GIVEN
            BigDecimal transferAmount = BigDecimal.ZERO;

            // WHEN & THEN
            assertThatThrownBy(() -> accountService.transfer(fromAccount, toAccount, transferAmount))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Сумма должна быть больше 0");
        }

        @Test
        @DisplayName("Перевод отрицательной суммы - ошибка")
        void testTransfer_NegativeAmount() {
            // GIVEN
            BigDecimal transferAmount = new BigDecimal("-100.00");

            // WHEN & THEN
            assertThatThrownBy(() -> accountService.transfer(fromAccount, toAccount, transferAmount))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Сумма должна быть больше 0");
        }

        @Test
        @DisplayName("Перевод с несуществующего счета - ошибка")
        void testTransfer_FromAccountNotFound() {
            // GIVEN
            Account fakeAccount = new Account();
            fakeAccount.setId(99999L);
            BigDecimal transferAmount = new BigDecimal("100.00");

            // WHEN & THEN
            assertThatThrownBy(() -> accountService.transfer(fakeAccount, toAccount, transferAmount))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Счет отправителя не найден");
        }

        @Test
        @DisplayName("Перевод на несуществующий счет - ошибка")
        void testTransfer_ToAccountNotFound() {
            // GIVEN
            Account fakeAccount = new Account();
            fakeAccount.setId(99999L);
            BigDecimal transferAmount = new BigDecimal("100.00");

            // WHEN & THEN
            assertThatThrownBy(() -> accountService.transfer(fromAccount, fakeAccount, transferAmount))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Счет получателя не найден");
        }
    }

    @Nested
    @DisplayName("Тесты корректности транзакций")
    class TransactionTests {

        @Test
        @DisplayName("При переводе должны создаваться 2 транзакции")
        void testTransfer_CreatesTwoTransactions() {
            // GIVEN
            BigDecimal transferAmount = new BigDecimal("150.00");
            long initialTransactionCount = transactionRepository.count();

            // WHEN
            accountService.transfer(fromAccount, toAccount, transferAmount);

            // THEN
            long finalTransactionCount = transactionRepository.count();
            assertThat(finalTransactionCount - initialTransactionCount)
                    .as("Должно быть создано 2 транзакции")
                    .isEqualTo(2);
        }

        @Test
        @DisplayName("Транзакция отправителя должна иметь правильные данные")
        void testTransfer_SenderTransactionCorrect() {
            // GIVEN
            BigDecimal transferAmount = new BigDecimal("150.00");

            // WHEN
            accountService.transfer(fromAccount, toAccount, transferAmount);

            // THEN
            // Находим транзакции отправителя
            List<Transaction> transactions = transactionRepository.findAll();

            // Ищем транзакцию отправителя
            Transaction senderTransaction = transactions.stream()
                    .filter(t -> t.getAccount().getId().equals(fromAccount.getId()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Транзакция отправителя не найдена"));

            assertThat(senderTransaction.getAmount()).isEqualByComparingTo(transferAmount);
            assertThat(senderTransaction.getOperationType()).isEqualTo(OperationType.TRANSFER);
            assertThat(senderTransaction.getBalanceAfter()).isEqualByComparingTo("850.00");
            assertThat(senderTransaction.getCorrespondentAccount().getId())
                    .isEqualTo(toAccount.getId());
        }

        @Test
        @DisplayName("Транзакция получателя должна иметь правильные данные")
        void testTransfer_ReceiverTransactionCorrect() {
            // GIVEN
            BigDecimal transferAmount = new BigDecimal("150.00");

            // WHEN
            accountService.transfer(fromAccount, toAccount, transferAmount);

            // THEN
            List<Transaction> transactions = transactionRepository.findAll();

            Transaction receiverTransaction = transactions.stream()
                    .filter(t -> t.getAccount().getId().equals(toAccount.getId()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Транзакция получателя не найдена"));

            assertThat(receiverTransaction.getAmount()).isEqualByComparingTo(transferAmount);
            assertThat(receiverTransaction.getOperationType()).isEqualTo(OperationType.TRANSFER);
            assertThat(receiverTransaction.getBalanceAfter()).isEqualByComparingTo("650.00");
            assertThat(receiverTransaction.getCorrespondentAccount().getId())
                    .isEqualTo(fromAccount.getId());
        }
    }

    @Nested
    @DisplayName("Тесты граничных значений")
    class BoundaryTests {

        @Test
        @DisplayName("Перевод 1 копейки - успешно")
        void testTransfer_OneKopeck() {
            // GIVEN
            BigDecimal transferAmount = new BigDecimal("0.01");

            // WHEN
            accountService.transfer(fromAccount, toAccount, transferAmount);

            // THEN
            Account updatedFrom = accountRepository.findById(fromAccount.getId()).get();
            Account updatedTo = accountRepository.findById(toAccount.getId()).get();

            assertThat(updatedFrom.getBalance()).isEqualByComparingTo("999.99");
            assertThat(updatedTo.getBalance()).isEqualByComparingTo("500.01");
        }

        @Test
        @DisplayName("Перевод когда баланс точно равен сумме")
        void testTransfer_ExactBalance() {
            // GIVEN
            BigDecimal transferAmount = new BigDecimal("1000.00");

            // WHEN
            accountService.transfer(fromAccount, toAccount, transferAmount);

            // THEN - не должно быть ошибки, баланс становится 0
            Account updatedFrom = accountRepository.findById(fromAccount.getId()).get();
            assertThat(updatedFrom.getBalance()).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("Перевод когда на счету 0 (начальное состояние)")
        void testTransfer_FromZeroBalance() {
            // GIVEN
            fromAccount.setBalance(BigDecimal.ZERO);
            accountService.update(fromAccount);
            BigDecimal transferAmount = new BigDecimal("100.00");

            // WHEN & THEN
            assertThatThrownBy(() -> accountService.transfer(fromAccount, toAccount, transferAmount))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Недостаточно средств");
        }
    }

    @Test
    @DisplayName("Общий тест: несколько переводов подряд")
    void testTransfer_MultipleTransfers() {
        // GIVEN
        BigDecimal firstTransfer = new BigDecimal("100.00");
        BigDecimal secondTransfer = new BigDecimal("50.00");
        BigDecimal thirdTransfer = new BigDecimal("25.50");

        // WHEN
        accountService.transfer(fromAccount, toAccount, firstTransfer);
        accountService.transfer(fromAccount, toAccount, secondTransfer);
        accountService.transfer(fromAccount, toAccount, thirdTransfer);

        // THEN
        Account finalFrom = accountRepository.findById(fromAccount.getId()).get();
        Account finalTo = accountRepository.findById(toAccount.getId()).get();

        // Ожидаемый баланс отправителя: 1000 - 100 - 50 - 25.50 = 824.50
        assertThat(finalFrom.getBalance()).isEqualByComparingTo("824.50");

        // Ожидаемый баланс получателя: 500 + 100 + 50 + 25.50 = 675.50
        assertThat(finalTo.getBalance()).isEqualByComparingTo("675.50");

        // Проверяем количество транзакций (3 перевода = 6 транзакций + возможно старые)
        long transactionCount = transactionRepository.count();
        assertThat(transactionCount).isGreaterThanOrEqualTo(6);
    }
}
