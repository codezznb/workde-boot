var builder = Packages.cn.workde.core.builder.engine.Builder.getInstance(),
    StringUtil = Packages.cn.workde.core.builder.utils.StringUtil,
    DateUtil = Packages.cn.workde.core.builder.utils.DateUtil,
    WebUtil = Packages.cn.workde.core.builder.utils.WebUtil,
    SysUtil = Packages.cn.workde.core.builder.utils.SysUtil,
    System = java.lang.System,
    StringBuilder = java.lang.StringBuilder,
    File = java.io.File,
    HashMap = java.util.HashMap,
    JavaString = java.lang.String,
    Integer = java.lang.Integer,
    Double = java.lang.Double,
    JavaDate = java.util.Date,
    JSONArray = org.json.JSONArray,
    JSONObject = org.json.JSONObject,
    Wb = {
        getApp: function(request, response) {

            var app = request.getAttribute('sysx.app');
            if (app && request.getAttribute('sysx.resp') === response) return app;

            app = {
                log: function(object) {
                    System.out.println(object);
                },
                send: function(object) {
                    WebUtil.send(response, object);
                },
                sendObject: function(object){
                    WebUtil.sendObject(response, object);
                },
                get: function(name, returnString) {
                    if(name == undefined) {
                        var jo = WebUtil.fetch(request),
                            result = {};
                        jo.toMap().entrySet().forEach(function(e) {
                            result[e.key] = e.value;
                        });
                        return result;
                    }else {
                        return returnString ? WebUtil.fetch(request, name) : WebUtil.fetchObject(request, name);
                    }
                },
                query: function(sql, config, returnScript) {
                    var configText, newConfig, dp = new Packages.cn.workde.core.builder.controls.DpControl();
                    app.log(config);
                    dp.request = request;
                    dp.response = response;
                    if (config) {
                        newConfig = {};
                        Wb.each(config, function(key, value) {
                            if (Wb.isObject(value))
                                value = Wb.encode(value);
                            else value = String(value);
                            newConfig[key] = value;
                        });
                        configText = Wb.encode(newConfig);
                    } else {
                        configText = '{}';
                    }
                    dp.configs = new JSONObject(configText);
                    dp.configs.put('sql', sql);
                    if (returnScript) return dp.getContent(false);
                    else dp.create();
                }

            };

            //end app
            request.setAttribute('sysx.app', app);
            request.setAttribute('sysx.resp', response);
            return app;

        },
        /**
         * 把对象转换成以字符串形式表示的值。
         * @param {Object} object 转换的对象。
         * @return {String} 字符串形式表示的值。
         */
        toString: function(object) {
            return toString.call(object);
        },
        /**
         * 判断是否是JS字符串。
         * @return {Boolean} true是，false不是。
         */
        isString: function(object) {
            return typeof object === 'string';
        },
        /**
         * 判断是否是JS数字。
         * @return {Boolean} true是，false不是。
         */
        isNumber: function(object) {
            return typeof object === 'number' && isFinite(object);
        },
        /**
         * 判断是否是JS对象。
         * @param {Object} object 判断的对象。
         * @return {Boolean} true是，false不是。
         */
        isObject: function(object) {
            return Wb.toString(object) === '[object Object]';
        },
        /**
         * 判断是否是JS数组或Java数组。
         * @param {Object} object 判断的对象。
         * @return {Boolean} true是，false不是。
         */
        isArray: function(object) {
            return Wb.toString(object) === '[object Array]' || SysUtil.isArray(object);
        },
        /**
         * 判断是否是JS日期。
         * @param {Object} object 判断的对象。
         * @return {Boolean} true是，false不是。
         */
        isDate: function(object) {
            return Wb.toString(object) === '[object Date]' && !isNaN(object);
        },
        /**
         * 判断是否是JS布尔型。
         * @param {Object} object 判断的对象。
         * @return {Boolean} true是，false不是。
         */
        isBoolean: function(object) {
            return typeof object === 'boolean';
        },
        /**
         * 判断是否是JS函数。
         * @param {Object} object 判断的对象。
         * @return {Boolean} true是，false不是。
         */
        isFunction: function(object) {
            return typeof object === 'function';
        },
        /**
         * 对字符、数字、日期、对象和数组等进行编码，并转换为以字符串表示的值。
         * @param {Object} o 需要编码的值。
         * @return {String} 编码后的值。
         */
        encode: function(o) {
            var buf, addComma;
            if (o === null || o === undefined || Wb.isFunction(o)) {
                return 'null'; //Java无undefined类型，因此通一为null。
            } else if (typeof o == 'boolean') {
                return String(o);
            } else if (typeof o == 'number') {
                return isFinite(o) ? String(o) : 'null';
            } else if (Wb.isDate(o)) {
                return '"' + Wb.dateToStr(o) + '"';
            } else if (o instanceof JavaDate) {
                return '"' + DateUtil.dateToStr(o) + '"';
            } else if (Wb.isArray(o)) {
                buf = new StringBuilder('[');
                addComma = false;
                Wb.each(o, function(value) {
                    if (addComma)
                        buf.append(',');
                    else
                        addComma = true;
                    buf.append(Wb.encode(value));
                });
                buf.append(']');
                return buf.toString();
            } else if (SysUtil.isIterable(o)) {
                buf = new StringBuilder('[');
                addComma = false;
                o.forEach(function(value) {
                    if (addComma)
                        buf.append(',');
                    else
                        addComma = true;
                    buf.append(Wb.encode(value));
                });
                buf.append(']');
                return buf.toString();
            } else if (Wb.isObject(o)) {
                buf = new StringBuilder('{');
                addComma = false;
                Wb.each(o, function(name, value) {
                    if (addComma)
                        buf.append(',');
                    else
                        addComma = true;
                    buf.append(StringUtil.quote(name));
                    buf.append(':');
                    buf.append(Wb.encode(value));
                });
                buf.append('}');
                return buf.toString();
            } else if (SysUtil.isMap(o) && o.entrySet) {
                buf = new StringBuilder('{');
                addComma = false;
                o.entrySet().forEach(function(entry) {
                    if (addComma)
                        buf.append(',');
                    else
                        addComma = true;
                    buf.append(StringUtil.quote(entry.key));
                    buf.append(':');
                    buf.append(Wb.encode(entry.value));
                });
                buf.append('}');
                return buf.toString();
            } else
                return StringUtil.encode(o);
        },
        /**
         * 遍历数组或对象的各个元素。
         * @param {Array/Object/JSONArray/JSONObject/Map} data 遍历的数组或对象。
         * @param {Function} fn 每遍历一个元素所执行的回调方法。对于数组传递的参数为值和索引，对于对象传递的参数为名称和值。
         * 如果函数返回false，将中断遍历。
         * @param {Boolean} [reverse] 是否倒序遍历，仅适用于数组类型值的遍历，默认为false。
         * @return {Boolean} true遍历完成，否则返回索引号（数组）或false（对象）。
         */
        each: function(data, fn, reverse) {
            if (!data)
                return;
            if (Wb.isArray(data) || data instanceof java.util.ArrayList) {
                var i, j = data.length;
                if (reverse !== true) {
                    for (i = 0; i < j; i++) {
                        if (fn(data[i], i) === false) {
                            return i;
                        }
                    }
                } else {
                    for (i = j - 1; i > -1; i--) {
                        if (fn(data[i], i) === false) {
                            return i;
                        }
                    }
                }
            } else if (data instanceof JSONArray) {
                var m, n = data.length();
                if (reverse !== true) {
                    for (m = 0; m < n; m++) {
                        if (fn(data.get(m), m) === false) {
                            return m;
                        }
                    }
                } else {
                    for (m = n - 1; m > -1; m--) {
                        if (fn(data.get(m), m) === false) {
                            return m;
                        }
                    }
                }
            } else if ((SysUtil.isMap(data) || data instanceof JSONObject) && data.entrySet) {
                data.entrySet().forEach(function(entry) {
                    if (fn(entry.key, entry.value) === false) {
                        return false;
                    }
                });
            } else {
                var property;
                for (property in data) {
                    if (data.hasOwnProperty(property)) {
                        if (fn(property, data[property]) === false) {
                            return false;
                        }
                    }
                }
            }
            return true;
        },

    };
