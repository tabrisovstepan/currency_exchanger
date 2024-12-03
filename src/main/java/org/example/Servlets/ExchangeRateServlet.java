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
import org.example.Exceptions.RecordNotFoundException;
import org.example.Services.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;

@MultipartConfig
@WebServlet(name = "exchangeRateServlet", value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private ExchangeRateService exchangeRateService;
    private ObjectMapper mapper;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        exchangeRateService = (ExchangeRateService) context.getAttribute("exchange_rate_service");
        mapper = (ObjectMapper) context.getAttribute("json_mapper");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing field");
            return;
        }

        String pairCodesRaw = req.getPathInfo().replaceFirst("/", "").toUpperCase();

        // may be refactor
        if (pairCodesRaw.length() != 6) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid currencies codes.");
            return;
        }

        String baseCurrencyCode = pairCodesRaw.substring(0, 3);
        String targetCurrencyCode = pairCodesRaw.substring(3, 6);

        try {
            ExchangeRateDTO exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.addHeader("Content-Type", "application/json;charset=UTF-8");
            resp.getWriter().write(mapper.writeValueAsString(exchangeRate));
        } catch (RecordNotFoundException ex) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        }
    }

    // need fix
    private void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing field");
            return;
        }

        String pairCodesRaw = req.getPathInfo().replaceFirst("/", "").toUpperCase();

        if (pairCodesRaw.length() != 6) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid currencies codes");
            return;
        }

        String rateRaw = req.getParameter("rate");

        if (!isValidParameter(rateRaw)) {
            String message = "Missing field rate. " + req.getPathInfo();
            resp.addHeader("Content-Type", "application/json;charset=UTF-8");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, mapper.writeValueAsString(message));
            return;
        }

        BigDecimal rate = BigDecimal.valueOf(Double.parseDouble(rateRaw));

        String baseCurrencyCode = pairCodesRaw.substring(0, 3);
        String targetCurrencyCode = pairCodesRaw.substring(3, 6);

        try {
            exchangeRateService.updateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            ExchangeRateDTO exchangeRate = exchangeRateService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.addHeader("Content-Type", "application/json;charset=UTF-8");
            resp.getWriter().write(mapper.writeValueAsString(exchangeRate));
        } catch (RecordNotFoundException ex) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        }
    }

    private boolean isValidParameter(String rate) {
        return rate != null && isNumber(rate);
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
