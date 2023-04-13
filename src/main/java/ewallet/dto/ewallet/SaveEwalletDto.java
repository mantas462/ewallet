package ewallet.dto.ewallet;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class SaveEwalletDto {

    @Id
    private UUID uuid;
}
