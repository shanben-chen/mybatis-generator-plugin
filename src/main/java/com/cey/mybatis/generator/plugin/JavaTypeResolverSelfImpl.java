package com.cey.mybatis.generator.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

/**
 * 描述
 *
 * @Author 陈善奔（cey）
 * @Date 2019-06-19
 */
public class JavaTypeResolverSelfImpl  extends JavaTypeResolverDefaultImpl
{
    @Override
    public String calculateJdbcTypeName(IntrospectedColumn introspectedColumn) {
        String answer;
        JavaTypeResolverDefaultImpl.JdbcTypeInformation jdbcTypeInformation = (JavaTypeResolverDefaultImpl.JdbcTypeInformation)this.typeMap.get(Integer.valueOf(introspectedColumn.getJdbcType()));

        if (jdbcTypeInformation == null) {
             switch (introspectedColumn.getJdbcType()) {
                case 3:
                    return "DECIMAL";

                case 2:
                    return "NUMERIC";

                case 91:
                    return "TIMESTAMP";
            }

            answer = null;

        }
        else if (jdbcTypeInformation.getJdbcTypeName().equals("DATE")) {
            answer = "TIMESTAMP";
        } else {
            answer = jdbcTypeInformation.getJdbcTypeName();
        }


        return answer;
    }
}
