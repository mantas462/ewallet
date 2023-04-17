package ewallet.dto.ewallet.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class EwalletDto {

    private UUID uuid;

    private BigDecimal balance;

    private UUID customerUuid;

    public EwalletDto deposit(BigDecimal amount) {

        BigDecimal newBalance = balance.add(amount).setScale(2);
        setBalance(newBalance);
        return this;
    }

    public EwalletDto withdraw(BigDecimal amount) {

        BigDecimal newBalance = balance.subtract(amount).setScale(2);
        setBalance(newBalance);
        return this;
    }

    public boolean isEnoughBalance(BigDecimal amount) {

        return balance.compareTo(amount) >= 0;
    }
}
