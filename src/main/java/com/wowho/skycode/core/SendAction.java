package com.wowho.skycode.core;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.wowho.skycode.ChatGPTHandler;
import com.wowho.skycode.GPT35TurboHandler;
import com.wowho.skycode.message.ChatGPTBundle;
import com.wowho.skycode.settings.OpenAISettingsState;
import com.wowho.skycode.settings.SettingConfiguration;
import com.wowho.skycode.ui.MainPanel;
import com.wowho.skycode.ui.MessageComponent;
import com.wowho.skycode.ui.MessageGroupComponent;
import com.wowho.skycode.util.StringUtil;
import okhttp3.Call;
import okhttp3.sse.EventSource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

import static com.wowho.skycode.MyToolWindowFactory.ACTIVE_CONTENT;

/**
 * @author Wuzi
 */
public class SendAction extends AnAction {

    private static final Logger LOG = LoggerFactory.getLogger(SendAction.class);

    private String data;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Object mainPanel = project.getUserData(ACTIVE_CONTENT);
        doActionPerformed((MainPanel) mainPanel, data);
    }

    private boolean presetCheck(boolean isChatGPTModel) {
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        if (isChatGPTModel) {
            if (instance.urlType == SettingConfiguration.SettingURLType.OFFICIAL) {
                if (StringUtil.isEmpty(instance.accessToken)){
                    Notifications.Bus.notify(
                            new Notification(ChatGPTBundle.message("group.id"),
                                    "Wrong setting",
                                    "Please configure the access token or login in at first.\n" +
                                            "Open Setting/Preference - Tools - OpenAI - ChatGPT, and login.",
                                    NotificationType.ERROR));
                    return false;
                }
            } else if (instance.urlType == SettingConfiguration.SettingURLType.CUSTOMIZE) {
                if (StringUtil.isEmpty(instance.customizeUrl)) {
                    Notifications.Bus.notify(
                            new Notification(ChatGPTBundle.message("group.id"),
                                    "Wrong setting",
                                    "Please configure a Customize URL first.",
                                    NotificationType.ERROR));
                    return false;
                }
            }
        } else {
            if (StringUtil.isEmpty(instance.apiKey)) {
                Notifications.Bus.notify(
                        new Notification(ChatGPTBundle.message("group.id"),
                                "Wrong setting",
                                "Please configure a API Key first.",
                                NotificationType.ERROR));
                return false;
            }
        }
        return true;
    }

    public void doActionPerformed(MainPanel mainPanel, String data) {
        // Filter the empty text
        if (StringUtils.isEmpty(data)) {
            return;
        }

        // Check the configuration first
        if (!presetCheck(mainPanel.isChatGPTModel())) {
            return;
        }

        // Reset the question container
        mainPanel.getSearchTextArea().getTextArea().setText("");
        mainPanel.aroundRequest(true);
        Project project = mainPanel.getProject();
        MessageGroupComponent contentPanel = mainPanel.getContentPanel();

        // Add the message component to container
        MessageComponent question = new MessageComponent(data,true);
        MessageComponent answer = new MessageComponent("Waiting for response...",false);
        contentPanel.add(question);
        contentPanel.add(answer);

        try {
            ExecutorService executorService = mainPanel.getExecutorService();
            // Request the server.
            if (!mainPanel.isChatGPTModel() && !OpenAISettingsState.getInstance().enableGPT35StreamResponse) {
                GPT35TurboHandler gpt35TurboHandler = project.getService(GPT35TurboHandler.class);
                executorService.submit(() -> {
                    Call handle = gpt35TurboHandler.handle(mainPanel, answer, data);
                    mainPanel.setRequestHolder(handle);
                    contentPanel.updateLayout();
                    contentPanel.scrollToBottom();
                });
            } else {
                ChatGPTHandler chatGPTHandler = project.getService(ChatGPTHandler.class);
                executorService.submit(() -> {
                    EventSource handle = chatGPTHandler.handle(mainPanel, answer, data);
                    mainPanel.setRequestHolder(handle);
                    contentPanel.updateLayout();
                    contentPanel.scrollToBottom();
                });
            }
        } catch (Exception e) {
            answer.setSourceContent(e.getMessage());
            answer.setContent(e.getMessage());
            mainPanel.aroundRequest(false);
            LOG.error("ChatGPT: Request failed, error={}", e.getMessage());
        }
    }
}
