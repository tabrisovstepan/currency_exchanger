package org.example.Services;

import org.example.DAO.CurrencyDAO;
import org.example.Entities.Currency;

import java.util.List;

public class CurrencyService {

    private final CurrencyDAO currencyDAO;

    public CurrencyService(CurrencyDAO currencyDAO) {
        this.currencyDAO = currencyDAO;
    }

    public List<Currency> getListOfCurrencies() {
        return List.of();
    }

    public void addCurrency(String code, String fullName, String sign) {
        // dao.save(new Currency(code, fullName, sign))
    }

    public Currency getCurrency(String code) {
        //Optional<Currency> currency = dao.findByCode(code);
        // if (!currency.isPresent()) throw new CustomException();
        // return currency.get();
        return new Currency("", "", "");
    }
}
