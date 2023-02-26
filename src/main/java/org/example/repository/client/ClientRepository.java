package org.example.repository.client;

import org.example.model.client.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    long findMaxId();
    long count();
    List<Client> findAll(long page, int pageSize);
    Optional<Client> findById(long id);
    void save(Client client);
}
