package com.wowho.skycode;

import com.intellij.openapi.project.Project;
import com.wowho.skycode.ui.MainPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


/**
 * @author Wuzi
 */
public class ChatGPTToolWindow {

  private final MainPanel panel;

  public ChatGPTToolWindow(@NotNull Project project) {
    panel = new MainPanel(project, true);
  }

  public JPanel getContent() {
    return panel.init();
  }

  public MainPanel getPanel() {
    return panel;
  }
}
