var wb = {
    meta: {},
    init: function(metaPath) {
        wb.get(metaPath + "/meta", function(resp) {
            if(resp.code == 200) {
                wb.initListTable(metaPath, resp.data);
            }
        })
    },
    initListTable: function(path, meta) {
        var table = layui.table;
        meta.fields.push({title:'操作', toolbar: '#toolbar', width:150});
        table.render({
            elem: '#table-list-data'
            ,height: 312
            ,url: path + '/list'    //数据接口
            ,page: meta.page        //开启分页
            ,cols: [meta.fields]
            ,toolbar: true
            ,defaultToolbar: ['filter', 'print', 'exports']
            ,even: true //开启隔行背景
            ,size: meta.size
            ,limit: 20
            ,limits: [20,50,80,100]
            ,height: 'full - 20'
            ,parseData: function(res) {
                return {
                    "code": res.code == 200 ? 0 : res.code,
                    "count": res.data.total,
                    "data": res.data.list
                };
            }
        });

        table.on('tool(table-list-data)', wb.tableToolEvent);
    },
    tableToolEvent: function(obj) {
        console.log(obj);
    },

    ajax: function(url, type, data, func) {
        $.ajax({
            url: url,
            type: type,
            data: data,
            success: function(resp){
                if(func) func(resp);
            }
        })
    },
    get: function(url, func){
        wb.ajax(url, 'get', null, function(resp){
            if(func) func(resp);
        });
    },

    post: function (url, data, func) {
        wb.ajax(url, 'post', data, function (resp) {
            if (func) func(resp);
        });
    },
    delete: function(url, func) {
        wb.ajax(url,'delete', {}, function(resp){
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
}



