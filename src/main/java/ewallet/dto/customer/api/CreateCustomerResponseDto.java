package ewallet.dto.customer.api;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
public class CreateCustomerResponseDto {

    @NotNull
    private final UUID uuid;
}
