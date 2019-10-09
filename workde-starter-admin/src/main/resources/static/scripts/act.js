var Act = {
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
                if (Act.fire(element, 'ajax:beforeSend', [xhr, settings])) {
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
            Act.confirm(message, function() {
                return Act.handleRemote(element);
            });
        }else {
            return Act.handleRemote(element);
        }
    },
    ready: function(func) {
        layui.use(['jquery','form', 'layer', 'laydate', 'element', 'laypage'], function(){
            Act.initPluging();
            var $ = layui.jquery,
                form = layui.form,
                laydate = layui.laydate;
            form.render();
            if(func) func();
        });
    },
    initPluging: function() {
        var $ = layui.jquery;
        $('.act-date').each(function() {
            var fmt = $(this).data('format') || "yyyy-MM-dd";
            laydate.render({
                elem: this,
                format: fmt,
                type: fmt == 'yyyy-MM-dd' ? 'date' : 'datetime'
            });
        });
        $('input[data-search-url]').keydown(function(e){
            if(e.keyCode === 13){
                var me = $(this);
                var url = $(this).data("search-url");
                var name = $(this).prop("name");
                var query = $(this).val();
                var obj =  Act.Url.parseParams((url.split("?")[1] || "").split("#")[0]);
                obj[name] = query;
                url = Act.Url.toString((url.split("?")[0] || "").split("#")[0], obj);
                Act.stack.load(url);
            }
        });
        $(document).on('click.act', Act.linkClickSelector, function (e) {
            var link = $(this);
            if (Act.isRemote(link)) Act.allowAction(link);
            return false;
        });
    },
    remote: function(that) {
        var url = that.data('url');
        if(!url) url = that.attr('href');
        if(url) {
            var method = that.data('method') || 'POST';
            Act.ajax(url, method, {}, function(resp) {
                if(resp.code == 200 && resp.success) {
                    var success = that.data("success") || "操作成功";
                    Act.msg(success);
                }else {
                    var fail = that.data("fail") || "操作失败";
                    Act.msg(fail);
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
        Act.ajax(url, 'get', null, function(resp){
            if(func) func(resp);
        });
    },

    post: function (url, data, func) {
        Act.ajax(url, 'post', data, function (resp) {
            if (func) func(resp);
        });
    },
    put: function(url, data, func) {
        Act.ajax(url,'put', data, function(resp){
            if (func) func(resp);
        });
    },
    delete: function(url, func) {
        Act.ajax(url,'delete', {}, function(resp){
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
    },
    stack: {
        load: function(url, func){
            if(Act.supportHistory) {
                $.ajax({
                    url: url,
                    cache: false,
                    dataType: 'html',
                    headers: {
                        pjax: true
                    },
                    success: function(resp){
                        $('.act-admin .act-right').html(resp);
                        history.replaceState({}, "", url);
                        if(func) func();
                    }
                });
            }else{
                window.location.href = url;
            }
        },
        reload: function(){
            var url = location.href;
            Act.stack.load(url);
        }
    },
    Url: {
        toString: function(url, obj){
            search = Act.Url.serializeParams(obj);
            if (search) {
                url += "?" + search;
            }
            return url;
        },
        parseParams: function(str) {
            var k, obj, param, v, _i, _len, _ref, _ref1;
            obj = {};
            _ref = str.split('&');
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                param = _ref[_i];
                _ref1 = param.split('='), k = _ref1[0], v = _ref1[1];
                if (k) {
                    obj[k] = v;
                }
            }
            return obj;
        },
        serializeParams: function(obj) {
            var k, v;
            if (!obj) {
                return "";
            }
            return ((function() {
                var _results;
                _results = [];
                for (k in obj) {
                    v = obj[k];
                    _results.push([k, v].join('='));
                }
                return _results;
            })()).join('&');
        }
    }
}



