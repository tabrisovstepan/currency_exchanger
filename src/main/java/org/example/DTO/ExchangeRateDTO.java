package org.example.DTO;

import lombok.*;
import org.example.Entities.Currency;
import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ExchangeRateDTO {
    Long id;

    @NonNull
    private Currency baseCurrency;

    @NonNull
    private Currency targetCurrency;

    @NonNull
    private BigDecimal rate;
}
