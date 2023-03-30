package com.wowho.godgpt.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.wowho.godgpt.message.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class PluginAction extends DumbAwareAction {

    public PluginAction() {
        super(() -> ChatGPTBundle.message("action.plugins"), AllIcons.Nodes.Plugin);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        BrowserUtil.browse("https://plugins.jetbrains.com/organizations/obiscr");
    }
}
