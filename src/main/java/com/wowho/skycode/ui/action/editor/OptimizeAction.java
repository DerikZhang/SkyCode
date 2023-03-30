package com.wowho.skycode.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.wowho.skycode.message.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class OptimizeAction extends AbstractEditorAction {

    public OptimizeAction() {
        super(() -> ChatGPTBundle.message("action.code.optimize.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = "Optimize this code:";
        super.actionPerformed(e);
    }

}
