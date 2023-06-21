package com.wowho.skycode.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class EditorIllustrationAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 获取修改所必须的对象
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        Document document = editor.getDocument();

        // 从文本插入符(caret) 中获取已选择文本的相关信息
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();

        // 替换字符串
        // 必须在write action 里执行.
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(start, end, "editor_basics")
        );
        // 因为已选择文本已被替换，所以移除它的选择状态
        primaryCaret.removeSelection();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 获取 Project对象
        Project project = e.getProject();
        //获取编辑器实例
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        // 当存在 Project对象 、编辑器实例，并选中文本的时候，启用该Action
        e.getPresentation().setEnabledAndVisible(project != null && editor != null && editor.getSelectionModel().hasSelection());
    }
}
