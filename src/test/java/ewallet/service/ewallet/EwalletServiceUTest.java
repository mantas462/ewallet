package ewallet.service.ewallet;

import ewallet.dto.operation.internal.OperationDto;
import ewallet.dto.operation.internal.OperationStatusDto;
import ewallet.dto.operation.internal.OperationTypeDto;
import ewallet.entity.ewallet.Ewallet;
import ewallet.repository.ewallet.EwalletDao;
import ewallet.service.operation.OperationService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static ewallet.TestHelper.createEwallet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EwalletServiceUTest {

    @Mock
    private EwalletDao ewalletDao;

    @Mock
    private OperationService operationService;

    @InjectMocks
    private EwalletService ewalletService;

    @Nested
    class DepositEwalletTests {

        @Test
        void deposit_success() {

            // given
            OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(1), OperationTypeDto.DEPOSIT);

            Ewallet ewallet = createEwallet(operationDto.getEwalletUuid(), BigDecimal.valueOf(2));

            // when
            when(ewalletDao.findByIdAndLock(ewallet.getUuid())).thenReturn(Optional.of(ewallet));
            when(operationService.save(operationDto)).thenReturn(operationDto);

            // then
            OperationDto returnedOperationDto = ewalletService.deposit(operationDto);

            verifyReturnedOperationDto(operationDto, returnedOperationDto);
            verifyEwallet(ewallet, 3);
            verifySavedOperation(ewallet.getUuid(), OperationStatusDto.COMPLETED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.DEPOSIT, operationDto.getDestinationWalletUuid());
        }

        @Test
        void deposit_whenOperationServiceFails_thenNoEwalletSave() {

            // given
            OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(1), OperationTypeDto.DEPOSIT);
            Ewallet ewallet = createEwallet(operationDto.getEwalletUuid(), BigDecimal.ONE);

            // when
            when(ewalletDao.findByIdAndLock(ewallet.getUuid())).thenReturn(Optional.of(ewallet));
            doThrow(new RuntimeException()).when(operationService).save(any());

            // then
            assertThrows(RuntimeException.class, () -> ewalletService.deposit(operationDto));

            verify(ewalletDao, times(1)).findByIdAndLock(ewallet.getUuid());
            verifyEwallet(ewallet, 2);
            verifySavedOperation(ewallet.getUuid(), OperationStatusDto.COMPLETED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.DEPOSIT, operationDto.getDestinationWalletUuid());
        }

        @Test
        void deposit_whenEwalletDoesNotExist_thenNoSaveDeclinedOperation() {

            // given
            OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(1), OperationTypeDto.DEPOSIT);

            // when
            when(operationService.save(operationDto)).thenReturn(operationDto);

            // then
            OperationDto returnedOperationDto = ewalletService.deposit(operationDto);

            verify(ewalletDao, times(1)).findByIdAndLock(operationDto.getEwalletUuid());
            verify(ewalletDao, never()).save(any());
            verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.DEPOSIT, operationDto.getDestinationWalletUuid());
            verifyReturnedOperationDto(operationDto, returnedOperationDto);
        }

        @ParameterizedTest
        @MethodSource("provideArgumentsForIncorrectAmountCase")
        void deposit_whenAmountIsIncorrect_thenNoSaveDeclinedOperation(BigDecimal amount) {

            // given
            OperationDto operationDto = createOperationDtoWithBalance(amount, OperationTypeDto.DEPOSIT);

            // when
            when(operationService.save(operationDto)).thenReturn(operationDto);

            // then
            OperationDto returnedOperationDto = ewalletService.deposit(operationDto);

            verify(ewalletDao, never()).save(any());
            verify(ewalletDao, never()).findByIdAndLock(any());
            verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.DEPOSIT, operationDto.getDestinationWalletUuid());
            verifyReturnedOperationDto(operationDto, returnedOperationDto);
        }

        private static Stream<Arguments> provideArgumentsForIncorrectAmountCase() {
            return Stream.of(
                    // Amount is zero or negative
                    Arguments.of(BigDecimal.valueOf(0.0000000000)),
                    Arguments.of(BigDecimal.valueOf(0)),
                    Arguments.of(BigDecimal.valueOf(-1)),
                    Arguments.of(BigDecimal.valueOf(-1.11)),
                    // Incorrect fractional part
                    Arguments.of(BigDecimal.valueOf(0.333)),
                    Arguments.of(BigDecimal.valueOf(1.3333333333333333))
            );
        }

        private void verifySavedOperation(UUID walletUuid, OperationStatusDto operationStatusDto, BigDecimal amount, String description, OperationTypeDto operationTypeDto, UUID destinationWalletUuid) {

            ArgumentCaptor<OperationDto> captor = ArgumentCaptor.forClass(OperationDto.class);
            verify(operationService, times(1)).save(captor.capture());
            OperationDto capturedValue = captor.getValue();

            assertThat(capturedValue.getAmount()).isEqualTo(amount);
            assertThat(capturedValue.getOperationType()).isEqualTo(operationTypeDto);
            assertThat(capturedValue.getDescription()).isEqualTo(description);
            assertThat(capturedValue.getEwalletUuid()).isEqualTo(walletUuid);
            assertThat(capturedValue.getDestinationWalletUuid()).isEqualTo(destinationWalletUuid);
            assertThat(capturedValue.getOperationStatus()).isEqualTo(operationStatusDto);
        }

        //
        private static OperationDto createOperationDtoWithBalance(BigDecimal amount, OperationTypeDto operationTypeDto) {
            return OperationDto.builder()
                    .uuid(UUID.randomUUID())
                    .amount(amount)
                    .operationType(operationTypeDto)
                    .description(RandomStringUtils.randomAlphabetic(5))
                    .ewalletUuid(UUID.randomUUID())
                    .destinationWalletUuid(UUID.randomUUID())
                    .operationStatus(OperationStatusDto.COMPLETED)
                    .createdDate(new Timestamp(System.currentTimeMillis()))
                    .build();
        }

        @Nested
        class WithdrawEwalletTests {

            @Test
            void withdraw_success() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(1), OperationTypeDto.WITHDRAWAL);

                Ewallet ewallet = createEwallet(operationDto.getEwalletUuid(), BigDecimal.valueOf(2));

                // when
                when(ewalletDao.findByIdAndLock(ewallet.getUuid())).thenReturn(Optional.of(ewallet));
                when(operationService.save(operationDto)).thenReturn(operationDto);

                // then
                OperationDto returnedOperationDto = ewalletService.withdraw(operationDto);

                verifyReturnedOperationDto(operationDto, returnedOperationDto);
                verifyEwallet(ewallet, 1);
                verifySavedOperation(ewallet.getUuid(), OperationStatusDto.COMPLETED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.WITHDRAWAL, operationDto.getDestinationWalletUuid());
            }

            @Test
            void withdraw_whenOperationServiceFails_thenNoEwalletSave() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(1), OperationTypeDto.WITHDRAWAL);
                Ewallet ewallet = createEwallet(operationDto.getEwalletUuid(), BigDecimal.valueOf(2));

                // when
                when(ewalletDao.findByIdAndLock(ewallet.getUuid())).thenReturn(Optional.of(ewallet));
                doThrow(new RuntimeException()).when(operationService).save(any());

                // then
                assertThrows(RuntimeException.class, () -> ewalletService.withdraw(operationDto));

                verify(ewalletDao, times(1)).findByIdAndLock(ewallet.getUuid());
                verifyEwallet(ewallet, 1);
                verifySavedOperation(ewallet.getUuid(), OperationStatusDto.COMPLETED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.WITHDRAWAL, operationDto.getDestinationWalletUuid());
            }

            @Test
            void withdraw_whenEwalletDoesNotExist_thenNoSaveDeclinedOperation() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(1), OperationTypeDto.WITHDRAWAL);

                // when
                when(operationService.save(operationDto)).thenReturn(operationDto);

                // then
                OperationDto returnedOperationDto = ewalletService.withdraw(operationDto);

                verify(ewalletDao, times(1)).findByIdAndLock(operationDto.getEwalletUuid());
                verify(ewalletDao, never()).save(any());
                verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.WITHDRAWAL, operationDto.getDestinationWalletUuid());
                verifyReturnedOperationDto(operationDto, returnedOperationDto);
            }

            @Test
            void withdraw_whenNotEnoughBalance_thenNoSaveDeclinedOperation() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(2), OperationTypeDto.WITHDRAWAL);
                Ewallet ewallet = createEwallet(operationDto.getEwalletUuid(), BigDecimal.valueOf(1));

                // when
                when(operationService.save(operationDto)).thenReturn(operationDto);
                when(ewalletDao.findByIdAndLock(ewallet.getUuid())).thenReturn(Optional.of(ewallet));

                // then
                OperationDto returnedOperationDto = ewalletService.withdraw(operationDto);

                verify(ewalletDao, times(1)).findByIdAndLock(operationDto.getEwalletUuid());
                verify(ewalletDao, never()).save(any());
                verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.WITHDRAWAL, operationDto.getDestinationWalletUuid());
                verifyReturnedOperationDto(operationDto, returnedOperationDto);
            }

            @Test
            void withdraw_whenExceededDailyLimit_thenNoSaveDeclinedOperation() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(2), OperationTypeDto.WITHDRAWAL);
                Ewallet ewallet = createEwallet(operationDto.getEwalletUuid(), BigDecimal.valueOf(2));

                // when
                when(operationService.save(operationDto)).thenReturn(operationDto);
                when(ewalletDao.findByIdAndLock(ewallet.getUuid())).thenReturn(Optional.of(ewallet));
                when(operationService.lastDayWithdrawalsByWalletUuid(operationDto.getEwalletUuid())).thenReturn(List.of(createOperationDtoWithBalance(BigDecimal.valueOf(5001), OperationTypeDto.WITHDRAWAL)));

                // then
                OperationDto returnedOperationDto = ewalletService.withdraw(operationDto);

                verify(ewalletDao, times(1)).findByIdAndLock(operationDto.getEwalletUuid());
                verify(ewalletDao, never()).save(any());
                verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.WITHDRAWAL, operationDto.getDestinationWalletUuid());
                verifyReturnedOperationDto(operationDto, returnedOperationDto);
            }

            @Test
            void withdraw_whenAmountExceededDailyLimit_thenNoSaveDeclinedOperation() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(5001), OperationTypeDto.WITHDRAWAL);

                // when
                when(operationService.save(operationDto)).thenReturn(operationDto);

                // then
                OperationDto returnedOperationDto = ewalletService.withdraw(operationDto);

                verify(ewalletDao, never()).saveAll(any());
                verify(ewalletDao, never()).findByIdAndLock(any());
                verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.WITHDRAWAL, operationDto.getDestinationWalletUuid());
                verifyReturnedOperationDto(operationDto, returnedOperationDto);
            }

            @ParameterizedTest
            @MethodSource("provideArgumentsForIncorrectAmountCase")
            void withdraw_whenAmountIsIncorrect_thenNoSaveDeclinedOperation(BigDecimal amount) {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(amount, OperationTypeDto.WITHDRAWAL);

                // when
                when(operationService.save(operationDto)).thenReturn(operationDto);

                // then
                OperationDto returnedOperationDto = ewalletService.withdraw(operationDto);

                verify(ewalletDao, never()).saveAll(any());
                verify(ewalletDao, never()).findByIdAndLock(any());
                verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.WITHDRAWAL, operationDto.getDestinationWalletUuid());
                verifyReturnedOperationDto(operationDto, returnedOperationDto);
            }

            private static Stream<Arguments> provideArgumentsForIncorrectAmountCase() {
                return Stream.of(
                        // Amount is zero or negative
                        Arguments.of(BigDecimal.valueOf(0.0000000000)),
                        Arguments.of(BigDecimal.valueOf(0)),
                        Arguments.of(BigDecimal.valueOf(-1)),
                        Arguments.of(BigDecimal.valueOf(-1.11)),
                        // Incorrect fractional part
                        Arguments.of(BigDecimal.valueOf(0.333)),
                        Arguments.of(BigDecimal.valueOf(1.3333333333333333))
                );
            }
        }

        @Nested
        class MakeTransactionEwalletTests {

            @Test
            void makeTransaction_success() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(1), OperationTypeDto.TRANSACTION);

                Ewallet sourceEwallet = createEwallet(operationDto.getEwalletUuid(), BigDecimal.valueOf(2));
                Ewallet destinationEwallet = createEwallet(operationDto.getDestinationWalletUuid(), BigDecimal.valueOf(2));

                // when
                when(ewalletDao.findByIdAndLock(sourceEwallet.getUuid())).thenReturn(Optional.of(sourceEwallet));
                when(ewalletDao.findByIdAndLock(destinationEwallet.getUuid())).thenReturn(Optional.of(destinationEwallet));
                when(operationService.save(operationDto)).thenReturn(operationDto);

                // then
                OperationDto returnedOperationDto = ewalletService.makeTransaction(operationDto);

                verify(ewalletDao, times(1)).findByIdAndLock(sourceEwallet.getUuid());
                verify(ewalletDao, times(1)).findByIdAndLock(destinationEwallet.getUuid());
                verifyTransactionWallets(operationDto);
                verifyReturnedOperationDto(operationDto, returnedOperationDto);
                verifySavedOperation(sourceEwallet.getUuid(), OperationStatusDto.COMPLETED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.TRANSACTION, operationDto.getDestinationWalletUuid());
                verifyTransactionWallets(operationDto);
            }

            @Test
            void makeTransaction_whenOperationServiceFails_thenNoEwalletSave() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(1), OperationTypeDto.TRANSACTION);
                Ewallet sourceEwallet = createEwallet(operationDto.getEwalletUuid(), BigDecimal.valueOf(2));
                Ewallet destinationEwallet = createEwallet(operationDto.getDestinationWalletUuid(), BigDecimal.valueOf(2));

                // when
                when(ewalletDao.findByIdAndLock(sourceEwallet.getUuid())).thenReturn(Optional.of(sourceEwallet));
                when(ewalletDao.findByIdAndLock(destinationEwallet.getUuid())).thenReturn(Optional.of(destinationEwallet));

                doThrow(new RuntimeException()).when(operationService).save(any());

                // then
                assertThrows(RuntimeException.class, () -> ewalletService.makeTransaction(operationDto));

                verify(ewalletDao, times(1)).findByIdAndLock(sourceEwallet.getUuid());
                verify(ewalletDao, times(1)).findByIdAndLock(destinationEwallet.getUuid());
                verifyTransactionWallets(operationDto);
                verifySavedOperation(sourceEwallet.getUuid(), OperationStatusDto.COMPLETED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.TRANSACTION, operationDto.getDestinationWalletUuid());
                verifyTransactionWallets(operationDto);
            }

            @Test
            void makeTransaction_whenSourceEwalletDoesNotExist_thenNoSaveDeclinedOperation() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(1), OperationTypeDto.TRANSACTION);

                // when
                when(operationService.save(operationDto)).thenReturn(operationDto);

                // then
                OperationDto returnedOperationDto = ewalletService.makeTransaction(operationDto);

                verify(ewalletDao, times(1)).findByIdAndLock(operationDto.getEwalletUuid());
                verify(ewalletDao, never()).saveAll(any());
                verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.TRANSACTION, operationDto.getDestinationWalletUuid());
                verifyReturnedOperationDto(operationDto, returnedOperationDto);
            }

            @Test
            void makeTransaction_whenDestinationEwalletDoesNotExist_thenNoSaveDeclinedOperation() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(1), OperationTypeDto.TRANSACTION);
                Ewallet sourceEwallet = createEwallet(operationDto.getEwalletUuid(), BigDecimal.valueOf(2));

                // when
                when(ewalletDao.findByIdAndLock(sourceEwallet.getUuid())).thenReturn(Optional.of(sourceEwallet));
                when(operationService.save(operationDto)).thenReturn(operationDto);

                // then
                OperationDto returnedOperationDto = ewalletService.makeTransaction(operationDto);

                verify(ewalletDao, times(1)).findByIdAndLock(operationDto.getEwalletUuid());
                verify(ewalletDao, never()).saveAll(any());
                verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.TRANSACTION, operationDto.getDestinationWalletUuid());
                verifyReturnedOperationDto(operationDto, returnedOperationDto);
            }

            @Test
            void makeTransaction_whenNotEnoughBalance_thenNoSaveDeclinedOperation() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(4), OperationTypeDto.TRANSACTION);
                Ewallet sourceEwallet = createEwallet(operationDto.getEwalletUuid(), BigDecimal.valueOf(2));

                // when
                when(operationService.save(operationDto)).thenReturn(operationDto);
                when(ewalletDao.findByIdAndLock(sourceEwallet.getUuid())).thenReturn(Optional.of(sourceEwallet));

                // then
                OperationDto returnedOperationDto = ewalletService.makeTransaction(operationDto);

                verify(ewalletDao, times(1)).findByIdAndLock(operationDto.getEwalletUuid());
                verify(ewalletDao, never()).save(any());
                verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.TRANSACTION, operationDto.getDestinationWalletUuid());
                verifyReturnedOperationDto(operationDto, returnedOperationDto);
            }

            @Test
            void makeTransaction_whenAmountIsBiggerThanLimit_thenNoSaveDeclinedOperation() {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(BigDecimal.valueOf(2001), OperationTypeDto.TRANSACTION);

                // when
                when(operationService.save(operationDto)).thenReturn(operationDto);

                // then
                OperationDto returnedOperationDto = ewalletService.makeTransaction(operationDto);

                verify(ewalletDao, never()).saveAll(any());
                verify(ewalletDao, never()).findByIdAndLock(any());
                verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.TRANSACTION, operationDto.getDestinationWalletUuid());
                verifyReturnedOperationDto(operationDto, returnedOperationDto);
            }


            @ParameterizedTest
            @MethodSource("provideArgumentsForIncorrectAmountCase")
            void makeTransaction_whenAmountIsIncorrect_thenNoSaveDeclinedOperation(BigDecimal amount) {

                // given
                OperationDto operationDto = createOperationDtoWithBalance(amount, OperationTypeDto.TRANSACTION);

                // when
                when(operationService.save(operationDto)).thenReturn(operationDto);

                // then
                OperationDto returnedOperationDto = ewalletService.makeTransaction(operationDto);

                verify(ewalletDao, never()).saveAll(any());
                verify(ewalletDao, never()).findByIdAndLock(any());
                verifySavedOperation(operationDto.getEwalletUuid(), OperationStatusDto.DECLINED, operationDto.getAmount(), operationDto.getDescription(), OperationTypeDto.TRANSACTION, operationDto.getDestinationWalletUuid());
                verifyReturnedOperationDto(operationDto, returnedOperationDto);
            }

            private static Stream<Arguments> provideArgumentsForIncorrectAmountCase() {
                return Stream.of(
                        // Amount is zero or negative
                        Arguments.of(BigDecimal.valueOf(0.0000000000)),
                        Arguments.of(BigDecimal.valueOf(0)),
                        Arguments.of(BigDecimal.valueOf(-1)),
                        Arguments.of(BigDecimal.valueOf(-1.11)),
                        // Incorrect fractional part
                        Arguments.of(BigDecimal.valueOf(0.333)),
                        Arguments.of(BigDecimal.valueOf(1.3333333333333333))
                );
            }

        }
    }

    private void verifyTransactionWallets(OperationDto operationDto) {
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(ewalletDao, times(1)).saveAll(captor.capture());
        List<Ewallet> capturedEwallet = captor.getValue();

        Ewallet savedSourceEwallet = capturedEwallet.stream().filter(wallet -> wallet.getUuid().equals(operationDto.getEwalletUuid())).findFirst().get();
        Ewallet savedDestinationEwallet = capturedEwallet.stream().filter(wallet -> wallet.getUuid().equals(operationDto.getDestinationWalletUuid())).findFirst().get();

        assertThat(savedSourceEwallet.getUuid()).isEqualTo(savedSourceEwallet.getUuid());
        assertThat(savedSourceEwallet.getBalance()).isEqualTo(BigDecimal.valueOf(1).setScale(2));
        assertThat(savedSourceEwallet.getCustomerUuid()).isEqualTo(savedSourceEwallet.getCustomerUuid());
        assertThat(savedDestinationEwallet.getUuid()).isEqualTo(savedDestinationEwallet.getUuid());
        assertThat(savedDestinationEwallet.getBalance()).isEqualTo(BigDecimal.valueOf(3).setScale(2));
        assertThat(savedDestinationEwallet.getCustomerUuid()).isEqualTo(savedDestinationEwallet.getCustomerUuid());
    }

    private static void verifyReturnedOperationDto(OperationDto operationDto, OperationDto returnedOperationDto) {

        assertThat(returnedOperationDto.getUuid()).isEqualTo(operationDto.getUuid());
        assertThat(returnedOperationDto.getAmount()).isEqualTo(operationDto.getAmount());
        assertThat(returnedOperationDto.getOperationType()).isEqualTo(operationDto.getOperationType());
        assertThat(returnedOperationDto.getDescription()).isEqualTo(operationDto.getDescription());
        assertThat(returnedOperationDto.getEwalletUuid()).isEqualTo(operationDto.getEwalletUuid());
        assertThat(returnedOperationDto.getDestinationWalletUuid()).isEqualTo(operationDto.getDestinationWalletUuid());
        assertThat(returnedOperationDto.getOperationStatus()).isEqualTo(operationDto.getOperationStatus());
        assertThat(returnedOperationDto.isSuspicious()).isEqualTo(operationDto.isSuspicious());
        assertThat(returnedOperationDto.getCreatedDate()).isEqualTo(operationDto.getCreatedDate());
    }

    private void verifyEwallet(Ewallet ewallet, int balance) {

        ArgumentCaptor<Ewallet> captor = ArgumentCaptor.forClass(Ewallet.class);
        verify(ewalletDao, times(1)).save(captor.capture());

        Ewallet capturedEwallet = captor.getValue();
        assertThat(capturedEwallet.getUuid()).isEqualTo(ewallet.getUuid());
        assertThat(capturedEwallet.getBalance()).isEqualTo(BigDecimal.valueOf(balance).setScale(2));
        assertThat(capturedEwallet.getCustomerUuid()).isEqualTo(ewallet.getCustomerUuid());
    }
}
