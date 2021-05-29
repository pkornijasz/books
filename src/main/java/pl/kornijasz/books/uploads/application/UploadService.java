package pl.kornijasz.books.uploads.application;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import pl.kornijasz.books.uploads.application.port.UploadUseCase;
import pl.kornijasz.books.uploads.domain.Upload;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UploadService implements UploadUseCase {
    private final Map<String, Upload> storage = new ConcurrentHashMap<>();

    @Override
    public Upload save(SaveUploadCommand command) {
        String newId = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
        Upload upload = new Upload(newId, command.getFile(), command.getContentType(), command.getFilename(), LocalDateTime.now());
        storage.put(upload.getId(), upload); // return ?
        System.out.println("Upload saved: " + upload.getFilename() + " with id: " + newId);
        return upload;
    }

    @Override
    public Optional<Upload> getById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public void removeById(String id) {
        storage.remove(id);
    }
}
