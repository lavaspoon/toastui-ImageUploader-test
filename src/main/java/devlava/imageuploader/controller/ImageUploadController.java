package devlava.imageuploader.controller;

import devlava.imageuploader.util.FileUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "http://localhost:3000") // React 주소 명시
public class ImageUploadController {

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile image, HttpServletRequest request) {
        try {
            if (image.isEmpty()) {
                throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
            }

            String uploadDir = "/Users/lavaspoon/Desktop/ImageUploader/file";
            String uniqueName = FileUtils.generateUniqueName.apply(image.getOriginalFilename());
            Path uploadPath = FileUtils.createUploadPath.apply(uploadDir);
            Path savePath = uploadPath.resolve(uniqueName);

            image.transferTo(savePath.toFile());

            String serverUrl = FileUtils.createServerUrl.apply(request);
            String imageUrl = serverUrl + "/static/images/" + uniqueName;

            return ResponseEntity.ok(Map.of("url", imageUrl));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}