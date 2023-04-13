package ewallet.entity.operation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Operation {

    @Id
    private UUID uuid;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType operationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationStatus operationStatus;

    private String description;

    private boolean suspicious;

    @Column(nullable = false)
    private UUID ewalletUuid;

    private UUID destinationWalletUuid;

    @CreationTimestamp
    private Timestamp createdDate;
}
