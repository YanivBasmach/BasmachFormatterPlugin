package com.yaniv.bsmchformat;

import com.intellij.codeInsight.generation.GetterTemplatesManager;
import com.intellij.codeInsight.generation.SetterTemplatesManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.java.generate.template.TemplateResource;

public class TemplateInstaller implements ApplicationComponent {

  private static final String GETTER_TEMPLATE = "#if($field.modifierStatic)\n" +
          "static ##\n" +
          "#end\n" +
          "$field.type ##\n" +
          "$StringUtil.sanitizeJavaIdentifier($helper.getPropertyName($field, $project))() {\n" +
          "  return this.$field.name;\n" +
          "}";
  private static final String SETTER_TEMPLATE = "#set($paramName = $helper.getParamName($field, $project))\n" +
          "#if($field.modifierStatic)\n" +
          "static ##\n" +
          "#end\n" +
          "void change$StringUtil.capitalizeWithJavaBeanConvention($StringUtil.sanitizeJavaIdentifier($helper.getPropertyName($field, $project)))($field.type $paramName) {\n" +
          "  #if ($field.name == $paramName)\n" +
          "    #if (!$field.modifierStatic)\n" +
          "      this.##\n" +
          "    #else\n" +
          "      $classname.##\n" +
          "    #end\n" +
          "  #end\n" +
          "  $field.name = $paramName;\n" +
          "}";

  @Override
  public void initComponent() {
    TemplateResource getterTemplate = new TemplateResource("Basmach", GETTER_TEMPLATE, true);
    GetterTemplatesManager.getInstance().addTemplate(getterTemplate);
    GetterTemplatesManager.getInstance().setDefaultTemplate(getterTemplate);
    TemplateResource setterTemplate = new TemplateResource("Basmach", SETTER_TEMPLATE, true);
    SetterTemplatesManager.getInstance().addTemplate(setterTemplate);
    SetterTemplatesManager.getInstance().setDefaultTemplate(setterTemplate);
  }


}
