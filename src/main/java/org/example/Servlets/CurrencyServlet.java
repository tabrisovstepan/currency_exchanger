package org.example.Servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Entities.Currency;
import org.example.Exceptions.RecordNotFoundException;
import org.example.Services.CurrencyService;

import java.io.IOException;

@WebServlet(name = "currencyServlet", value = "/currency/*")
public class CurrencyServlet extends HttpServlet {

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
        if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing field");
            return;
        }

        String code = req.getPathInfo().replaceFirst("/", "").toUpperCase();

        try {
            Currency currency = currencyService.getCurrency(code);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(mapper.writeValueAsString(currency));
        } catch (RecordNotFoundException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }
}
