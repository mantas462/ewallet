package ewallet.dto.customer.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class CustomerDto {

    private UUID uuid;
    private String firstName;
    private String lastName;
    private String email;
}
