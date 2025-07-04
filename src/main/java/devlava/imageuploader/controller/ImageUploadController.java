package devlava.imageuploader.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "http://localhost:3000")  // React 주소 명시
public class ImageUploadController {

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("image") MultipartFile image,
            HttpServletRequest request // 요청 정보로부터 서버 주소를 얻기 위해 필요
    ) {
        try {
            if (image.isEmpty()) {
                throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
            }

            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueName = UUID.randomUUID() + extension;

            String uploadDir = "/Users/lavaspoon/Desktop/ImageUploader/file";
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);

            Path savePath = uploadPath.resolve(uniqueName);
            image.transferTo(savePath.toFile());

            // ✅ 절대 URL 생성
            String serverUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String imageUrl = serverUrl + "/static/images/" + uniqueName;

            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}