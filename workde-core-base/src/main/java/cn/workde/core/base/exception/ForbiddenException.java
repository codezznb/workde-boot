package cn.workde.core.base.exception;

/**
 * 无权限异常
 * @author zhujingang
 * @date 2019/8/28 6:10 PM
 */
public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = -7818277682527873103L;

    public ForbiddenException(String msg) {
        super(msg);
    }
}
