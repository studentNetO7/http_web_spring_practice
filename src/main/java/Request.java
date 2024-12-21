import java.util.Map;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.NameValuePair;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.HashMap;


public class Request {
    private String method;
    private String path;
    private Map<String, String> headers;
    private String body;
    private Map<String, String> queryParams; // Добавляем поле для параметров запроса

    public Request(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.queryParams = parseQueryParams(path); // Инициализация параметров запроса при создании
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    static Map<String, String> parseQueryParams(String path) {
        Map<String, String> params = new HashMap<>();
        int queryIndex = path.indexOf('?');
        if (queryIndex != -1) {
            String query = path.substring(queryIndex + 1); // Получаем строку после "?"
            System.out.println("Query: " + query);  // Логируем строку запроса
            List<NameValuePair> pairs = URLEncodedUtils.parse(query, StandardCharsets.UTF_8); // Парсим строку
            for (NameValuePair pair : pairs) {
                System.out.println("Parsed parameter: " + pair.getName() + "=" + pair.getValue());  // Логируем каждый параметр
                params.put(pair.getName(), pair.getValue()); // Добавляем параметры в Map
            }
        }
        return params;
    }

    // Метод для получения одного параметра запроса
    public String getQueryParam(String name) {
        return queryParams.get(name); // Возвращаем значение параметра по имени
    }

    // Метод для получения всех параметров запроса
    public Map<String, String> getQueryParams() {
        return queryParams; // Возвращаем все параметры
    }
}

