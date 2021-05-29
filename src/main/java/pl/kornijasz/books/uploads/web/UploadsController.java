package pl.kornijasz.books.uploads.web;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kornijasz.books.uploads.application.port.UploadUseCase;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/uploads")
@AllArgsConstructor
public class UploadsController {
    private final UploadUseCase upload;

    @GetMapping("/{id}")
    public ResponseEntity<UploadResponse> getUpload(@PathVariable String id) {
        return upload.getById(id)
                .map(file -> {
            UploadResponse response = new UploadResponse(
                    file.getId(),
                    file.getContentType(),
                    file.getFilename(),
                    file.getFile().length,
                    file.getCreatedAt());
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getUploadFile(@PathVariable String id) {
        return upload.getById(id)
                .map(file -> {
                    String contentDisposition = "attachment; filename=\"" + file.getFilename() + "\"";
//                    byte[] bytes = file.getFile();
                    Resource resource = new ByteArrayResource(file.getFile());
                    return ResponseEntity
                            .ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                            .contentType(MediaType.parseMediaType(file.getContentType()))
                            .body(resource);
                }).orElse(ResponseEntity.notFound().build());
    }

    @Value
    @AllArgsConstructor
    static class UploadResponse {
        String id;
        String contentType;
        String filename;
        int length;
        LocalDateTime createdAt;
    }
}
