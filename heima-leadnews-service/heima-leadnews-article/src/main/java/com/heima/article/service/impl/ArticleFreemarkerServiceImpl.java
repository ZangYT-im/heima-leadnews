package com.heima.article.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * description
 * </p>
 *
 * @author zangyt
 * @since 2024/10/9 22:47
 */

@Service
@Slf4j
@Transactional
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {


    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;


    @Autowired
    private ApArticleService apArticleService;


    /**
     * 生成静态文件上传到 minio 中
     *
     * @param apArticle
     * @param content
     */
    @Override
    @Async
    public void buildArticleToMinIo(ApArticle apArticle, String content) throws IOException, TemplateException {
        if (StringUtils.isNotBlank(content)) {
            //2.文章内容通过freemarker生成html文件
            StringWriter out = new StringWriter();
            Template template = configuration.getTemplate("article.ftl");

            Map<String, Object> params = new HashMap<>();
            params.put("content", JSONArray.parseArray(content));
            System.out.println(JSONArray.parseArray(content));

            template.process(params, out);
            InputStream is = new ByteArrayInputStream(out.toString().getBytes());

            //3.把html文件上传到minio中
            String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", is);

            //4.修改ap_article表，保存static_url字段
//            ApArticle article = new ApArticle();
//            article.setId(apArticleContent.getArticleId());
//            article.setStaticUrl(path);
//            apArticleMapper.updateById(article);

            apArticleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId,
                    apArticle.getId()).set(ApArticle::getStaticUrl, path));

        }
    }
}
