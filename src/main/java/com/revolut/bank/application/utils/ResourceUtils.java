package com.revolut.bank.application.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility method to retrieve resources from classpath
 *
 * @author Konstantin Novokreshchenov (novokrest013@gmail.com)
 * @since 30.03.2019
 */
public class ResourceUtils {

    private static final Logger log = LoggerFactory.getLogger(ResourceUtils.class);

    public static String list(@Nonnull String path) {
        InputStream stream = ResourceUtils.class.getClassLoader().getResourceAsStream(path);
        try {
            String str = IOUtils.toString(stream, StandardCharsets.UTF_8);
            log.info("PATH: str={}", str);
            return str;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String resourceToString(@Nonnull String path) {
        try {
            return IOUtils.resourceToString(path, StandardCharsets.UTF_8, ResourceUtils.class.getClassLoader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> listResources(@Nonnull String dirPath) {
        try {
            return Files.list(getResourcePath(dirPath))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Path getResourcePath(String path) throws URISyntaxException, IOException {
        URI uri = ResourceUtils.class.getClassLoader().getResource(path).toURI();
        if (uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
            return fileSystem.getPath(path);
        } else {
            return Paths.get(uri);
        }
    }

    private ResourceUtils() {

    }

}
