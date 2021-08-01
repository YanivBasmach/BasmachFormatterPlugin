package com.yaniv.formatter.inspections;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.codeInspection.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmptyLinesInspector extends AbstractBaseJavaLocalInspectionTool {
  
  @Override
  public boolean runForWholeFile() {
    return true;
  }
  
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {
      
      @Override
      public void visitIfStatement(PsiIfStatement statement) {
        if (!(statement.getParent() instanceof PsiIfStatement)) {
          checkEmptyLines(statement, statement.getPrevSibling(), statement.getPrevSibling().getPrevSibling());
          checkEmptyLines(statement, statement.getNextSibling(), statement.getNextSibling().getNextSibling());
        }
      }
      
      @Override
      public void visitWhileStatement(PsiWhileStatement statement) {
        checkEmptyLines(statement, statement.getPrevSibling(), statement.getPrevSibling().getPrevSibling());
        checkEmptyLines(statement, statement.getNextSibling(), statement.getNextSibling().getNextSibling());
      }
      
      @Override
      public void visitDoWhileStatement(PsiDoWhileStatement statement) {
        checkEmptyLines(statement, statement.getPrevSibling(), statement.getPrevSibling().getPrevSibling());
        checkEmptyLines(statement, statement.getNextSibling(), statement.getNextSibling().getNextSibling());
      }
      
      @Override
      public void visitForStatement(PsiForStatement statement) {
        checkEmptyLines(statement, statement.getPrevSibling(), statement.getPrevSibling().getPrevSibling());
        checkEmptyLines(statement, statement.getNextSibling(), statement.getNextSibling().getNextSibling());
      }
      
      @Override
      public void visitForeachStatement(PsiForeachStatement statement) {
        checkEmptyLines(statement, statement.getPrevSibling(), statement.getPrevSibling().getPrevSibling());
        checkEmptyLines(statement, statement.getNextSibling(), statement.getNextSibling().getNextSibling());
      }
      
      @Override
      public void visitReturnStatement(PsiReturnStatement statement) {
        checkEmptyLines(statement, statement.getPrevSibling(), statement.getPrevSibling().getPrevSibling());
      }
      
      @Override
      public void visitSwitchStatement(PsiSwitchStatement statement) {
        checkEmptyLines(statement, statement.getPrevSibling(), statement.getPrevSibling().getPrevSibling());
        checkEmptyLines(statement, statement.getNextSibling(), statement.getNextSibling().getNextSibling());
      }
      
      @Override
      public void visitBreakStatement(PsiBreakStatement statement) {
        checkEmptyLines(statement, statement.getPrevSibling(), statement.getPrevSibling().getPrevSibling());
      }
      
      private void checkEmptyLines(PsiStatement statement, PsiElement possibleSpace,
                                   PsiElement possibleBraceOrComment) {
        if (possibleSpace instanceof PsiWhiteSpace) {
          int lc = newLineCount(possibleSpace.getText());
          
          if (possibleBraceOrComment != null && (possibleBraceOrComment.getNode().getElementType() == JavaTokenType.LBRACE || possibleBraceOrComment.getNode().getElementType() == JavaTokenType.RBRACE || possibleBraceOrComment.getNode().getElementType() == JavaTokenType.COLON || possibleBraceOrComment instanceof PsiComment)) {
            if (lc > 1) {
              holder.registerProblem(statement.getFirstChild(), "Standards: No empty lines before" +
                              " and after a control statement when " +
                              "it's after a comment or at the start of a block!",
                      ProblemHighlightType.WARNING, new SetBlankLinesFix("Remove blank lines", "\n",
                              possibleSpace));
            }
          } else if (lc != 2) {
            holder.registerProblem(statement.getFirstChild(), "Standards: One blank line is " +
                    "needed before/after a " +
                    "control statement", new SetBlankLinesFix("Insert 1 blank lines", "\n\n",
                    possibleSpace));
          }
        }
      }
    };
  }
  
  private int newLineCount(String text) {
    final Matcher MATCHER = Pattern.compile("\n").matcher(text);
    int count = 0;
    
    while (MATCHER.find()) {
      count++;
    }
    
    return count;
  }
  
  private static class SetBlankLinesFix implements LocalQuickFix {
    private final String message;
    private final String blankLines;
    private SmartPsiElementPointer<PsiElement> whitespace;
  
    public SetBlankLinesFix(String message, String blankLines, PsiElement whitespace) {
      this.message = message;
      this.blankLines = blankLines;
      this.whitespace = SmartPointerManager.createPointer(whitespace);
    }
    
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
      return "Standards";
    }
  
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
      return message;
    }
  
    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      ((CompositeElement) whitespace.getElement().getParent()).replaceChild(whitespace.getElement().getNode(),
              new LeafPsiElement(TokenType.WHITE_SPACE,
              blankLines));
      ApplicationManager.getApplication().invokeLater(() -> {
        new ReformatCodeProcessor(project, descriptor.getPsiElement().getContainingFile(), null,
                false).run();
      });
    }
  }
}
