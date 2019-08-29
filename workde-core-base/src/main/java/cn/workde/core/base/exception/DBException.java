package cn.workde.core.base.exception;

import lombok.NoArgsConstructor;

/**
 * @author zhujingang
 * @date 2019/8/28 6:06 PM
 */
@NoArgsConstructor
public class DBException extends RuntimeException {

    public DBException(String msg) {
        super(msg);
    }
}
