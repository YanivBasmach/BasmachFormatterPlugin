package com.yaniv.formatter;

import com.intellij.codeInsight.generation.GetterTemplatesManager;
import com.intellij.codeInsight.generation.SetterTemplatesManager;
import com.intellij.openapi.components.BaseComponent;
import org.jetbrains.java.generate.template.TemplateResource;
import org.jetbrains.java.generate.template.TemplatesManager;

public class AccessorTemplatesInstaller implements BaseComponent {

  private static final String GETTER_TEMPLATE = "#if($field.modifierStatic)\n" +
          "static ##\n" +
          "#end\n" +
          "$field.type ##\n" +
          "#set($name = $StringUtil.sanitizeJavaIdentifier($helper.getPropertyName($field, $project)))\n" +
          "#if($field.modifierStatic)\n" +
          "#set($access = \"\")\n" +
          "#else\n" +
          "#set($access = \"this.\")\n" +
          "#end\n" +
          "${name}() {\n" +
          "  return ##\n" +
          "  #if($field.primitive || $field.string)\n" +
          "    $access $field.name;\n" +
          "  #else\n" +
          "    new $field.type ($access $field.name);\n" +
          "  #end\n" +
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
          "  #if ($field.primitive || $field.string)\n" +
          "    $field.name = $paramName;\n" +
          "  #else\n" +
          "    $field.name = new $field.type($paramName);\n" +
          "  #end\n" +
          "}";

  @Override
  public void initComponent() {
    replaceTemplate(GetterTemplatesManager.getInstance(), GETTER_TEMPLATE);
    replaceTemplate(SetterTemplatesManager.getInstance(), SETTER_TEMPLATE);
  }

  private void replaceTemplate(TemplatesManager manager, String template) {
    TemplateResource resource = new TemplateResource("Basmach", template, true);
    manager.addTemplate(resource);
    manager.setDefaultTemplate(resource);
  }




}
