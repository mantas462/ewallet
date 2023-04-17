package ewallet.service.customer;

import ewallet.dto.customer.internal.CustomerDto;
import ewallet.dto.ewallet.internal.EwalletDto;
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
    public CustomerDto save(CustomerDto customerDto) {

        Customer customer = CustomerMapper.toEntity(customerDto);
        Customer savedCustomer = customerDao.save(customer);

        EwalletDto ewalletDto = EwalletMapper.createDtoWithCustomerUuid(customer.getUuid());
        ewalletService.save(ewalletDto);

        return CustomerMapper.toDto(savedCustomer);
    }

    public Customer get(UUID uuid) {

        return customerDao.findById(uuid).orElseThrow(() -> new NoSuchElementException("Customer was not found with uuid=[%s]".formatted(uuid)));
    }
}
