import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {
    public static void main(String[] args) {
        try {
            String profile = Files.readString(Path.of("web", "index.html"));
            RequestHandler handler = new ProfileHandler(profile);
            new WebServer(1989, handler).run();
        } catch (IOException e) {
            System.err.println("Sunucu hatası: " + e.getMessage());
            System.exit(1);
        }
    }
}
