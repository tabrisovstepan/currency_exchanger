package org.example.Listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.DAO.CurrencyDAO;
import org.example.DAO.ExchangeRateDAO;
import org.example.Services.CurrencyService;
import org.example.Services.ExchangeRateService;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@WebListener
public class ApplicationContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        URI uri;
        try {
            uri = getClass().getClassLoader().getResource("currency_exchanger.db").toURI();
        } catch (URISyntaxException e) {

            throw new RuntimeException(e);
        }
        String db = Paths.get(uri).toString();

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + db);
        HikariDataSource dataSource = new HikariDataSource(config);

        CurrencyDAO currencyDAO = new CurrencyDAO(dataSource);
        ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO(dataSource);
        CurrencyService currencyService = new CurrencyService(currencyDAO);
        ExchangeRateService exchangeRateService = new ExchangeRateService(currencyDAO, exchangeRateDAO);

        ObjectMapper jsonMapper = new ObjectMapper();
        context.setAttribute("currency_service", currencyService);
        context.setAttribute("exchange_rate_service", exchangeRateService);
        context.setAttribute("json_mapper", jsonMapper);
    }
}
