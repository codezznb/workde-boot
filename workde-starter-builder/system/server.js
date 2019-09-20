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
                },
                /**
                 * 运行SQL语句，并获取返回结果对象。
                 * @param {String} sql 运行的SQL语句。
                 * @param {Object} [config] 配置对象。
                 * @param {String} config.arrayName 执行批处理时，指定数据源来自request中存储的该变量。
                 * @param {JSONArray/Array/String} config.arrayData 执行批处理时，指定数据源来自该值。
                 * @param {Boolean} config.batchUpdate 是否允许批处理操作。
                 * @param {String} config.errorText 当该值不为空且查询结果集不为空，系统将抛出该信息的异常。
                 * @param {String} config.type  执行何种SQL操作，可为"query","update","execute","call"，默认为自动。
                 * @param {String} config.transaction 执行何种数据库事务操作，可为"start","commit","none"。
                 * @param {String} config.isolation 数据库事务隔离级别，可为"readCommitted","readUncommitted","repeatableRead","serializable"。
                 * @param {Boolean} config.uniqueUpdate 指定插入、更改或删除记录操作是否有且只有1条。
                 * @return {Object} 运行SQL语句获得的结果，可能值为结果集，影响记录数或输出参数结果Map。
                 */
                run: function(sql, config) {
                    var arrayData,
                        query = new Packages.cn.workde.core.builder.db.Query();
                    if (config && config.arrayData) {
                        arrayData = config.arrayData;
                        if (!(arrayData instanceof JSONArray)) {
                            if (Wb.isArray(arrayData))
                                arrayData = Wb.reverse(arrayData);
                            else
                                arrayData = new JSONArray(arrayData);
                            config.arrayData = arrayData;
                        }
                    }
                    Wb.apply(query, {
                        request: request,
                        sql: sql
                    }, config);
                    return query.run();
                },
                /**
                 * 执行上下文绑定的insert, update, delete数据库更新操作。
                 * @param {Object} config 配置参数对象，见Updater控件的使用。
                 */
                update: function(configs) {
                    var updater = new Packages.cn.workde.core.builder.db.Updater();

                    if (Wb.isObject(configs.fieldsMap))
                        configs.fieldsMap = Wb.toJSONObject(configs.fieldsMap);
                    Wb.apply(updater, {
                        request: request
                    }, configs);
                    updater.run();
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
        apply: function(object, config, defaults) {
            if (defaults)
                Wb.apply(object, defaults);
            if (object && config) {
                var name;
                for (name in config)
                    object[name] = config[name];
            }
            return object;
        },
        /**
         * 在JS和Java类型之间反转指定值类型。返转的值类型为Date/JavaDate,Object/JSONObject,Array/JSONArray,其他按原值返回。
         * @param {Object} value 需要反转类型的值。
         * @return {Object} 反转类型后的值。
         */
        reverse: function(value) {
            if (value instanceof JavaDate)
                return Wb.createDate(value.getTime());
            else if (Wb.isDate(value))
                return new JavaDate(value.getTime());
            else if (Wb.isObject(value))
                return Wb.toJSONObject(value);
            else if (Wb.isArray(value))
                return Wb.toJSONArray(value);
            else if (value instanceof JSONArray || value instanceof JSONObject)
                return Wb.decode(value.toString());
            else
                return value;
        },
        /**
         * 把JS的Object对象转换为Java的JSONObject对象。如果参数不是Object对象，将直接返回此参数。
         * 如果object中的值为原始值，可以使用new JSONObject(object)替代。
         * @param {Object} object 需要转换的对象。
         * @return {JSONObject} 转换后的JSONObject对象或object参数原值。
         */
        toJSONObject: function(object) {
            if (Wb.isObject(object)) {
                var jo = new JSONObject();
                Wb.each(object, function(k, v) {
                    if (v === null || v === undefined || Wb.isFunction(v))
                        v = null;
                    else if (Wb.isObject(v))
                        v = Wb.toJSONObject(v);
                    else if (Wb.isArray(v))
                        v = Wb.toJSONArray(v);
                    else if (Wb.isDate(v))
                        v = new JavaDate(v.getTime());
                    jo.put(k, v);
                });
                return jo;
            }
            return object;
        },
        /**
         * 把JS的Array对象转换为Java的JSONArray对象。如果参数不是Array对象，将直接返回此参数。
         * 如果array中的值为原始值，可以使用new JSONArray(array)替代。
         * @param {Array} array 需要转换的数组。
         * @return {JSONArray} 转换后的JSONArray对象或array参数原值。
         */
        toJSONArray: function(array) {
            if (Wb.isArray(array)) {
                var ja = new JSONArray();
                Wb.each(array, function(v) {
                    if (v === null || v === undefined || Wb.isFunction(v))
                        v = null;
                    else if (Wb.isObject(v))
                        v = Wb.toJSONObject(v);
                    else if (Wb.isArray(v))
                        v = Wb.toJSONArray(v);
                    else if (Wb.isDate(v))
                        v = new JavaDate(v.getTime());
                    ja.put(v);
                });
                return ja;
            }
            return array;
        },

    };
