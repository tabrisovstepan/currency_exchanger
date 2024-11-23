package org.example.DAO;

import javax.sql.DataSource;
import org.example.Entities.Currency;

public class CurrencyDAO {
    private final DataSource dataSource;

    public CurrencyDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Currency findById(Long id) {
        return null;
    }
}
