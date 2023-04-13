package ewallet.util.mapper.operation;

import ewallet.dto.ewallet.DepositEwalletRequestDto;
import ewallet.dto.ewallet.MakeTransactionEwalletRequestDto;
import ewallet.dto.ewallet.WithdrawEwalletRequestDto;
import ewallet.dto.operation.OperationStatusDto;
import ewallet.dto.operation.OperationTypeDto;
import ewallet.dto.operation.SaveOperationDto;
import ewallet.entity.operation.Operation;
import ewallet.entity.operation.OperationStatus;
import ewallet.entity.operation.OperationType;

import java.util.UUID;

public final class OperationMapper {

    public static Operation toEntity(SaveOperationDto saveOperationDto) {

        return Operation.builder()
                .uuid(UUID.randomUUID())
                .operationType(OperationType.valueOf(saveOperationDto.getOperationTypeDto().toString()))
                .amount(saveOperationDto.getAmount())
                .description(saveOperationDto.getDescription())
                .ewalletUuid(saveOperationDto.getWalletUuid())
                .destinationWalletUuid(saveOperationDto.getDestinationWalletUuid())
                .operationStatus(OperationStatus.valueOf(saveOperationDto.getOperationStatusDto().toString()))
                .build();
    }

    public static SaveOperationDto toSaveDto(UUID uuid, DepositEwalletRequestDto depositEwalletRequestDto, OperationStatusDto operationStatusDto) {

        return SaveOperationDto.builder()
                .amount(depositEwalletRequestDto.getAmount())
                .operationTypeDto(OperationTypeDto.DEPOSIT)
                .description(depositEwalletRequestDto.getDescription())
                .walletUuid(uuid)
                .operationStatusDto(operationStatusDto)
                .build();
    }

    public static SaveOperationDto toSaveDto(UUID uuid, WithdrawEwalletRequestDto withdrawEwalletRequestDto, OperationStatusDto operationStatusDto) {

        return SaveOperationDto.builder()
                .amount(withdrawEwalletRequestDto.getAmount())
                .operationTypeDto(OperationTypeDto.WITHDRAWAL)
                .description(withdrawEwalletRequestDto.getDescription())
                .walletUuid(uuid)
                .operationStatusDto(operationStatusDto)
                .build();
    }

    public static SaveOperationDto toSaveDto(UUID uuid, MakeTransactionEwalletRequestDto makeTransactionEwalletRequestDto, OperationStatusDto operationStatusDto) {

        return SaveOperationDto.builder()
                .amount(makeTransactionEwalletRequestDto.getAmount())
                .operationTypeDto(OperationTypeDto.TRANSACTION)
                .description(makeTransactionEwalletRequestDto.getDescription())
                .walletUuid(uuid)
                .destinationWalletUuid(makeTransactionEwalletRequestDto.getDestinationWalletUuid())
                .operationStatusDto(operationStatusDto)
                .build();
    }
}
