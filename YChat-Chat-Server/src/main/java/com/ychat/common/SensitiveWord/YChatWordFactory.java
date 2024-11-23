package com.ychat.common.SensitiveWord;

import com.ychat.common.user.dao.SensitiveWordDao;
import com.ychat.common.user.domain.entity.SensitiveWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
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