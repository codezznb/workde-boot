package cn.workde.core.base.result;

/**
 * @author zhujingang
 * @date 2019/8/29 10:19 AM
 */
public class ResultPrompt {

    //---------------------直接定义异常---------------------

    /** 超出最大limit限制 */
    public static final String MAX_LIMIT = "超出最大limit限制";

    /** 分布式锁 */
    public static final String DISTRIBUTED_LOCK = "分布式锁...";

    /** 多行插入错误 */
    public static final String INSERT_BATCH_ERROR = "执行多行插入命令失败，可能原因是：数据结构异常或无ID主键。请立即检查数据的一致性、唯一性。";

    /** 单行删除错误 */
    public static final String DELETE_ERROR = "执行单行删除命令失败，可能原因是：数据结构异常或无ID主键。请立即检查数据的一致性、唯一性。";

    /** 批次删除错误 */
    public static final String DELETE_BATCH_ERROR = "执行批次删除命令失败，可能原因是：数据结构异常或无ID主键。请立即检查数据的一致性、唯一性。";

    /** 批次更新错误 */
    public static final String UPDATE_BATCH_ERROR = "执行批次更新命令失败，可能原因是：数据结构异常或无ID主键。请立即检查数据的一致性、唯一性。";


    //---------------------引用定义异常---------------------

    /**
     * 数据结构异常-不正确的结果
     * @param expected	预期内容
     * @param actual	实际内容
     * @return 提示信息
     */
    public static String dataStructure(Object expected, Object actual) {
        return ResultEnum.DATA_STRUCTURE.getMsg() + " Incorrect result size: expected " + expected + ", actual " + actual;
    }
}
