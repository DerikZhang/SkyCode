package com.obiscr.chatgpt.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.intellij.plugins.markdown.ui.preview.jcef.MarkdownJCEFHtmlPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Wuzi
 */
public class HttpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);
    private static final String LOGIN_URL = "https://chat.openai.com/api/auth/session";

    private static final String CONVERSATION_URL = "https://chat.openai.com/backend-api/conversation";

    public static String get(String urlString) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.setUseCaches(false);
        conn.setConnectTimeout(5000);
        // Set Headers
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("Authorization", "Bearer accessToken");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
        Map<String, List<String>> requestProperties = conn.getRequestProperties();

        // 连接并发送HTTP请求:
        conn.connect();

        // 判断HTTP响应是否200:
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Bad response");
        }
        // 获取响应内容:
        BufferedReader reader= new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String current;
        while((current = reader.readLine()) != null)
        {
            sb.append(current);
        }
        reader.close();
        return sb.toString();
    }

    /**
     * Get the answer
     * @param question Question String
     * @param accessToken Access Token
     * @throws Exception /
     */
    public static void sse(String question, String accessToken,
                           MarkdownJCEFHtmlPanel panel) throws Exception {
        String json = "{\n" + "\"action\": \"next\",\n" + "\"messages\": [\n" + "{\n" + "\"id\": \"" + UUID.randomUUID() + "\",\n" + "\"role\": \"user\",\n" + "\"content\": {\n" + "\"content_type\": \"text\",\n" + "\"parts\": [\n\"" + question + "\"]\n" + "}\n" + "}\n" + "],\n" + "\"parent_message_id\": \""+ UUID.randomUUID() +"\",\n" + "\"model\": \"text-davinci-002-render\"\n" + "}";
        JSONObject object = JSON.parseObject(json);

        Stack<String> stack =new Stack<>();

        // Create Pool
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        URL url = new URL(CONVERSATION_URL);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(10 * 1000);
        connection.setReadTimeout(10 * 1000);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        connection.setRequestProperty("Accept", "text/event-stream");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");

        // Write data
        connection.getOutputStream().write(object.toJSONString().getBytes());

        connection.connect();

        if (connection.getResponseCode() != 200) {
            System.out.println(connection.getResponseCode());
            throw new Exception("Failed to connect to SSE server");
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );
        executorService.submit(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    if ("data: [DONE]".equals(line)) {
                        String last = stack.peek();
                        LOG.info("Last content: {}", last);
                        stack.clear();
                        break;
                    } else if (!line.isEmpty()) {
                        stack.push(line);
                        String result = StringUtil.parse(line);
                        String html = HtmlUtil.md2html(result);
                        panel.setHtml(html, 0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        });

        executorService.shutdown();
        if (!executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}