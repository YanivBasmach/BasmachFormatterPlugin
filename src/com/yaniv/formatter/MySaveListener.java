package com.yaniv.formatter;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.PsiErrorElementUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

public class MySaveListener implements FileDocumentManagerListener {
  
  @Override
  public void beforeDocumentSaving(@NotNull Document document) {
    Optional<Pair<Project, PsiFile>> file =
            Arrays.stream(ProjectManager.getInstance().getOpenProjects())
                    .map(p -> Pair.create(p, PsiDocumentManager.getInstance(p).getPsiFile(document)))
                    .filter(p -> p.getSecond() != null)
                    .findAny();
    
    if (file.isPresent() && !PsiErrorElementUtil.hasErrors(file.get().getFirst(),
            file.get().getSecond().getVirtualFile()) && document.isWritable()) {
      ApplicationManager.getApplication().invokeLater(()->{
        new ReformatCodeProcessor(file.get().getFirst(), file.get().getSecond(), null, false).run();
      });
    }
  }
  
  @Override
  public void beforeAllDocumentsSaving() {
  
  }
  
  @Override
  public void beforeFileContentReload(@NotNull VirtualFile file, @NotNull Document document) {
  
  }
  
  @Override
  public void fileContentLoaded(@NotNull VirtualFile file, @NotNull Document document) {
  
  }
  
  @Override
  public void fileContentReloaded(@NotNull VirtualFile file, @NotNull Document document) {
  
  }
  
  @Override
  public void fileWithNoDocumentChanged(@NotNull VirtualFile file) {
  
  }
  
  @Override
  public void unsavedDocumentsDropped() {
  
  }
}
