package com.yaniv.bsmchformat;

import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import com.intellij.psi.codeStyle.JavaCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MyCodeStyleSettingsProvider extends CodeStyleSettingsProvider {

  @Nullable
  @Override
  public Language getLanguage() {
    return JavaLanguage.INSTANCE;
  }

  @Nullable
  @Override
  public String getConfigurableDisplayName() {
    return "Standards Code Style";
  }

  @Nullable
  @Override
  public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
    JavaCodeStyleSettings styleSettings = new JavaCodeStyleSettings(settings);

    return styleSettings;
  }
}
