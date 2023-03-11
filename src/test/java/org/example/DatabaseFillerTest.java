package org.example;

import com.github.javafaker.Faker;
import org.example.model.client.Address;
import org.example.model.client.Client;
import org.example.model.product.Product;
import org.example.repository.client.ClientRepository;
import org.example.repository.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

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
    public void probe() {
        ClientRepository clientRepository = context.getBean(ClientRepository.class);

        List<Client> clients = clientRepository.findAll(0, 25);

        for (Client client : clients) {
            System.out.println(client);
            System.out.println();
        }

        ProductRepository productRepository = context.getBean(ProductRepository.class);

        List<Product> products = productRepository.findAll(0, 25);

        for (Product product : products) {
            System.out.println(product);
            System.out.println();
        }
    }
}
