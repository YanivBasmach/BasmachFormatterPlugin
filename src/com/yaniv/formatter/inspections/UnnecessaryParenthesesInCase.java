package com.yaniv.formatter.inspections;

import com.intellij.codeInspection.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class UnnecessaryParenthesesInCase extends AbstractBaseJavaLocalInspectionTool {
  
  @NotNull
  @Override
  public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new JavaElementVisitor() {
      @Override
      public void visitSwitchLabelStatement(PsiSwitchLabelStatement statement) {
        PsiExpression caseValue = statement.getCaseValue();
  
        if (caseValue instanceof PsiParenthesizedExpression) {
          holder.registerProblem(caseValue, "Basmach Standard: Don't use parenthesised " +
                  "expression in case value",
                  ProblemHighlightType.WEAK_WARNING, new RemoveParenthesesFix());
        }
      }
    };
  }
  
  private static class RemoveParenthesesFix implements LocalQuickFix {
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
      return "Remove parentheses from case";
    }
  
    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiExpression child = ((PsiParenthesizedExpression) descriptor.getPsiElement()).getExpression();
      
      if (child != null) {
        descriptor.getPsiElement().replace(child);
      }
    }
  }
}
