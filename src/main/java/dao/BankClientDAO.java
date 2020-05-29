package dao;

import model.BankClient;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class BankClientDAO {

    private Connection connection;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() throws SQLException {
        List<BankClient> bankClientList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM bank_client")) {
            preparedStatement.executeQuery();

            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                bankClientList.add(
                        new BankClient(
                                resultSet.getLong("id"),
                                resultSet.getString("name"),
                                resultSet.getString("password"),
                                resultSet.getLong("money")
                        )
                );
            }
            resultSet.close();
        } catch (SQLException | NullPointerException e) {
            e.getStackTrace();
        } finally {
            return bankClientList;
        }
    }

    public boolean validateClient(String name, String password) {
        try {
            BankClient bankClient = getClientByName(name);
            return bankClient.getPassword().equals(password);
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean deleteClient(String name) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM bank_client WHERE name=?")) {
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            return true;
        } catch (NullPointerException | SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean transaction(String producer, String passwordProducer, String transactValueString, String consumer) throws SQLException {
        if (!isNumberInt(transactValueString)) {
            return false;
        }
        Long transactValue = Long.parseLong(transactValueString);
        return transaction(producer, passwordProducer, transactValue, consumer);
    }

    public boolean transaction(String producer, String passwordProducer, Long transactValue, String consumer) throws SQLException {
        connection.setAutoCommit(false);
        Savepoint savepoint = connection.setSavepoint();
        try {
            String passwordConsumer = getClientByName(consumer).getPassword();

            boolean result = validateClient(producer, passwordProducer)
                    && isClientHasSum(producer, transactValue)
                    && updateClientsMoney(producer, passwordProducer, -transactValue)
                    && updateClientsMoney(consumer, passwordConsumer, +transactValue);
            if (result) {
                connection.commit();
            } else {
                connection.rollback(savepoint);
            }
            return result;
        } catch (NullPointerException | SQLException e) {
            connection.rollback(savepoint);
            return false;
        } finally {
            connection.setAutoCommit(true);
            connection.releaseSavepoint(savepoint);
        }
    }

    public boolean updateClientsMoney(String name, String password, Long transactValue) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE bank_client SET money = money + ? WHERE name=? AND password=?")) {
            preparedStatement.setLong(1, transactValue);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, password);
            preparedStatement.execute();
            return true;
        } catch (SQLException | NullPointerException e) {
            return false;
        }
    }

    public BankClient getClientById(long id) {
        String sqlRequest = "SELECT * FROM bank_client WHERE id='" + id + "'";
        return getClientUsingFilter(sqlRequest);
    }

    private BankClient getClientUsingFilter(String filterSql) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(filterSql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            BankClient bankClient = new BankClient(
                    resultSet.getLong("id"),
                    resultSet.getString("name"),
                    resultSet.getString("password"),
                    resultSet.getLong("money")
            );
            resultSet.close();
            return bankClient;

        } catch (SQLException | NullPointerException e) {
            return null;
        }
    }

    public boolean isClientHasSum(String name, Long expectedSum) {
        return getClientByName(name).getMoney() >= expectedSum;
    }

    public long getClientIdByName(String name) {
        return getClientByName(name).getId();
    }

    public BankClient getClientByName(String name) {
        String sqlRequest = "SELECT * FROM bank_client WHERE name='" + name + "'";
        return getClientUsingFilter(sqlRequest);
    }

    public boolean addClient(BankClient client) {
        String sqlRequest = "INSERT bank_client (name, password, money) value (?,?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest)) {
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getPassword());
            preparedStatement.setLong(3, client.getMoney());
            preparedStatement.execute();
            return true;
        } catch (SQLException | NullPointerException e) {
            return false;
        }
    }

    public boolean addClient(String name, String password, String moneyString) {
        if (!isNumberInt(moneyString)) {
            return false;
        }
        Long money = Long.parseLong(moneyString);
        return addClient(new BankClient(name, password, money));
    }

    public static boolean isNumberInt(String moneyString) {
        try {
            final Pattern INTEGER_PATTERN = Pattern.compile("\\d+");
            return INTEGER_PATTERN.matcher(moneyString).matches();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean actionTable(String sqlRequest) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest)) {
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createTable() {
        String createTableRequest = "create table if not exists bank_client (id bigint auto_increment, name varchar(256) not null UNIQUE, password varchar(256) not null, money bigint not null, primary key (id))";
        return actionTable(createTableRequest);
    }

    public boolean dropTable() {
        String dropTableRequest = "DROP TABLE IF EXISTS bank_client";
        return actionTable(dropTableRequest);
    }
}
