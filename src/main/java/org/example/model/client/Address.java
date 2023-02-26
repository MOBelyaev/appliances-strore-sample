package org.example.model.client;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Address {
    private String city;
    private String street;
    private String house;
    private String flat;
}
