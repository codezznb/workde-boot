package cn.workde.core.base.exception;

/**
 * 参数校验不通过
 * @author zhujingang
 * @date 2019/8/28 6:10 PM
 */
public class ParamException extends RuntimeException {

    private static final long serialVersionUID = -7818277682527873103L;

    public ParamException(String msg) {
        super(msg);
    }
}
