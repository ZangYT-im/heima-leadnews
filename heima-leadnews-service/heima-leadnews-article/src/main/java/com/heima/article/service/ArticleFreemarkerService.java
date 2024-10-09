package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * <p>
 * description
 * </p>
 *
 * @author zangyt
 * @since 2024/10/9 22:44
 */
public interface ArticleFreemarkerService {

    /**
     * 生成静态文件上传到 minio 中
     * @param apArticle
     * @param content
     */
    public void buildArticleToMinIo(ApArticle apArticle,String content) throws IOException, TemplateException;
}
