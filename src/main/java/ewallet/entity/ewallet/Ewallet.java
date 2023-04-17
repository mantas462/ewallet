package ewallet.entity.ewallet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Ewallet {

    @Id
    private UUID uuid;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private UUID customerUuid;
}
