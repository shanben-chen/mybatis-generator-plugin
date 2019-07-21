package com.cey.mybatis.generator.plugin.dom.formatter;

import org.mybatis.generator.api.dom.DefaultXmlFormatter;
import org.mybatis.generator.api.dom.xml.Document;

/**
 * 描述
 *
 * @Author 陈善奔（cey）
 * @Date 2019-06-19
 */
public class XmlFormatter extends DefaultXmlFormatter
{
    @Override
    public String getFormattedContent(Document document) {
        String content = document.getFormattedContent();
        try {
            if ("true".equalsIgnoreCase(this.context.getProperty("suppressColumnType"))) {
                content = content.replaceAll("[, ]?jdbcType=\"?[A-Z]+\"?", "");
            }
            content = content.replaceAll("  ", "    ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }
}