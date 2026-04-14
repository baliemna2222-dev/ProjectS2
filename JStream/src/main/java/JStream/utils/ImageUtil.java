package JStream.utils;

import java.io.InputStream;
import java.io.File;
import java.net.URL;

import javafx.scene.image.Image;

public class ImageUtil {

    private static final String FALLBACK = "/assets/images/Loading.jpeg";

    public static Image load(String url) {
        try {
            Image img = null;

            // 1. Try loading from URL / file / classpath
            if (url != null && !url.isBlank()) {

                // Case 1: HTTP or file URL
                if (url.startsWith("http") || url.startsWith("file:")) {
                    img = new Image(url, true);
                } else {
                    // Case 2: Classpath resource
                    URL resource = ImageUtil.class.getResource(
                        url.startsWith("/") ? url : "/" + url
                    );

                    if (resource != null) {
                        img = new Image(resource.toExternalForm(), true);
                    } else {
                        // Case 3: Local file
                        File file = new File(url);
                        if (file.exists()) {
                            img = new Image(file.toURI().toString(), true);
                        }
                    }
                }
            }

            // 2. Fallback image from resources
            if (img == null || img.isError()) {
                InputStream is = ImageUtil.class.getResourceAsStream(FALLBACK);
                if (is != null) {
                    img = new Image(is);
                }
            }

            // 3. Last fallback (1px image to avoid crash)
            if (img == null || img.isError()) {
                img = new Image(
                    "data:image/png;base64," +
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII="
                );
            }

            return img;

        } catch (Exception e) {
            System.err.println("ImageUtil error: " + e.getMessage());

            return new Image(
                "data:image/png;base64," +
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII="
            );
        }
    }
}