import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {

    private static final int PORT = 9999;
    private static final int THREAD_POOL_SIZE = 64;
    private final ExecutorService threadPool;
    private final ConcurrentMap<String, ConcurrentMap<String, Handler>> handlers;

    public Server() {
        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        handlers = new ConcurrentHashMap<>();
    }

    // Метод для добавления обработчиков
    public void addHandler(String method, String path, Handler handler) {
        handlers
                .computeIfAbsent(method, k -> new ConcurrentHashMap<>())
                .put(path, handler);
    }

    public void listen(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();  // Ожидание подключения клиента
                System.out.println("New connection from " + clientSocket.getInetAddress());  // Вывод о подключении клиента
                threadPool.submit(new ConnectionHandler(clientSocket, handlers));  // Обработка запроса клиента в пуле потоков
            }
        } catch (IOException e) {
            System.err.println("Error while starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
