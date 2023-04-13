package ewallet.dto.ewallet;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@Builder
public class WithdrawEwalletRequestDto {

    @NotNull
    private final BigDecimal amount;

    private final String description;
}
