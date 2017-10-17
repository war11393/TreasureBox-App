package com.treasurebox.titwdj.treasurebox.Model.robot;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RobotImpl implements Robot{
	private static final String TAG = "Robot";
	String reqbody2 = "key=4148a67384a84fcca210863253f081d2&info=\"str\"&loc=\"北京市中关村\"&userid=\"123456\"";
	private String respon = "";

	@Override
	public void talk(String msg) {
		final String body = reqbody2.replaceAll("str", msg);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					OkHttpClient client = new OkHttpClient();
					Request req = new Request.Builder().url("http://www.tuling123.com/openapi/api?"+body).build();
					Response resp = client.newCall(req).execute();
					String res = resp.body().string();
					response(res);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void response(String data) {
		try {
			JSONObject obj = new JSONObject(data);
			this.respon = obj.getString("text");
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public String getResp() {
		if (!respon.equals(""))
			return respon;
		else
			return "没东西啊";
	}
}
