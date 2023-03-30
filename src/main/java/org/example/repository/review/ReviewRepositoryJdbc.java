package org.example.repository.review;

import org.example.config.DataSourceSupplier;
import org.example.model.client.Client;
import org.example.model.product.Product;
import org.example.model.review.Review;
import org.example.model.review.ReviewType;
import org.example.repository.QueryException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ReviewRepositoryJdbc implements ReviewRepository {

    private final DataSource dataSource;

    public ReviewRepositoryJdbc(DataSourceSupplier dataSource) {
        this.dataSource = dataSource.get();
    }

    @Override
    public long count() {
        //language=sql
        String query = "select count(*) as cnt from review";
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(query)) {

                    if (resultSet.next()) {
                        return resultSet.getLong("cnt");
                    } else {
                        throw new QueryException("Ошибка при извлечении количества записей. ResultSet пуст");
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new QueryException("Ошибка при работе с БД", e);
        }
    }

    @Override
    public Optional<Review> findById(long id) {

        String query = "select * from review r where r.id = ?";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement prepareStatement = connection.prepareStatement(query)) {

                prepareStatement.setLong(1, id);

                try (ResultSet resultSet = prepareStatement.executeQuery()) {

                    if (resultSet.next()) {
                        return Optional.of(map(resultSet));
                    }
                    else {
                        return Optional.empty();
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new QueryException("Ошибка при работе с БД", e);
        }
    }

    @Override
    public long countReviewForProduct(long productId) {

        //language=SQL
        String query = "select count(*) as cnt from review r where r.product_id = ?";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setLong(1, productId);

                try (ResultSet resultSet = statement.executeQuery()) {

                    if (resultSet.next()) {
                        return resultSet.getLong("cnt");
                    }
                    else {
                        throw new QueryException("Ошибка при извлечении количества записей. ResultSet пуст");
                    }

                }
            }
        }
        catch (SQLException e) {
            throw new QueryException("Ошибка при работе с БД", e);
        }

    }

    @Override
    public List<Review> findReviewForProduct(long productId, int page, int pageSize) {
        //language=SQL
        String query = "select * from review r where r.product_id = ? and r.type = ? offset ? rows fetch next ? rows only";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                int offset = page * pageSize;

                statement.setLong(1, productId);
                statement.setString(2,ReviewType.REVIEW.name());
                statement.setInt(3, offset);
                statement.setInt(4, pageSize);

                try (ResultSet resultSet = statement.executeQuery()) {

                    List<Review> review = new ArrayList<>();

                    while (resultSet.next()) {
                        review.add(map(resultSet));
                    }
                    return review;
                }
            }
        }
        catch (SQLException e) {
            throw new QueryException("Ошибка при работе с БД", e);
        }
    }

    @Override
    public long countCommentsForReview(long reviewId) {

        //language=SQL
        String query = "select count(*) as cnt from review_comment r where r.review_id = ?";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setLong(1, reviewId);

                try (ResultSet resultSet = statement.executeQuery()) {

                    if (resultSet.next()) {
                        return resultSet.getLong("cnt");
                    }
                    else {
                        throw new QueryException("Ошибка при получении результата. resultSet");
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new QueryException("Ошибка при работе с БД", e);
        }
    }

    @Override
    public List<Review> findCommentsForReview(long reviewId, int page, int pageSize) {

        //language=SQL
        String query = "select r.* from review_comment rc " +
                "inner join review r on rc.comment_id = r.id " +
                "where rc.review_id = ? offset ? rows fetch next ? rows only";

        try(Connection connection = dataSource.getConnection()){
            try(PreparedStatement statement = connection.prepareStatement(query)){

                int offset = page * pageSize;

                statement.setLong(1, reviewId);
                statement.setInt(2,offset);
                statement.setInt(3, pageSize);

                List<Review> comments = new ArrayList<>();

                try(ResultSet resultSet = statement.executeQuery()){

                    while (resultSet.next()){
                        comments.add(map(resultSet));
                    }
                    return comments;
                }
            }
        }
        catch (SQLException e){
            throw new QueryException("Ошибка при работе с БД", e);
        }

    }

    private Review map (ResultSet resultSet) throws SQLException {

        Review review = new Review();

        review.setId(resultSet.getLong("id"));
        review.setType(ReviewType.valueOf(resultSet.getString("type")));
        review.setDate(resultSet.getTimestamp("date").toLocalDateTime());
        review.setProductRating(resultSet.getInt("product_rating"));
        review.setText(resultSet.getString("text"));
        review.setLike(resultSet.getInt("review_like"));
        review.setDislike(resultSet.getInt("review_dislike"));

        Product product = new Product();

        product.setId(resultSet.getLong("product_id"));

        Client client = new Client();

        client.setId(resultSet.getLong("client_id"));

        review.setProduct(product);
        review.setAuthor(client);

        return review;
    }

    @Override
    public void saveComment(long reviewId, Review comment) {
        //language=SQL
        String query = "insert into review(id, type, date, product_rating, text, review_like, review_dislike, product_id, client_id) values (?,?,?,?,?,?,?,?,?)";
        //language=SQL
        String commentQuery = "insert into review_comment(review_id, comment_id) values (?,?)";

        validateReview(comment);

        try(Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(false);
            try(PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, comment.getId());
                statement.setString(2, comment.getType().name());
                statement.setTimestamp(3, Timestamp.valueOf(comment.getDate()));
                statement.setInt(4, comment.getProductRating());
                statement.setString(5, comment.getText());
                statement.setInt(6, comment.getLike());
                statement.setInt(7, comment.getDislike());
                statement.setLong(8, comment.getProduct().getId());
                statement.setLong(9, comment.getAuthor().getId());

                statement.executeUpdate();
            }
            catch (SQLException e) {
                connection.rollback();
                throw new QueryException("Ошибка при исполнении запроса", e);
            }

            try (PreparedStatement statement = connection.prepareStatement(commentQuery)) {
                statement.setLong(1, reviewId);
                statement.setLong(2, comment.getId());

                statement.executeUpdate();
            }
            catch (SQLException e) {
                connection.rollback();
                throw new QueryException("Ошибка при исполнении запроса", e);
            }

            connection.commit();
        }
        catch (SQLException e){
            throw new QueryException("Ошибка при работе с БД", e);
        }
    }

    @Override
    public void saveReview(Review review){
        //language=sql
        String query = "insert into review (type, date, product_rating, text, review_like, review_dislike, product_id, client_id)  values (?, ?, ?, ?, ?, ?, ?, ?)";

        validateReview(review);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, review.getType().name());
                statement.setTimestamp(2, Timestamp.valueOf(review.getDate()));
                statement.setInt(3, review.getProductRating());
                statement.setString(4, review.getText());
                statement.setInt(5, review.getLike());
                statement.setInt(6, review.getDislike());
                statement.setLong(7, review.getProduct().getId());
                statement.setLong(8, review.getAuthor().getId());

                statement.executeUpdate();
            }
        }
        catch (SQLException e) {
            throw new QueryException("Ошибка при работе с БД", e);
        }

    }

    private void validateReview (Review review){
        if (review == null) {
            throw new QueryException("Отзыв не должен быть null");
        }
        if (review.getProduct() == null) {
            throw new QueryException("Отзыв должен относиться к продукту");
        }
        if (review.getAuthor() == null) {
            throw new QueryException("Отзыв должен содержать автора");
        }
    }
}
