package ewallet.dto.ewallet;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class MakeTransactionEwalletRequestDto {

    @NotNull
    private final BigDecimal amount;

    @NotNull
    private final UUID destinationWalletUuid;

    private final String description;
}
