package ewallet.controller.ewallet;

import ewallet.dto.ewallet.DepositEwalletRequestDto;
import ewallet.dto.ewallet.MakeTransactionEwalletRequestDto;
import ewallet.dto.ewallet.WithdrawEwalletRequestDto;
import ewallet.service.ewallet.EwalletService;
import ewallet.util.api.MediaType;
import ewallet.util.api.RestUrl;
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
    public void deposit(@PathVariable(value = "uuid") UUID uuid, @RequestBody @Validated DepositEwalletRequestDto depositEwalletRequestDto) {

        ewalletService.deposit(uuid, depositEwalletRequestDto);
    }

    @PutMapping(value = RestUrl.EWALLET_BY_UUID_WITHDRAWAL,
            consumes = MediaType.WITHDRAW_EWALLET_REQUEST)
    public void withdraw(@PathVariable(value = "uuid") UUID uuid, @RequestBody @Validated WithdrawEwalletRequestDto withdrawEwalletRequestDto) {

        ewalletService.withdraw(uuid, withdrawEwalletRequestDto);
    }

    @PutMapping(value = RestUrl.EWALLET_BY_UUID_TRANSACTION,
            consumes = MediaType.MAKE_TRANSACTION_EWALLET_REQUEST)
    public void makeTransaction(@PathVariable(value = "uuid") UUID uuid, @RequestBody @Validated MakeTransactionEwalletRequestDto makeTransactionEwalletRequestDto) {

        ewalletService.makeTransaction(uuid, makeTransactionEwalletRequestDto);
    }
}
