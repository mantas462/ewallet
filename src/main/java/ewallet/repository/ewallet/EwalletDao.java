package ewallet.repository.ewallet;

import ewallet.entity.ewallet.Ewallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EwalletDao extends JpaRepository<Ewallet, UUID> {
}
