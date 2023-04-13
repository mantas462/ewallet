package ewallet.repository.customer;

import ewallet.entity.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerDao extends JpaRepository<Customer, UUID> {
}
