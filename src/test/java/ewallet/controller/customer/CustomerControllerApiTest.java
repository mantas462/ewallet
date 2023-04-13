package ewallet.controller.customer;

import ewallet.dto.customer.CreateCustomerRequestDto;
import ewallet.dto.customer.CreateCustomerResponseDto;
import ewallet.entity.customer.Customer;
import ewallet.service.customer.CustomerService;
import ewallet.util.api.ErrorCode;
import ewallet.util.api.MediaType;
import ewallet.util.api.RestUrl;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static ewallet.TestHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        @Test
        void save_success() throws Exception {

            // given
            CreateCustomerResponseDto createCustomerResponseDto = createCustomerResponseDto();

            // when
            when(customerService.save(any())).thenReturn(createCustomerResponseDto);

            // then
            mockMvc.perform(post(SAVE_URL)
                            .content(asJsonString(randomCreateCustomerRequestDto()))
                            .contentType(MediaType.CREATE_CUSTOMER_REQUEST))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.uuid").value(createCustomerResponseDto.getUuid().toString()));

            verify(customerService, times(1)).save(any());
        }

        @Test
        void save_whenServiceFails_thenBadRequest() throws Exception {

            // when
            when(customerService.save(any())).thenThrow(NoSuchElementException.class);

            // then
            mockMvc.perform(post(SAVE_URL)
                            .content(asJsonString(randomCreateCustomerRequestDto()))
                            .contentType(MediaType.CREATE_CUSTOMER_REQUEST))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCode.ENTITY_NOT_FOUND.toString()));

            verify(customerService, times(1)).save(any());
        }

        @Test
        void save_whenEmptyContentType_thenBadRequest() throws Exception {

            // then
            mockMvc.perform(post(SAVE_URL)
                            .content(asJsonString(randomCreateCustomerRequestDto())))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCode.BAD_REQUEST.toString()));

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
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCode.BAD_REQUEST.toString()))
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

        private static CreateCustomerResponseDto createCustomerResponseDto() {
            return CreateCustomerResponseDto.builder()
                    .uuid(UUID.randomUUID())
                    .build();
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
                            .accept(MediaType.GET_CUSTOMER_RESPONSE))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.uuid").value(customer.getUuid().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(customer.getFirstName().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(customer.getLastName().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(customer.getEmail().toString()));

            verify(customerService, times(1)).get(any());
        }

        @Test
        void get_whenServiceFails_thenBadRequest() throws Exception {

            // when
            when(customerService.get(any())).thenThrow(NoSuchElementException.class);

            // then
            mockMvc.perform(get(GET_URL)
                            .accept(MediaType.GET_CUSTOMER_RESPONSE))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCode.ENTITY_NOT_FOUND.toString()));

            verify(customerService, times(1)).get(any());
        }

        @Test
        void get_whenEmptyUuidInPath_thenBadRequest() throws Exception {

            // given
            String urlWithoutUuid = RestUrl.API_V1 + "/customer/ ";

            // then
            mockMvc.perform(get(urlWithoutUuid)
                            .accept(MediaType.GET_CUSTOMER_RESPONSE))
                    .andExpect(status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(ErrorCode.BAD_REQUEST.toString()));

            verifyNoInteractions(customerService);
        }
    }
}
