import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public record HttpRequest(String method, String path) {
    private static final int MAX_HEADER_BYTES = 8192;
    private static final Pattern TOKEN = Pattern.compile("[!#$%&'*+.^_`|~0-9A-Za-z-]+");

    // null, istemcinin veri göndermeden bağlantıyı kapattığı anlamına gelir.
    public static HttpRequest read(InputStream input) throws IOException, InvalidRequest {
        ByteArrayOutputStream headers = new ByteArrayOutputStream();
        int lastFourBytes = 0;
        while (headers.size() < MAX_HEADER_BYTES) {
            int value = input.read();
            if (value == -1) {
                if (headers.size() == 0) {
                    return null;
                }
                throw new InvalidRequest("HTTP başlıkları tamamlanmadı.");
            }

            headers.write(value);
            lastFourBytes = (lastFourBytes << 8) | value;
            if (lastFourBytes == 0x0D0A0D0A) { // CRLF CRLF
                return parse(headers.toString(StandardCharsets.ISO_8859_1));
            }
        }

        throw new InvalidRequest("HTTP başlıkları 8192 baytı aşıyor.");
    }

    private static HttpRequest parse(String headerBlock) throws InvalidRequest {
        String[] lines = headerBlock.split("\\r\\n");
        String[] start = lines[0].split(" ", -1);
        if (start.length != 3 || !TOKEN.matcher(start[0]).matches()
                || !start[1].matches("/[!-~]*")
                || !(start[2].equals("HTTP/1.1") || start[2].equals("HTTP/1.0"))) {
            throw new InvalidRequest("Geçersiz HTTP istek satırı.");
        }

        boolean hostSeen = false;
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            int colon = line.indexOf(':');
            if (colon < 1 || !TOKEN.matcher(line.substring(0, colon)).matches()) {
                throw new InvalidRequest("Geçersiz HTTP başlığı.");
            }

            if (line.substring(0, colon).equalsIgnoreCase("Host")) {
                if (hostSeen || line.substring(colon + 1).isBlank()) {
                    throw new InvalidRequest("Geçersiz Host başlığı.");
                }
                hostSeen = true;
            }
        }

        if (start[2].equals("HTTP/1.1") && !hostSeen) {
            throw new InvalidRequest("HTTP/1.1 isteğinde Host başlığı eksik.");
        }

        int query = start[1].indexOf('?');
        String path = query < 0 ? start[1] : start[1].substring(0, query);
        return new HttpRequest(start[0], path);
    }

    public static final class InvalidRequest extends Exception {
        public InvalidRequest(String message) {
            super(message);
        }
    }
}
