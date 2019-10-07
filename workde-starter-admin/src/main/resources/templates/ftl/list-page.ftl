<#macro list_page value mm>
<@ui.table value=value;object><#rt/>
    <#list mm.fields as field>
        <@ui.column title=field.label width=field.width>${object[field.name]}</@ui.column><#rt />
    </#list>
    <@ui.column title="操作" width="10%"><a href="${object.id}" class="layui-btn layui-btn-xs">修改</a></@ui.column>
</@ui.table>
<#if mm.page && value.pages>
<@ui.page value />
</#if>
</#macro>
