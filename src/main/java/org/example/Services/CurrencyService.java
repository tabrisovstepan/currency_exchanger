package org.example.Services;

import org.example.DAO.CurrencyDAO;
import org.example.Entities.Currency;
import org.example.Exceptions.RecordNotFoundException;
import org.sqlite.SQLiteException;

import java.util.List;
import java.util.Optional;

public class CurrencyService {

    private final CurrencyDAO currencyDAO;

    public CurrencyService(CurrencyDAO currencyDAO) {
        this.currencyDAO = currencyDAO;
    }

    public List<Currency> getListOfCurrencies() {
        return currencyDAO.findAll();
    }

    public void addCurrency(String code, String fullName, String sign) throws SQLiteException {
        currencyDAO.save(new Currency(code, fullName, sign));
    }

    public Currency getCurrency(String code) throws RecordNotFoundException {
        Optional<Currency> currency = currencyDAO.findByCode(code);
        if (currency.isEmpty()) {
            throw new RecordNotFoundException("Currency with code " + code + " not found.");
        }
        return currency.get();
    }

    public Currency postCurrency(String code, String fullName, String sign) throws SQLiteException, RecordNotFoundException {
        addCurrency(code, fullName, sign);
        return getCurrency(code);
    }
}
