package org.example.model.product;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Product {
    private Long id;
    private String title;
    private String photo;
    private Double price;
    private String description;
    private Integer count;
    private Vendor vendor;
}
