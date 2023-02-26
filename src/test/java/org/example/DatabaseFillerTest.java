package org.example;

import com.github.javafaker.Faker;
import org.example.model.client.Address;
import org.example.model.client.Client;
import org.example.model.product.Product;
import org.example.repository.client.ClientRepository;
import org.example.repository.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DatabaseFillerTest {

    private final static Faker faker = new Faker();

    @Test
    public void createProducts() {
        final Long cnt = 1000L;

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("org.example");

        ProductRepository repository = context.getBean(ProductRepository.class);
        Long init = repository.findMaxId();

        for (long i = init + 1L; i <= init + cnt; i++) {
            repository.save(createProduct(i));
        }

        context.close();
    }

    private Product createProduct(Long id) {
        Product product = new Product();

        product.setId(id);
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

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("org.example");

        ClientRepository repository = context.getBean(ClientRepository.class);
        long init = repository.findMaxId();

        for (long i = init + 1L; i <= init + cnt; i++) {
            repository.save(createClient(i));
        }

        context.close();
    }

    private Client createClient(Long id) {
        Client client = new Client();
        client.setId(id);
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
}
