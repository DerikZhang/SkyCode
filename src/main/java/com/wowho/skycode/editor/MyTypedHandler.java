package com.wowho.skycode.editor;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

public class MyTypedHandler implements TypedActionHandler {
    protected int returnStartOffset = 0;
    protected int returnEndOffset = 0;

    @Override
    public void execute(@NotNull Editor editor, char charTyped, @NotNull DataContext dataContext) {
        Document document = editor.getDocument();
        Project project = editor.getProject();
        CaretModel caretModel = editor.getCaretModel();
        //每一次按键都在 Document 的开头插入 ”editor_basics“ 字符串
        Runnable runnable = () -> {
            if (charTyped == '\t' && returnStartOffset != 0) {
                // 处理 Tab 键的逻辑
            } else if (charTyped == '\u001B' || charTyped == '\b') {
                // 处理 ESC 键的逻辑 或 Back 键的逻辑
                undoLastAction(editor);
                returnStartOffset = 0;
                returnEndOffset = 0;
            } else if (caretModel.getVisualLineEnd() - 1 != caretModel.getOffset()
                    && returnStartOffset == 0) {
                document.insertString(caretModel.getOffset(), String.valueOf(charTyped));
                caretModel.moveToOffset(caretModel.getOffset()+1);
            } else {
//                int charTypeNum = (int) charTyped;
//                Messages.showInfoMessage(charTyped + "\n" + charTypeNum, "Input charType");
                CharSequence charSequence = document.getCharsSequence().subSequence(Math.max(caretModel.getOffset() - 10, 0), Math.min(caretModel.getOffset() + 10, document.getTextLength()));
                String completionCode = getCompletionCode(charSequence.toString());
                if (completionCode.length() > 0) {
                    document.insertString(caretModel.getOffset(), completionCode);
                    TextAttributes attributes = new TextAttributes();
                    attributes.setForegroundColor(JBColor.RED);
                    returnStartOffset = caretModel.getOffset();
                    returnEndOffset = caretModel.getOffset() + completionCode.length();
                    setTextColor(editor, returnStartOffset, returnEndOffset, attributes);
                }
            }

        };
        //写操作必须保证线程安全，所以使用 WriteCommandAction.runWriteCommandAction（）
        WriteCommandAction.runWriteCommandAction(project, runnable);
    }

    public static void undoLastAction(Editor editor) {
        CommandProcessor.getInstance().executeCommand(null, () -> {
            UndoManager.getInstance(editor.getProject()).undo(null);
        }, "Undo", null);
    }

    public void setTextColor(Editor editor, int startOffset, int endOffset, TextAttributes attributes) {
        MarkupModel markupModel = editor.getMarkupModel();
        RangeHighlighter highlighter = markupModel.addRangeHighlighter(
                startOffset, endOffset, HighlighterLayer.SELECTION,
                attributes, HighlighterTargetArea.EXACT_RANGE);
        // 可以根据需要设置其他属性，如错误条纹标记颜色
        highlighter.setErrorStripeMarkColor(attributes.getForegroundColor());
    }

    private String getCompletionCode(String text) {
//        Messages.showInfoMessage(text, "Completion code");
//        return text;
        return "gpt";
    }
}
