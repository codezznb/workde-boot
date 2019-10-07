<#--
判断field类型，加载对应的UI
-->

<#macro field field>
<#if field.type == 'text'><@ui.text id=field.id label=field.label name=field.name value=field.value required=field.required help=field.help /></#if>
<#if field.type == 'select'><@ui.select id=field.id label=field.label name=field.name value=field.value required=field.required help=field.help list=field.list headerKey=field.headerKey headerValue=field.headerValue /></#if>
<#if field.type == 'radio'><@ui.radio id=field.id label=field.label name=field.name value=field.value required=field.required help=field.help list=field.list /></#if>
<#if field.type == 'textarea'><@ui.textarea id=field.id label=field.label name=field.name value=field.value required=field.required help=field.help cols=field.cols rows=field.rows/></#if>
<#if field.type == 'switch'><@ui.switch id=field.id label=field.label name=field.name required=field.required help=field.help checked=field.checked /></#if>
</#macro>
