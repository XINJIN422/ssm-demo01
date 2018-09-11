package ${packageName};

import com.yangkang.ssmdemo01.tools.annotation.Table;
import com.yangkang.ssmdemo01.tools.annotation.Column;
import java.io.Serializable;
${isDateType}${isBigDecimal}

/**
 * @Type ${className}
 * @Desc ${tableDesc}
 * @author ${userName}
 * @date ${createDate}
 * 1.本类由工具类DbToBeanUtil自动生成
 * 2.默认读取resources下的jdbc.properties配置文件,也可以在main函数里设置覆盖相关属性
 * 3.不建议直接修改本类,必要时建议创建子类扩展
 */
@Table("${tableName}")
public class ${className} implements Serializable {

    private static final long serialVersionUID = 1L;

    <#list fields as item>
    @Column("${item.colName}")
    private ${item.javType} ${item.javName};
    </#list>

    <#list fields as item>
    public void set${item.javNameCap}(${item.javType} ${item.javName}){
        this.${item.javName} = ${item.javName};
    }

    public ${item.javType} get${item.javNameCap}(){
        return ${item.javName};
    }

    </#list>
}