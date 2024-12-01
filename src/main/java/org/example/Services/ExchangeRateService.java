package org.example.Services;

import org.example.DAO.CurrencyDAO;
import org.example.DAO.ExchangeRateDAO;
import org.example.DTO.ExchangeRateDTO;
import org.example.Entities.Currency;
import org.example.Entities.ExchangeRate;
import org.example.Exceptions.RecordExistsException;
import org.example.Exceptions.RecordNotFoundException;
import org.example.Mappers.ExchangeRateMapper;
import org.sqlite.SQLiteException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO;
    private final CurrencyDAO currencyDAO;
    private final ExchangeRateMapper mapper;

    public ExchangeRateService(CurrencyDAO currencyDAO, ExchangeRateDAO exchangeRateDAO) {
        this.currencyDAO = currencyDAO;
        this.exchangeRateDAO = exchangeRateDAO;
        mapper = new ExchangeRateMapper(currencyDAO);
    }

    public List<ExchangeRateDTO> getListOfExchangeRates() {
        List<ExchangeRate> exchangeRates = exchangeRateDAO.findAll();
        return mapper.mapToDTO(exchangeRates);
    }

    public ExchangeRateDTO getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws RecordNotFoundException {
        // may be move queries in exchange rate dao
        Optional<Currency> baseCurrency = currencyDAO.findByCode(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyDAO.findByCode(targetCurrencyCode);

        if (baseCurrency.isEmpty() || targetCurrency.isEmpty()) {
            throw new RecordNotFoundException("Currency not found");
        }

        Optional<ExchangeRate> exchangeRate = exchangeRateDAO.findById(baseCurrency.get().getId(), targetCurrency.get().getId());

        if (exchangeRate.isEmpty()) {
            throw new RecordNotFoundException("Exchange rate not found");
        }
        return mapper.mapToDTO(exchangeRate.get());
    }

    public void addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws RecordExistsException, RecordNotFoundException {
        Optional<Currency> baseCurrency = currencyDAO.findByCode(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyDAO.findByCode(targetCurrencyCode);

        if (baseCurrency.isEmpty() || targetCurrency.isEmpty()) {
            throw new RecordNotFoundException("Currency not found");
        }

        ExchangeRate exchangeRate = new ExchangeRate(0L, baseCurrency.get().getId(), targetCurrency.get().getId(), rate);
        exchangeRateDAO.save(exchangeRate);
    }
}
