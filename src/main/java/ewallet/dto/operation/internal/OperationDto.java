package ewallet.dto.operation.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class OperationDto {

    private UUID uuid;

    private BigDecimal amount;

    private OperationTypeDto operationType;

    private String description;

    private UUID ewalletUuid;

    private UUID destinationWalletUuid;

    private OperationStatusDto operationStatus;

    private boolean suspicious;

    private Timestamp createdDate;
}
