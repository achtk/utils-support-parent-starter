package com.chua.tts.support.utils;

import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.function.Joiner;
import com.github.houbb.pinyin.constant.enums.PinyinStyleEnum;
import com.github.houbb.pinyin.util.PinyinHelper;
import com.rnkrsoft.bopomofo4j.Bopomofo4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SequenceUtils {
	/**
	 * 分隔英文字母
	 */
	static final Pattern EN_RE = Pattern.compile("([a-zA-Z]+)");


	static final Map<String, Integer> PH_2_ID_DICT = ImmutableBuilder.newHashMap();
	static final Map<Integer, String> ID_2_PH_DICT = ImmutableBuilder.newHashMap();

	static {
		int size = SymbolUtils.SYMBOL_CHINESE.length;
		for (int i = 0; i < size; i++) {
			PH_2_ID_DICT.put(SymbolUtils.SYMBOL_CHINESE[i], i);
			ID_2_PH_DICT.put(i, SymbolUtils.SYMBOL_CHINESE[i]);
		}
	}

	public static List<Integer> text2sequence(String text) {
		   
	   /* 	文本转为ID序列。
	    :param text:
	    :return:*/
		List<String> phs = text2phoneme(text);
		return phoneme2sequence(phs);
	}

	public static List<String> text2phoneme(String text) {
	    
	  /*  文本转为音素，用中文音素方案。
	    中文转为拼音，按照清华大学方案转为音素，分为辅音、元音、音调。
	    英文全部大写，转为字母读音。
	    英文非全部大写，转为英文读音。
	    标点映射为音素。
	    :param text: str,正则化后的文本。
	    :return: list,音素列表*/

		text = normalizeChinese(text);
		text = normalizeEnglish(text);
		//System.out.println(text);
		String pys = text2pinyin(text);
		List<String> phs = pinyin2phoneme(pys);
		phs = changeDiao(phs);
		return phs;
	}

	public static List<Integer> phoneme2sequence(List<String> src) {
		List<Integer> out = ImmutableBuilder.newArrayList();
		for (String w : src) {
			if (PH_2_ID_DICT.containsKey(w)) {
				out.add(PH_2_ID_DICT.get(w));
			}
		}
		return out;
	}


	public static List<String> changeDiao(List<String> src) {
	   /*
	    	拼音变声调，连续上声声调的把前一个上声变为阳平。
	    :param src: list,音素列表
	    :return: list,变调后的音素列表*/

		int flag = -5;
		List<String> out = ImmutableBuilder.newArrayList();
		Collections.reverse(src);
		int size = src.size();
		for (int i = 0; i < size; i++) {
			String w = src.get(i);
			if ("3".equals(w)) {
				if (i - flag == 4) {
					out.add("2");
				} else {
					flag = i;
					out.add(w);
				}
			} else {
				out.add(w);
			}
		}
		Collections.reverse(out);
		return out;
	}


	public static String text2pinyin(String text) {
		Bopomofo4j.local();//启用本地模式（也就是禁用沙盒）
		return PinyinHelper.toPinyin(text, PinyinStyleEnum.NUM_LAST, " ");
		//return Bopomofo4j.pinyin(text,1, false, false," ").replaceAll("0", "5");
	}


	static String normalizeChinese(String text) {
		text = ConvertUtils.quan2ban(text);
		text = ConvertUtils.fan2jian(text);
		text = NumberUtils.convertNumber(text);
		return text;
	}

	static String normalizeEnglish(String text) {
		Matcher matcher = EN_RE.matcher(text);
		LinkedList<Integer> postion = new LinkedList();
		while (matcher.find()) {
			postion.add(matcher.start());
			postion.add(matcher.end());
		}
		if (postion.size() == 0) {
			return text;
		}
		List<String> parts = ImmutableBuilder.newArrayList();
		parts.add(text.substring(0, postion.getFirst()));
		int size = postion.size() - 1;
		for (int i = 0; i < size; i++) {
			parts.add(text.substring(postion.get(i), postion.get(i + 1)));
		}
		parts.add(text.substring(postion.getLast()));
		LinkedList<String> out = new LinkedList();
		for (String part : parts) {
			out.add(part.toLowerCase());
		}
		return Joiner.on("").join(out);
	}

	public static List<String> pinyin2phoneme(String src) {
		String[] srcs = src.split(" ");
		List<String> out = ImmutableBuilder.newArrayList();
		for (String py : srcs) {
			List<String> phs = ImmutableBuilder.newArrayList();

			if (PhonemeUtils.pinyin2ph_dict.containsKey(py)) {
				String[] ph = PhonemeUtils.pinyin2ph_dict.get(py).split(" ");
				List<String> list = new ArrayList<>(ph.length);
				Collections.addAll(list, ph);
				phs.addAll(list);
			} else {
				String[] pys = py.split("");
				for (String w : pys) {
					List<String> ph = py_errors(w);
					phs.addAll(ph);
				}
			}
			phs.add(SymbolUtils.CHAIN);  // 一个字符对应一个chain符号
			out.addAll(phs);
		}
		out.add(SymbolUtils.EOS);
		out.add(SymbolUtils.PAD);
		return out;
	}

	static List<String> py_errors(String text) {
		List<String> out = ImmutableBuilder.newArrayList();
		String[] texts = text.split("");
		for (String p : texts) {
			if (PhonemeUtils.char2ph_dict.containsKey(p)) {
				out.add(PhonemeUtils.char2ph_dict.get(p));
			}
		}
		return out;
	}

	public static void main(String[] args) {
		System.out.println(normalizeEnglish("我hello,I love you 我是你"));
		System.out.println(text2pinyin("这是实力很牛逼,"));
		System.out.println(text2phoneme("这是实力很牛逼,"));
		System.out.println(text2sequence("这是实力很牛逼,"));
		//System.out.println(MessageFormat.format("{0}","1"));
	}

}
