package cn.workde.core.base.exception;

/**
 * 登录异常
 * @author zhujingang
 * @date 2019/8/28 6:10 PM
 */
public class LoginException extends RuntimeException {

    private static final long serialVersionUID = -7818277682527873103L;

    public LoginException(String msg) {
        super(msg);
    }
}
