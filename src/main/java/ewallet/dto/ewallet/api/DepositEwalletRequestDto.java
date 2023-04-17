package ewallet.dto.ewallet.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@Builder
public class DepositEwalletRequestDto {

    @NotNull
    @Positive
    private final BigDecimal amount;

    private final String description;
}
