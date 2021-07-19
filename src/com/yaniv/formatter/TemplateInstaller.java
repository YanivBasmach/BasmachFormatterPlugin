package com.yaniv.formatter;

import com.intellij.codeInsight.generation.GetterTemplatesManager;
import com.intellij.codeInsight.generation.SetterTemplatesManager;
import com.intellij.openapi.components.BaseComponent;
import org.jetbrains.java.generate.template.TemplateResource;
import org.jetbrains.java.generate.template.TemplatesManager;

public class TemplateInstaller implements BaseComponent {

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
    replaceTemplate(GetterTemplatesManager.getInstance(), GETTER_TEMPLATE);
    replaceTemplate(SetterTemplatesManager.getInstance(), SETTER_TEMPLATE);
  }

  private void replaceTemplate(TemplatesManager manager, String template) {
    TemplateResource prev = manager.findTemplateByName("Basmach");
    manager.removeTemplate(prev);

    TemplateResource resource = new TemplateResource("Basmach", template, true);
    manager.addTemplate(resource);
    manager.setDefaultTemplate(resource);
  }


}
