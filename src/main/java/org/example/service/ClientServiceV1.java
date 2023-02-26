package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.client.Client;
import org.example.repository.client.ClientRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class ClientServiceV1 implements ClientService {

    private final ClientRepository repository;

    private ClientServiceV1(ClientRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Client> getClient(long id) {
        log.info("Поиск клиента с ID {}", id);
        return repository.findById(id);
    }

    @Override
    public List<Client> getPageClient(long page) {
        log.info("Возвращаем страницу {} с клиентами", page);
        return repository.findAll(page, 25);
    }

    @Override
    public long getClientCount() {
        return repository.count();
    }

    @Override
    public void saveClient(Client client) {
        log.info("Сохраняем клиента {}", client);
        repository.save(client);
    }
}
