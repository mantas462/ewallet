package ewallet.service.operation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OperationServiceUTest {

    /* TODO LIST:
        1. Test save method:
            1.1. Test when OperationType is not Transaction
            1.2. Test when OperationType is Transaction and amount does not exceed the limit
            1.2. Test when OperationType is Transaction and amount exceeds the limit
        2. Test lastDayWithdrawalsByWalletUuid method:
            2.1. Test success scenario
     */
}
