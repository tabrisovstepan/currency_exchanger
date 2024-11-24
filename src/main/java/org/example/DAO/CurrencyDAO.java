package org.example.DAO;

import javax.sql.DataSource;
import org.example.Entities.Currency;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAO {
    private final DataSource dataSource;

    public CurrencyDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Currency findById(Long id) {
        return null;
    }
    // return optional
    public Currency findByCode(String code) {
        String query = "SELECT * from currencies WHERE code = ?";
        Currency currency = null;
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            prepStmt.setString(1, code);
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                currency = fromRS(rs);
            }
            return currency;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Currency> findAll() {
        String query = "SELECT * from currencies;";
        List<Currency> currencies = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            ResultSet rs = prepStmt.executeQuery();
            while (rs.next()) {
                currencies.add(fromRS(rs));
            }
            return currencies;
        } catch (SQLException ex) {
            return List.of();
        }
    }
    // may be change signature to string, string, string
    public void save(Currency currency) throws SQLiteException {
        String query = "INSERT INTO currencies(code, full_name, sign) VALUES(?, ?, ?);";

        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement prepStmt = conn.prepareStatement(query);
            prepStmt.setString(1, currency.getCode());
            prepStmt.setString(2, currency.getFullName());
            prepStmt.setString(3, currency.getSign());
            prepStmt.execute();
        } catch (SQLiteException ex) {
            //may be use custom exception
            throw new SQLiteException("Currency already exists.", SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE);
            // parse SQLite error code for disconnection
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Currency fromRS (ResultSet rs) throws SQLException {
        return new Currency(rs.getLong("id"),
                            rs.getString("code"),
                            rs.getString("full_name"),
                            rs.getString("sign"));
    }
}
