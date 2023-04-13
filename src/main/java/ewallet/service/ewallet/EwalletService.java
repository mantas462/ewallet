package ewallet.service.ewallet;

import ewallet.dto.ewallet.DepositEwalletRequestDto;
import ewallet.dto.ewallet.MakeTransactionEwalletRequestDto;
import ewallet.dto.ewallet.SaveEwalletDto;
import ewallet.dto.ewallet.WithdrawEwalletRequestDto;
import ewallet.dto.operation.OperationStatusDto;
import ewallet.dto.operation.SaveOperationDto;
import ewallet.entity.ewallet.Ewallet;
import ewallet.entity.operation.Operation;
import ewallet.repository.ewallet.EwalletDao;
import ewallet.service.operation.OperationService;
import ewallet.util.mapper.ewallet.EwalletMapper;
import ewallet.util.mapper.operation.OperationMapper;
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

    public void save(SaveEwalletDto saveEwalletDto) {

        Ewallet ewallet = EwalletMapper.toEntity(saveEwalletDto);
        ewalletDao.save(ewallet);
    }

    @Transactional
    public void deposit(UUID uuid, DepositEwalletRequestDto request) {

        BigDecimal amount = request.getAmount();
        if (!isAmountValid(amount)) {
            declineDepositOperation(uuid, request);
            return;
        }

        Optional<Ewallet> optionalEwallet = ewalletDao.findById(uuid);
        if (optionalEwallet.isEmpty()) {
            declineDepositOperation(uuid, request);
            return;
        }

        Ewallet ewallet = optionalEwallet.get();
        ewallet.deposit(amount);

        SaveOperationDto saveOperationDto = OperationMapper.toSaveDto(uuid, request, OperationStatusDto.COMPLETED);
        saveEwallets(saveOperationDto, List.of(ewallet));
    }

    private void declineDepositOperation(UUID uuid, DepositEwalletRequestDto request) {

        SaveOperationDto saveOperationDto = OperationMapper.toSaveDto(uuid, request, OperationStatusDto.DECLINED);
        operationService.save(saveOperationDto);
    }

    @Transactional
    public void withdraw(UUID uuid, WithdrawEwalletRequestDto request) {

        BigDecimal amount = request.getAmount();

        if (!isAmountValid(amount)) {
            declineWithdrawOperation(uuid, request);
            return;
        }

        Optional<Ewallet> optionalEwallet = ewalletDao.findById(uuid);

        if (optionalEwallet.isEmpty()) {
            declineWithdrawOperation(uuid, request);
            return;
        }

        Ewallet ewallet = optionalEwallet.get();

        if (!ewallet.isEnoughBalance(amount) || exceededDailyLimit(ewallet.getUuid())) {
            declineWithdrawOperation(uuid, request);
            return;
        }

        ewallet.withdraw(amount);

        SaveOperationDto saveOperationDto = OperationMapper.toSaveDto(uuid, request, OperationStatusDto.COMPLETED);
        saveEwallets(saveOperationDto, List.of(ewallet));
    }

    private void declineWithdrawOperation(UUID uuid, WithdrawEwalletRequestDto request) {

        SaveOperationDto saveOperationDto = OperationMapper.toSaveDto(uuid, request, OperationStatusDto.DECLINED);
        operationService.save(saveOperationDto);
    }

    private boolean exceededDailyLimit(UUID uuid) {

        List<Operation> operations = operationService.lastDayWithdrawalsByWalletUuid(uuid);
        BigDecimal lastDayOperationsAmount = operations.stream()
                .map(operation -> operation.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (lastDayOperationsAmount.compareTo(DAILY_WITHDRAW_LIMIT) > 0) {
            return true;
        }
        return false;
    }

    @Transactional
    public void makeTransaction(UUID uuid, MakeTransactionEwalletRequestDto request) {

        BigDecimal amount = request.getAmount();

        if (!isTransactionRequestValid(amount)) {
            declineTransactionOperation(uuid, request);
            return;
        }

        Optional<Ewallet> optionalSourceEwallet = ewalletDao.findById(uuid);

        if (optionalSourceEwallet.isEmpty()) {
            declineTransactionOperation(uuid, request);
            return;
        }

        Ewallet sourceEwallet = optionalSourceEwallet.get();

        if (!sourceEwallet.isEnoughBalance(amount)) {
            declineTransactionOperation(uuid, request);
            return;
        }

        Optional<Ewallet> optionalDestinationEwallet = ewalletDao.findById(uuid);

        if (optionalDestinationEwallet.isEmpty()) {
            declineTransactionOperation(uuid, request);
            return;
        }

        Ewallet destinationWallet = optionalDestinationEwallet.get();

        sourceEwallet.withdraw(amount);
        destinationWallet.deposit(amount);

        SaveOperationDto saveOperationDto = OperationMapper.toSaveDto(uuid, request, OperationStatusDto.COMPLETED);
        saveEwallets(saveOperationDto, List.of(sourceEwallet, destinationWallet));
    }

    private void saveEwallets(SaveOperationDto saveOperationDto, List<Ewallet> wallets) {

        operationService.save(saveOperationDto);
        ewalletDao.saveAll(wallets);
    }


    private void declineTransactionOperation(UUID uuid, MakeTransactionEwalletRequestDto request) {
        SaveOperationDto saveOperationDto = OperationMapper.toSaveDto(uuid, request, OperationStatusDto.DECLINED);
        operationService.save(saveOperationDto);
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

        if (fractionalDigitsQuantity > 2) {
            return false;
        }
        return true;
    }

    private static int getFractionalDigitsQuantity(BigInteger fractionalPart) {

        return (int) (Math.log10(fractionalPart.intValue()) + 1);
    }
}
