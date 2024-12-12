import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionHandler implements Runnable {

    private final Socket clientSocket;
    private final ConcurrentMap<String, ConcurrentMap<String, Handler>> handlers;

    public ConnectionHandler(Socket clientSocket, ConcurrentMap<String, ConcurrentMap<String, Handler>> handlers) {
        this.clientSocket = clientSocket;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream())
        ) {
            String requestLine = in.readLine();
            if (requestLine == null) {
                return;
            }

            String[] parts = requestLine.split(" ");
            if (parts.length != 3) {
                return;
            }

            String method = parts[0];
            String path = parts[1];

            // Парсим заголовки запроса
            Map<String, String> headers = parseHeaders(in);

            // Чтение тела запроса (если есть)
            String body = "";
            if ("POST".equals(method)) {
                body = readRequestBody(in);
            }

            // Создаём объект запроса
            Request request = new Request(method, path, headers, body);

            // Ищем подходящий обработчик для метода и пути
            Handler handler = handlers.getOrDefault(method, new ConcurrentHashMap<>()).get(path);
            if (handler != null) {
                handler.handle(request, out);  // Вызываем обработчик
            } else {
                sendNotFoundResponse(out);  // Если обработчик не найден, возвращаем 404
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map<String, String> parseHeaders(BufferedReader in) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(": ");
            if (parts.length == 2) {
                headers.put(parts[0], parts[1]);
            }
        }
        return headers;
    }

    private String readRequestBody(BufferedReader in) throws IOException {
        StringBuilder body = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            body.append(line).append("\n");
        }
        return body.toString();
    }

    private void sendNotFoundResponse(BufferedOutputStream out) throws IOException {
        String response = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 0\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        out.write(response.getBytes());
        out.flush();
    }
}
