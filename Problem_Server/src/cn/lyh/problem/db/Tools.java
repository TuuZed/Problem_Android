package cn.lyh.problem.db;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

class Tools {
	String decode(String s) {
		byte[] bs = Base64.decodeBase64(s.replace(" ", "+"));
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

	JSONObject error(String msg) {

		JSONObject object = new JSONObject();
		try {
			object.put("code", 400);
			object.put("msg", msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}

	JSONObject success(String msg) {
		JSONObject object = new JSONObject();
		try {
			object.put("code", 100);
			object.put("msg", msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
}
