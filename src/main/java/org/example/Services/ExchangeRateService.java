package org.example.Services;

import org.example.DAO.CurrencyDAO;
import org.example.DAO.ExchangeRateDAO;
import org.example.DTO.ExchangeRateDTO;
import org.example.Entities.Currency;
import org.example.Mappers.ExchangeRateMapper;

import java.math.BigDecimal;
import java.util.List;

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
        // List<ExchangeRate> exchangeRates = exchangeRateDAO.findAll();
        // map entity -> dto
        // return list
        return List.of();
    }

    public ExchangeRateDTO getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        // Optional<ExchangeRate> exchangeRate = exchangeRateDAO.findByCodes(baseCurrencyCode, targetCurrencyCode);
        // if (!exchangeRate.isPresent()) throw new CustomException();
        // map entity -> dto
        // return exchangeRateDTO
        return new ExchangeRateDTO(new Currency("", "", ""), new Currency("", "", ""), BigDecimal.valueOf(0));
    }

    public void addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        // Optional<Currency> baseCurrency = currencyDAO.findByCode(baseCurrencyCode);
        // Optional<Currency> targetCurrency = currencyDAO.findByCode(targetCurrencyCode);
        // if (!baseCurrency.isPresent() || !targetCurrency.isPresent()) throw new CustomException();
        // ExchangeRate exchangeRate = new ExchangeRate(baseCurrency.get().getId(), targetCurrency.get().getId(), rate);
        // exchangeRateDAO.save(exchangeRate);
    }
}
