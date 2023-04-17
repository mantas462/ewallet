package ewallet.service.customer;

import ewallet.dto.customer.internal.CustomerDto;
import ewallet.dto.ewallet.internal.EwalletDto;
import ewallet.entity.customer.Customer;
import ewallet.repository.customer.CustomerDao;
import ewallet.service.ewallet.EwalletService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static ewallet.TestHelper.createCustomer;
import static ewallet.TestHelper.randomCustomer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceUTest {

    @Mock
    private EwalletService ewalletService;

    @Mock
    private CustomerDao customerDao;

    @InjectMocks
    private CustomerService customerService;

    @Nested
    class SaveCustomerTests {

        @Test
        void save_success() {

            // given
            Customer customer = randomCustomer();
            CustomerDto customerDto = createCustomer(customer.getUuid(), customer.getFirstName(), customer.getLastName(), customer.getEmail());

            // when
            when(customerDao.save(any())).thenReturn(customer);

            // then
            CustomerDto returnedCustomer = customerService.save(customerDto);

            verifyReturnedCustomer(customer, returnedCustomer);
            verifySavedCustomer(customer);
            verifySavedEwallet();
        }

        @Test
        void save_whenCustomerDaoFails_thenException() {

            // when
            when(customerDao.save(any())).thenThrow(RuntimeException.class);

            // then
            assertThrows(RuntimeException.class, () -> customerService.save(randomCustomerDto()));

            verify(customerDao, times(1)).save(any());
            verify(customerDao, never()).flush();
            verifyNoInteractions(ewalletService);
        }

        private static CustomerDto randomCustomerDto() {

            return CustomerDto.builder()
                    .uuid(UUID.randomUUID())
                    .firstName(RandomStringUtils.randomAlphabetic(5))
                    .lastName(RandomStringUtils.randomAlphabetic(5))
                    .email(RandomStringUtils.randomAlphabetic(5))
                    .build();
        }

        private void verifySavedEwallet() {

            ArgumentCaptor<EwalletDto> ewalletCaptor = ArgumentCaptor.forClass(EwalletDto.class);
            verify(ewalletService, times(1)).save(ewalletCaptor.capture());
            EwalletDto capturedEwallet = ewalletCaptor.getValue();
            assertThat(capturedEwallet.getCustomerUuid()).isNotNull();
        }

        private void verifySavedCustomer(Customer customer) {

            ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
            verify(customerDao, times(1)).save(customerCaptor.capture());
            Customer capturedCustomer = customerCaptor.getValue();
            assertThat(capturedCustomer.getUuid()).isNotNull();
            assertThat(capturedCustomer.getFirstName()).isEqualTo(customer.getFirstName());
            assertThat(capturedCustomer.getLastName()).isEqualTo(customer.getLastName());
            assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        }

        private static void verifyReturnedCustomer(Customer customer, CustomerDto returnedCustomer) {

            assertThat(returnedCustomer.getFirstName()).isEqualTo(customer.getFirstName());
            assertThat(returnedCustomer.getLastName()).isEqualTo(customer.getLastName());
            assertThat(returnedCustomer.getEmail()).isEqualTo(customer.getEmail());
            assertThat(returnedCustomer.getUuid()).isEqualTo(customer.getUuid());
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
