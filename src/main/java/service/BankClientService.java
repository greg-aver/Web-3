package service;

import dao.BankClientDAO;
import model.BankClient;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class BankClientService {
    private static BankClientService bankClientService;
    private static BankClientDAO bankClientDAO;

    public static BankClientService getBankClientService() {
        if (bankClientService == null) {
            bankClientService = new BankClientService();
        }
        return bankClientService;
    }

    private BankClientService() {
    }

    public BankClient getClientById(long id) {
        return getBankClientDAO().getClientById(id);
    }

    public BankClient getClientByName(String name) {
        return getBankClientDAO().getClientByName(name);
    }

    public List<BankClient> getAllClient() throws SQLException {
        return getBankClientDAO().getAllBankClient();
    }

    public boolean deleteClient(String name) {
        return getBankClientDAO().deleteClient(name);
    }

    public boolean addClient(BankClient client) {
        return getBankClientDAO().addClient(client);
    }

    public boolean addClient(String name, String password, String moneyString) {
        return getBankClientDAO().addClient(name, password, moneyString);
    }

    public boolean transaction(String producer, String passwordProducer, String transactValueString, String consumer) {
        try {
            return getBankClientDAO().transaction(producer, passwordProducer, transactValueString, consumer);
        } catch (NullPointerException | SQLException e) {
            return false;
        }
    }

    public boolean cleanUp() {
        return getBankClientDAO().dropTable();
    }

    public boolean createTable() {
        return getBankClientDAO().createTable();
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
            StringBuilder url = new StringBuilder();
            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("bank_dataBase?").          //db name
                    append("user=root&").          //login
                    append("password=password&").       //password
                    append("serverTimezone=Europe/Moscow&useSSL=false");
            System.out.println("URL: " + url + "\n");
            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        if (bankClientDAO == null) {
            bankClientDAO = new BankClientDAO(getMysqlConnection());
        }
        return bankClientDAO;
    }
}
