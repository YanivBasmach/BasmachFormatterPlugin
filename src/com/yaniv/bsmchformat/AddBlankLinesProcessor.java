package com.yaniv.bsmchformat;

import com.intellij.codeInsight.actions.AbstractLayoutCodeProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.FutureTask;

public class AddBlankLinesProcessor extends AbstractLayoutCodeProcessor {
  public AddBlankLinesProcessor(Project project, PsiFile file) {
    super(project,"process.add_blank_lines","process.add_blank_lines.progress", false);
  }

  @NotNull
  @Override
  protected FutureTask<Boolean> prepareTask(@NotNull PsiFile psiFile, boolean b) throws IncorrectOperationException {
    return null;
  }
}
