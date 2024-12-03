package org.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;
import org.example.Entities.Currency;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
public class ExchangeDTO {
    @NonNull
    Currency baseCurrency;

    @NonNull
    Currency targetCurrency;

    @NonNull
    BigDecimal rate;

    @NonNull
    BigDecimal amount;

    @NonNull
    BigDecimal convertedAmount;
}
