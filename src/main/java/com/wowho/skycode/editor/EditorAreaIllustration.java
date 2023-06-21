package com.wowho.skycode.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class EditorAreaIllustration extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        //获取 编辑器 对象
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        //获取 文本插入符(caret) 对象
        CaretModel caretModel = editor.getCaretModel();

        // 获取 primary 文本插入符对象
        Caret primaryCaret = caretModel.getPrimaryCaret();
        //从 文本插入符(caret) 对象 中获取相关信息
        //逻辑位置 详见下文
        LogicalPosition logicalPos = primaryCaret.getLogicalPosition();
        //视觉位置 详见下文
        VisualPosition visualPos = primaryCaret.getVisualPosition();
        int caretOffset = primaryCaret.getOffset();

        // 拼接并显示位置信息
        String report = logicalPos.toString() + "\n" +
                visualPos.toString() + "\n" +
                "Offset: " + caretOffset;
        Messages.showInfoMessage(report, "Caret Parameters Inside The Editor");
    }
}
