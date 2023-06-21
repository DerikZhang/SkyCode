package com.wowho.skycode.editor;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class EditorHandlerIllustration extends AnAction {

    //静态代码将在系统启动时执行
    static {
        //获取 actionManager
        EditorActionManager actionManager = EditorActionManager.getInstance();
        TypedAction typedAction = actionManager.getTypedAction();
        // 注册TypedActionHandler
        typedAction.setupHandler(new MyTypedHandler());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        EditorActionManager actionManager = EditorActionManager.getInstance();
        EditorActionHandler actionHandler =
                actionManager.getActionHandler(IdeActions.ACTION_EDITOR_CLONE_CARET_BELOW);
        //克隆文本插入符(caret)
//        actionHandler.execute(editor,
//                editor.getCaretModel().getPrimaryCaret(), event.getDataContext());
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = event.getData(CommonDataKeys.EDITOR);

        // 确定是否至少存在一个文本插入符(caret)
        boolean menuAllowed = false;
        if (editor != null && project != null) {
            // 确定 文本插入符(caret) 数组不为空
            menuAllowed = !editor.getCaretModel().getAllCarets().isEmpty();
        }
        event.getPresentation().setEnabledAndVisible(menuAllowed);
    }

}
