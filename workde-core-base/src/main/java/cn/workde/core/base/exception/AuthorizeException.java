package cn.workde.core.base.exception;

/**
 * Admin登录异常
 * @author zhujingang
 * @date 2019/8/28 6:10 PM
 */
public class AuthorizeException extends RuntimeException {

    private static final long serialVersionUID = -7818277682527873103L;

    public AuthorizeException(String msg) {
        super(msg);
    }
}
