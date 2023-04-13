package ewallet.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class SaveOperationDto {

    private final BigDecimal amount;

    private final OperationTypeDto operationTypeDto;

    private final String description;

    private final UUID walletUuid;

    private final UUID destinationWalletUuid;

    private final OperationStatusDto operationStatusDto;
}
