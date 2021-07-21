package com.yaniv.formatter.generators;

import com.intellij.codeInsight.generation.ConstructorBodyGenerator;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiParameter;
import org.jetbrains.annotations.NotNull;

public class MyConstructorGenerator implements ConstructorBodyGenerator {
  @Override
  public void generateFieldInitialization(@NotNull StringBuilder stringBuilder, @NotNull PsiField[] psiFields, @NotNull PsiParameter[] psiParameters) {
    for (int i = 0; i < psiFields.length; i++) {
      PsiField f = psiFields[i];
      PsiParameter p = psiParameters[i];
      stringBuilder.append("this.change");
      stringBuilder.append(StringUtil.capitalize(f.getName()));
      stringBuilder.append("(");
      stringBuilder.append(p.getName());
      stringBuilder.append(");\n");
    }
  }

  @Override
  public void generateSuperCallIfNeeded(@NotNull StringBuilder stringBuilder, @NotNull PsiParameter[] psiParameters) {
    if (psiParameters.length > 0) {
      stringBuilder.append("super(");

      for(int j = 0; j < psiParameters.length; ++j) {
        PsiParameter param = psiParameters[j];
        stringBuilder.append(param.getName());
        if (j < psiParameters.length - 1) {
          stringBuilder.append(",");
        }
      }

      stringBuilder.append(");\n");
    }
  }

  @Override
  public StringBuilder start(StringBuilder stringBuilder, @NotNull String s, @NotNull PsiParameter[] psiParameters) {
    stringBuilder.append("public ").append(s).append("(");

    for (PsiParameter parameter : psiParameters) {
      stringBuilder.append(parameter.getType().getPresentableText()).append(' ').append(parameter.getName()).append(',');
    }

    if (psiParameters.length > 0) {
      stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
    }

    stringBuilder.append("){\n");
    return stringBuilder;
  }

  @Override
  public void finish(StringBuilder stringBuilder) {
    stringBuilder.append("}");
  }


}
