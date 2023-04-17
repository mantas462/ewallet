package ewallet.service.ewallet;

import ewallet.dto.ewallet.internal.EwalletDto;
import ewallet.dto.operation.internal.OperationDto;
import ewallet.dto.operation.internal.OperationStatusDto;
import ewallet.entity.ewallet.Ewallet;
import ewallet.repository.ewallet.EwalletDao;
import ewallet.service.operation.OperationService;
import ewallet.util.mapper.ewallet.EwalletMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EwalletService {

    private EwalletDao ewalletDao;

    private OperationService operationService;

    private static final BigDecimal SINGLE_TRANSACTION_LIMIT = BigDecimal.valueOf(2000);

    private static final BigDecimal DAILY_WITHDRAW_LIMIT = BigDecimal.valueOf(5000);

    public void save(EwalletDto ewalletDto) {

        Ewallet ewallet = EwalletMapper.createEntity(ewalletDto);
        ewalletDao.save(ewallet);
    }

    @Transactional
    public OperationDto deposit(OperationDto operation) {

        BigDecimal amount = operation.getAmount();
        if (!isAmountValid(amount)) {
            return declineOperationAndGet(operation);
        }

        Optional<Ewallet> optionalEwallet = ewalletDao.findByIdAndLock(operation.getEwalletUuid());
        if (optionalEwallet.isEmpty()) {
            return declineOperationAndGet(operation);
        }

        Ewallet ewallet = optionalEwallet.get();
        EwalletDto ewalletDto = EwalletMapper.toDto(ewallet);
        ewalletDto.deposit(amount);
        ewalletDao.save(EwalletMapper.toEntity(ewalletDto));

        operation.setOperationStatus(OperationStatusDto.COMPLETED);
        return operationService.save(operation);
    }

    @Transactional
    public OperationDto withdraw(OperationDto operation) {

        BigDecimal amount = operation.getAmount();
        if (!isAmountValid(amount) || amountIsBiggerThanDailyLimit(amount)) {
            return declineOperationAndGet(operation);
        }

        Optional<Ewallet> optionalEwallet = ewalletDao.findByIdAndLock(operation.getEwalletUuid());
        if (optionalEwallet.isEmpty()) {
            return declineOperationAndGet(operation);
        }

        Ewallet ewallet = optionalEwallet.get();
        EwalletDto ewalletDto = EwalletMapper.toDto(ewallet);
        if (!ewalletDto.isEnoughBalance(amount) || amountIsBiggerThanDailyLimit(ewallet.getUuid())) {
            return declineOperationAndGet(operation);
        }

        ewalletDto.withdraw(amount);
        ewalletDao.save(EwalletMapper.toEntity(ewalletDto));

        operation.setOperationStatus(OperationStatusDto.COMPLETED);
        return operationService.save(operation);
    }

    private boolean amountIsBiggerThanDailyLimit(BigDecimal amount) {

        return amount.compareTo(DAILY_WITHDRAW_LIMIT) > 0;
    }

    @Transactional
    public OperationDto makeTransaction(OperationDto operation) {

        BigDecimal amount = operation.getAmount();
        if (!isTransactionRequestValid(amount)) {
            return declineOperationAndGet(operation);
        }

        Optional<Ewallet> optionalSourceEwallet = ewalletDao.findByIdAndLock(operation.getEwalletUuid());
        if (optionalSourceEwallet.isEmpty()) {
            return declineOperationAndGet(operation);
        }

        Ewallet sourceEwallet = optionalSourceEwallet.get();
        EwalletDto sourceEwalletDto = EwalletMapper.toDto(sourceEwallet);
        if (!sourceEwalletDto.isEnoughBalance(amount)) {
            return declineOperationAndGet(operation);
        }

        Optional<Ewallet> optionalDestinationEwallet = ewalletDao.findByIdAndLock(operation.getDestinationWalletUuid());
        if (optionalDestinationEwallet.isEmpty()) {
            return declineOperationAndGet(operation);
        }

        Ewallet destinationEwallet = optionalDestinationEwallet.get();
        EwalletDto destinationEwalletDto = EwalletMapper.toDto(destinationEwallet);

        sourceEwalletDto.withdraw(amount);
        destinationEwalletDto.deposit(amount);

        Ewallet sourceEwalletToSave = EwalletMapper.toEntity(sourceEwalletDto);
        Ewallet destinationEwalletToSave = EwalletMapper.toEntity(destinationEwalletDto);
        ewalletDao.saveAll(List.of(sourceEwalletToSave, destinationEwalletToSave));

        operation.setOperationStatus(OperationStatusDto.COMPLETED);
        return operationService.save(operation);
    }

    private boolean amountIsBiggerThanDailyLimit(UUID uuid) {

        List<OperationDto> operations = operationService.lastDayWithdrawalsByWalletUuid(uuid);
        BigDecimal lastDayOperationsAmount = operations.stream()
                .map(OperationDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return lastDayOperationsAmount.compareTo(DAILY_WITHDRAW_LIMIT) > 0;
    }

    private OperationDto declineOperationAndGet(OperationDto operationDto) {

        operationDto.setOperationStatus(OperationStatusDto.DECLINED);
        return operationService.save(operationDto);
    }

    private boolean isTransactionRequestValid(BigDecimal amount) {

        return isAmountValid(amount) && amount.compareTo(SINGLE_TRANSACTION_LIMIT) < 0;
    }

    private boolean isAmountValid(BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        BigInteger fractionalPart = amount.remainder(BigDecimal.ONE).unscaledValue();
        int fractionalDigitsQuantity = getFractionalDigitsQuantity(fractionalPart);

        return fractionalDigitsQuantity <= 2;
    }

    private static int getFractionalDigitsQuantity(BigInteger fractionalPart) {

        return (int) (Math.log10(fractionalPart.intValue()) + 1);
    }
}
