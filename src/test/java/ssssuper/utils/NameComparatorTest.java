package ssssuper.utils;

import com.github.promeg.pinyinhelper.Pinyin;
import utils.NameComparator;

import java.util.LinkedList;
import java.util.List;

public class NameComparatorTest {
    public static void main(String[] args) {
        NameComparator nameComparator = new NameComparator();
        List<String> names = new LinkedList<>();
        names.add("汪伯良");
        names.add("王得逞");
        names.add("安庆生");
        names.add("安庆");
        names.add("汪渝函");
        names.add("安请盛");
        names.add("安庆生");
        names.add("王雨薇");
        names.add("安请圣");
        names.add("汪波两");
        names.add("曾辉");
        names.add("陈迪");
        names.sort(nameComparator);

        System.out.println(names);

        System.out.println(Pinyin.toPinyin('曾'));
        System.out.println(Pinyin.toPinyin("曾",""));
        System.out.println(Pinyin.toPinyin('茜'));

        System.out.println(Pinyin.toPinyin("曾辉", ""));
        System.out.println(Pinyin.toPinyin("曾经", ""));
    }
}
