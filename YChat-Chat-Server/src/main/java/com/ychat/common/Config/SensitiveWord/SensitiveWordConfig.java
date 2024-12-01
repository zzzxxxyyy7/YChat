package com.ychat.common.Config.SensitiveWord;

import com.ychat.common.SensitiveWord.DFAFilter;
import com.ychat.common.SensitiveWord.SensitiveWordBootStrap;
import com.ychat.common.SensitiveWord.YChatWordFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensitiveWordConfig {

    @Autowired
    private YChatWordFactory yChatWordFactory;

    /**
     * 注册敏感词引导类
     *
     * @return 初始化引导类
     * @since 1.0.0
     */
    @Bean
    public SensitiveWordBootStrap sensitiveWordBs() {
        return SensitiveWordBootStrap.newInstance()
                .filterStrategy(DFAFilter.getInstance()) // 指定敏感词引导类使用的敏感词过滤算法
                .sensitiveWord(yChatWordFactory) // 指定敏感词引导类使用的敏感词工厂
                .init();
    }

}