package com.yaniv.bsmchformat;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.PredefinedCodeStyle;

public class MyCodeStyle extends PredefinedCodeStyle {
  public MyCodeStyle() {
    super("Basmach Standards", JavaLanguage.INSTANCE);
  }

  @Override
  public void apply(CodeStyleSettings settings) {
    settings.getIndentOptions().INDENT_SIZE = 2;
    settings.getLanguageIndentOptions(JavaLanguage.INSTANCE).INDENT_SIZE = 2;
    settings.WRAP_WHEN_TYPING_REACHES_RIGHT_MARGIN = true;
  }
}
