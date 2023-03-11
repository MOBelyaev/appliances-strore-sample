package org.example.model.review;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.client.Client;
import org.example.model.product.Product;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class Review {
    private long id;
    private ReviewType type;
    private LocalDateTime date;
    private int productRating;
    private String text;
    private int like;
    private int dislike;
    private List<Attachment> attachments;
    private List<Review> comments;
    private Product product;
    private Client author;
}
