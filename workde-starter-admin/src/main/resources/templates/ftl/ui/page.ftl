<#macro page value>
<div id="wd-page">
    <div class="layui-box layui-laypage layui-laypage-default">
        <#if value.hasPreviousPage>
            <a href="?page=${value.prePage}" class="layui-laypage-prev">上一页</a>
        <#else>
            <span class="layui-disabled"><em class="layui-laypage-prev"></em><em>上一页</em></span>
        </#if>
        <#if value.navigateFirstPage != 1><a href="?page=1">1</a><span class="layui-laypage-spr">...</span></#if>
        <#list value.navigatepageNums as p>
            <#if p == value.pageNum>
                <span class="layui-laypage-curr"><em class="layui-laypage-em"></em><em>${p}</em></span>
            <#else>
                <a href="?page=${p}">${p}</a>
            </#if>
        </#list>
        <#if value.navigateLastPage != value.pages><span class="layui-laypage-spr">...</span><a href="?page=${value.pages}">${value.pages}</a></#if>
        <#if value.hasNextPage>
            <a href="?page=${value.nextPage}" class="layui-laypage-next">下一页</a>
        <#else>
            <span class="layui-disabled"><em class="layui-laypage-next"></em><em>下一页</em></span>
        </#if>
    </div>
</div>
</#macro>
