package ewallet.repository.customer;

import ewallet.entity.customer.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static ewallet.TestHelper.randomCustomer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class CustomerRepositoryITest {

    @Autowired
    private CustomerDao customerDao;

    @Test
    void whenSaved_thenFindsById() {

        // given
        Customer customer = randomCustomer();
        customerDao.save(customer);

        // then
        assertThat(customerDao.findById(customer.getUuid())).isNotEmpty();
    }
}
