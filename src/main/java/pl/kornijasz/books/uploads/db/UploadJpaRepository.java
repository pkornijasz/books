package pl.kornijasz.books.uploads.db;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kornijasz.books.uploads.domain.Upload;

public interface UploadJpaRepository extends JpaRepository<Upload, Long> {
}
