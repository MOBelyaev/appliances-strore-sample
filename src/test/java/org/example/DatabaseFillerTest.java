package org.example;

import com.github.javafaker.Faker;
import org.example.model.client.Address;
import org.example.model.client.Client;
import org.example.model.product.Product;
import org.example.model.review.Review;
import org.example.model.review.ReviewType;
import org.example.repository.client.ClientRepository;
import org.example.repository.product.ProductRepository;
import org.example.repository.review.ReviewRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DatabaseFillerTest {

    private final static Faker faker = new Faker();

    private final static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("org.example");

    @Test
    public void createProducts() {
        final long cnt = 1000L;

        ProductRepository repository = context.getBean(ProductRepository.class);

        for (long i = 0; i < cnt; i++) {
            repository.save(createProduct());
        }

        context.close();
    }

    private Product createProduct() {
        Product product = new Product();

        product.setTitle(faker.code().ean8());
        product.setCount(faker.number().numberBetween(0, 20));
        product.setPrice(faker.number().randomDouble(2, 50, 5000));
        product.setPhoto(faker.internet().url());
        product.setDescription(faker.letterify("???????????????????????????????????????????????????????????????????????????"));

        return product;
    }

    @Test
    public void createClients() {
        final long cnt = 200L;

        ClientRepository repository = context.getBean(ClientRepository.class);

        for (long i = 0; i < cnt; i++) {
            repository.save(createClient());
        }

        context.close();
    }

    private Client createClient() {
        Client client = new Client();
        client.setFirstName(faker.name().firstName());
        client.setLastName(faker.name().lastName());
        client.setPhone(faker.numerify("8##########"));
        Address address = new Address();
        address.setCity(faker.address().city());
        address.setStreet(faker.address().streetName());
        address.setHouse(faker.address().buildingNumber());
        address.setFlat(Integer.toString(faker.number().numberBetween(0, 10)));
        client.setAddress(address);

        return client;
    }

    @Test
    public void createReviews() {
        long cnt = 15;

        ClientRepository clientRepository = context.getBean(ClientRepository.class);
        ProductRepository productRepository = context.getBean(ProductRepository.class);

        List<Product> products = productRepository.findAll(0, 100);
        List<Client> clients = clientRepository.findAll(0, 100);

        Assertions.assertFalse(products.isEmpty());
        Assertions.assertFalse(clients.isEmpty());

        ReviewRepository reviewRepository = context.getBean(ReviewRepository.class);

        for (long i = 0; i < cnt; i++) {
            int productIndex = faker.number().numberBetween(0, products.size());
            int clientIndex = faker.number().numberBetween(0, clients.size());

            Review review = createReview(products.get(productIndex), clients.get(clientIndex));

            reviewRepository.saveReview(review);
        }
    }

    private Review createReview(Product product, Client author) {
        Review review = new Review();

        review.setType(ReviewType.REVIEW);
        review.setDate(faker.date().future(20, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        review.setProductRating(faker.number().numberBetween(1, 5));
        review.setText(faker.letterify("?????????????????????????????????????????????????????"));
        review.setLike(faker.number().numberBetween(0, 20));
        review.setDislike(faker.number().numberBetween(0, 20));

        review.setProduct(product);
        review.setAuthor(author);

        return review;
    }
}
