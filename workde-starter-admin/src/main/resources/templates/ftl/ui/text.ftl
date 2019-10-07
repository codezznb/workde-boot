<#--
<input type="text"/>
-->
<#macro text
maxlength="" readonly="" value=""
label="" noHeight="false" required="false" colspan="" width="100" help="" helpPosition="2" colon=":" hasColon="true"
id="" name="" class="layui-input" style="" size="" title="" disabled="" tabindex="" accesskey=""
vld="" equalTo="" maxlength="" minlength="" max="" min="" rname="" rvalue=""
onclick="" ondblclick="" onmousedown="" onmouseup="" onmouseover="" onmousemove="" onmouseout="" onfocus="" onblur="" onkeypress="" onkeydown="" onkeyup="" onselect="" onchange=""
>
<#include "ftl/ui/control.ftl"/><#rt/>
<input type="text"<#rt/>
<#if id!=""> id="${id}"</#if><#rt/>
<#if maxlength!=""> maxlength="${maxlength}"</#if><#rt/>
<#if max?string!=""> max="${max}"</#if><#rt/>
<#if min?string!=""> min="${min}"</#if><#rt/>
<#if readonly!=""> readonly="${readonly}"</#if><#rt/>
<#if rname!=""> rname="${rname}"</#if><#rt/>
<#if rvalue!=""> rvalue="${rvalue}"</#if><#rt/>
<#if value?? && value?string!=""> value="${value?html}"</#if><#rt/>
<#include "ftl/ui/common-attributes.ftl"/><#rt/>
<#include "ftl/ui/scripting-events.ftl"/><#rt/>
/><#rt/>
<#include "ftl/ui/control-close.ftl"/><#rt/>
</#macro>
