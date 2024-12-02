package com.ychat.common.SensitiveWord;

import com.ychat.common.SensitiveWord.acpro.ACProTrie;
import io.micrometer.core.instrument.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 基于ACFilter的优化增强版本
 */
public class ACProFilter implements SensitiveWordFilter{

    private ACProTrie acProTrie;

    @Override
    public boolean hasSensitiveWord(String text) {
        if(StringUtils.isBlank(text)) return false;
        return !Objects.equals(filter(text),text);
    }

    @Override
    public String filter(String text) {
        return acProTrie.match(text);
    }

    @Override
    public void loadWord(List<String> words) {
        if (words == null) return;
        acProTrie = new ACProTrie();
        acProTrie.createACTrie(words);
    }

}