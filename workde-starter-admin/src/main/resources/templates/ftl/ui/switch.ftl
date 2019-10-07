<#--
<input type="checkbox"/>
-->
<#macro switch
value="1" text="" readonly="" checked=""
label="" required="false" help=""
id="" name="" class="" style="" size="" title="" disabled="" tabindex="" accesskey=""
onclick="" ondblclick="" onmousedown="" onmouseup="" onmouseover="" onmousemove="" onmouseout="" onfocus="" onblur="" onkeypress="" onkeydown="" onkeyup="" onselect="" onchange=""
>
<#include "ftl/ui/control.ftl"/><#rt/>
<input type="checkbox"<#rt/>
       value="${value}"<#rt/>
       id="${id}"<#rt/>
       lay-skin="switch"
<#if readonly!=""> readonly="${readonly}"</#if><#rt/>
<#if checked!=""> checked="${checked}"</#if><#rt/>
<#include "ftl/ui/common-attributes.ftl"/><#rt/>
<#include "ftl/ui/scripting-events.ftl"/><#rt/>
/>
<#include "ftl/ui/control-close.ftl"/>
</#macro>
