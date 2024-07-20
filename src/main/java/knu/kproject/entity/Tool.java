package knu.kproject.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Tool{
    private String figma;
    private String notion;
    private String github;
}
