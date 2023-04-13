package ewallet.service.customer;

import ewallet.entity.customer.Customer;
import ewallet.repository.customer.CustomerDao;
import ewallet.repository.ewallet.EwalletDao;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static ewallet.TestHelper.randomCreateCustomerRequestDto;
import static ewallet.TestHelper.randomCustomer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceUTest {

    @Mock
    private EwalletDao ewalletDao;

    @Mock
    private CustomerDao customerDao;

    @InjectMocks
    private CustomerService customerService;

    @Nested
    class SaveCustomerTests {


        /*TODO LIST:
            1. Test success scenario
            2. Test when ewalletService fails
         */

        @Test
        void save_whenCustomerDaoFails_thenException() {

            // when
            when(customerDao.save(any())).thenThrow(RuntimeException.class);

            // then
            assertThrows(RuntimeException.class, () -> customerService.save(randomCreateCustomerRequestDto()));

            verify(customerDao, times(1)).save(any());
            verify(customerDao, never()).flush();
            verifyNoInteractions(ewalletDao);
        }
    }

    @Nested
    class GetCustomerTests {

        @Test
        void get_success() {

            // given
            Customer customer = randomCustomer();

            // when
            when(customerDao.findById(customer.getUuid())).thenReturn(Optional.of(customer));

            // then
            Customer fetchedCustomer = customerService.get(customer.getUuid());

            assertThat(fetchedCustomer.getUuid()).isEqualTo(customer.getUuid());
            verify(customerDao, times(1)).findById(customer.getUuid());
        }

        @Test
        void get_whenNotFound_thenNoSuchElementException() {

            // when
            when(customerDao.findById(any())).thenReturn(Optional.empty());

            // then
            assertThrows(NoSuchElementException.class, () -> customerService.get(any()));

            verify(customerDao, times(1)).findById(any());
        }
    }
}
