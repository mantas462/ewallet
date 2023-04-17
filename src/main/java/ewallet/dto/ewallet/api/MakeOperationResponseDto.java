package ewallet.dto.ewallet.api;

import ewallet.dto.operation.internal.OperationStatusDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class MakeOperationResponseDto {

    @NotNull
    private final UUID uuid;

    @NotNull
    private final OperationStatusDto status;
}
