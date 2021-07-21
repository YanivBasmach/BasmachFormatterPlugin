package com.yaniv.formatter.templates;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.intention.impl.TypeExpression;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
import com.intellij.codeInsight.template.postfix.templates.editable.JavaEditablePostfixTemplate;
import com.intellij.codeInsight.template.postfix.templates.editable.JavaPostfixTemplateExpressionCondition;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ThrowIfNullPostfixTemplate extends JavaEditablePostfixTemplate {
  public ThrowIfNullPostfixTemplate(@NotNull PostfixTemplateProvider provider) {
    super("thrnull", "if ($EXPR$ == null) { throw new $EXCEPTION$($END$); }", "if (myVar == null) { throw new InvalidParameterException(\"The provided value is null!\"); }", ImmutableSet.of(new JavaPostfixTemplateExpressionCondition.JavaPostfixTemplateNotPrimitiveTypeExpressionCondition()), LanguageLevel.JDK_1_3, false, provider);
  }

  @Override
  protected void addTemplateVariables(@NotNull PsiElement element, @NotNull Template template) {
    super.addTemplateVariables(element, template);
    PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
    if (method != null) {
      PsiTryStatement trySt = PsiTreeUtil.getParentOfType(element, PsiTryStatement.class);
      Set<PsiType> caught = trySt == null ? ImmutableSet.of() : Arrays.stream(trySt.getCatchBlockParameters()).map(PsiParameter::getType).collect(Collectors.toSet());
      Set<PsiType> throwsList = Arrays.stream(method.getThrowsTypes()).map(t->(PsiType)t).collect(Collectors.toSet());
      template.addVariable("EXCEPTION", new TypeExpression(element.getProject(), Sets.union(caught, throwsList)), true);
    }
  }

  @Override
  public boolean isBuiltin() {
    return true;
  }
}
