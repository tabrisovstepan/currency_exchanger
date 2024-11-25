package org.example.Mappers;

import org.example.DAO.CurrencyDAO;
import org.example.DTO.ExchangeRateDTO;
import org.example.Entities.Currency;
import org.example.Entities.ExchangeRate;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

public class ExchangeRateMapper {

    private final CurrencyDAO currencyDAO;
    private final ModelMapper mapper;
    private final Converter<Long, Currency> converter;

    public ExchangeRateMapper(CurrencyDAO currencyDAO) {
        this.currencyDAO = currencyDAO;
        mapper = new ModelMapper();
        converter = new CurrencyConverter();

        mapper.emptyTypeMap(ExchangeRate.class, ExchangeRateDTO.class)
                .addMappings(m -> m.using(converter).map(ExchangeRate::getBaseCurrencyId, ExchangeRateDTO::setBaseCurrency))
                .addMappings(m -> m.using(converter).map(ExchangeRate::getTargetCurrencyId, ExchangeRateDTO::setTargetCurrency))
                .implicitMappings();
    }

    public ExchangeRateDTO mapToDTO(ExchangeRate exchangeRate) {
        return mapper.map(exchangeRate, ExchangeRateDTO.class);
    }

    private class CurrencyConverter extends AbstractConverter<Long, Currency> {
        @Override
        protected Currency convert(Long id) {
            return currencyDAO.findById(id).orElseGet(() -> new Currency("", "",""));
        }
    }
}
