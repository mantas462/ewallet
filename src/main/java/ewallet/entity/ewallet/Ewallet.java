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

    public boolean isEnoughBalance(BigDecimal amount) {

        return balance.compareTo(amount) > 0;
    }

    public Ewallet withdraw(BigDecimal amount) {

        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Amount=[%s] is bigger than the ewallet balance=[%s]".formatted(amount, balance));
        }

        BigDecimal newBalance = balance.subtract(amount).setScale(2);
        setBalance(newBalance);
        return this;
    }

    public Ewallet deposit(BigDecimal amount) {

        BigDecimal newBalance = balance.add(amount).setScale(2);
        setBalance(newBalance);
        return this;
    }
}
