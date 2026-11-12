import java.util.Set;

public final class ProfileHandler implements RequestHandler {
    private static final Set<String> OTHER_METHODS = Set.of(
            "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE", "CONNECT");

    private final HttpResponse profile;

    public ProfileHandler(String html) {
        profile = new HttpResponse(200, "OK", "text/html", html);
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        if (!request.method().equals("GET") && !request.method().equals("HEAD")) {
            if (OTHER_METHODS.contains(request.method())) {
                return new HttpResponse(405, "Method Not Allowed", "text/plain",
                        "Bu sunucu GET ve HEAD isteklerini kabul eder.");
            }
            return new HttpResponse(501, "Not Implemented", "text/plain",
                    "Bu HTTP yöntemi tanınmıyor.");
        }

        if (request.path().equals("/")) {
            return profile;
        }

        return new HttpResponse(404, "Not Found", "text/plain", "Sayfa bulunamadı.");
    }
}
