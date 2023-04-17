package ewallet.util.mapper.ewallet;

import ewallet.dto.ewallet.internal.EwalletDto;
import ewallet.entity.ewallet.Ewallet;

import java.math.BigDecimal;
import java.util.UUID;

public final class EwalletMapper {

    public static Ewallet toEntity(EwalletDto ewalletDto) {

        return Ewallet.builder()
                .uuid(ewalletDto.getUuid())
                .balance(ewalletDto.getBalance())
                .customerUuid(ewalletDto.getCustomerUuid())
                .build();
    }

    public static Ewallet createEntity(EwalletDto ewalletDto) {

        return Ewallet.builder()
                .uuid(UUID.randomUUID())
                .balance(BigDecimal.ZERO)
                .customerUuid(ewalletDto.getCustomerUuid())
                .build();
    }

    public static EwalletDto createDtoWithCustomerUuid(UUID uuid) {

        return EwalletDto.builder()
                .customerUuid(uuid)
                .build();
    }

    public static EwalletDto toDto(Ewallet ewallet) {

        return EwalletDto.builder()
                .uuid(ewallet.getUuid())
                .customerUuid(ewallet.getCustomerUuid())
                .balance(ewallet.getBalance())
                .build();
    }
}
