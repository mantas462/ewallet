package ewallet.repository.operation;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class OperationRepositoryITest {

    /*
    TODO LIST:
        1. Test save() method
        1. Test custom query lastDayWithdrawalsByWalletUuid() method
     */
}
