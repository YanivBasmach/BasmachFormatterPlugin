package com.yaniv.formatter;

import com.intellij.codeInsight.CodeInsightWorkspaceSettings;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.codeStyle.*;

public class CodeStyleSettingsInstaller implements ProjectComponent {

  private Project project;

  public CodeStyleSettingsInstaller(Project project) {
    this.project = project;
  }

  @Override
  public void projectOpened() {
    System.out.println("Applying code style settings according to the Basmach Standards...");
    CodeInsightWorkspaceSettings.getInstance(project).optimizeImportsOnTheFly = true;
    JavaCodeStyleSettings javaSettings = JavaCodeStyleSettings.getInstance(project);
    CodeStyleSettings container = javaSettings.getContainer();

    container.WRAP_WHEN_TYPING_REACHES_RIGHT_MARGIN = true;

    CommonCodeStyleSettings settings = container.getCommonSettings(JavaLanguage.INSTANCE);
    settings.getIndentOptions().INDENT_SIZE = 2;
    settings.getIndentOptions().KEEP_INDENTS_ON_EMPTY_LINES = true;
    settings.RIGHT_MARGIN = 100;

    settings.SPACE_BEFORE_IF_LBRACE = true;
    settings.SPACE_BEFORE_IF_PARENTHESES = true;
    settings.SPACE_BEFORE_DO_LBRACE = true;
    settings.SPACE_BEFORE_WHILE_KEYWORD = true;
    settings.SPACE_BEFORE_WHILE_LBRACE = true;
    settings.SPACE_BEFORE_WHILE_PARENTHESES = true;
    settings.SPACE_BEFORE_TRY_LBRACE = true;
    settings.SPACE_BEFORE_TRY_PARENTHESES = true;
    settings.SPACE_BEFORE_FOR_LBRACE = true;
    settings.SPACE_BEFORE_FOR_PARENTHESES = true;
    settings.SPACE_BEFORE_SWITCH_PARENTHESES = true;
    settings.SPACE_BEFORE_SWITCH_LBRACE = true;
    settings.SPACE_BEFORE_CATCH_PARENTHESES = true;
    settings.SPACE_BEFORE_CATCH_KEYWORD = true;
    settings.SPACE_BEFORE_CATCH_LBRACE = true;
    settings.SPACE_BEFORE_ELSE_KEYWORD = true;
    settings.SPACE_BEFORE_ELSE_LBRACE = true;
    settings.SPACE_BEFORE_FINALLY_KEYWORD = true;
    settings.SPACE_BEFORE_FINALLY_LBRACE = true;
    settings.SPACE_BEFORE_SYNCHRONIZED_PARENTHESES = true;
    settings.SPACE_BEFORE_SYNCHRONIZED_LBRACE = true;

    settings.SPACE_AROUND_ADDITIVE_OPERATORS = true;
    settings.SPACE_AROUND_ASSIGNMENT_OPERATORS = true;
    settings.SPACE_AROUND_EQUALITY_OPERATORS = true;
    settings.SPACE_AROUND_BITWISE_OPERATORS = true;
    settings.SPACE_AROUND_LAMBDA_ARROW = true;
    settings.SPACE_AROUND_RELATIONAL_OPERATORS = true;
    settings.SPACE_AROUND_SHIFT_OPERATORS = true;
    settings.SPACE_AROUND_LOGICAL_OPERATORS = true;
    settings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS = true;

    settings.SPACE_WITHIN_BRACES = true;

    settings.SPACE_AFTER_COMMA = true;
    settings.SPACE_AFTER_COLON = true;
    settings.SPACE_AFTER_COMMA_IN_TYPE_ARGUMENTS = true;
    settings.SPACE_AFTER_SEMICOLON = true;
    settings.SPACE_AFTER_TYPE_CAST = true;

    settings.IF_BRACE_FORCE = CommonCodeStyleSettings.FORCE_BRACES_ALWAYS;
    settings.WHILE_BRACE_FORCE = CommonCodeStyleSettings.FORCE_BRACES_ALWAYS;
    settings.FOR_BRACE_FORCE = CommonCodeStyleSettings.FORCE_BRACES_ALWAYS;
    settings.INDENT_BREAK_FROM_CASE = true;
    settings.INDENT_CASE_FROM_SWITCH = true;
  }



}
