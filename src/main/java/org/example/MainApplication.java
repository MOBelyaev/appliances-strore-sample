package org.example;

import org.example.model.client.Client;
import org.example.repository.client.ClientRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class MainApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("org.example");
        ClientRepository repository = context.getBean(ClientRepository.class);
        List<Client> all = repository.findAll(2, 21);
        for (Client client : all) {
            System.out.println(client);
        }

        context.close();
    }
}
