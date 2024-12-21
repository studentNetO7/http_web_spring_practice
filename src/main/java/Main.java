import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
//        // Тестирование парсинга строки запроса
//        System.out.println("Testing with test path: /messages?last=10&user=admin");
//        String testPath = "/messages?last=10&user=admin";
//        Map<String, String> result = Request.parseQueryParams(testPath);  // Ваш метод для парсинга
//        System.out.println("Parsed query params: " + result);  // Логирование результата
        Server server = new Server();

        // Добавление обработчиков для различных путей и методов
        server.addHandler("GET", "/messages", (request, responseStream) -> {
            // Извлекаем параметр из Query String
            String last = request.getQueryParam("last");
            String response = "GET request received for /messages with last parameter: " + (last != null ? last : "none");
            try {
                sendSuccessResponse(responseStream, response);  // Отправка успешного ответа
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        server.addHandler("POST", "/messages", (request, responseStream) -> {
            // Получаем параметры из тела запроса
            String response = "POST request received for /messages with body: " + request.getBody();

            // Также можно работать с параметрами из query string, если они присутствуют
            String last = request.getQueryParam("last");
            if (last != null) {
                response += " with query parameter last=" + last;
            }
            try {
                sendSuccessResponse(responseStream, response);  // Отправка успешного ответа
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Запуск сервера
        server.listen(9999);
    }

    // Метод для отправки успешного ответа
    private static void sendSuccessResponse(BufferedOutputStream out, String responseContent) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + responseContent.length() + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                responseContent;
        out.write(response.getBytes());
        out.flush();
    }
}
