package cn.workde.core.sample.boot.service;

import cn.workde.core.sample.boot.entity.Article;
import cn.workde.core.sample.boot.mapper.ArticleMapper;
import cn.workde.core.tk.base.BaseService;
import org.springframework.stereotype.Service;

/**
 * @author zhujingang
 * @date 2019/9/1 9:18 PM
 */
@Service
public class ArticleService extends BaseService<Article, ArticleMapper> {
}
