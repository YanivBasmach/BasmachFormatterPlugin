package com.yaniv.formatter.templates;

import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateWithExpressionSelector;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplatesUtils;
import com.intellij.codeInsight.template.postfix.util.JavaPostfixTemplatesUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.yaniv.formatter.surrounders.JavaNewSurrounder;
import org.jetbrains.annotations.NotNull;

public class CopyCtorPostfixTemplate extends PostfixTemplateWithExpressionSelector {
  public CopyCtorPostfixTemplate(@NotNull PostfixTemplateProvider provider) {
    super("copy","copy","new MyObject(value)",
            JavaPostfixTemplatesUtils.selectorAllExpressionsWithCurrentOffset(JavaPostfixTemplatesUtils.IS_NON_VOID), provider);
  }

  @Override
  protected void expandForChooseExpression(@NotNull PsiElement psiElement, @NotNull Editor editor) {
    PostfixTemplatesUtils.surround(new JavaNewSurrounder(),editor, psiElement);
  }

}
