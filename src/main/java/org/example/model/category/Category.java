package org.example.model.category;

import lombok.Data;
import java.util.UUID;

@Data
public class Category {
    private UUID id;
    private String name;
    private String logo;
    private Category parent;
}
