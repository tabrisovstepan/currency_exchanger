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
import org.example.DTO.ExchangeRateDTO;
import org.example.Exceptions.RecordExistsException;
import org.example.Exceptions.RecordNotFoundException;
import org.example.Services.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@MultipartConfig
@WebServlet(name = "exchangeRatesServlet", value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

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
        List<ExchangeRateDTO> exchangeRates = exchangeRateService.getListOfExchangeRates();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.addHeader("Content-Type", "application/json;charset=UTF-8");
        resp.getWriter().write(mapper.writeValueAsString(exchangeRates));
    }

    //fix multiply add. check dao
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rateRaw = req.getParameter("rate");

        if (!isValidParameters(baseCurrencyCode, targetCurrencyCode, rateRaw)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
            return;
        }

        BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(rateRaw));

        //may be move logic in service
        try {
            exchangeRateService.addExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateDTO exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.addHeader("Content-Type", "application/json;charset=UTF-8");
            resp.getWriter().write(mapper.writeValueAsString(exchangeRate));
        } catch (RecordNotFoundException ex) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        } catch (RecordExistsException ex) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, ex.getMessage());
        }
    }

    // to do better (check lengths)
    private boolean isValidParameters(String baseCode, String targetCode, String rate) {
        return baseCode != null && targetCode != null && rate != null &&
                !baseCode.isEmpty() && !targetCode.isEmpty()  && isNumber(rate);
    }

    private boolean isNumber(String rate) {
        if (rate.isEmpty()) return false;
        try {
            double d = Double.parseDouble(rate);
        } catch (NumberFormatException ex) {
            return false;
        }
        return true;
    }


}
