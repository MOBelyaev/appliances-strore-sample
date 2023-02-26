package org.example.repository.client;

import org.example.model.client.Client;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ClientRepositoryMock implements ClientRepository {

    @Override
    public long findMaxId() {
        return 0L;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public List<Client> findAll(long page, int pageSize) {
        return null;
    }

    @Override
    public Optional<Client> findById(long id) {
        return Optional.empty();
    }

    @Override
    public void save(Client client) {

    }
}
