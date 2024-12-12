import java.io.BufferedOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();

        // Добавление обработчиков для различных путей и методов
        server.addHandler("GET", "/messages", (request, responseStream) -> {
            String response = "GET request received for /messages";
            try {
                sendSuccessResponse(responseStream, response);  // Отправка успешного ответа
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("POST", "/messages", (request, responseStream) -> {
            String response = "POST request received for /messages with body: " + request.getBody();
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
