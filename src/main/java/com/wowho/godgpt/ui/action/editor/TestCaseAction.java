package com.wowho.godgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.wowho.godgpt.message.ChatGPTBundle;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class TestCaseAction extends AbstractEditorAction {

    public TestCaseAction() {
        super(() -> ChatGPTBundle.message("action.code.test.menu"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        key = "Add test case for code:";
        super.actionPerformed(e);
    }

}
