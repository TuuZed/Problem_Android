package cn.lyh.problem.test;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

class ToolsTest {
	String decode(String s) {
		byte[] bs = Base64.decodeBase64(s);
		String decode = null;
		try {
			decode = new String(bs, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decode;
	}

	String encode(String s) {
		String encode = null;
		try {
			encode = Base64.encodeBase64String(s.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encode;
	}

}
