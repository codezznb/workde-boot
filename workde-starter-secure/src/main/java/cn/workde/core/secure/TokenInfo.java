package cn.workde.core.secure;

import lombok.Data;

/**
 * @author zhujingang
 * @date 2019/8/29 5:09 PM
 */
@Data
public class TokenInfo {

    /**
     * 令牌值
     */
    private String token;

    /**
     * 过期秒数
     */
    private int expire;

}
