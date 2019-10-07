<input type="radio" value="${rkey}" title="${rvalue}"<#rt/>
<#if (rkey?string=="" && (!value?? || value?string=="")) || (value?? && value?string!="" && value?string==rkey?string)> checked="checked"</#if><#rt/>
<#include "ftl/ui/common-attributes.ftl"/><#rt/>
<#include "ftl/ui/scripting-events.ftl"/><#rt/>
/><#if hasNext> </#if>
