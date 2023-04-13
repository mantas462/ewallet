package ewallet.service.ewallet;

import ewallet.dto.ewallet.DepositEwalletRequestDto;
import ewallet.dto.ewallet.WithdrawEwalletRequestDto;
import ewallet.entity.ewallet.Ewallet;
import ewallet.repository.ewallet.EwalletDao;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static ewallet.TestHelper.depositEwalletRequestDtoWithAmount;
import static ewallet.TestHelper.ewalletWithBalance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EwalletServiceUTest {

    @Mock
    private EwalletDao ewalletDao;

    @InjectMocks
    private EwalletService ewalletService;

    @Nested
    class DepositEwalletTests {

        /*TODO LIST:
            1. Test success scenario
            2. Test when ewallet does not exist
            3. Test when operationService fails
            4. Test isAmountValid()
         */

        @ParameterizedTest
        @MethodSource("provideArgumentsForZeroAmountCase")
        void deposit_whenZero_thenException(BigDecimal amount) {

            // given
            DepositEwalletRequestDto depositEwalletRequestDto = depositEwalletRequestDtoWithAmount(amount);

            // then
            assertThrows(RuntimeException.class, () -> ewalletService.deposit(UUID.randomUUID(), depositEwalletRequestDto));

            verifyNoInteractions(ewalletDao);
        }

        private static Stream<Arguments> provideArgumentsForZeroAmountCase() {
            return Stream.of(
                    Arguments.of(BigDecimal.valueOf(0.0000000000)),
                    Arguments.of(BigDecimal.valueOf(0))
            );
        }

        @ParameterizedTest
        @MethodSource("provideArgumentsForIncorrectFractionalCase")
        void deposit_whenFractionalIsIncorrect_thenException(BigDecimal amount) {

            // given
            DepositEwalletRequestDto depositEwalletRequestDto = depositEwalletRequestDtoWithAmount(amount);

            // then
            assertThrows(RuntimeException.class, () -> ewalletService.deposit(UUID.randomUUID(), depositEwalletRequestDto));

            verifyNoInteractions(ewalletDao);
        }

        private static Stream<Arguments> provideArgumentsForIncorrectFractionalCase() {
            return Stream.of(
                    Arguments.of(BigDecimal.valueOf(0.3333333333333333)),
                    Arguments.of(BigDecimal.valueOf(1.3333333333333333))
            );
        }
    }

    @Nested
    class WithdrawEwalletTests {

        /*TODO LIST:
            1. Test success scenario
            2. Test when ewallet does not exist
            3. Test when balance is not enough for withdrawal
            4. Test when exceeded daily limit
            5. Test when operationService fails
            6. Test isAmountValid()
         */

        @ParameterizedTest
        @MethodSource("provideArgumentsForZeroAmountCase")
        void withdraw_whenZero_thenException(BigDecimal amount) {

            // given
            DepositEwalletRequestDto depositEwalletRequestDto = depositEwalletRequestDtoWithAmount(amount);

            // then
            assertThrows(RuntimeException.class, () -> ewalletService.deposit(UUID.randomUUID(), depositEwalletRequestDto));

            verifyNoInteractions(ewalletDao);
        }

        private static Stream<Arguments> provideArgumentsForZeroAmountCase() {
            return Stream.of(
                    Arguments.of(BigDecimal.valueOf(0.0000000000)),
                    Arguments.of(BigDecimal.valueOf(0))
            );
        }

        @ParameterizedTest
        @MethodSource("provideArgumentsForIncorrectFractionalCase")
        void withdraw_whenFractionalIsIncorrect_thenException(BigDecimal amount) {

            // given
            DepositEwalletRequestDto depositEwalletRequestDto = depositEwalletRequestDtoWithAmount(amount);

            // then
            assertThrows(RuntimeException.class, () -> ewalletService.deposit(UUID.randomUUID(), depositEwalletRequestDto));

            verifyNoInteractions(ewalletDao);
        }

        private static Stream<Arguments> provideArgumentsForIncorrectFractionalCase() {
            return Stream.of(
                    Arguments.of(BigDecimal.valueOf(0.3333333333333333)),
                    Arguments.of(BigDecimal.valueOf(1.3333333333333333))
            );
        }

        private static Stream<Arguments> provideArgumentsForSuccessCase() {
            return Stream.of(
                    Arguments.of(BigDecimal.valueOf(1), BigDecimal.valueOf(1.11), BigDecimal.valueOf(0.11)),
                    Arguments.of(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(1).setScale(2)),
                    Arguments.of(BigDecimal.valueOf(1.0), BigDecimal.valueOf(1.11), BigDecimal.valueOf(0.11)),
                    Arguments.of(BigDecimal.valueOf(1.1), BigDecimal.valueOf(1.11), BigDecimal.valueOf(0.01)),
                    Arguments.of(BigDecimal.valueOf(1.11), BigDecimal.valueOf(1.11), BigDecimal.ZERO.setScale(2)),
                    Arguments.of(BigDecimal.valueOf(0.11), BigDecimal.valueOf(1.11), BigDecimal.ONE.setScale(2))
            );
        }

        @ParameterizedTest
        @MethodSource("provideArgumentsForBiggerAmountThanBalanceCase")
        void withdraw_whenAmountIsBiggerThanBalance_thenException(BigDecimal amount, BigDecimal balance) {

            // given
            WithdrawEwalletRequestDto withdrawEwalletRequestDto = WithdrawEwalletRequestDto.builder()
                    .amount(amount)
                    .build();

            Ewallet ewallet = ewalletWithBalance(balance);

            // when
            when(ewalletDao.findById(any())).thenReturn(Optional.of(ewallet));

            // then
            assertThrows(RuntimeException.class, () -> ewalletService.withdraw(UUID.randomUUID(), withdrawEwalletRequestDto));
            verify(ewalletDao, never()).save(any());
        }

        private static Stream<Arguments> provideArgumentsForBiggerAmountThanBalanceCase() {
            return Stream.of(
                    Arguments.of(BigDecimal.valueOf(2), BigDecimal.valueOf(1)),
                    Arguments.of(BigDecimal.valueOf(2.22), BigDecimal.valueOf(1)),
                    Arguments.of(BigDecimal.valueOf(0.11), BigDecimal.valueOf(0.1))
            );
        }
    }

    @Nested
    class TransactionEwalletTests {

        /*TODO LIST:
            1. Test success scenario
            2. Test when source ewallet does not exist
            3. Test when balance is not enough for withdrawal
            4. Test when exceeded daily limit
            5. Test when operationService fails
            6. Test when destination ewallet does not exist
            7. Test isAmountValid()
            8. Test SINGLE_TRANSACTION_LIMIT
         */
    }
}
