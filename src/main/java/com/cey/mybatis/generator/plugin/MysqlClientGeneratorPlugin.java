package com.cey.mybatis.generator.plugin;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.internal.util.StringUtility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 描述
 *
 * @Author 陈善奔（cey）
 * @Date 2019-06-19
 */
public class MysqlClientGeneratorPlugin extends PluginAdapter {
    private static String XMLFILE_POSTFIX = "Ext";
    private static String JAVAFILE_POTFIX = "Ext";
    private static String SQLMAP_COMMON_POTFIX = "and IS_DELETED = '0'";
    private static String ANNOTATION_RESOURCE = "javax.annotation.Resource";
    private static String ANNOTATION_APIPARAM = "com.weidai.apiExtractor.annotation.ApiParam";
    private static FullyQualifiedJavaType APIPARAM_INSTANCE;
    private static String MAPPER_EXT_HINT;

    public MysqlClientGeneratorPlugin() {
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.addModelClassComment(interfaze, introspectedTable, false);
        return true;
    }

    private void addModelClassComment(Interface topLevelClass, IntrospectedTable introspectedTable, boolean isExt) {
        StringBuilder sb = new StringBuilder();
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * <p>");
        if (!isExt) {
            sb.append(" * 表 : ");
            sb.append(introspectedTable.getFullyQualifiedTable());
            sb.append("的 mapper 类");
        } else {
            String name = topLevelClass.getType().getShortName();
            sb.append(" * ").append(name.substring(0, name.indexOf("Ext")));
            sb.append("的扩展 mapper 接口");
        }

        topLevelClass.addJavaDocLine(sb.toString());
        topLevelClass.addJavaDocLine(" * ");
        new SimpleDateFormat("yyyy年MM月dd日");
        String author = this.context.getProperty("author");
        author = author == null ? "$author$" : author;
        topLevelClass.addJavaDocLine(" * @author \t" + author);
        topLevelClass.addJavaDocLine(" */");
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        if ("true".equalsIgnoreCase(this.context.getProperty("addApiAnnotation"))) {
            if (!topLevelClass.getImportedTypes().contains(APIPARAM_INSTANCE)) {
                topLevelClass.addImportedType(APIPARAM_INSTANCE);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("@ApiParam(description=\"");
            String remarks = introspectedColumn.getRemarks();
            if (StringUtility.stringHasValue(remarks)) {
                sb.append(remarks);
            }

            sb.append("\")");
            field.addAnnotation(sb.toString());
        }

        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType() + JAVAFILE_POTFIX);
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        this.context.getCommentGenerator().addJavaFileComment(interfaze);
        FullyQualifiedJavaType baseInterfaze = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        interfaze.addSuperInterface(baseInterfaze);
        this.addModelClassComment(interfaze, introspectedTable, true);
        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType(ANNOTATION_RESOURCE);
        interfaze.addAnnotation("@Resource");
        interfaze.addImportedType(annotation);
        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(interfaze, this.context.getJavaModelGeneratorConfiguration().getTargetProject(), this.context.getProperty("javaFileEncoding"), this.context.getJavaFormatter());
        if (this.isExistExtFile(generatedJavaFile.getTargetProject(), generatedJavaFile.getTargetPackage(), generatedJavaFile.getFileName())) {
            return super.contextGenerateAdditionalJavaFiles(introspectedTable);
        } else {
            List<GeneratedJavaFile> generatedJavaFiles = new ArrayList(1);
            generatedJavaFile.getFileName();
            generatedJavaFiles.add(generatedJavaFile);
            return generatedJavaFiles;
        }
    }

    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        String[] splitFile = introspectedTable.getMyBatis3XmlMapperFileName().split("\\.");
        String fileNameExt = null;
        if (splitFile[0] != null) {
            fileNameExt = splitFile[0] + XMLFILE_POSTFIX + ".xml";
        }

        if (this.isExistExtFile(this.context.getSqlMapGeneratorConfiguration().getTargetProject(), introspectedTable.getMyBatis3XmlMapperPackage(), fileNameExt)) {
            return super.contextGenerateAdditionalXmlFiles(introspectedTable);
        } else {
            Document document = new Document("-//mybatis.org//DTD Mapper 3.0//EN", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
            XmlElement root = new XmlElement("mapper");
            document.setRootElement(root);
            String namespace = introspectedTable.getMyBatis3SqlMapNamespace() + XMLFILE_POSTFIX;
            root.addAttribute(new Attribute("namespace", namespace));
            root.addElement(new TextElement(MAPPER_EXT_HINT));
            GeneratedXmlFile gxf = new GeneratedXmlFile(document, fileNameExt, introspectedTable.getMyBatis3XmlMapperPackage(), this.context.getSqlMapGeneratorConfiguration().getTargetProject(), false, this.context.getXmlFormatter());
            List<GeneratedXmlFile> answer = new ArrayList(1);
            answer.add(gxf);
            return answer;
        }
    }

    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement parentElement = document.getRootElement();
        this.updateDocumentNameSpace(introspectedTable, parentElement);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Element> elements = element.getElements();
        XmlElement setItem = null;
        int modifierItemIndex = -1;
        int gmtModifiedItemIndex = -1;
        boolean needIsDeleted = false;
        XmlElement gmtCreatedEle = null;
        XmlElement creatorEle = null;
        Iterator var10 = elements.iterator();

        while (true) {
            Element e;
            do {
                if (!var10.hasNext()) {
                    if (setItem != null) {
                        if (modifierItemIndex != -1) {
                            this.addModifierXmlElement(setItem, modifierItemIndex);
                        }

                        if (gmtModifiedItemIndex != -1) {
                            this.addGmtModifiedXmlElement(setItem, gmtModifiedItemIndex);
                        }

                        if (gmtCreatedEle != null) {
                            setItem.getElements().remove(gmtCreatedEle);
                        }

                        if (creatorEle != null) {
                            setItem.getElements().remove(creatorEle);
                        }
                    }

                    if (needIsDeleted) {
                        TextElement text = new TextElement(SQLMAP_COMMON_POTFIX);
                        element.addElement(text);
                    }

                    return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element, introspectedTable);
                }

                e = (Element) var10.next();
            } while (!(e instanceof XmlElement));

            setItem = (XmlElement) e;

            for (int i = 0; i < setItem.getElements().size(); ++i) {
                XmlElement xmlElement = (XmlElement) setItem.getElements().get(i);
                Iterator var14 = xmlElement.getAttributes().iterator();

                while (var14.hasNext()) {
                    Attribute att = (Attribute) var14.next();
                    if (att.getValue().equals("modifier != null")) {
                        modifierItemIndex = i;
                        break;
                    }

                    if (att.getValue().equals("gmtModified != null")) {
                        gmtModifiedItemIndex = i;
                        break;
                    }

                    if (att.getValue().equals("isDeleted != null")) {
                        needIsDeleted = true;
                        break;
                    }

                    if (att.getValue().equals("gmtCreated != null")) {
                        gmtCreatedEle = xmlElement;
                        break;
                    }

                    if (att.getValue().equals("creator != null")) {
                        creatorEle = xmlElement;
                        break;
                    }
                }
            }
        }
    }

    private void updateDocumentNameSpace(IntrospectedTable introspectedTable, XmlElement parentElement) {
        Attribute namespaceAttribute = null;
        Iterator var4 = parentElement.getAttributes().iterator();

        while (var4.hasNext()) {
            Attribute attribute = (Attribute) var4.next();
            if (attribute.getName().equals("namespace")) {
                namespaceAttribute = attribute;
            }
        }

        parentElement.getAttributes().remove(namespaceAttribute);
        parentElement.getAttributes().add(new Attribute("namespace", introspectedTable.getMyBatis3JavaMapperType() + JAVAFILE_POTFIX));
    }

    private void addGmtModifiedXmlElement(XmlElement setItem, int gmtModifiedItemIndex) {
        XmlElement defaultGmtModified = new XmlElement("if");
        defaultGmtModified.addAttribute(new Attribute("test", "gmtModified == null"));
        defaultGmtModified.addElement(new TextElement("GMT_MODIFIED = NOW(),"));
        setItem.getElements().add(gmtModifiedItemIndex + 1, defaultGmtModified);
    }

    private void addModifierXmlElement(XmlElement setItem, int modifierItemIndex) {
        XmlElement defaultmodifier = new XmlElement("if");
        defaultmodifier.addAttribute(new Attribute("test", "modifier == null"));
        defaultmodifier.addElement(new TextElement("MODIFIER = 'SYSTEM',"));
        setItem.getElements().add(modifierItemIndex + 1, defaultmodifier);
    }

    private boolean isExistExtFile(String targetProject, String targetPackage, String fileName) {
        File project = new File(targetProject);
        if (!project.isDirectory()) {
            return true;
        } else {
            StringBuilder sb = new StringBuilder();
            StringTokenizer st = new StringTokenizer(targetPackage, ".");

            while (st.hasMoreTokens()) {
                sb.append(st.nextToken());
                sb.append(File.separatorChar);
            }

            File directory = new File(project, sb.toString());
            if (!directory.isDirectory()) {
                boolean rc = directory.mkdirs();
                if (!rc) {
                    return true;
                }
            }

            File testFile = new File(directory, fileName);
            return testFile.exists();
        }
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

//    public static void main(String[] args) {
//        String config = WdMysqlClientGeneratorPlugin.class.getClassLoader().getResource("generatorConfig.xml").getFile();
//        String[] arg = new String[]{"-configfile", config};
//        ShellRunner.main(arg);
//    }

    static {
        APIPARAM_INSTANCE = new FullyQualifiedJavaType(ANNOTATION_APIPARAM);
        MAPPER_EXT_HINT = "<!-- 扩展自动生成或自定义的SQl语句写在此文件中 -->";
    }
}