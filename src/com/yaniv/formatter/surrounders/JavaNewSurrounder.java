package com.yaniv.formatter.surrounders;

import com.intellij.codeInsight.generation.surroundWith.JavaExpressionSurrounder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.refactoring.introduceField.ElementToWorkOn;
import com.intellij.util.IncorrectOperationException;

public class JavaNewSurrounder extends JavaExpressionSurrounder {

  @Override
  public boolean isApplicable(PsiExpression psiExpression) {
    return !(psiExpression.getType() instanceof PsiPrimitiveType);
  }

  @Override
  public TextRange surroundExpression(Project project, Editor editor, PsiExpression expr) throws IncorrectOperationException {
    String exprText = expr.getText();
    Template template = generateTemplate(project, exprText, expr.getType());
    TextRange range;
    if (expr.isPhysical()) {
      range = expr.getTextRange();
    } else {
      RangeMarker rangeMarker = expr.getUserData(ElementToWorkOn.TEXT_RANGE);
      if (rangeMarker == null) {
        return null;
      }

      range = new TextRange(rangeMarker.getStartOffset(), rangeMarker.getEndOffset());
    }

    editor.getDocument().deleteString(range.getStartOffset(), range.getEndOffset());
    editor.getCaretModel().moveToOffset(range.getStartOffset());
    editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
    TemplateManager.getInstance(project).startTemplate(editor, template);
    return null;
  }

  private static Template generateTemplate(Project project, String exprText, PsiType type) {
    TemplateManager templateManager = TemplateManager.getInstance(project);
    Template template = templateManager.createTemplate("", "");
    template.setToReformat(true);

    template.addTextSegment("new " + type.getCanonicalText() + "(" + exprText + ")");
    template.addEndVariable();
    return template;
  }

  @Override
  public String getTemplateDescription() {
    return "Surround with new";
  }
}
