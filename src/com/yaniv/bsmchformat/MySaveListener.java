package com.yaniv.bsmchformat;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.PsiErrorElementUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class MySaveListener implements FileDocumentManagerListener {

  @Override
  public void beforeDocumentSaving(@NotNull Document document) {
    Optional<Pair<Project,PsiFile>> file = Arrays.stream(ProjectManager.getInstance().getOpenProjects())
            .map(p -> Pair.create(p,PsiDocumentManager.getInstance(p).getPsiFile(document)))
            .filter(p -> p.getSecond() != null)
            .findAny();

    if (file.isPresent() && !PsiErrorElementUtil.hasErrors(file.get().getFirst(),file.get().getSecond().getVirtualFile())) {
      System.out.println("Reformatting code...");
      new ReformatCodeProcessor(file.get().getFirst(), file.get().getSecond(), null, false).run();
    }
  }
}
