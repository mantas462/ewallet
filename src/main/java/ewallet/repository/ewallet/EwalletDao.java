package ewallet.repository.ewallet;

import ewallet.entity.ewallet.Ewallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface EwalletDao extends JpaRepository<Ewallet, UUID> {

    @Query(value = "SELECT u FROM Ewallet u WHERE uuid = ?1")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Ewallet> findByIdAndLock(UUID uuid);
}
