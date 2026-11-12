import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class WebServer {
    private static final int READ_TIMEOUT_MS = 5000;

    private final int port;
    private final RequestHandler handler;

    public WebServer(int port, RequestHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void run() throws IOException {
        try (ServerSocket listener = new ServerSocket(port)) {
            System.out.println("Sunucu hazır: http://127.0.0.1:" + port + "/");
            while (true) {
                Socket connection = listener.accept();
                try (connection) {
                    handleConnection(connection);
                } catch (IOException e) {
                    System.err.println("İstemci bağlantısı kapandı: " + e.getMessage());
                }
            }
        }
    }

    private void handleConnection(Socket connection) throws IOException {
        connection.setSoTimeout(READ_TIMEOUT_MS);
        try {
            HttpRequest request = HttpRequest.read(
                    new BufferedInputStream(connection.getInputStream()));
            if (request != null) {
                HttpResponse response = handler.handle(request);
                response.write(connection.getOutputStream(), request.method().equals("HEAD"));
            }
        } catch (HttpRequest.InvalidRequest e) {
            HttpResponse response = new HttpResponse(400, "Bad Request", "text/plain",
                    e.getMessage());
            response.write(connection.getOutputStream(), false);
        }
    }
}
