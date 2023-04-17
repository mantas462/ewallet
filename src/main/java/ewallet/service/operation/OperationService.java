package ewallet.service.operation;

import ewallet.dto.operation.internal.OperationDto;
import ewallet.dto.operation.internal.OperationTypeDto;
import ewallet.entity.operation.Operation;
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

    private static final int DAYS_AFTER_COMPLETED_OPERATION = -1;

    private static final BigDecimal SUSPICIOUS_AMOUNT = BigDecimal.valueOf(10000);

    public OperationDto save(OperationDto operationDto) {

        if (isSuspicious(operationDto)) {
            operationDto.setSuspicious(true);
        }
        Operation operation = OperationMapper.createEntity(operationDto);

        Operation savedOperation = operationDao.save(operation);

        return OperationMapper.toDto(savedOperation);
    }

    public List<OperationDto> lastDayWithdrawalsByWalletUuid(UUID uuid) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.add(Calendar.DAY_OF_MONTH, DAYS_AFTER_COMPLETED_OPERATION);
        timestamp.setTime(calendar.getTime().getTime());

        List<Operation> operations = operationDao.lastDayWithdrawalsByWalletUuidAndLock(uuid, timestamp);

        return operations.stream().map(OperationMapper::toDto).toList();
    }

    private boolean isSuspicious(OperationDto operation) {

        return operation.getOperationType() == OperationTypeDto.TRANSACTION &&
                operation.getAmount().compareTo(SUSPICIOUS_AMOUNT) > 0;
    }
}
