package com.wowho.godgpt.core;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.wowho.godgpt.settings.OpenAISettingsState;
import com.wowho.godgpt.util.StringUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class StartUpActivity implements StartupActivity.DumbAware {

    @Override
    public void runActivity(@NotNull Project project) {
        OpenAISettingsState state = OpenAISettingsState.getInstance();
        if (StringUtil.isEmpty(state.expireTime)) {
            return;
        }
        DateTime expire = DateUtil.parse(state.expireTime);
        DateTime date = DateUtil.date();
        long delta = DateUtil.betweenDay(expire, date,true);

        // Within one week before the Token expires, try to
        // refresh the Token every time you open the project
        if (delta < 7) {
            TokenManager.getInstance().refreshTokenAsync();
        }
    }
}
