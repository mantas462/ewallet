package ewallet.controller.ewallet;

import ewallet.dto.ewallet.api.DepositEwalletRequestDto;
import ewallet.dto.ewallet.api.MakeTransactionEwalletRequestDto;
import ewallet.dto.ewallet.api.WithdrawEwalletRequestDto;
import ewallet.dto.operation.internal.OperationDto;
import ewallet.dto.operation.internal.OperationTypeDto;
import ewallet.service.ewallet.EwalletService;
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

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static ewallet.TestHelper.asJsonString;
import static ewallet.TestHelper.createOperationDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EwalletController.class)
class EwalletControllerApiTest {

    @MockBean
    private EwalletService ewalletService;

    @Autowired
    private MockMvc mockMvc;

    private static UUID EWALLET_UUID = UUID.randomUUID();

    @Nested
    class DepositEwalletTests {

        private static DepositEwalletRequestDto request = randomDepositRequestDto();

        private static final String DEPOSIT_URL = RestUrl.API_V1 + "/ewallet/" + EWALLET_UUID + "/deposit";

        @Test
        void deposit_success() throws Exception {

            // given
            OperationDto operationDto = createOperationDto(EWALLET_UUID, request);

            // when
            when(ewalletService.deposit(any())).thenReturn(operationDto);

            // then
            mockMvc.perform(put(DEPOSIT_URL)
                            .content(asJsonString(request))
                            .contentType(MediaType.DEPOSIT_EWALLET_REQUEST))
                    .andExpect(status().isAccepted());

            ArgumentCaptor<OperationDto> captor = ArgumentCaptor.forClass(OperationDto.class);
            verify(ewalletService, times(1)).deposit(captor.capture());
            verifyThatWalletServiceWasCalled(captor.getValue(), request.getAmount(), OperationTypeDto.DEPOSIT, request.getDescription(), null);
        }

        @Test
        void deposit_whenEmptyContentType_thenBadRequest() throws Exception {

            // then
            mockMvc.perform(put(DEPOSIT_URL)
                            .content(asJsonString(request)))
                    .andExpect(status().isUnsupportedMediaType());

            verifyNoInteractions(ewalletService);
        }

        @ParameterizedTest
        @MethodSource("provideArgumentsForRequestValidation")
        void deposit_whenAmountIsNotValid_thenBadRequest(DepositEwalletRequestDto request) throws Exception {

            // then
            mockMvc.perform(put(DEPOSIT_URL)
                            .content(asJsonString(request))
                            .contentType(MediaType.DEPOSIT_EWALLET_REQUEST))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            verifyNoInteractions(ewalletService);
        }

        @Test
        void deposit_whenEmptyUuidInPath_thenBadRequest() throws Exception {

            // given
            String urlWithoutUuid = RestUrl.API_V1 + "/ewallet/ /deposit";

            // then
            mockMvc.perform(put(urlWithoutUuid)
                            .contentType(MediaType.DEPOSIT_EWALLET_REQUEST))
                    .andExpect(status().isInternalServerError());

            verifyNoInteractions(ewalletService);
        }

        private static Stream<Arguments> provideArgumentsForRequestValidation() {

            return Stream.of(
                    Arguments.of(DepositEwalletRequestDto.builder()
                            .amount(null)
                            .build()),
                    Arguments.of(DepositEwalletRequestDto.builder()
                            .amount(BigDecimal.valueOf(-1))
                            .build()),
                    Arguments.of(DepositEwalletRequestDto.builder()
                            .amount(BigDecimal.ZERO)
                            .build())
            );
        }

        public static DepositEwalletRequestDto randomDepositRequestDto() {

            return DepositEwalletRequestDto.builder()
                    .amount(BigDecimal.ONE)
                    .description(RandomStringUtils.randomAlphabetic(5))
                    .build();
        }
    }

    @Nested
    class WithdrawEwalletTests {

        private static WithdrawEwalletRequestDto request = randomWithdrawRequestDto();

        private static final String WITHDRAWAL_URL = RestUrl.API_V1 + "/ewallet/" + EWALLET_UUID + "/withdrawal";

        @Test
        void withdraw_success() throws Exception {

            // given
            OperationDto operationDto = createOperationDto(EWALLET_UUID, request);

            // when
            when(ewalletService.withdraw(any())).thenReturn(operationDto);

            // then
            mockMvc.perform(put(WITHDRAWAL_URL)
                            .content(asJsonString(request))
                            .contentType(MediaType.WITHDRAW_EWALLET_REQUEST))
                    .andExpect(status().isAccepted());

            ArgumentCaptor<OperationDto> captor = ArgumentCaptor.forClass(OperationDto.class);
            verify(ewalletService, times(1)).withdraw(captor.capture());
            verifyThatWalletServiceWasCalled(captor.getValue(), request.getAmount(), OperationTypeDto.WITHDRAWAL, request.getDescription(), null);
        }

        @Test
        void withdraw_whenEmptyContentType_thenBadRequest() throws Exception {

            // then
            mockMvc.perform(put(WITHDRAWAL_URL)
                            .content(asJsonString(request)))
                    .andExpect(status().isUnsupportedMediaType());

            verifyNoInteractions(ewalletService);
        }

        @ParameterizedTest
        @MethodSource("provideArgumentsForRequestValidation")
        void withdraw_whenAmountIsNotValid_thenBadRequest(WithdrawEwalletRequestDto request) throws Exception {

            // then
            mockMvc.perform(put(WITHDRAWAL_URL)
                            .content(asJsonString(request))
                            .contentType(MediaType.WITHDRAW_EWALLET_REQUEST))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            verifyNoInteractions(ewalletService);
        }

        @Test
        void withdraw_whenEmptyUuidInPath_thenBadRequest() throws Exception {

            // given
            String urlWithoutUuid = RestUrl.API_V1 + "/ewallet/ /withdrawal";

            // then
            mockMvc.perform(put(urlWithoutUuid)
                            .contentType(MediaType.WITHDRAW_EWALLET_REQUEST))
                    .andExpect(status().isInternalServerError());

            verifyNoInteractions(ewalletService);
        }

        private static Stream<Arguments> provideArgumentsForRequestValidation() {

            return Stream.of(
                    Arguments.of(WithdrawEwalletRequestDto.builder()
                            .amount(null)
                            .build()),
                    Arguments.of(WithdrawEwalletRequestDto.builder()
                            .amount(BigDecimal.valueOf(-1))
                            .build()),
                    Arguments.of(WithdrawEwalletRequestDto.builder()
                            .amount(BigDecimal.ZERO)
                            .build())
            );
        }

        public static WithdrawEwalletRequestDto randomWithdrawRequestDto() {

            return WithdrawEwalletRequestDto.builder()
                    .amount(BigDecimal.ONE)
                    .description(RandomStringUtils.randomAlphabetic(5))
                    .build();
        }
    }

    @Nested
    class MakeTransactionEwalletTests {

        private static MakeTransactionEwalletRequestDto request = randomMakeTransactionRequestDto();

        private static final String MAKE_TRANSACTION_URL = RestUrl.API_V1 + "/ewallet/" + EWALLET_UUID + "/transaction";

        @Test
        void makeTransaction_success() throws Exception {

            // given
            OperationDto operationDto = createOperationDto(EWALLET_UUID, request);

            // when
            when(ewalletService.makeTransaction(any())).thenReturn(operationDto);

            // then
            mockMvc.perform(put(MAKE_TRANSACTION_URL)
                            .content(asJsonString(request))
                            .contentType(MediaType.MAKE_TRANSACTION_EWALLET_REQUEST))
                    .andExpect(status().isAccepted());

            ArgumentCaptor<OperationDto> captor = ArgumentCaptor.forClass(OperationDto.class);
            verify(ewalletService, times(1)).makeTransaction(captor.capture());
            verifyThatWalletServiceWasCalled(captor.getValue(), request.getAmount(), OperationTypeDto.TRANSACTION, request.getDescription(), request.getDestinationWalletUuid());
        }

        @Test
        void makeTransaction_whenEmptyContentType_thenBadRequest() throws Exception {

            // then
            mockMvc.perform(put(MAKE_TRANSACTION_URL)
                            .content(asJsonString(request)))
                    .andExpect(status().isUnsupportedMediaType());

            verifyNoInteractions(ewalletService);
        }

        @ParameterizedTest
        @MethodSource("provideArgumentsForRequestValidation")
        void makeTransaction_whenAmountIsNotValid_thenBadRequest(MakeTransactionEwalletRequestDto request) throws Exception {

            // then
            mockMvc.perform(put(MAKE_TRANSACTION_URL)
                            .content(asJsonString(request))
                            .contentType(MediaType.MAKE_TRANSACTION_EWALLET_REQUEST))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            verifyNoInteractions(ewalletService);
        }

        @Test
        void makeTransaction_whenEmptyUuidInPath_thenBadRequest() throws Exception {

            // given
            String urlWithoutUuid = RestUrl.API_V1 + "/ewallet/ /transaction";

            // then
            mockMvc.perform(put(urlWithoutUuid)
                            .contentType(MediaType.MAKE_TRANSACTION_EWALLET_REQUEST))
                    .andExpect(status().isInternalServerError());

            verifyNoInteractions(ewalletService);
        }

        private static Stream<Arguments> provideArgumentsForRequestValidation() {

            return Stream.of(
                    Arguments.of(MakeTransactionEwalletRequestDto.builder()
                            .amount(null)
                            .build()),
                    Arguments.of(MakeTransactionEwalletRequestDto.builder()
                            .amount(BigDecimal.valueOf(-1))
                            .build()),
                    Arguments.of(MakeTransactionEwalletRequestDto.builder()
                            .amount(BigDecimal.ZERO)
                            .build())
            );
        }

        public static MakeTransactionEwalletRequestDto randomMakeTransactionRequestDto() {

            return MakeTransactionEwalletRequestDto.builder()
                    .amount(BigDecimal.ONE)
                    .description(RandomStringUtils.randomAlphabetic(5))
                    .destinationWalletUuid(UUID.randomUUID())
                    .build();
        }
    }

    private void verifyThatWalletServiceWasCalled(OperationDto capturedRequest, BigDecimal amount, OperationTypeDto operationTypeDto, String description, UUID destinationWalletUuid) {

        assertThat(capturedRequest).isNotNull();
        assertThat(capturedRequest.getEwalletUuid()).isEqualTo(EWALLET_UUID);
        assertThat(capturedRequest.getAmount()).isEqualTo(amount);
        assertThat(capturedRequest.getOperationType()).isEqualTo(operationTypeDto);
        assertThat(capturedRequest.getDescription()).isEqualTo(description);
        assertThat(capturedRequest.getDestinationWalletUuid()).isEqualTo(destinationWalletUuid);
    }
}