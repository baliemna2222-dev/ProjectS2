package JStream.utils;

import java.io.InputStream;

import javafx.scene.image.Image;

public class ImageUtil {

    private static final String FALLBACK = "/assets/images/Loading.jpeg";

    public static Image load(String url) {
        try {
            Image img = null;

            if (url != null && !url.trim().isEmpty()) {
                img = new Image(url, true);
            }

            // If invalid → fallback
            if (img == null || img.isError()) {
                InputStream is = ImageUtil.class.getResourceAsStream(FALLBACK);
                if (is != null) {
                    img = new Image(is);
                }
            }

            // LAST fallback (never null texture)
            if (img == null || img.isError()) {
                img = new Image(
                    "data:image/png;base64," +
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII="
                );
            }

            return img;

        } catch (Exception e) {
            return new Image(
                "data:image/png;base64," +
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII="
            );
        }
    }
}