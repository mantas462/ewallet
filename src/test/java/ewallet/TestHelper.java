package ewallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ewallet.dto.customer.CreateCustomerRequestDto;
import ewallet.dto.ewallet.DepositEwalletRequestDto;
import ewallet.entity.customer.Customer;
import ewallet.entity.ewallet.Ewallet;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.UUID;

public class TestHelper {

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Customer randomCustomer() {
        return Customer.builder()
                .uuid(UUID.randomUUID())
                .firstName(RandomStringUtils.randomAlphabetic(5))
                .lastName(RandomStringUtils.randomAlphabetic(5))
                .email(RandomStringUtils.randomAlphabetic(5))
                .build();
    }

    public static CreateCustomerRequestDto randomCreateCustomerRequestDto() {
        return createCustomerRequestDto(
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5),
                RandomStringUtils.randomAlphabetic(5)
        );
    }

    public static CreateCustomerRequestDto createCustomerRequestDto(String firstName, String lastName, String email) {
        return CreateCustomerRequestDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
    }

    public static Ewallet ewalletWithBalance(BigDecimal balance) {
        return Ewallet.builder()
                .uuid(UUID.randomUUID())
                .balance(balance)
                .build();
    }

    public static Ewallet ewalletWithCustomer(Customer customer) {
        return Ewallet.builder()
                .uuid(UUID.randomUUID())
                .balance(BigDecimal.ONE)
                .build();
    }

    public static DepositEwalletRequestDto depositEwalletRequestDtoWithAmount(BigDecimal amount) {
        return DepositEwalletRequestDto.builder()
                .amount(amount)
                .description(RandomStringUtils.randomAlphabetic(5))
                .build();
    }

    public static DepositEwalletRequestDto randomDepositEwalletRequestDto() {
        return depositEwalletRequestDtoWithAmount(BigDecimal.ONE);
    }
}
