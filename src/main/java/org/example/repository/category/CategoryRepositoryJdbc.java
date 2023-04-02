package org.example.repository.category;

import org.example.config.DataSourceSupplier;
import org.example.model.category.Category;
import org.example.repository.QueryException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryRepositoryJdbc implements CategoryRepository {

    private final DataSource dataSource;

    public CategoryRepositoryJdbc(DataSourceSupplier supplier) {
        this.dataSource = supplier.get();
    }



    @Override
    public long count() {
        return 0;
    }

    @Override
    public Optional<Category> findById(UUID id) {
        //language=SQL
        String query = "select * from category c where c.id = ?";

        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){

                statement.setObject(1,id);

                try(ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()){
                        return Optional.of(map(resultSet));
                    }
                    return Optional.empty();
                }
            }
        }

        catch (SQLException e){
            throw new QueryException("Ошибка при работе с БД!", e);
        }
    }

    private Category map(ResultSet resultSet) throws SQLException {

        Category category = new Category();

        category.setId(resultSet.getObject("id", UUID.class));
        category.setName(resultSet.getString("name"));
        category.setLogo(resultSet.getString("logo"));

        return category;
    }

    @Override
    public void save(Category category) {
    }
}
