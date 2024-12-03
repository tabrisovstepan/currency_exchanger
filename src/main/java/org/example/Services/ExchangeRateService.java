package org.example.Services;

import org.example.DAO.CurrencyDAO;
import org.example.DAO.ExchangeRateDAO;
import org.example.DTO.ExchangeDTO;
import org.example.DTO.ExchangeRateDTO;
import org.example.Entities.Currency;
import org.example.Entities.ExchangeRate;
import org.example.Exceptions.RecordExistsException;
import org.example.Exceptions.RecordNotFoundException;
import org.example.Mappers.ExchangeRateMapper;
import org.sqlite.SQLiteException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
        Optional<ExchangeRate> exchangeRate = exchangeRateDAO.findByCodes(baseCurrencyCode, targetCurrencyCode);

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

    public void updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) throws RecordNotFoundException {
        Optional<ExchangeRate> exchangeRate = exchangeRateDAO.findByCodes(baseCurrencyCode, targetCurrencyCode);

        if (exchangeRate.isEmpty()) {
            throw new RecordNotFoundException("Exchange rate not found");
        }

        exchangeRate.get().setRate(rate);
        exchangeRateDAO.update(exchangeRate.get());
    }

    public ExchangeDTO calculateExchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) throws RecordNotFoundException {
        Optional<Currency> baseCurrency = currencyDAO.findByCode(baseCurrencyCode);
        Optional<Currency> targetCurrency = currencyDAO.findByCode(targetCurrencyCode);

        if (baseCurrency.isEmpty() || targetCurrency.isEmpty()) {
            throw new RecordNotFoundException("Currency not exists");
        }

        BigDecimal rate = getRate(baseCurrencyCode, targetCurrencyCode);
        BigDecimal convertedAmount = amount.multiply(rate);
        return new ExchangeDTO(baseCurrency.get(), targetCurrency.get(), rate, amount, convertedAmount);
    }

    private BigDecimal getRate(String baseCurrencyCode, String targetCurrencyCode) throws RecordNotFoundException {
        Optional<ExchangeRate> exchangeRateAB = exchangeRateDAO.findByCodes(baseCurrencyCode, targetCurrencyCode);

        if (exchangeRateAB.isPresent()) {
            return exchangeRateAB.get().getRate();
        }

        Optional<ExchangeRate> exchangeRateBA = exchangeRateDAO.findByCodes(targetCurrencyCode, baseCurrencyCode);

        if (exchangeRateBA.isPresent()) {
            return BigDecimal.ONE.divide(exchangeRateBA.get().getRate(), 2, RoundingMode.HALF_EVEN);
        }

        Optional<ExchangeRate> exchangeRateUSDA = exchangeRateDAO.findByCodes(baseCurrencyCode, "USD");
        Optional<ExchangeRate> exchangeRateUSDB = exchangeRateDAO.findByCodes(targetCurrencyCode, "USD");

        if (exchangeRateUSDA.isEmpty() || exchangeRateUSDB.isEmpty()) {
            throw new RecordNotFoundException("Can not find exchange rate");
        }

        BigDecimal rateUSDA = exchangeRateUSDA.get().getRate();
        BigDecimal rateUSDB = exchangeRateUSDB.get().getRate();

        return rateUSDA.divide(rateUSDB, 2, RoundingMode.HALF_EVEN);
    }
}
