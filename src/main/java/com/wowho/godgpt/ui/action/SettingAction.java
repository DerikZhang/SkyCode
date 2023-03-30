package com.wowho.godgpt.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.wowho.godgpt.settings.OpenAISettingsPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class SettingAction extends DumbAwareAction {

  public SettingAction(@NotNull @Nls String text) {
    super(() -> text,AllIcons.General.Settings);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), OpenAISettingsPanel.class);
  }
}
