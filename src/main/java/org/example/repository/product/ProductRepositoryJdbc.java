package org.example.repository.product;

import lombok.extern.slf4j.Slf4j;
import org.example.config.DataSourceSupplier;
import org.example.model.product.Product;
import org.example.model.product.Vendor;
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
@Slf4j
public class ProductRepositoryJdbc implements ProductRepository {
    private final DataSource dataSource;

    public ProductRepositoryJdbc(DataSourceSupplier dataSourceSupplier) {
        this.dataSource = dataSourceSupplier.get();
    }

    @Override
    public Long count() {
        // language=SQL
        String query = "select count(*) as count from product";

        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    if (resultSet.next()) {
                        return resultSet.getLong("count");
                    }
                    else {
                        throw new QueryException("Ошибка при извлечении количества записей. ResultSet пуст");
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new QueryException("Ошибка при работе c БД", e);
        }
    }

    @Override
    public List<Product> findAll(long page, int pageSize) {
        // language=SQL
        String query = "select p.*, v.id as v_id, v.name, v.logo from product p " +
                "left join vendor v on p.vendor_id = v.id " +
                "offset ? rows fetch next ? rows only";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                long offset = page * pageSize;
                statement.setLong(1, offset);
                statement.setInt(2, pageSize);

                try (ResultSet result = statement.executeQuery()) {
                    List<Product> products = new ArrayList<>();
                    while (result.next()) {
                        products.add(map(result));
                    }
                    return products;
                }
            }
        }
        catch (SQLException e) {
            throw new QueryException("Ошибка при работе c БД", e);
        }
    }

    @Override
    public Optional<Product> findById(long id) {
        // language=SQL
        String query = "select p.*, v.id as v_id, v.name, v.logo " +
                "from product p left join vendor v on p.vendor_id = v.id where p.id = ?";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setLong(1, id);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return Optional.of(map(result));
                    }
                    else {
                        return Optional.empty();
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new QueryException("Ошибка при работе c БД", e);
        }
    }

    private Product map(ResultSet resultSet) throws SQLException {
        Product product = new Product();

        product.setId(resultSet.getLong("id"));
        product.setTitle(resultSet.getString("title"));
        product.setPhoto(resultSet.getString("photo"));
        product.setPrice(resultSet.getDouble("price"));
        product.setDescription(resultSet.getString("description"));
        product.setCount(resultSet.getInt("count"));

        Vendor vendor = new Vendor();
        vendor.setId(resultSet.getLong("v_id"));
        vendor.setName(resultSet.getString("name"));
        vendor.setLogo(resultSet.getString("logo"));

        return product;
    }

    @Override
    public void save(Product product) {
        // language=SQL
        String query = "insert into product(title, photo, price, description, count, vendor_id) values (?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, product.getTitle());
                statement.setString(2, product.getPhoto());
                statement.setDouble(3, product.getPrice());
                statement.setString(4, product.getDescription());
                statement.setInt(5, product.getCount());

                if (product.getVendor() != null) {
                    statement.setLong(6, product.getVendor().getId());
                }
                else {
                    statement.setObject(6, null);
                }

                statement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new QueryException("Ошибка при работе c БД", e);
        }
    }
}
