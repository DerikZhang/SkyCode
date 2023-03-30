package com.wowho.godgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.wowho.godgpt.message.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class BugAction extends AbstractEditorAction {

    public BugAction() {
        super(() -> ChatGPTBundle.message("action.code.wrong.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = "Find the bug in the code below:";
        super.actionPerformed(e);
    }

}
