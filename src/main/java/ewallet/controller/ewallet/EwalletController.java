package ewallet.controller.ewallet;

import ewallet.dto.ewallet.api.DepositEwalletRequestDto;
import ewallet.dto.ewallet.api.MakeOperationResponseDto;
import ewallet.dto.ewallet.api.MakeTransactionEwalletRequestDto;
import ewallet.dto.ewallet.api.WithdrawEwalletRequestDto;
import ewallet.dto.operation.internal.OperationDto;
import ewallet.service.ewallet.EwalletService;
import ewallet.util.api.MediaType;
import ewallet.util.api.RestUrl;
import ewallet.util.mapper.operation.OperationMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(RestUrl.API_V1)
@ResponseStatus(HttpStatus.ACCEPTED)
public class EwalletController {

    private EwalletService ewalletService;

    @PutMapping(value = RestUrl.EWALLET_BY_UUID_DEPOSIT,
            consumes = MediaType.DEPOSIT_EWALLET_REQUEST)
    public MakeOperationResponseDto deposit(@PathVariable(value = "uuid") UUID uuid, @RequestBody @Validated DepositEwalletRequestDto request) {

        OperationDto operation = OperationMapper.toOperationDto(uuid, request);
        OperationDto depositOperation = ewalletService.deposit(operation);

        return OperationMapper.toMakeOperationResponseDto(depositOperation);
    }

    @PutMapping(value = RestUrl.EWALLET_BY_UUID_WITHDRAWAL,
            consumes = MediaType.WITHDRAW_EWALLET_REQUEST)
    public MakeOperationResponseDto withdraw(@PathVariable(value = "uuid") UUID uuid, @RequestBody @Validated WithdrawEwalletRequestDto request) {

        OperationDto operation = OperationMapper.toOperationDto(uuid, request);
        OperationDto depositOperation = ewalletService.withdraw(operation);

        return OperationMapper.toMakeOperationResponseDto(depositOperation);
    }

    @PutMapping(value = RestUrl.EWALLET_BY_UUID_TRANSACTION,
            consumes = MediaType.MAKE_TRANSACTION_EWALLET_REQUEST)
    public MakeOperationResponseDto makeTransaction(@PathVariable(value = "uuid") UUID uuid, @RequestBody @Validated MakeTransactionEwalletRequestDto request) {

        OperationDto operation = OperationMapper.toOperationDto(uuid, request);
        OperationDto depositOperation = ewalletService.makeTransaction(operation);

        return OperationMapper.toMakeOperationResponseDto(depositOperation);
    }
}
