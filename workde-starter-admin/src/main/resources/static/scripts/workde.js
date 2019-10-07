var Wb = {
    init: function(path, func) {
        layui.use(['jquery','form', 'layer', 'laydate', 'element', 'table'], function(){
            Wb.path = path;
            Wb.get(Wb.path + "/meta", function(resp) {
                if(resp.code == 200) {
                    Wb.meta = resp.data;
                    Wb.initTable();
                }
            })
        });
    },
    ready: function(func) {
        layui.use(['jquery','form', 'layer', 'laydate', 'element', 'laypage'], function(){
           func();
        });
    },
    initTable: function() {
        var table = layui.table;
        table.render({
            elem: '#table-list-data'
            ,url: Wb.path + '/list'    //数据接口
            ,page: Wb.meta.page        //开启分页
            ,cols: [Wb.meta.fields]
            ,even: true //开启隔行背景
            ,size: Wb.meta.size
            ,limit: 20
            ,limits: [20,50,80,100]
            ,parseData: function(res) {
                return {
                    "code": res.code == 200 ? 0 : res.code,
                    "count": res.data.total,
                    "data": res.data.list
                };
            }
        });
        table.on('toolbar(table-list-data)', Wb.tableToolbarEvent);
        // table.on('tool(table-list-data)', wb.tableToolEvent);
    },
    tableToolbarEvent: function(obj) {
        if(obj.event == 'add') {
            Wb.open(Wb.path + '/new');
        }else if(obj.event == 'update') {
            var checkStatus = layui.table.checkStatus('table-list-data');
            if(checkStatus.data.length != 1) {
                Wb.alert("请选择一条数据进行修改");
            }else {

            }
        }

        console.log(checkStatus);
    },
    tableToolEvent: function(obj) {
        var checkStatus = layui.table.checkStatus('table-list-data');
        console.log(checkStatus);
    },

    ajax: function(url, type, data, func) {
        var $ = layui.jquery;
        $.ajax({
            url: url,
            type: type,
            contentType: "application/json", //必须有
            data: JSON.stringify(data),
            success: function(resp){
                if(func) func(resp);
            }
        })
    },
    get: function(url, func){
        Wb.ajax(url, 'get', null, function(resp){
            if(func) func(resp);
        });
    },

    post: function (url, data, func) {
        Wb.ajax(url, 'post', data, function (resp) {
            if (func) func(resp);
        });
    },
    put: function(url, data, func) {
        Wb.ajax(url,'put', data, function(resp){
            if (func) func(resp);
        });
    },
    delete: function(url, func) {
        Wb.ajax(url,'delete', {}, function(resp){
            if (func) func(resp);
        });
    },
    msg: function(msg){
        var index = layer.msg(msg);
    },
    confirm: function(msg, func) {
        layer.confirm(msg, function(index){
            layer.close(index);
            if(func) func(index);
        });
    },
    alert: function(msg){
        var index = layer.alert(msg);
    },
    open: function(url) {
        location.href = url;
    },
    jump: function(url, second) {
        if(second && second > 0) {
            setTimeout(function(){
                window.location.href = url;
            }, second * 1000);
        }else {
            window.location.href = url;
        }
    }
}



