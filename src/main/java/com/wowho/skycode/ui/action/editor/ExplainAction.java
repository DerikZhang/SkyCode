package com.wowho.skycode.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.wowho.skycode.message.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class ExplainAction extends AbstractEditorAction {

    public ExplainAction() {
        super(() -> ChatGPTBundle.message("action.code.explain.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = "Explain this code:";
        super.actionPerformed(e);
    }

}
