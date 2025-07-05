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
import java.util.function.Function;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "http://localhost:3000") // React 주소 명시
public class ImageUploadController {

    private final Function<String, String> generateUniqueName = originalFilename -> {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    };

    private final Function<HttpServletRequest, String> createServerUrl = request -> request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort();

    private final Function<String, Path> createUploadPath = uploadDir -> {
        Path path = Paths.get(uploadDir);
        try {
            Files.createDirectories(path);
        } catch (Exception e) {
            throw new RuntimeException("디렉토리 생성 실패", e);
        }
        return path;
    };

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("image") MultipartFile image,
            HttpServletRequest request // 요청 정보로부터 서버 주소를 얻기 위해 필요
    ) {
        try {
            if (image.isEmpty()) {
                throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
            }

            String uploadDir = "/Users/lavaspoon/Desktop/ImageUploader/file";
            String uniqueName = generateUniqueName.apply(image.getOriginalFilename());
            Path uploadPath = createUploadPath.apply(uploadDir);
            Path savePath = uploadPath.resolve(uniqueName);

            image.transferTo(savePath.toFile());

            String serverUrl = createServerUrl.apply(request);
            String imageUrl = serverUrl + "/static/images/" + uniqueName;

            return ResponseEntity.ok(Map.of("url", imageUrl));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}