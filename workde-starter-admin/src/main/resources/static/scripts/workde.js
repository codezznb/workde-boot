var Wb = {
    page: {},
    linkClickSelector: 'a[data-remote]',
    buttonClickSelector: 'button[data-remote]:not([form]):not(form button), button[data-confirm]:not([form]):not(form button)',
    inputChangeSelector: 'select[data-remote], input[data-remote], textarea[data-remote]',
    fire: function(obj, name, data) {
        var $ = layui.jquery,
            event = $.Event(name);
        obj.trigger(event, data);
        return event.result !== false;
    },
    isRemote: function(element) {
        return element.data('remote') !== undefined && element.data('remote') !== false;
    },
    handleRemote: function(element) {
        var method, url, data, dataType, options, $ = layui.jquery;
        dataType = element.data('type') || ($.ajaxSettings && $.ajaxSettings.dataType);
        if (element.is('form')) {
            method = element.attr('method');
            url = element.attr('action');
            data = $(element[0]).serializeArray();
        } else if (element.is(Wb.inputChangeSelector)) {
            method = element.data('method');
            url = element.data('url');
            data = element.serialize();
        } else if (element.is(Wb.buttonClickSelector)) {
            method = element.data('method') || 'get';
            url = element.data('url');
            data = element.serialize();
            if (element.data('params')) data = data + '&' + element.data('params');
        } else {
            method = element.data('method');
            url = element[0].href;
            data = element.data('params') || null;
        }
        options = {
            type: method || 'GET', data: data || {}, dataType: dataType,
            beforeSend: function(xhr, settings) {
                if (settings.dataType === undefined) {
                    xhr.setRequestHeader('accept', '*/*;q=0.5, ' + settings.accepts.script);
                }
                if (Wb.fire(element, 'ajax:beforeSend', [xhr, settings])) {
                    element.trigger('ajax:send', xhr);
                } else {
                    return false;
                }
            },
            success: function(data, status, xhr) {
                element.trigger('ajax:success', [data, status, xhr]);
            },
            complete: function(xhr, status) {
                element.trigger('ajax:complete', [xhr, status]);
            },
            error: function(xhr, status, error) {
                element.trigger('ajax:error', [xhr, status, error]);
            }
        };
        if (url) { options.url = url; }
        return $.ajax(options);
    },
    allowAction: function(element) {
        var message = element.data('confirm');
        if (message) {
            Wb.confirm(message, function() {
                return Wb.handleRemote(element);
            });
        }else {
            return Wb.handleRemote(element);
        }
    },
    ready: function(func) {
        layui.use(['jquery','form', 'layer', 'laydate', 'element', 'laypage'], function(){
            var $ = layui.jquery,
                form = layui.form;
            form.render();
            func();
            $(document).on('click.wb', Wb.linkClickSelector, function (e) {
                var link = $(this);
                if (Wb.isRemote(link)) Wb.allowAction(link);
                return false;
            });
        });
    },
    remote: function(that) {
        var url = that.data('url');
        if(!url) url = that.attr('href');
        if(url) {
            var method = that.data('method') || 'POST';
            Wb.ajax(url, method, {}, function(resp) {
                if(resp.code == 200 && resp.success) {
                    var success = that.data("success") || "操作成功";
                    Wb.msg(success);
                }else {
                    var fail = that.data("fail") || "操作失败";
                    Wb.msg(fail);
                }
            })
        }
    },
    ajax: function(url, type, data, func) {
        var $ = layui.jquery;
        $.ajax({
            url: url,
            type: type,
            data: data,
            success: function(resp){
                if(func) func(resp);
            }
        })
    },
    submit: function(url, data, func) {
        var $ = layui.jquery;
        $.ajax({
            url: url,
            type: 'POST',
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



