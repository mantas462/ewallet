package ewallet.util.mapper.ewallet;

import ewallet.dto.ewallet.SaveEwalletDto;
import ewallet.entity.ewallet.Ewallet;

import java.math.BigDecimal;
import java.util.UUID;

public final class EwalletMapper {

    public static Ewallet toEntity(SaveEwalletDto saveEwalletDto) {

        return Ewallet.builder()
                .uuid(saveEwalletDto.getUuid())
                .balance(BigDecimal.ZERO)
                .build();
    }

    public static SaveEwalletDto toDto(UUID uuid) {

        return SaveEwalletDto.builder()
                .uuid(uuid)
                .build();
    }
}
