package ewallet.service.operation;

import ewallet.dto.operation.SaveOperationDto;
import ewallet.entity.operation.Operation;
import ewallet.entity.operation.OperationType;
import ewallet.repository.operation.OperationDao;
import ewallet.util.mapper.operation.OperationMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OperationService {

    private OperationDao operationDao;

    private static final BigDecimal FLAGGED_AMOUNT = BigDecimal.valueOf(10000);

    public void save(SaveOperationDto saveOperationDto) {

        Operation operation = OperationMapper.toEntity(saveOperationDto);

        if (isSuspicious(operation)) {
            operation.setSuspicious(true);
        }

        operationDao.save(operation);
    }

    public List<Operation> lastDayWithdrawalsByWalletUuid(UUID uuid) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        timestamp.setTime(calendar.getTime().getTime());

        return operationDao.lastDayWithdrawalsByWalletUuid(uuid, timestamp);
    }

    private boolean isSuspicious(Operation operation) {

        return operation.getOperationType() == OperationType.TRANSACTION &&
                operation.getAmount().compareTo(FLAGGED_AMOUNT) > 0;
    }
}
