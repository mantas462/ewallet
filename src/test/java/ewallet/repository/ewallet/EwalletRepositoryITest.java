package ewallet.repository.ewallet;

import ewallet.entity.customer.Customer;
import ewallet.entity.ewallet.Ewallet;
import ewallet.repository.customer.CustomerDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static ewallet.TestHelper.ewalletWithCustomer;
import static ewallet.TestHelper.randomCustomer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class EwalletRepositoryITest {

    @Autowired
    private EwalletDao ewalletDao;

    @Autowired
    private CustomerDao customerDao;

    /*
    TODO LIST:
        1. Test saveAll() method
     */

    @Test
    void whenSaved_thenFindsById() {

        // given
        Customer customer = randomCustomer();
        Ewallet ewallet = ewalletWithCustomer(customer);

        customerDao.save(customer);
        ewalletDao.save(ewallet);

        // then
        assertThat(ewalletDao.findById(ewallet.getUuid())).isNotEmpty();
    }
}
