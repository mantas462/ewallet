package ewallet.util.mapper.customer;

import ewallet.dto.customer.CreateCustomerRequestDto;
import ewallet.dto.customer.CreateCustomerResponseDto;
import ewallet.dto.customer.GetCustomerResponseDto;
import ewallet.entity.customer.Customer;

import java.util.UUID;

public final class CustomerMapper {

    public static Customer toEntity(CreateCustomerRequestDto createCustomerRequestDto) {

        return Customer.builder()
                .uuid(UUID.randomUUID())
                .firstName(createCustomerRequestDto.getFirstName())
                .lastName(createCustomerRequestDto.getLastName())
                .ewalletUuid(UUID.randomUUID())
                .email(createCustomerRequestDto.getEmail())
                .build();
    }

    public static GetCustomerResponseDto toGetCustomerResponseDto(Customer customer) {

        return GetCustomerResponseDto.builder()
                .uuid(customer.getUuid())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .build();
    }

    public static CreateCustomerResponseDto toCreateCustomerResponseDto(Customer customer) {

        return CreateCustomerResponseDto.builder()
                .uuid(customer.getUuid())
                .build();
    }
}
