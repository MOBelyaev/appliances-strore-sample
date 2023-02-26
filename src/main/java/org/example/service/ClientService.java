package org.example.service;

import org.example.model.client.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    Optional<Client> getClient(long id);

    List<Client> getPageClient(long page);

    long getClientCount();

    void saveClient(Client client);
}
