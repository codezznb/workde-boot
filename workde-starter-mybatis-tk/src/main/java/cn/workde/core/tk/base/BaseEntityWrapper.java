package cn.workde.core.tk.base;

import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhujingang
 * @date 2019/8/29 4:39 PM
 */
public abstract class BaseEntityWrapper<E, V> {

    /**
     * 单个实体类包装
     * @param entity
     * @return
     */
    public abstract V entityVO(E entity);

    /**
     * 实体类集合包装
     * @param list
     * @return
     */
    public List<V> listVO(List<E> list) {
        return list.stream().map(this::entityVO).collect(Collectors.toList());
    }

    /**
     * 分页实体类集合包装
     * @param pages
     * @return
     */
    public PageInfo<V> pageVO(PageInfo<E> pages) {
        List<V> records = listVO(pages.getList());
        PageInfo<V> pageVo = new PageInfo<>(records, 8);
        return pageVo;
    }
}
