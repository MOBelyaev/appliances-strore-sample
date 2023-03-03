package org.example.repository.client;

import org.example.config.DataSourceSupplier;
import org.example.model.client.Address;
import org.example.model.client.Client;
import org.example.repository.QueryException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ClientRepositoryJdbc implements ClientRepository {


    private final DataSource dataSource;


    public ClientRepositoryJdbc(DataSourceSupplier dataSource) {
        this.dataSource = dataSource.get();
    }

    @Override
    public long findMaxId() {
        // language=sql
        String query = "select max(c.id) as maxId from client c";

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    if (resultSet.next()) {
                        return resultSet.getLong("maxId");
                    } else {
                        throw new QueryException("Результат не найден!");
                    }
                } catch (SQLException e) {
                    throw new QueryException("Ошибка при получении результата запроса!", e);
                }
            } catch (SQLException e) {
                throw new QueryException("Ошибка при создании statement!", e);
            }
        } catch (SQLException e) {
            throw new QueryException("Ошибка при открытии соединения!", e);
        }
    }

    @Override
    public long count() {
        //language=sql
        String query = "select count(*) as cnt from client";

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    if (resultSet.next()) {
                        return resultSet.getLong("cnt");
                    } else {
                        throw new QueryException("Результат не найден!");
                    }
                } catch (SQLException e) {
                    throw new QueryException("Ошибка при получении результата запроса!", e);
                }
            } catch (SQLException e) {
                throw new QueryException("Ошибка при создании statement!", e);
            }
        } catch (SQLException e) {
            throw new QueryException("Ошибка при открытии соединения!", e);
        }

    }


    @Override
    public List<Client> findAll(long page, int pageSize) {
        //language=sql
        String query = "select c.id as id," +
                "c.first_name as fname," +
                "c.last_name as lname," +
                "c.phone as phone," +
                "c.city as city," +
                "c.street as street," +
                "c.house as house," +
                "c.flat as flat" +
                " from client c offset ? rows fetch next ? rows only";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                long offset = page * pageSize;
                statement.setLong(1, offset);
                statement.setInt(2, pageSize);

                try (ResultSet resultSet = statement.executeQuery()) {

                    List<Client> clients = new ArrayList<>();

                    while (resultSet.next()) {
                        clients.add(map(resultSet));
                    }
                    return clients;
                } catch (SQLException e) {
                    throw new QueryException("Ошибка при получении результата запроса!", e);
                }
            } catch (SQLException e) {
                throw new QueryException("Ошибка при создании statement!", e);
            }
        } catch (SQLException e) {
            throw new QueryException("Ошибка при открытии соединения!", e);
        }

    }

    @Override
    public Optional<Client> findById(long id) {

        //language=SQL
        String query = "select c.id as id," +
                "c.first_name as fname," +
                "c.last_name as lname," +
                "c.phone as phone," +
                "c.city as city," +
                "c.street as street," +
                "c.house as house," +
                "c.flat as flat " +
                "from client c where c.id = ?";

        try(Connection connection = dataSource.getConnection()){
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setLong(1, id);

                try(ResultSet  resultSet = statement.executeQuery()) {

                    if (resultSet.next()){

                        return Optional.of(map(resultSet));
                    }

                    return  Optional.empty();


                }
                catch (SQLException e) {
                    throw new QueryException("Ошибка при получении результата запроса!", e);
                }

            }
            catch (SQLException e) {
                throw new QueryException("Ошибка при создании statement");
            }
        }
        catch (SQLException e) {
            throw new QueryException("Ошибка при открытии соединения!", e);
        }

    }

    private Client map(ResultSet resultSet) throws SQLException {

        Client client = new Client();

        client.setId(resultSet.getLong("id"));
        client.setFirstName(resultSet.getString("fname"));
        client.setLastName(resultSet.getString("lname"));
        client.setPhone(resultSet.getString("phone"));

        Address address = new Address();

        address.setCity(resultSet.getString("city"));
        address.setStreet(resultSet.getString("street"));
        address.setHouse(resultSet.getString("house"));
        address.setFlat(resultSet.getString("flat"));

        client.setAddress(address);

        return client;
    }

    @Override
    public void save(Client client) {
        // language=SQL
        String query = "insert into client(id, first_name, last_name, phone, city, street, house, flat) values (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, client.getId());
                statement.setString(2, client.getFirstName());
                statement.setString(3, client.getLastName());
                statement.setString(4, client.getPhone());

                Address address = client.getAddress();
                if (address != null) {
                    statement.setString(5, address.getCity());
                    statement.setString(6, address.getStreet());
                    statement.setString(7, address.getHouse());
                    statement.setString(8, address.getFlat());
                }

                statement.executeUpdate();
            } catch (SQLException e) {
                throw new QueryException("Ошибка при создании statement!", e);
            }
        } catch (SQLException e) {
            throw new QueryException("Ошибка при открытии соединения!", e);
        }
    }
}
