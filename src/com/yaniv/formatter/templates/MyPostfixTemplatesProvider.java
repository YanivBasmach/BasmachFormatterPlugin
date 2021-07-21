package com.yaniv.formatter.templates;

import com.intellij.codeInsight.template.postfix.templates.JavaPostfixTemplateProvider;
import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MyPostfixTemplatesProvider extends JavaPostfixTemplateProvider {

  private Set<PostfixTemplate> templates = ContainerUtil.newHashSet(
          new CopyCtorPostfixTemplate(this),
          new ThrowIfNegativePostfixTemplate(this),
          new ThrowIfNullPostfixTemplate(this)
  );

  @NotNull
  @Override
  public Set<PostfixTemplate> getTemplates() {
    return templates;
  }

}
