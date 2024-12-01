package com.ychat.common.SensitiveWord;

import com.ychat.common.User.Dao.SensitiveWordDao;
import com.ychat.common.User.Domain.entity.SensitiveWord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 敏感词工厂
 */
@Component
@Slf4j
public class YChatWordFactory implements IWordFactory {

    @Autowired
    private SensitiveWordDao sensitiveWordDao;

    @Override
    public List<String> getWordList() {
        return sensitiveWordDao.list()
                .stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toList());
    }

}
