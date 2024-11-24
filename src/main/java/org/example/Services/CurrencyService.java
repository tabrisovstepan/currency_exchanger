package org.example.Services;

import org.example.DAO.CurrencyDAO;
import org.example.Entities.Currency;
import org.sqlite.SQLiteException;

import java.util.List;

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

    public Currency getCurrency(String code) {
        //Optional<Currency> currency = dao.findByCode(code);
        // if (!currency.isPresent()) throw new CustomException();
        // return currency.get();
        return currencyDAO.findByCode(code);
    }

    public Currency postCurrency(String code, String fullName, String sign) throws SQLiteException {
        currencyDAO.save(new Currency(code, fullName, sign));
        return currencyDAO.findByCode(code);
    }
}
