package com.cey.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * 描述
 *
 * @Author 陈善奔（cey）
 * @Date 2019-06-19
 */
public class CNCommentGenerator extends DefaultCommentGenerator {
    private Properties properties;

    public CNCommentGenerator() {
    }

    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        this.properties = new Properties();
        this.properties.putAll(properties);
    }

    public void addJavaFileComment(CompilationUnit compilationUnit) {
        compilationUnit.addFileCommentLine("/**");
        compilationUnit.addFileCommentLine(" * ");
        String copyright = " * 版权信息.";
        compilationUnit.addFileCommentLine(copyright);
        compilationUnit.addFileCommentLine(" * ");
        compilationUnit.addFileCommentLine(" * " + compilationUnit.getType().getShortNameWithoutTypeArguments() + ".java");
        compilationUnit.addFileCommentLine(" * ");
        compilationUnit.addFileCommentLine(" */");
    }

    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * <p>");
        String remarks = introspectedTable.getRemarks();
        if (StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator"));
            String[] var6 = remarkLines;
            int var7 = remarkLines.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                String remarkLine = var6[var8];
                topLevelClass.addJavaDocLine(" * " + remarkLine);
            }

            sb.append(" * ");
        }

        sb.append(" * 表 : ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append("的 model 类");
        topLevelClass.addJavaDocLine(sb.toString());
        topLevelClass.addJavaDocLine(" * ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String author = "$author$";
        if (this.properties.containsKey("author")) {
            author = this.properties.getProperty("author");
        }

        topLevelClass.addJavaDocLine(" * @author \t" + author);
        topLevelClass.addJavaDocLine(" * @date \t" + sdf.format(new Date()));
        topLevelClass.addJavaDocLine(" */");
        FullyQualifiedJavaType serializable = new FullyQualifiedJavaType("java.io.Serializable");
        topLevelClass.addImportedType(serializable);
        topLevelClass.addSuperInterface(serializable);
        Field serialVersionUID = new Field();
        serialVersionUID.setVisibility(JavaVisibility.PRIVATE);
        serialVersionUID.setStatic(true);
        serialVersionUID.setFinal(true);
        serialVersionUID.setName("serialVersionUID");
        serialVersionUID.setType(new FullyQualifiedJavaType("long"));
        serialVersionUID.setInitializationString("1L");
        sb = new StringBuilder();
        sb.append("/** ");
        sb.append(" 类的 seri version id");
        sb.append(" */");
        serialVersionUID.addJavaDocLine(sb.toString());
        topLevelClass.addField(serialVersionUID);
    }

    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        StringBuffer sb = new StringBuffer();
        sb.append("/** ");
        sb.append("字段:");
        sb.append(introspectedColumn.getActualColumnName());
        String remarks = introspectedColumn.getRemarks();
        if (StringUtility.stringHasValue(remarks)) {
            sb.append("，");
            sb.append(remarks);
        }

        sb.append(" */");
        field.addJavaDocLine(sb.toString());
    }
}
