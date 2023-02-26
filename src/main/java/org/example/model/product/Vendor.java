package org.example.model.product;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Vendor {
    private Long id;
    private String name;
    private String logo;
}
