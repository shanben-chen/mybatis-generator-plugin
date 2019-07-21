package com.cey.mybatis.generator.plugin;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @Author 陈善奔（cey）
 * @Date 2019-06-19
 */
public class IntrospectedTableMyBatis3ImplExt extends IntrospectedTableMyBatis3Impl
{
    @Override
    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>();

        if (this.xmlMapperGenerator != null) {
            Document document = this.xmlMapperGenerator.getDocument();
            boolean mergeable = false;
            if ("true".equalsIgnoreCase(this.context.getProperty("mergeable"))) {
                mergeable = true;
            }

            GeneratedXmlFile gxf = new GeneratedXmlFile(document, getMyBatis3XmlMapperFileName(), getMyBatis3XmlMapperPackage(), this.context.getSqlMapGeneratorConfiguration().getTargetProject(), mergeable, this.context.getXmlFormatter());

            if (this.context.getPlugins().sqlMapGenerated(gxf, this)) {
                answer.add(gxf);
            }
        }

        return answer;
    }
}