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
import org.example.DTO.ExchangeDTO;
import org.example.Exceptions.RecordNotFoundException;
import org.example.Services.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;

@MultipartConfig
@WebServlet(name = "exchangeServlet", value = "/exchange")
public class ExchangeServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        exchangeRateService = (ExchangeRateService) context.getAttribute("exchange_rate_service");
        mapper = (ObjectMapper) context.getAttribute("json_mapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fromCurrencyCode = req.getParameter("from");
        String toCurrencyCode = req.getParameter("to");
        String amountRaw = req.getParameter("amount");

        if (!isValidParameters(fromCurrencyCode, toCurrencyCode, amountRaw)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
            return;
        }

        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(amountRaw));

        try {
            ExchangeDTO exchange = exchangeRateService.calculateExchange(fromCurrencyCode, toCurrencyCode, amount);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(mapper.writeValueAsString(exchange));
        } catch (RecordNotFoundException ex) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        }
    }

    private boolean isValidParameters(String baseCode, String targetCode, String amount) {
        return baseCode != null && targetCode != null && amount != null &&
                !baseCode.isEmpty() && !targetCode.isEmpty() && isNumber(amount);
    }

    private boolean isNumber(String amount) {
        if (amount.isEmpty()) return false;
        try {
            double d = Double.parseDouble(amount);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }
}
