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

public class RegistrationServlet extends HttpServlet {
    static Map<String, Object> pageVariables = new HashMap<>();
    PageGenerator pageGenerator = PageGenerator.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        pageVariables.put("message", "");
        resp.getWriter().println(pageGenerator.getPage("registrationPage.html", pageVariables));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BankClientService.getBankClientService().createTable();
        BankClientService bankClientService = BankClientService.getBankClientService();
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        String money = req.getParameter("money");
        if (bankClientService.addClient(name, password, money)) {
            pageVariables.put("message", "Add client successful");
        } else {
            pageVariables.put("message", "Client not add");
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println(pageGenerator.getPage("resultPage.html", pageVariables));
    }
}
