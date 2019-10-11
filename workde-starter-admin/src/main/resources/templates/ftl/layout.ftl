<#macro layout>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>后台管理中心</title>
    <link rel="stylesheet" href="/layui/css/layui.css"  media="all">
    <link rel="stylesheet" href="/styles/act.css" />
    <script src="/layui/layui.js" charset="utf-8"></script>
    <script src="/scripts/act.js"></script>
</head>
<body class="act-admin">
<div class="act-skin-default main-lg-body">
    <div class="act-head">
        <div class="act-container">
            <div class="act-logo">后台管理中心</div>
            <ul class="layui-nav layui-layout-left">
                <#list menuGroupList as menuGroup>
                    <li class="layui-nav-item"><a href="${menuGroup.url}">${menuGroup.text}</a></li>
                </#list>
            </ul>
            <ul class="layui-nav layui-layout-right">
                <li class="layui-nav-item">
                    <a href="javascript:;">
                    超级管理员
                    </a>
                    <dl class="layui-nav-child">
                        <dd><a href="">修改密码</a></dd>
                        <dd><a href="">退出后台</a></dd>
                    </dl>
                </li>
            </ul>
        </div>
    </div>

    <div class="act-main">
        <div class="act-container">
            <div class="act-panel">
                <a href="javascript:;" class="js-big-main button-to-big color-gray" title="正常">正常</a>
                <div class="act-panel-header">${mm.moduleDefine.moduleTitle}</div>
                <div class="act-body">
                    <div class="act-left-menu">
                        <ul class="layui-nav layui-nav-tree">
                            <#list menuGroupList as menuGroup>
                            <li class="layui-nav-item layui-nav-itemed">
                                <a class="heading" href="javascript:;">${menuGroup.text}</a>
                                <dl class="layui-nav-child">
                                    <#list menuGroup.items as menu>
                                    <dd><a href="${menu.url}">&nbsp;&nbsp;${menu.text}</a></dd>
                                    </#list>
                                </dl>
                            </li>
                            </#list>
                        </ul>
                    </div>
                    <div class="act-right">
                        <#nested />
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
</#macro>
