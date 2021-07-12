package com.yaniv.bsmchformat;

import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInspection.IntentionAndQuickFixAction;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.ide.DataManager;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.SharedPsiElementImplUtil;
import com.intellij.psi.impl.source.tree.*;
import com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import com.intellij.psi.util.PsiUtil;
import com.intellij.refactoring.openapi.impl.JavaRenameRefactoringImpl;
import com.intellij.refactoring.rename.RenameHandler;
import com.intellij.refactoring.rename.RenameHandlerRegistry;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
    if (psiElement instanceof PsiSwitchLabelStatement) {
      PsiExpression caseValue = ((PsiSwitchLabelStatement) psiElement).getCaseValue();

      if (caseValue instanceof PsiParenthesizedExpression) {
        warnWithFix(annotationHolder, caseValue.getNode(),"Basmach Standard: Don't use parenthesised expression in case value", "Remove parentheses from case",(file)->{
          PsiElement child = ((PsiParenthesizedExpression) caseValue).getExpression();
          if (child != null) {
            caseValue.replace(child);
          }
        });
      }
    }

    if (psiElement.getNode().getElementType() == TokenType.WHITE_SPACE) {
      PsiElement after = psiElement.getNextSibling();
      if (after instanceof PsiIfStatement || after instanceof PsiReturnStatement || after instanceof PsiWhileStatement || after instanceof PsiForStatement || after instanceof PsiForeachStatement || after instanceof PsiSwitchStatement) {
        int lc = newLineCount(psiElement.getText());
        if (psiElement.getPrevSibling().getNode().getElementType() == JavaTokenType.LBRACE) {
          if (lc > 1) {
            warnWithFix(annotationHolder, psiElement.getNode().getTreeNext().getFirstChildNode(), "Basmach Standard: No empty lines before a control statement at the start of a code block", "Remove empty lines", (file)->{
              ((CompositeElement) psiElement.getParent()).replaceChild(psiElement.getNode(),new LeafPsiElement(TokenType.WHITE_SPACE,"\n"));
              ApplicationManager.getApplication().invokeLater(()->{
                new ReformatCodeProcessor(psiElement.getProject(), file, null,false).run();
              });
            });
          }
        } else {
          if (lc != 2) {
            warnWithFix(annotationHolder,psiElement.getNode().getTreeNext().getFirstChildNode(),"Basmach Standard: One empty line before a control statement", "Insert 1 empty line", (file)->{
              ((CompositeElement) psiElement.getParent()).replaceChild(psiElement.getNode(),new LeafPsiElement(TokenType.WHITE_SPACE,"\n\n"));
              ApplicationManager.getApplication().invokeLater(()->{
                new ReformatCodeProcessor(psiElement.getProject(), file, null,false).run();
              });
            });
          }
        }
      }
    }

    if (psiElement instanceof PsiVariable) {
      String name = ((PsiVariable) psiElement).getName();
      if (isConstant((PsiVariable) psiElement)) {
        if (!isUpperSnakeCase(name)) {
          warnWithFix(annotationHolder,((PsiVariable) psiElement).getNameIdentifier().getNode(), "Basmach Standard: Final variable names must be UPPER_SNAKE_CASE", "Convert to UPPER_SNAKE_CASE", (file)->{
            doRename(psiElement, toUpperSnakeCase(name));
          });
        }
      } else {
        if (!isLowerCamelCase(name)) {
          warnWithFix(annotationHolder,((PsiVariable) psiElement).getNameIdentifier().getNode(),"Basmach Standard: Variable names must be lowerCamelCase","Convert to lowerCamelCase",(file)->{
            doRename(psiElement, toLowerCamelCase(name));
          });
        }
      }

      if (hasDigits(name)) {
        warnWithFix(annotationHolder,((PsiVariable) psiElement).getNameIdentifier().getNode(),"Basmach Standard: Variable names must not contain numbers","Replace numbers with their name",(file)->{
          String namedNumbers = nameNumbers(name);
          if (isConstant(((PsiVariable) psiElement)) && isUpperSnakeCase(name)) {
            namedNumbers = toUpperSnakeCase(namedNumbers);
          }
          doRename(psiElement,namedNumbers);
        });
      }
    }

    if (psiElement instanceof PsiMethod) {
      String name = ((PsiMethod) psiElement).getName();
      if (!name.equals("<unnamed>") && !isLowerCamelCase(name)) {
        warnWithFix(annotationHolder,((PsiMethod) psiElement).getNameIdentifier().getNode(), "Basmach Standard: Method names must be lowerCamelCase", "Convert to lowerCamelCase", (file)->{
          doRename(psiElement,toLowerCamelCase(name));
        });
      }
    }

    if (psiElement instanceof PsiClass) {
      String name = ((PsiClass) psiElement).getQualifiedName();
      if (!isUpperCamelCase(name)) {
        warnWithFix(annotationHolder,((PsiClass) psiElement).getNameIdentifier().getNode(),"Basmach Standard: Method names must be UpperCamelCase","Convert to UpperCamelCase",(file)->{
          doRename(psiElement,toUpperCamelCase(name));
        });
      }
    }

    if (psiElement instanceof PsiLiteral) {
      if (!(psiElement.getParent() instanceof PsiField) && ((PsiLiteral) psiElement).getValue() instanceof Integer) {
        Annotation a = annotationHolder.createWeakWarningAnnotation(psiElement, "Basmach Standard: Use a constant field for literal values");
        a.registerFix(new IntentionAndQuickFixAction() {
          @NotNull
          @Override
          public String getName() {
            return "Introduce constant field";
          }

          @NotNull
          @Override
          public String getFamilyName() {
            return "Standards";
          }

          @Override
          public void applyFix(@NotNull Project project, PsiFile psiFile, @Nullable Editor editor) {
            PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
            int value = (int) ((PsiLiteral) psiElement).getValue();
            String name = toUpperSnakeCase(nameNumber(value, false));
            PsiClass cls = PsiUtil.getTopLevelClass(psiElement);
            if (cls != null) {
              PsiField field = factory.createField(name, PsiType.INT);
              field.setInitializer(factory.createExpressionFromText("" + value, null));
              field.getModifierList().setModifierProperty("static",true);
              field.getModifierList().setModifierProperty("final", true);
              PsiExpression element = factory.createExpressionFromText(name, field);
              psiElement.replace(element);
              cls.add(field);

              ApplicationManager.getApplication().invokeLater(()->{
                DataContext context = DataManager.getInstance().getDataContext(editor.getComponent());
                RenameHandler renameHandler = RenameHandlerRegistry.getInstance().getRenameHandler(context);
                renameHandler.invoke(project, editor, psiFile, context);
              });

            }
          }
        });
      }
    }

    if (psiElement instanceof PsiMethod) {
      PsiReturnStatement[] statements = PsiUtil.findReturnStatements(((PsiMethod) psiElement));
      if (statements.length > 2) {
        for (PsiReturnStatement s : statements) {
          annotationHolder.createWeakWarningAnnotation(s,"Basmach Standards: Try avoiding multiple return statements in a method");
        }
      }
    }

  }

  private int newLineCount(String text) {
    final Matcher matcher = Pattern.compile("\n").matcher(text);
    int count = 0;
    while (matcher.find()) {
      count++;
    }
    return count;
  }

  private void doRename(PsiElement psiElement, String name) {
    ApplicationManager.getApplication().invokeLater(()->{
      new JavaRenameRefactoringImpl(psiElement.getProject(),psiElement,name,false,false).run();
    });
  }

  private void warnWithFix(AnnotationHolder holder, ASTNode node, String message, String fixMessage, Consumer<PsiFile> fixer) {
    Annotation a = holder.createWarningAnnotation(node,message);
    a.registerFix(new IntentionAndQuickFixAction() {
      @NotNull
      @Override
      public String getName() {
        return fixMessage;
      }

      @NotNull
      @Override
      public String getFamilyName() {
        return "Standards";
      }

      @Override
      public void applyFix(@NotNull Project project, PsiFile psiFile, @Nullable Editor editor) {
        fixer.accept(psiFile);
      }
    });
  }

  private boolean isConstant(PsiVariable variable) {
    return variable.hasModifier(JvmModifier.FINAL);
  }

  private String nameNumbers(String name) {
    Matcher m = Pattern.compile("\\d+").matcher(name);
    StringBuffer b = new StringBuffer();
    while (m.find()) {
      String numberName = nameNumber(Integer.parseInt(m.group()),false);
      m.appendReplacement(b,numberName);
    }
    return b.toString();
  }

  private static final String[] namesToTwenty = {"Zero","One","Two","Three","Four","Five","Six","Seven","Eight","Nine","Ten",
          "Eleven","Twelve","Thirteen","Fourteen","Fifteen","Sixteen","Seventeen","Eighteen","Nineteen"};

  private static final String[] tenMultipleNames = {"Zero","Ten","Twenty","Thirty","Forty","Fifty","Sixty","Seventy","Eighty","Ninety"};

  private String nameNumber(int number, boolean zeroBlank) {
    if (number == 0 && zeroBlank) return "";
    if (number < 20) {
      return namesToTwenty[number];
    } else if (number < 100) {
      return tenMultipleNames[number / 10] + nameNumber(number % 10,true);
    } else if (number < 1000) {
      int h = number / 100;
      return namesToTwenty[h] + "Hundred" + nameNumber(number % 100, true);
    } else if (number < 10000) {
      int t = number / 1000;
      return namesToTwenty[t] + "Thousand" + nameNumber(number % 1000,true);
    }
    return "";
  }

  private boolean hasDigits(String name) {
    for (int i = 0; i < name.length(); i++) {
      if (Character.isDigit(name.charAt(i))) return true;
    }
    return false;
  }


  private boolean isLowerCamelCase(String name) {
    return name.matches("[a-z][a-zA-Z0-9]*");
  }

  private String toLowerCamelCase(String name) {
    String newName = "";
    boolean nextUpper = false;
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (i == 0) {
        if (Character.isLetter(c)) {
          newName += Character.toLowerCase(c);
        }
      } else {
        if (Character.isLetterOrDigit(c)) {
          if (nextUpper) {
            newName += Character.toUpperCase(c);
          } else {
            newName += c;
          }
          nextUpper = false;
        } else if (c == '_') {
          nextUpper = true;
        }
      }
    }
    return newName;
  }

  private boolean isUpperCamelCase(String name) {
    return name.matches("[A-Z][a-zA-Z0-9]*");
  }

  private String toUpperCamelCase(String name) {
    String newName = "";
    boolean nextUpper = false;
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (i == 0) {
        if (Character.isLetter(c)) {
          newName += Character.toUpperCase(c);
        }
      } else {
        if (Character.isLetterOrDigit(c)) {
          if (nextUpper) {
            newName += Character.toUpperCase(c);
          } else {
            newName += c;
          }
          nextUpper = false;
        } else if (c == '_') {
          nextUpper = true;
        }
      }
    }
    return newName;
  }

  private boolean isUpperSnakeCase(String name) {
    return name.matches("[A-Z0-9_]+");
  }

  private String toUpperSnakeCase(String name) {
    String newName = "";
    boolean prevWasUS = false;
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (Character.isLetter(c) && Character.isUpperCase(c) && i > 0 && !prevWasUS) {
        newName += "_";
      }
      if (Character.isLetterOrDigit(c) || c == '_') {
        newName += Character.toUpperCase(c);
      }
      prevWasUS = c == '_';
    }
    return newName;
  }

}
