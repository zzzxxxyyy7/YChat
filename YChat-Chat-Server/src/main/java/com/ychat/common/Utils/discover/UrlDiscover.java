package com.ychat.common.Utils.Discover;

import com.ychat.Domain.Dto.UrlInfo;
import cn.hutool.core.date.StopWatch;
import org.jsoup.nodes.Document;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * 文本消息 - URL 解析视图接口
 */
public interface UrlDiscover {

    @Nullable
    Map<String, UrlInfo> getUrlContentMap(String content);

    /**
     * 将文本解析为对应 URL 属性
     * @param url
     * @return
     */
    @Nullable
    UrlInfo getContent(String url);

    @Nullable
    String getTitle(Document document);

    @Nullable
    String getDescription(Document document);

    @Nullable
    String getImage(String url, Document document);

}
