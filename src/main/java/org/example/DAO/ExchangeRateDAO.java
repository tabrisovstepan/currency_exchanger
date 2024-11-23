package org.example.DAO;

import javax.sql.DataSource;

public class ExchangeRateDAO {
    private final DataSource dataSource;

    public ExchangeRateDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
