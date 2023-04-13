package ewallet.service.customer;

import ewallet.dto.customer.CreateCustomerRequestDto;
import ewallet.dto.customer.CreateCustomerResponseDto;
import ewallet.dto.ewallet.SaveEwalletDto;
import ewallet.entity.customer.Customer;
import ewallet.repository.customer.CustomerDao;
import ewallet.service.ewallet.EwalletService;
import ewallet.util.mapper.customer.CustomerMapper;
import ewallet.util.mapper.ewallet.EwalletMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CustomerService {

    private CustomerDao customerDao;

    private EwalletService ewalletService;

    @Transactional
    public CreateCustomerResponseDto save(CreateCustomerRequestDto createCustomerRequestDto) {

        Customer customer = CustomerMapper.toEntity(createCustomerRequestDto);
        SaveEwalletDto saveEwalletDto = EwalletMapper.toDto(customer.getEwalletUuid());

        Customer savedCustomer = customerDao.save(customer);
        ewalletService.save(saveEwalletDto);

        return CustomerMapper.toCreateCustomerResponseDto(savedCustomer);
    }

    public Customer get(UUID uuid) {

        return customerDao.findById(uuid).orElseThrow(() -> new NoSuchElementException("Customer was not found with uuid=[%s]".formatted(uuid)));
    }
}
