# Java Socket ile Web Sunucusu

Bu proje, Java standart kütüphanesindeki `ServerSocket` ve `Socket` sınıflarıyla yazılmış basit bir HTTP sunucusudur. TCP üzerinden 1989 numaralı portu dinler. Ana sayfada öğrencinin adı ve soyadı `h1`, öğrenci numarası `h2`, kısa biyografisi ise paragraf olarak gösterilir. Sayfanın renk ve yazı tipleri kendi HTML dosyasındaki CSS ile biçimlendirilir.

## Gereksinim ve çalıştırma

JDK 17 veya üzeri gerekir. Proje klasöründe terminal açıp şu komutları çalıştırın:

```powershell
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

Tarayıcıda [http://127.0.0.1:1989/](http://127.0.0.1:1989/) adresini açın. Sunucuyu durdurmak için terminalde **Ctrl+C** kullanın.

## Profil bilgilerini güncelleme

[`web/index.html`](web/index.html) dosyasındaki `h1`, `h2` ve `p` içeriğini kendi adınız, numaranız ve biyografinizle değiştirin. Sunucu HTML dosyasını açılışta okuduğu için düzenlemeden sonra yeniden başlatın.

## Proje yapısı

| Dosya | Görevi |
| --- | --- |
| [`Main.java`](src/Main.java) | HTML dosyasını okur ve sunucuyu 1989 numaralı portta başlatır. |
| [`WebServer.java`](src/WebServer.java) | TCP bağlantılarını kabul eder ve her bağlantıyı bir istekten sonra kapatır. |
| [`HttpRequest.java`](src/HttpRequest.java) | HTTP istek satırını ve başlıklarını okur, yöntemi ve yolu çıkarır. |
| [`RequestHandler.java`](src/RequestHandler.java) | HTTP isteğini yanıta dönüştüren tek metotlu arayüzdür. |
| [`ProfileHandler.java`](src/ProfileHandler.java) | Yönteme ve yola göre uygun HTTP yanıtını seçer. |
| [`HttpResponse.java`](src/HttpResponse.java) | Durum satırını, başlıkları ve UTF-8 gövdeyi bağlantıya yazar. |

## HTTP davranışı

| İstek | Beklenen yanıt |
| --- | --- |
| `GET /` | `200 OK` ve HTML profil sayfası |
| `HEAD /` | `200 OK` ve yalnızca başlıklar |
| `GET /olmayan` | `404 Not Found` |
| `POST /` | `405 Method Not Allowed` ve `Allow: GET, HEAD` |
| `HTTP/1.1` isteğinde `Host` eksik | `400 Bad Request` |

Sunucu bağlantıları sırayla işler. Okuma işlemi 5 saniye boyunca veri gelmezse zaman aşımına uğrar. İstek satırı ve başlıkların toplamı 8192 baytla sınırlıdır.

## Hızlı kontrol

Sunucu çalışırken ikinci bir terminalden aşağıdaki istekleri deneyebilirsiniz. Windows PowerShell'de curl yerine `curl.exe` yazın.

```sh
curl -i http://127.0.0.1:1989/
curl -I http://127.0.0.1:1989/
curl -i http://127.0.0.1:1989/olmayan
curl -i -X POST http://127.0.0.1:1989/
```

## SOLID

Bağlantı yönetimi, HTTP mesajlarının işlenmesi ve yanıt seçimi ayrı sınıflardadır. `WebServer`, somut profil sınıfı yerine `RequestHandler` arayüzünü kullanır. Bu sayede aynı sözleşmeyi karşılayan bir test işleyicisi veya farklı bir sayfa işleyicisi, sunucu döngüsünde değişiklik yapmadan kullanılabilir.

## Kaynaklar

- [Java SE 17 ServerSocket API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/ServerSocket.html)
- [Java SE 17 Socket API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/Socket.html)
- [RFC 9110 HTTP Semantics](https://www.rfc-editor.org/rfc/rfc9110.html)
- [RFC 9112 HTTP/1.1](https://www.rfc-editor.org/rfc/rfc9112.html)
