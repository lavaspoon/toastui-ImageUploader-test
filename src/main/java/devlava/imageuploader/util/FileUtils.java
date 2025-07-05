package devlava.imageuploader.util;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Function;

public class FileUtils {

    public static final Function<String, String> generateUniqueName = originalFilename -> {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    };

    public static final Function<HttpServletRequest, String> createServerUrl = request -> request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort();

    public static final Function<String, Path> createUploadPath = uploadDir -> {
        Path path = Paths.get(uploadDir);
        try {
            Files.createDirectories(path);
        } catch (Exception e) {
            throw new RuntimeException("디렉토리 생성 실패", e);
        }
        return path;
    };
}