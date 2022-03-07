package com.example.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class HttpUtil {

	public static String post(String postpath, Map<String, Object> parammap) {
		return post(postpath, JSONObject.toJSONString(parammap), null);
	}

	public static JSONObject post(Map<String, Object> parammap, String postpath) {
		String responseString = post(postpath, JSONObject.toJSONString(parammap), null);
		return JSON.parseObject(responseString);
	}

	public static String postObjectForString(String url, Object params, Integer timeOut) {
		String responseString = post(url, JSONObject.toJSONString(params), timeOut);
		return responseString;
	}

	public static JSONObject postJson(String url, String json) {
		String responseString = post(url, json, null);
		return JSON.parseObject(responseString);
	}

	public static String getByEncodeParam(String url, Map<String, ? extends Object> paramsMap) {
		String params = "";
		if (paramsMap != null) {
			StringBuilder builder = new StringBuilder();
			paramsMap.forEach((k, v) -> {
				try {
					builder.append(k).append("=").append(URLEncoder.encode(v.toString(), "utf-8")).append("&");
				} catch (UnsupportedEncodingException e) {
					log.info("encode error", e);
				}
			});
			String s = builder.toString();
			params = s.substring(0, s.length() - 1);
		}
		return get(url, params);
	}

	public static String post(String url, String param, Integer timeOut) {
		return post(url, param, null, timeOut);
	}

	/***
	 * FormPost-Okhttp3
	 * @param url
	 * @param param
	 * @param header
	 * @return
	 */
	public static String okhttpPostForm(String url, String param, Map<String, String> header){
		MultipartBody.Builder bodyBuilder =
				new MultipartBody.Builder()
						.setType(MultipartBody.FORM);
		JSONObject object = JSONObject.parseObject(param);
		object.keySet().forEach(key->{
			Object value = object.get(key);
			if(value != null){
				bodyBuilder.addFormDataPart(key, value.toString());
			}
		});
		RequestBody requestBody = bodyBuilder.build();

		Request.Builder requestBuilder = new Request.Builder();
		header.keySet().forEach(key->{
			Object value = header.get(key);
			if(value != null){
				requestBuilder.addHeader(key, value.toString());
			}
		});
		Request request = requestBuilder.url(url).post(requestBody).build();

		String result = null;
		try{
			OkHttpClient client = new OkHttpClient();
			Response response = client.newCall(request).execute();
			if(response.body() != null){
				result = response.body().string();
			}
		}catch (IOException ioException){
			log.error("请求失败，发生IO异常！", ioException);
		}finally {
			log.info("请求地址：{} \n 请求参数：header:{} \n body:{} \n 返回：{}", url, JSON.toJSONString(header), param, result);
		}

		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 *
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String post(String url, String param, Map<String, String> header, Integer timeOut) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-type", "application/json; charset=utf-8");
			conn.setRequestProperty("Accept", "application/json");

			if(header != null){
				for(String key : header.keySet()){
					conn.setRequestProperty(key, header.get(key));
				}
			}
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			if (timeOut == null) {
				conn.setConnectTimeout(10 * 1000);
			} else {
				conn.setConnectTimeout(timeOut);
			}
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			log.error("发送post请求异常", e);
		}
		//使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		log.info("请求地址：{} \n 请求参数：header:{} \n body:{} \n 返回：{}", url, JSON.toJSONString(header), param, result);
		return result;
	}

	public static String get(String url, Map<String, ? extends Object> paramsMap) {
		return get(url, paramsMap, null);
	}

	public static String get(String url, Map<String, ? extends Object> paramsMap, Map<String, ? extends Object> headersMap) {
		String params = "";
		if (paramsMap != null) {
			StringBuilder builder = new StringBuilder();
			paramsMap.forEach((k,v)->{
				builder.append(k).append("=").append(v.toString()).append("&");
			});
			String s = builder.toString();
			params = s.substring(0, s.length() -1);
		}
		return get(url, params, headersMap);
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 *
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String get(String url, String param) {
		return get(url, param, null);
	}

	public static String get(String url, String param, Map<String, ? extends Object> headersMap) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			if (!"".equals(param)) {
				urlNameString += "?" + param;
			}
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			if(headersMap != null){
				for(String header : headersMap.keySet()){
					connection.setRequestProperty(header, headersMap.get(header).toString());
				}
			}
			// 建立实际的连接
			connection.connect();
			connection.setConnectTimeout(10 * 1000);
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			log.error("发送get请求异常", e);
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		log.info("请求地址：{} \n 请求参数：{} \n 返回：{}", url, param, result);
		return result;
	}

	public static byte[] get4DownLoad(String url, Map<String, String> paramsMap) {
		String params = "";
		if (paramsMap != null) {
			StringBuilder builder = new StringBuilder();
			paramsMap.forEach((k,v)->{
				builder.append(k).append("=").append(v).append("&");
			});
			String s = builder.toString();
			params = s.substring(0, s.length() -1);
		}
		return get4DownLoad(url, params);
	}

	public static InputStream getUrlStream(String url){
		InputStream in = null;
		try {
		URL realUrl = new URL(url);
		// 打开和URL之间的连接
		URLConnection connection = realUrl.openConnection();
		// 设置通用的请求属性
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		// 建立实际的连接
		connection.connect();
		connection.setConnectTimeout(10 * 1000);
		// 获取所有响应头字段
		Map<String, List<String>> map = connection.getHeaderFields();
		// 遍历所有的响应头字段
		for (String key : map.keySet()) {
			System.out.println(key + "--->" + map.get(key));
		}
		// 定义 BufferedReader输入流来读取URL的响应
		in = connection.getInputStream();
		return in;
		}catch (Exception e) {
			log.error("获取输入流异常", e);
			return null;
		}
	}

	public static byte[] get4DownLoad(String url, String param) {
		byte[] result = null;
		InputStream in = null;
		try {
			String urlNameString = url;
			if (param != null && !"".equals(param)) {
				urlNameString += "?" + param;
			}
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			connection.setConnectTimeout(10 * 1000);
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.out.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = connection.getInputStream();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int line = 0;
			while ((line = in.read(buff, 0, buff.length)) > 0) {
				byteArrayOutputStream.write(buff, 0, line);
			}
			result = byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			log.error("发送get请求异常", e);
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		log.info("请求地址：{} \n 请求参数：{} \n", url, param);
		return result;
	}

	/***
	 * 发送带文件上传的http post
	 *
	 * @// TODO: 2020/9/14  注：原先没有这种case，开始想用JDK11的api，结果发现还得升版本
	 * @// TODO: 2020/9/14  也不想用XXHttpclient了，不便于和上面强哥的代码统一风格
	 * @// TODO: 2020/9/14  基于w3c发文的协议自实现一下，面向过程的代码可读性不高，以后有机会还是重构下
	 *
	 * @param url
	 * @param param
	 * @param header
	 * @param uploadFileInputStream
	 * @param fileFormName
	 * @param fileOriginName
	 * @param timeOut
	 * @return
	 */
	public static String postFormDataFile(String url,
								   Map<String, String> param,
								   Map<String, String> header,
								   InputStream uploadFileInputStream,
								   String contentType,
								   String fileFormName,
								   String fileOriginName,
								   Integer timeOut){
		BufferedReader in = null;
		DataOutputStream dataOutputStream = null;
		String result = "";
		try {
			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
			String boundary = UUID.randomUUID().toString();
			String newLine = "\r\n";
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			HttpURLConnection httpURLConnection = (HttpURLConnection)conn;
			// 设置通用的请求属性
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			boundary = "--" + boundary;
			if(header != null){
				for(String key : header.keySet()){
					conn.setRequestProperty(key, header.get(key));
				}
			}
			if (timeOut == null) {
				conn.setConnectTimeout(10 * 1000);
			} else {
				conn.setConnectTimeout(timeOut);
			}

			contentType = Strings.isEmpty(contentType) ? "application/octet-stream" : contentType;
			dataOutputStream = new DataOutputStream(conn.getOutputStream());
			dataOutputStream.writeBytes(boundary + newLine);
			dataOutputStream.writeBytes(
					String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", fileFormName, fileOriginName) + newLine);
			dataOutputStream.writeBytes("Content-Type: ".concat(contentType) + newLine);
			dataOutputStream.writeBytes(newLine);

			byte[] buffer = new byte[1024];
			int offst;
			while( (offst = uploadFileInputStream.read(buffer, 0, buffer.length)) > 0 ){
				dataOutputStream.write(buffer, 0, offst);
			}
			dataOutputStream.writeBytes(newLine);
			dataOutputStream.writeBytes(boundary.concat(param != null && param.size() > 0 ? "" : "--") + newLine);

			int i = 0;
			if(param != null){
				for(String key : param.keySet()){
					if(param.get(key) == null){
						continue;
					}
					Object value = param.get(key);
					if(value instanceof String && StringUtils.isEmpty(value.toString())){
						continue;
					}
					dataOutputStream.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"", key) + newLine);
					dataOutputStream.writeBytes(newLine + value + newLine);
					dataOutputStream.writeBytes(boundary);
					i++;
					dataOutputStream.writeBytes(i == param.size() ? "--" + newLine : newLine);
				}
			}

			dataOutputStream.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			log.error("发送post请求异常", e);
		}
		//使用finally块来关闭输出流、输入流
		finally {
			try {
				if (dataOutputStream != null) {
					dataOutputStream.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		log.info("请求地址：{} \n 请求参数：header:{} \n body:{} \n 返回：{}", url, JSON.toJSONString(header), param, result);
		return result;
	}

	public static String postFormDataFileV2(String url,
										  Map<String, String> param,
										  Map<String, String> header,
										  InputStream uploadFileInputStream,
										  String contentType,
										  String fileFormName,
										  String fileOriginName,
										  Integer timeOut){
		String result = "";
		try{
			CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
			String boundary = UUID.randomUUID().toString();
			String newLine = "\r\n";
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			HttpURLConnection httpURLConnection = (HttpURLConnection)conn;
			// 设置通用的请求属性
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

			boundary = "--" + boundary;
			if(header != null){
				for(String key : header.keySet()){
					conn.setRequestProperty(key, header.get(key));
				}
			}
			if (timeOut == null) {
				conn.setConnectTimeout(10 * 1000);
			} else {
				conn.setConnectTimeout(timeOut);
			}

			contentType = Strings.isEmpty(contentType) ? "application/octet-stream" : contentType;
			OutputStream outputStream = conn.getOutputStream();
			try(DataOutputStream dataOutputStream = new DataOutputStream(outputStream)){
				dataOutputStream.writeBytes(boundary + newLine);
				dataOutputStream.writeBytes(
						String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"", fileFormName, fileOriginName) + newLine);
				dataOutputStream.writeBytes("Content-Type: ".concat(contentType) + newLine);
				dataOutputStream.writeBytes(newLine);

				byte[] buffer = new byte[1024];
				int offst;
				while( (offst = uploadFileInputStream.read(buffer, 0, buffer.length)) > 0 ){
					dataOutputStream.write(buffer, 0, offst);
				}
				dataOutputStream.writeBytes(newLine);
				dataOutputStream.writeBytes(boundary.concat(param != null && param.size() > 0 ? "" : "--") + newLine);

				int i = 0;
				if(param != null){
					for(String key : param.keySet()){
						if(param.get(key) == null){
							continue;
						}
						Object value = param.get(key);
						if(value instanceof String && StringUtils.isEmpty(value.toString())){
							continue;
						}
						dataOutputStream.writeBytes(String.format("Content-Disposition: form-data; name=\"%s\"", key) + newLine);
						dataOutputStream.writeBytes(newLine + value + newLine);
						dataOutputStream.writeBytes(boundary);
						i++;
						dataOutputStream.writeBytes(i == param.size() ? "--" + newLine : newLine);
					}
				}

				dataOutputStream.flush();
				// 定义BufferedReader输入流来读取URL的响应
				try(BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
					String line;
					while ((line = in.readLine()) != null) {
						result += line;
					}
				}
			}
		} catch (Exception e) {
			log.error("发送post请求异常", e);
		}
		log.info("请求地址：{} \n 请求参数：header:{} \n body:{} \n 返回：{}", url, JSON.toJSONString(header), param, result);
		return result;
	}

	@Data
	@AllArgsConstructor
	@Builder
	public static class UpLoadFile{
		@ApiModelProperty("文件名")
		private String fileName;
		@ApiModelProperty("文件在表单中使用的参数名")
		private String fileParameterName;
		@ApiModelProperty("文件编码格式")
		private String fileContentType;
		@ApiModelProperty("文件输入流")
		private InputStream fileInputStream;
	}

	/***
	 * 将httpurl解析为结构化数据
	 * 正则功力弱怕出问题，就不写正则了，之后的同事可以改造一下~
	 * @param httpUrl
	 * @return
	 */
	public static HttpUrlInfo DisassembleUrl(String httpUrl){
		httpUrl = httpUrl.trim().replaceAll(" ", "");

		//协议
		String protocol = httpUrl.startsWith(HttpUrlInfo.PROTOCOL_HTTPS) ? HttpUrlInfo.PROTOCOL_HTTPS :
				httpUrl.startsWith(HttpUrlInfo.PROTOCOL_HTTP) ? HttpUrlInfo.PROTOCOL_HTTP : null;
		Assert.notNull(protocol, "仅解析标准Http协议的地址！");
		httpUrl = httpUrl.substring(httpUrl.indexOf("://") + "://".length(), httpUrl.length());

		//查询字符串
		String queryStr = null;
		if(httpUrl.indexOf("?") > 0){
			queryStr = httpUrl.substring(httpUrl.indexOf("?") + 1, httpUrl.length());
			httpUrl = httpUrl.substring(0, httpUrl.indexOf("?"));
		}

		//资源路径
		String resource = null;
		if(httpUrl.indexOf("/") > 0 && httpUrl.indexOf("/") < httpUrl.length() - 1){
			resource = httpUrl.substring(httpUrl.indexOf("/"), httpUrl.length());
			if(resource.endsWith("/")){
				resource = resource.substring(0, resource.length() - 1);
			}
			httpUrl = httpUrl.substring(0, httpUrl.indexOf("/"));
		}

		//端口号
		Integer port = 80;
		if(httpUrl.endsWith("/")){
			httpUrl = httpUrl.substring(0, httpUrl.length() - 1);
		}
		if(httpUrl.contains(":")){
			String[] temp = httpUrl.split(":");
			httpUrl = temp[0];
			port = Integer.parseInt(temp[1]);
		}

		//主机域名或ip地址
		String host = httpUrl;

		return HttpUrlInfo
				.builder()
				.host(host)
				.port(port)
				.protocol(protocol)
				.queryStr(queryStr)
				.resource(resource)
				.build();
	}

	@Data
	@Builder
	public static class HttpUrlInfo{
		private static final String PROTOCOL_HTTP = "http";

		private static final String PROTOCOL_HTTPS = "https";

		private String protocol;

		private String host;

		private Integer port;

		private String resource;

		private String queryStr;
	}
}
