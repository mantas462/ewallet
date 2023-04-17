package ewallet.repository.ewallet;

import ewallet.entity.ewallet.Ewallet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static ewallet.TestHelper.randomEwallet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class EwalletRepositoryITest {

    @Autowired
    private EwalletDao ewalletDao;

    private static Ewallet ewallet = randomEwallet();

    @Test
    void saveAndFindByAndLock_success() {

        // then
        ewalletDao.save(ewallet);

        assertThat(ewalletDao.findByIdAndLock(ewallet.getUuid())).isNotEmpty();
    }

    @Test
    void save_whenFindAndLockByRandomUuid_thenEmpty() {

        // then
        ewalletDao.save(ewallet);

        assertThat(ewalletDao.findByIdAndLock(UUID.randomUUID()).isEmpty());
    }
}
