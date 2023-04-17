package ewallet.repository.operation;

import ewallet.entity.operation.Operation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface OperationDao extends JpaRepository<Operation, UUID> {


    @Query(value = "SELECT u FROM Operation u WHERE ewalletUuid = ?1 AND operationType = 'WITHDRAWAL' AND createdDate > ?2 AND operationStatus = 'COMPLETED'")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Operation> lastDayWithdrawalsByWalletUuidAndLock(UUID uuid, Timestamp lastDay);
}

