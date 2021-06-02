package pl.kornijasz.books.uploads.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Upload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    private String contentType;

    private byte[] file;

    @CreatedDate
    private LocalDateTime createdAt;

    public Upload(String filename, String contentType, byte[] file) {
        this.filename = filename;
        this.contentType = contentType;
        this.file = file;
    }
}
