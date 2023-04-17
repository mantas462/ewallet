package ewallet.controller.customer;

import ewallet.dto.customer.api.CreateCustomerRequestDto;
import ewallet.dto.customer.api.CreateCustomerResponseDto;
import ewallet.dto.customer.api.GetCustomerResponseDto;
import ewallet.dto.customer.internal.CustomerDto;
import ewallet.entity.customer.Customer;
import ewallet.service.customer.CustomerService;
import ewallet.util.api.MediaType;
import ewallet.util.api.RestUrl;
import ewallet.util.mapper.customer.CustomerMapper;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(RestUrl.API_V1)
public class CustomerController {

    private CustomerService customerService;

    @PostMapping(
            value = RestUrl.CUSTOMER,
            consumes = MediaType.CREATE_CUSTOMER_REQUEST
    )
    public CreateCustomerResponseDto save(@RequestBody @Validated CreateCustomerRequestDto createCustomerRequestDto) {

        CustomerDto customer = CustomerMapper.toDto(createCustomerRequestDto);
        CustomerDto savedCustomer = customerService.save(customer);

        return CustomerMapper.toCreateCustomerResponseDto(savedCustomer);
    }

    @GetMapping(
            value = RestUrl.CUSTOMER_BY_UUID,
            consumes = MediaType.GET_CUSTOMER_REQUEST
    )
    public GetCustomerResponseDto get(@PathVariable(value = "uuid") UUID uuid) {

        Customer customer = customerService.get(uuid);

        return CustomerMapper.toGetCustomerResponseDto(customer);
    }
}
