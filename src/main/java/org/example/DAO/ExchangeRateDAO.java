package org.example.DAO;

import org.example.Entities.ExchangeRate;
import org.example.Exceptions.RecordExistsException;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeRateDAO {
    private final DataSource dataSource;

    public ExchangeRateDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // fix multiply add. may be use unique constraint
    public void save(ExchangeRate exchangeRate) throws RecordExistsException {
        String query = "insert into exchange_rates(base_currency_id, target_currency_id, rate) values(?, ?, ?);";

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            prepStmt.setLong(1, exchangeRate.getBaseCurrencyId());
            prepStmt.setLong(2, exchangeRate.getTargetCurrencyId());
            prepStmt.setBigDecimal(3, exchangeRate.getRate());
            prepStmt.execute();
        } catch (SQLiteException ex) {
            //may be use custom exception
            //throw new SQLiteException("Currency already exists.", SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE);
            throw new RecordExistsException("Exchange rate already exists");
            // parse SQLite error code for disconnection
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Optional<ExchangeRate> findByCodes(String baseCurrencyCode, String targetCurrencyCode) {
        return Optional.empty();
    }

    public Optional<ExchangeRate> findById(Long baseCurrencyId, Long targetCurrencyId) {
        String query = "select * from exchange_rates where base_currency_id = ? and target_currency_id = ?;";

        ExchangeRate exchangeRate = null;
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            prepStmt.setLong(1, baseCurrencyId);
            prepStmt.setLong(2, targetCurrencyId);
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                exchangeRate = fromRS(rs);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private ExchangeRate fromRS(ResultSet rs) throws SQLException {
        return new ExchangeRate(rs.getLong("id"),
                                rs.getLong("base_currency_id"),
                                rs.getLong("target_currency_id"),
                                rs.getBigDecimal("rate"));
    }
}
