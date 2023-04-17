package ewallet.dto.ewallet.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive
    private final BigDecimal amount;

    @NotNull
    private final UUID destinationWalletUuid;

    private final String description;
}
