package com.obiscr.chatgpt;

import com.intellij.openapi.project.Project;
import com.obiscr.chatgpt.core.parser.OfficialParser;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.ui.MainPanel;
import com.obiscr.chatgpt.ui.MessageComponent;
import com.obiscr.chatgpt.util.StringUtil;
import okhttp3.*;
import okhttp3.internal.http2.StreamResetException;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @author wuzi
 */
public class ChatGPTHandler extends AbstractHandler {

    private Project myProject;

    private static final Logger LOG = LoggerFactory.getLogger(ChatGPTHandler.class);


    public EventSource handle(MainPanel mainPanel, MessageComponent component, String question) {
        myProject = mainPanel.getProject();
        RequestProvider provider = new RequestProvider().create(mainPanel, question);
        try {
            Request request = new Request.Builder()
                    .url(provider.getUrl())
                    .headers(Headers.of(provider.getHeader()))
                    .post(RequestBody.create(provider.getData().getBytes(StandardCharsets.UTF_8),
                            MediaType.parse("application/json")))
                    .build();
            OpenAISettingsState instance = OpenAISettingsState.getInstance();
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(Integer.parseInt(instance.connectionTimeout), TimeUnit.MILLISECONDS)
                    .readTimeout(Integer.parseInt(instance.readTimeout), TimeUnit.MILLISECONDS);
            builder.hostnameVerifier(getHostNameVerifier());
            builder.sslSocketFactory(getSslContext().getSocketFactory(), (X509TrustManager) getTrustAllManager());
            if (instance.enableProxy) {
                Proxy proxy = getProxy();
                builder.proxy(proxy);
            }
            if (instance.enableProxyAuth) {
                Authenticator proxyAuth = getProxyAuth();
                builder.proxyAuthenticator(proxyAuth);
            }
            OkHttpClient httpClient = builder.build();
            EventSourceListener listener = new EventSourceListener() {

                boolean handler = false;

                @Override
                public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                    LOG.info("ChatGPT: conversation open. Url = {}",eventSource.request().url());
                }

                @Override
                public void onClosed(@NotNull EventSource eventSource) {
                    LOG.info("ChatGPT: conversation close. Url = {}",eventSource.request().url());
                    if (!handler) {
                        component.setContent("Connection to remote server failed. There are usually several reasons for this:<br />1. Request too frequently, please try again later.<br />2. It may be necessary to set up a proxy to request.");
                    }
                    mainPanel.aroundRequest(false);
                    component.scrollToBottom();
                }

                @Override
                public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                    handler = true;
                    data = new String(data.getBytes(StandardCharsets.UTF_8));
                    if (StringUtil.isEmpty(data)) {
                        return;
                    }
                    if (data.contains("[DONE]")) {
                        return;
                    }
                    if (mainPanel.isChatGPTModel() && !data.contains("message")) {
                        return;
                    }
                    try {
                        OfficialParser.ParseResult parseResult;
                        if (mainPanel.isChatGPTModel()) {
                            parseResult = OfficialParser.
                                    parseChatGPT(myProject, component, data);
                        } else {
                            parseResult = OfficialParser.
                                    parseGPT35TurboWithStream(component, data);
                        }
                        if (parseResult == null) {
                            return;
                        }
                        // Copy action only needed source content
                        component.setSourceContent(parseResult.getSource());
                        component.setContent(parseResult.getHtml());
                    } catch (Exception e) {
                        LOG.error("ChatGPT: Parse response error, e={}, message={}", e, e.getMessage());
                        component.setContent(e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                    if (t != null) {
                        if (t instanceof StreamResetException) {
                            LOG.info("ChatGPT: Stop generating");
                            component.setContent("Stop generating");
                            t.printStackTrace();
                            return;
                        }
                        LOG.info("ChatGPT: conversation failure. Url={}, response={}, errorMessage={}",eventSource.request().url(), response, t.getMessage());
                        component.setContent("Response failure, cause: " + t.getMessage() + ", please try again. <br><br> Tips: if proxy is enabled, please check if the proxy server is working.");
                        t.printStackTrace();
                    } else {
                        String responseString = "";
                        if (response != null) {
                            try {
                                responseString = response.body().string();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        LOG.info("ChatGPT: conversation failure. Url={}, response={}",eventSource.request().url(), response);
                        component.setContent("Response failure, please try again. Error message: " + responseString);
                    }
                    mainPanel.aroundRequest(false);
                    component.scrollToBottom();
                }
            };
            EventSource.Factory factory = EventSources.createFactory(httpClient);
            return factory.newEventSource(request, listener);
        } catch (Exception e) {

        }
        return null;
    }
}
