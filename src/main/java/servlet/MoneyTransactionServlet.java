package servlet;

import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MoneyTransactionServlet extends HttpServlet {
    static Map<String, Object> pageVariables = new HashMap<>();
    PageGenerator pageGenerator = PageGenerator.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        pageVariables.put("message", "");
        resp.getWriter().println(pageGenerator.getPage("moneyTransactionPage.html", pageVariables));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BankClientService bankClientService = BankClientService.getBankClientService();
        String producer = req.getParameter("senderName");
        String passwordProducer = req.getParameter("senderPass");
        String transactValue = req.getParameter("count");
        String consumer = req.getParameter("nameTo");
        if (bankClientService.transaction(producer, passwordProducer, transactValue, consumer)) {
            pageVariables.put("message", "The transaction was successful");
        } else {
            pageVariables.put("message", "transaction rejected");
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println(pageGenerator.getPage("resultPage.html", pageVariables));
    }
}
