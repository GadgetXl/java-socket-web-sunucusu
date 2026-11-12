import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record HttpResponse(int status, String reason, String contentType, String body) {
    private static final DateTimeFormatter HTTP_DATE = DateTimeFormatter.ofPattern(
            "EEE, dd MMM uuuu HH:mm:ss 'GMT'", Locale.US);

    public void write(OutputStream output, boolean headRequest) throws IOException {
        byte[] content = body.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 " + status + " " + reason + "\r\n"
                + "Date: " + HTTP_DATE.format(ZonedDateTime.now(ZoneOffset.UTC)) + "\r\n"
                + "Content-Type: " + contentType + "; charset=UTF-8\r\n"
                + "Content-Length: " + content.length + "\r\n"
                + "Connection: close\r\n"
                + (status == 405 ? "Allow: GET, HEAD\r\n" : "")
                + "\r\n";

        output.write(headers.getBytes(StandardCharsets.US_ASCII));
        if (!headRequest) {
            output.write(content);
        }
        output.flush();
    }
}
