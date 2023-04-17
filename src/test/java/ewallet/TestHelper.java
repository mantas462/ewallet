package ewallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ewallet.dto.customer.api.CreateCustomerRequestDto;
import ewallet.dto.customer.internal.CustomerDto;
import ewallet.dto.ewallet.api.DepositEwalletRequestDto;
import ewallet.dto.ewallet.api.MakeTransactionEwalletRequestDto;
import ewallet.dto.ewallet.api.WithdrawEwalletRequestDto;
import ewallet.dto.operation.internal.OperationDto;
import ewallet.entity.customer.Customer;
import ewallet.entity.ewallet.Ewallet;
import ewallet.entity.operation.Operation;
import ewallet.entity.operation.OperationStatus;
import ewallet.entity.operation.OperationType;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
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

    public static CustomerDto createCustomer(UUID uuid, String firstName, String lastName, String email) {
        return CustomerDto.builder()
                .uuid(uuid)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
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

    public static Ewallet randomEwallet() {
        return Ewallet.builder()
                .uuid(UUID.randomUUID())
                .balance(BigDecimal.ONE)
                .customerUuid(UUID.randomUUID())
                .build();
    }

    public static Ewallet createEwallet(UUID uuid, BigDecimal balance) {
        return Ewallet.builder()
                .uuid(uuid)
                .balance(balance)
                .customerUuid(UUID.randomUUID())
                .build();
    }

    public static Operation randomOperation() {

        return Operation.builder()
                .uuid(UUID.randomUUID())
                .amount(BigDecimal.ONE)
                .operationType(OperationType.WITHDRAWAL)
                .operationStatus(OperationStatus.COMPLETED)
                .ewalletUuid(UUID.randomUUID())
                .createdDate(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public static OperationDto createOperationDto(UUID uuid, DepositEwalletRequestDto depositEwalletRequestDto) {

        return OperationDto.builder()
                .amount(depositEwalletRequestDto.getAmount())
                .description(depositEwalletRequestDto.getDescription())
                .ewalletUuid(uuid)
                .build();
    }

    public static OperationDto createOperationDto(UUID uuid, WithdrawEwalletRequestDto withdrawEwalletRequestDto) {

        return OperationDto.builder()
                .amount(withdrawEwalletRequestDto.getAmount())
                .description(withdrawEwalletRequestDto.getDescription())
                .ewalletUuid(uuid)
                .build();
    }

    public static OperationDto createOperationDto(UUID uuid, MakeTransactionEwalletRequestDto makeTransactionEwalletRequestDto) {

        return OperationDto.builder()
                .amount(makeTransactionEwalletRequestDto.getAmount())
                .description(makeTransactionEwalletRequestDto.getDescription())
                .destinationWalletUuid(UUID.randomUUID())
                .ewalletUuid(uuid)
                .build();
    }
}
