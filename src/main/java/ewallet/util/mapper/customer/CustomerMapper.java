package ewallet.util.mapper.customer;

import ewallet.dto.customer.api.CreateCustomerRequestDto;
import ewallet.dto.customer.api.CreateCustomerResponseDto;
import ewallet.dto.customer.api.GetCustomerResponseDto;
import ewallet.dto.customer.internal.CustomerDto;
import ewallet.entity.customer.Customer;

import java.util.UUID;

public final class CustomerMapper {

    public static CustomerDto toDto(CreateCustomerRequestDto request) {

        return CustomerDto.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .build();
    }

    public static CustomerDto toDto(Customer customer) {

        return CustomerDto.builder()
                .uuid(customer.getUuid())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .build();
    }

    public static Customer toEntity(CustomerDto customerDto) {

        return Customer.builder()
                .uuid(UUID.randomUUID())
                .firstName(customerDto.getFirstName())
                .lastName(customerDto.getLastName())
                .email(customerDto.getEmail())
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

    public static CreateCustomerResponseDto toCreateCustomerResponseDto(CustomerDto customerDto) {

        return CreateCustomerResponseDto.builder()
                .uuid(customerDto.getUuid())
                .build();
    }
}
