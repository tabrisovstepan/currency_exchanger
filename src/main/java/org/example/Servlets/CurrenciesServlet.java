package org.example.Servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Entities.Currency;
import org.example.Exceptions.RecordNotFoundException;
import org.example.Services.CurrencyService;
import org.sqlite.SQLiteException;

import java.io.IOException;
import java.util.List;

@MultipartConfig
@WebServlet(name = "currenciesServlet", value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;
    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        currencyService = (CurrencyService) context.getAttribute("currency_service");
        mapper = (ObjectMapper) context.getAttribute("json_mapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // handle database disconnection
        List<Currency> currencies = currencyService.getListOfCurrencies();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.addHeader("Content-Type", "application/json;charset=UTF-8");
        resp.getWriter().write(mapper.writeValueAsString(currencies));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // validate
        String code = req.getParameter("code");
        String fullName = req.getParameter("name");
        String sign = req.getParameter("sign");

        if (!isValidParameters(code, fullName, sign)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing field.");
            return;
        }

        // may be move logic into service
        try {
            currencyService.addCurrency(code, fullName, sign);
            Currency currency = currencyService.getCurrency(code);
            resp.addHeader("Content-Type", "application/json;charset=UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(mapper.writeValueAsString(currency));
        } catch (SQLiteException | RecordNotFoundException ex) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, ex.getMessage());
        }
    }

    private boolean isValidParameters(String code, String fullName, String sign) {
        return code != null && fullName != null && sign != null &&
                !code.isEmpty() && !fullName.isEmpty() && !sign.isEmpty() &&
                code.length() <= 3 && fullName.length() <= 100 && sign.length() <=5;
    }
}
