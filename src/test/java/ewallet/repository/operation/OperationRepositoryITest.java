package ewallet.repository.operation;

import ewallet.entity.operation.Operation;
import ewallet.entity.operation.OperationStatus;
import ewallet.entity.operation.OperationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static ewallet.TestHelper.randomOperation;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class OperationRepositoryITest {

    @Autowired
    private OperationDao operationDao;

    private static Operation operation = randomOperation();

    @ParameterizedTest
    @MethodSource("provideHoursForSuccessCase")
    void lastDayWithdrawalsByWalletUuidAndLock_success(int timeType, int time) {

        // given
        Timestamp timestamp = createTimestamp(timeType, time);

        // then
        operationDao.save(operation);

        List<Operation> savedOperation = operationDao.lastDayWithdrawalsByWalletUuidAndLock(operation.getEwalletUuid(), timestamp);
        assertThat(savedOperation.isEmpty()).isFalse();
        assertThat(savedOperation.get(0).getUuid()).isEqualTo(operation.getUuid());
    }

    private static Stream<Arguments> provideHoursForSuccessCase() {
        return Stream.of(
                Arguments.of(Calendar.HOUR, -1),
                Arguments.of(Calendar.HOUR, -5),
                Arguments.of(Calendar.HOUR, -23)
        );
    }

    @ParameterizedTest
    @MethodSource("provideHoursForUnsuccessfulCase")
    void lastDayWithdrawalsByWalletUuidAndLock_whenOverDay_thenEmpty(int timeType, int time) {

        // given
        Timestamp timestamp = createTimestamp(timeType, time);

        // then
        operationDao.save(operation);

        List<Operation> savedOperation = operationDao.lastDayWithdrawalsByWalletUuidAndLock(operation.getEwalletUuid(), timestamp);
        assertThat(savedOperation.isEmpty()).isTrue();
    }

    private static Stream<Arguments> provideHoursForUnsuccessfulCase() {
        return Stream.of(
                Arguments.of(Calendar.DAY_OF_MONTH, 2),
                Arguments.of(Calendar.DAY_OF_MONTH, 3)
        );
    }

    @Test
    void lastDayWithdrawalsByWalletUuidAndLock_whenOperationType_thenEmpty() {

        // given
        Operation depositOperation = Operation.builder()
                .uuid(UUID.randomUUID())
                .amount(BigDecimal.ONE)
                .operationType(OperationType.DEPOSIT)
                .operationStatus(OperationStatus.COMPLETED)
                .ewalletUuid(UUID.randomUUID())
                .createdDate(createTimestamp(Calendar.HOUR, -1))
                .build();

        // then
        operationDao.save(depositOperation);

        List<Operation> savedOperation = operationDao.lastDayWithdrawalsByWalletUuidAndLock(operation.getEwalletUuid(), depositOperation.getCreatedDate());
        assertThat(savedOperation.isEmpty()).isTrue();
    }

    @Test
    void lastDayWithdrawalsByWalletUuidAndLock_whenOperationStatus_thenEmpty() {

        // given
        Operation declinedOperation = Operation.builder()
                .uuid(UUID.randomUUID())
                .amount(BigDecimal.ONE)
                .operationType(OperationType.WITHDRAWAL)
                .operationStatus(OperationStatus.DECLINED)
                .ewalletUuid(UUID.randomUUID())
                .createdDate(createTimestamp(Calendar.HOUR, -1))
                .build();

        // then
        operationDao.save(declinedOperation);

        List<Operation> savedOperation = operationDao.lastDayWithdrawalsByWalletUuidAndLock(operation.getEwalletUuid(), declinedOperation.getCreatedDate());
        assertThat(savedOperation.isEmpty()).isTrue();
    }

    private Timestamp createTimestamp(int timeType, int time) {

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(operation.getCreatedDate());
        calendar.add(timeType, time);
        timestamp.setTime(calendar.getTime().getTime());

        return timestamp;
    }
}
