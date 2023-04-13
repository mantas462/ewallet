package ewallet.repository.operation;

import ewallet.entity.operation.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface OperationDao extends JpaRepository<Operation, UUID> {

    @Query(value = "SELECT * FROM OPERATION WHERE EWALLET_UUID = ?1 AND OPERATION_TYPE = 'WITHDRAWAL' AND CREATED_DATE > ?2", nativeQuery = true)
    List<Operation> lastDayWithdrawalsByWalletUuid(UUID uuid, Timestamp lastDay);
}

