package gun.service.utils;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class TestUtils {
    public static String fromResource(String path) {
        try {
            File file = ResourceUtils.getFile("classpath:" + path);
            return Files.lines(file.toPath()).collect(Collectors.joining());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
