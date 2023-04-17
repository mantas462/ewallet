package ewallet.util.mapper.operation;

import ewallet.dto.ewallet.api.DepositEwalletRequestDto;
import ewallet.dto.ewallet.api.MakeOperationResponseDto;
import ewallet.dto.ewallet.api.MakeTransactionEwalletRequestDto;
import ewallet.dto.ewallet.api.WithdrawEwalletRequestDto;
import ewallet.dto.operation.internal.OperationDto;
import ewallet.dto.operation.internal.OperationStatusDto;
import ewallet.dto.operation.internal.OperationTypeDto;
import ewallet.entity.operation.Operation;
import ewallet.entity.operation.OperationStatus;
import ewallet.entity.operation.OperationType;

import java.util.UUID;

public final class OperationMapper {

    public static OperationDto toDto(Operation operation) {

        return OperationDto.builder()
                .uuid(operation.getUuid())
                .amount(operation.getAmount())
                .operationType(OperationTypeDto.valueOf(operation.getOperationType().toString()))
                .description(operation.getDescription())
                .ewalletUuid(operation.getEwalletUuid())
                .destinationWalletUuid(operation.getDestinationWalletUuid())
                .operationStatus(OperationStatusDto.valueOf(operation.getOperationStatus().toString()))
                .suspicious(operation.isSuspicious())
                .createdDate(operation.getCreatedDate())
                .build();
    }

    public static Operation createEntity(OperationDto operationDto) {

        return Operation.builder()
                .uuid(UUID.randomUUID())
                .operationType(OperationType.valueOf(operationDto.getOperationType().toString()))
                .amount(operationDto.getAmount())
                .description(operationDto.getDescription())
                .ewalletUuid(operationDto.getEwalletUuid())
                .destinationWalletUuid(operationDto.getDestinationWalletUuid())
                .operationStatus(OperationStatus.valueOf(operationDto.getOperationStatus().toString()))
                .suspicious(operationDto.isSuspicious())
                .build();
    }

    public static MakeOperationResponseDto toMakeOperationResponseDto(OperationDto operation) {

        return MakeOperationResponseDto.builder()
                .uuid(operation.getUuid())
                .status(operation.getOperationStatus())
                .build();
    }

    public static OperationDto toOperationDto(UUID uuid, DepositEwalletRequestDto depositEwalletRequestDto) {

        return OperationDto.builder()
                .ewalletUuid(uuid)
                .amount(depositEwalletRequestDto.getAmount())
                .operationType(OperationTypeDto.DEPOSIT)
                .description(depositEwalletRequestDto.getDescription())
                .build();
    }

    public static OperationDto toOperationDto(UUID uuid, MakeTransactionEwalletRequestDto makeTransactionEwalletRequestDto) {

        return OperationDto.builder()
                .ewalletUuid(uuid)
                .amount(makeTransactionEwalletRequestDto.getAmount())
                .operationType(OperationTypeDto.TRANSACTION)
                .description(makeTransactionEwalletRequestDto.getDescription())
                .destinationWalletUuid(makeTransactionEwalletRequestDto.getDestinationWalletUuid())
                .build();
    }

    public static OperationDto toOperationDto(UUID uuid, WithdrawEwalletRequestDto withdrawEwalletRequestDto) {

        return OperationDto.builder()
                .ewalletUuid(uuid)
                .amount(withdrawEwalletRequestDto.getAmount())
                .operationType(OperationTypeDto.WITHDRAWAL)
                .description(withdrawEwalletRequestDto.getDescription())
                .build();
    }
}
