package servlet;

import com.google.gson.Gson;
import service.BankClientService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ApiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            BankClientService bankClientService = BankClientService.getBankClientService();
            Gson gson = new Gson();
            String json;
            if (req.getPathInfo().contains("all")) {
                json = gson.toJson(bankClientService.getAllClient());
            } else {
                json = gson.toJson(bankClientService.getClientByName(req.getParameter("name")));
            }
            resp.getWriter().write(json);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.getStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        BankClientService bankClientService = BankClientService.getBankClientService();
        bankClientService.createTable();
        resp.setStatus(200);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        BankClientService bankClientService = BankClientService.getBankClientService();
        if (req.getPathInfo().contains("all")) {
            bankClientService.cleanUp();
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
