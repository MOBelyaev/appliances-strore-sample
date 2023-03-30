package org.example.model.review;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Attachment {
    private long id;
    private AttachmentType type;
    private String attachment;
}
