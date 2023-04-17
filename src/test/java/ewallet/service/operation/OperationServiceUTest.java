package ewallet.service.operation;

import ewallet.dto.operation.internal.OperationDto;
import ewallet.dto.operation.internal.OperationStatusDto;
import ewallet.dto.operation.internal.OperationTypeDto;
import ewallet.entity.operation.Operation;
import ewallet.entity.operation.OperationStatus;
import ewallet.entity.operation.OperationType;
import ewallet.repository.operation.OperationDao;
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
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperationServiceUTest {

    @Mock
    private OperationDao operationDao;

    @InjectMocks
    private OperationService operationService;

    @Nested
    class SaveTests {

        @ParameterizedTest
        @MethodSource("provideHoursForSuccessCase")
        void save_success(OperationTypeDto operationTypeDto, BigDecimal amount, boolean suspicious) {

            // given
            OperationDto operationDto = createOperationDto(operationTypeDto, amount);
            Operation operation = createOperation(operationDto, suspicious);

            // when
            when(operationDao.save(any())).thenReturn(operation);

            // then
            OperationDto returnedOperationDto = operationService.save(operationDto);

            verifyReturnedOperationDto(operationDto, returnedOperationDto, suspicious);
            verifySavedOperation(operationDto, suspicious);
        }

        private static Stream<Arguments> provideHoursForSuccessCase() {
            return Stream.of(
                    Arguments.of(OperationTypeDto.WITHDRAWAL, BigDecimal.valueOf(10001), false),
                    Arguments.of(OperationTypeDto.DEPOSIT, BigDecimal.valueOf(10001), false),
                    Arguments.of(OperationTypeDto.TRANSACTION, BigDecimal.valueOf(9999), false),
                    Arguments.of(OperationTypeDto.TRANSACTION, BigDecimal.valueOf(10001), true)
            );
        }


        @Test
        void save_whenDaoFails_thenException() {

            // given
            OperationDto operationDto = createOperationDto(OperationTypeDto.DEPOSIT, BigDecimal.ONE);

            // when
            when(operationDao.save(any())).thenThrow(RuntimeException.class);

            // then
            assertThrows(RuntimeException.class, () -> operationService.save(operationDto));
        }
    }

    @Nested
    class LastDayWithdrawalsByWalletUuidTests {

        @Test
        void save_success() {

            // given
            OperationDto operationDto = createOperationDto(OperationTypeDto.DEPOSIT, BigDecimal.ONE);
            Operation operation = createOperation(operationDto, false);

            // when
            when(operationDao.lastDayWithdrawalsByWalletUuidAndLock(any(UUID.class), any(Timestamp.class))).thenReturn(List.of(operation));

            // then
            List<OperationDto> returnedOperations = operationService.lastDayWithdrawalsByWalletUuid(operation.getUuid());
            ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
            verify(operationDao, times(1)).lastDayWithdrawalsByWalletUuidAndLock(captor.capture(), any());
            UUID capturedUuid = captor.getValue();
            assertThat(capturedUuid).isEqualTo(operation.getUuid());

            verifyReturnedOperationDto(operationDto, returnedOperations.get(0), false);
        }

        @Test
        void save_whenDaoFails_thenException() {

            // when
            when(operationDao.lastDayWithdrawalsByWalletUuidAndLock(any(), any())).thenThrow(RuntimeException.class);

            // then
            assertThrows(RuntimeException.class, () -> operationDao.lastDayWithdrawalsByWalletUuidAndLock(any(), any()));
        }
    }

    private void verifySavedOperation(OperationDto operationDto, boolean suspicious) {
        ArgumentCaptor<Operation> captor = ArgumentCaptor.forClass(Operation.class);
        verify(operationDao, times(1)).save(captor.capture());
        Operation capturedOperation = captor.getValue();

        assertThat(capturedOperation.getUuid()).isNotNull();
        assertThat(capturedOperation.getAmount()).isEqualTo(operationDto.getAmount());
        assertThat(capturedOperation.getOperationType().toString()).isEqualTo(operationDto.getOperationType().toString());
        assertThat(capturedOperation.getOperationStatus().toString()).isEqualTo(operationDto.getOperationStatus().toString());
        assertThat(capturedOperation.getEwalletUuid()).isEqualTo(operationDto.getEwalletUuid());
        assertThat(capturedOperation.getDestinationWalletUuid()).isEqualTo(operationDto.getDestinationWalletUuid());
        assertThat(capturedOperation.isSuspicious()).isEqualTo(suspicious);
    }

    private static void verifyReturnedOperationDto(OperationDto operationDto, OperationDto returnedOperationDto, boolean suspicious) {
        assertThat(returnedOperationDto.getUuid()).isNotNull();
        assertThat(returnedOperationDto.getAmount()).isEqualTo(operationDto.getAmount());
        assertThat(returnedOperationDto.getOperationType()).isEqualTo(operationDto.getOperationType());
        assertThat(returnedOperationDto.getOperationStatus()).isEqualTo(operationDto.getOperationStatus());
        assertThat(returnedOperationDto.getEwalletUuid()).isEqualTo(operationDto.getEwalletUuid());
        assertThat(returnedOperationDto.getDestinationWalletUuid()).isEqualTo(operationDto.getDestinationWalletUuid());
        assertThat(returnedOperationDto.isSuspicious()).isEqualTo(suspicious);
    }

    private static OperationDto createOperationDto(OperationTypeDto operationTypeDto, BigDecimal amount) {
        return OperationDto.builder()
                .uuid(UUID.randomUUID())
                .amount(amount)
                .operationType(operationTypeDto)
                .operationStatus(OperationStatusDto.COMPLETED)
                .ewalletUuid(UUID.randomUUID())
                .destinationWalletUuid(UUID.randomUUID())
                .build();
    }

    private static Operation createOperation(OperationDto operationDto, boolean suspicious) {
        return Operation.builder()
                .uuid(operationDto.getUuid())
                .amount(operationDto.getAmount())
                .operationType(OperationType.valueOf(operationDto.getOperationType().toString()))
                .operationStatus(OperationStatus.valueOf(operationDto.getOperationStatus().toString()))
                .ewalletUuid(operationDto.getEwalletUuid())
                .destinationWalletUuid(operationDto.getDestinationWalletUuid())
                .createdDate(operationDto.getCreatedDate())
                .suspicious(suspicious)
                .build();
    }
}
