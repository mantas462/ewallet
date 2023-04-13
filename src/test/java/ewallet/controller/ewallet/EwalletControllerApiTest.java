package ewallet.controller.ewallet;

import ewallet.dto.ewallet.DepositEwalletRequestDto;
import ewallet.service.ewallet.EwalletService;
import ewallet.util.api.MediaType;
import ewallet.util.api.RestUrl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static ewallet.TestHelper.asJsonString;
import static ewallet.TestHelper.randomDepositEwalletRequestDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EwalletController.class)
class EwalletControllerApiTest {

    /*
    TODO LIST:
        1. Deposit - ContentType header, empty path variable, body
        2. Withdraw - success scenario, ContentType header, empty path variable, body, service fail
        3. Transaction - success scenario, ContentType header, empty path variable, body, service fail
     */

    @MockBean
    private EwalletService ewalletService;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    class DepositEwalletTests {

        private static final UUID EWALLET_UUID = UUID.randomUUID();

        private static final String DEPOSIT_URL = RestUrl.API_V1 + "/ewallet/" + EWALLET_UUID + "/deposit";

        @Test
        void deposit_success() throws Exception {

            // given
            DepositEwalletRequestDto depositEwalletRequestDto = randomDepositEwalletRequestDto();

            // then
            mockMvc.perform(put(DEPOSIT_URL)
                            .content(asJsonString(depositEwalletRequestDto))
                            .contentType(MediaType.DEPOSIT_EWALLET_REQUEST))
                    .andExpect(status().isAccepted());

            verifyThatWalletServiceWasCalled(depositEwalletRequestDto);
        }

        @Test
        void deposit_whenServiceFails_thenException() throws Exception {

            // given
            DepositEwalletRequestDto depositEwalletRequestDto = randomDepositEwalletRequestDto();

            // when
            doThrow(new IllegalArgumentException()).when(ewalletService).deposit(EWALLET_UUID, depositEwalletRequestDto);

            // then
            mockMvc.perform(put(DEPOSIT_URL)
                            .content(asJsonString(depositEwalletRequestDto))
                            .contentType(MediaType.DEPOSIT_EWALLET_REQUEST))
                    .andExpect(status().isBadRequest());

            verifyThatWalletServiceWasCalled(depositEwalletRequestDto);
        }

        private void verifyThatWalletServiceWasCalled(DepositEwalletRequestDto depositEwalletRequestDto) {
            ArgumentCaptor<DepositEwalletRequestDto> captor = ArgumentCaptor.forClass(DepositEwalletRequestDto.class);
            verify(ewalletService).deposit(eq(EWALLET_UUID), captor.capture());
            DepositEwalletRequestDto capturedRequest = captor.getValue();
            assertThat(capturedRequest.getAmount()).isEqualTo(BigDecimal.ONE);
            assertThat(capturedRequest.getDescription()).isEqualTo(depositEwalletRequestDto.getDescription());
        }
    }
}