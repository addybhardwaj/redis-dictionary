package com.intelladept.oss.redis.dictionary;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Methods to removeDecoratingCharacters phrases for instance, remove dots, brackets, etc
 *
 * @author Aditya Bhardwaj
 */
@Named
public class PhraseSplitter {

    private String decoratingCharacters = "{}\\(\\)\\[\\]\"\\\\";
    private String decoratingCharRegEx;
    private String joinCharacters = "\\.@\\',";
    private String joinCharRegEx;

    @PostConstruct
    public void afterPropertiesSet() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(decoratingCharacters)
                .append("]");
        decoratingCharRegEx = sb.toString();
        sb = new StringBuilder("[");
        sb.append(joinCharacters).append("]");
        joinCharRegEx = sb.toString();
    }

    public Set<String> cleanseAndSplitPhrase(final String phrase, boolean preserveAll) {
        if (phrase == null) {
            return null;
        }
        String cleansedPhrase = phrase.replaceAll(joinCharRegEx, " ");
        cleansedPhrase = cleansedPhrase.replaceAll(decoratingCharRegEx, StringUtils.EMPTY);
        cleansedPhrase = cleansedPhrase.trim();
        String[] words = StringUtils.split(cleansedPhrase);
        Set<String> cleansedWords = new TreeSet<String>();
        if (!preserveAll) {
            if(!ArrayUtils.isEmpty(words)) {
                for(String word : words) {
                    if (word.length() > 2) {
                        cleansedWords.add(word);
                    }
                }
            }
        } else {
            cleansedWords.addAll(Arrays.asList(words));
        }
        return cleansedWords;
    }

    public void setDecoratingCharacters(String decoratingCharacters) {
        this.decoratingCharacters = decoratingCharacters;
    }

    public void setJoinCharacters(String joinCharacters) {
        this.joinCharacters = joinCharacters;
    }
}
