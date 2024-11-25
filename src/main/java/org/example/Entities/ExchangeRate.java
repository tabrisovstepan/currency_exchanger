package org.example.Entities;

import lombok.NonNull;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class ExchangeRate {
    private Long id;
    private Long baseCurrencyId;
    private Long targetCurrencyId;

    @NonNull
    private BigDecimal rate;

    //for DTO mapper, check how to check error
    private ExchangeRate() {}
}
