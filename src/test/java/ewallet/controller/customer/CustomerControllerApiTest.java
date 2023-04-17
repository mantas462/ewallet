package ewallet.controller.customer;

import ewallet.dto.customer.api.CreateCustomerRequestDto;
import ewallet.dto.customer.internal.CustomerDto;
import ewallet.entity.customer.Customer;
import ewallet.service.customer.CustomerService;
import ewallet.util.api.MediaType;
import ewallet.util.api.RestUrl;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.stream.Stream;

import static ewallet.TestHelper.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerApiTest {

    @MockBean
    private CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class SaveCustomerTests {

        private static final String SAVE_URL = RestUrl.API_V1 + RestUrl.CUSTOMER;

        private static CreateCustomerRequestDto request = randomCreateCustomerRequestDto();

        @Test
        void save_success() throws Exception {

            // given
            CustomerDto customerDto = createCustomer(UUID.randomUUID(), request.getFirstName(), request.getLastName(), request.getEmail());

            // when
            when(customerService.save(any(CustomerDto.class))).thenReturn(customerDto);

            // then
            mockMvc.perform(post(SAVE_URL)
                            .content(asJsonString(request))
                            .contentType(MediaType.CREATE_CUSTOMER_REQUEST))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.uuid").value(customerDto.getUuid().toString()));

            verifySavedCustomer(customerDto);
        }

        @Test
        void save_whenEmptyContentType_thenBadRequest() throws Exception {

            // then
            mockMvc.perform(post(SAVE_URL)
                            .content(asJsonString(request)))
                    .andExpect(status().isUnsupportedMediaType());

            verifyNoInteractions(customerService);
        }

        @ParameterizedTest
        @MethodSource("provideArgumentsForRequestValidation")
        void save_whenFieldsAreNotValid_thenBadRequest(CreateCustomerRequestDto createCustomerRequestDto) throws Exception {

            // then
            mockMvc.perform(post(SAVE_URL)
                            .content(asJsonString(createCustomerRequestDto))
                            .contentType(MediaType.CREATE_CUSTOMER_REQUEST))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            verifyNoInteractions(customerService);
        }

        private static Stream<Arguments> provideArgumentsForRequestValidation() {
            return Stream.of(
                    Arguments.of(createCustomerRequestDto("", RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5))),
                    Arguments.of(createCustomerRequestDto(null, RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5))),
                    Arguments.of(createCustomerRequestDto(RandomStringUtils.randomAlphabetic(5), "", RandomStringUtils.randomAlphabetic(5))),
                    Arguments.of(createCustomerRequestDto(RandomStringUtils.randomAlphabetic(5), null, RandomStringUtils.randomAlphabetic(5))),
                    Arguments.of(createCustomerRequestDto(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5), "")),
                    Arguments.of(createCustomerRequestDto(RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(5), null))
            );
        }

        private void verifySavedCustomer(CustomerDto customerDto) {
            ArgumentCaptor<CustomerDto> captor = ArgumentCaptor.forClass(CustomerDto.class);
            verify(customerService, times(1)).save(captor.capture());
            CustomerDto capturedCustomer = captor.getValue();
            assertThat(capturedCustomer).isNotNull();
            assertThat(capturedCustomer.getFirstName()).isEqualTo(customerDto.getFirstName());
            assertThat(capturedCustomer.getLastName()).isEqualTo(customerDto.getLastName());
            assertThat(capturedCustomer.getEmail()).isEqualTo(customerDto.getEmail());
        }
    }

    @Nested
    class GetCustomerTests {

        private static UUID customerUuid = UUID.randomUUID();

        private static final String GET_URL = RestUrl.API_V1 + "/customer/" + customerUuid;

        @Test
        void get_success() throws Exception {

            // given
            Customer customer = randomCustomer();

            // when
            when(customerService.get(customerUuid)).thenReturn(customer);

            // then
            mockMvc.perform(get(GET_URL)
                            .contentType(MediaType.GET_CUSTOMER_REQUEST))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.uuid").value(customer.getUuid().toString()))
                    .andExpect(jsonPath("$.firstName").value(customer.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(customer.getLastName()))
                    .andExpect(jsonPath("$.email").value(customer.getEmail()));

            verify(customerService, times(1)).get(customerUuid);
        }

        @Test
        void get_whenEmptyUuidInPath_thenBadRequest() throws Exception {

            // given
            String urlWithoutUuid = RestUrl.API_V1 + "/customer/ ";

            // then
            mockMvc.perform(get(urlWithoutUuid)
                            .contentType(MediaType.GET_CUSTOMER_REQUEST))
                    .andExpect(status().isInternalServerError());

            verifyNoInteractions(customerService);
        }
    }
}
