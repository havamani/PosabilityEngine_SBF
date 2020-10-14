package com.fss.pos.base.services.fssconnect;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



import com.fss.pos.base.commons.Log;

public final class HttpClient {

	public static final String POST = "POST";
	public static final String GET = "GET";

	public static byte[] send(String urlString, byte msg[],
			String requestMethod, String contentType, boolean noResponse,
			int connTimeout, int readTimeout) throws Exception {
		if (urlString == null)
			return null;
		if (!requestMethod.equals(POST) && !requestMethod.equals(GET))
			throw new Exception("Method not supported");
		HttpURLConnection conn = (HttpURLConnection) new URL(urlString.trim())
				.openConnection();
		requestMethod = requestMethod.toUpperCase();
		conn.setRequestMethod(requestMethod);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setAllowUserInteraction(false);
		conn.setConnectTimeout(connTimeout);
		conn.setReadTimeout(readTimeout);
		if (contentType != null)
			conn.setRequestProperty("Content-Type", contentType);
		if (msg != null && msg.length > 0) {
			conn.setRequestProperty("Content-Length",
					String.valueOf(msg.length));
			BufferedOutputStream out = new BufferedOutputStream(
					conn.getOutputStream());
			out.write(msg, 0, msg.length);
			out.flush();
			out.close();
		}
		int responseCode = conn.getResponseCode();
		Log.debug("Http response", String.valueOf(responseCode));
		if (noResponse)
			return null;
		int length = conn.getContentLength();
		byte bytes[];
		BufferedInputStream in = null;
		if (length < 0) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream(128);
			in = new BufferedInputStream(conn.getInputStream());
			do {
				int b = in.read();
				if (b == -1)
					break;
				bout.write(b);
			} while (true);
			bytes = bout.toByteArray();
			return bytes;
		}
		bytes = new byte[length];
		in = new BufferedInputStream(conn.getInputStream());
		int pos;
		int numBytesRead;
		for (pos = 0; pos < length; pos += numBytesRead) {
			numBytesRead = in.read(bytes, pos, length - pos);
			if (numBytesRead != -1)
				continue;
			break;
		}
		return bytes;
	}
	
	/*
	 * public static void main(String[] args) throws MalformedURLException,
	 * IOException {
	 * 
	 * String msg=
	 * "007f52534d35473055463031453732393035353339463731424336304135354546323034424438423555423342433939423639334433374638323034303839334141433146374341393741303546464646313030303031453130303030303346393132463337364135393033364236464630313031393036303630323539313832";
	 * HttpURLConnection conn = (HttpURLConnection) new
	 * URL("http://10.44.59.141:8006/".trim()) .openConnection();
	 * conn.setRequestMethod(HttpClient.POST); conn.setDoOutput(true);
	 * conn.setDoInput(true); conn.setAllowUserInteraction(false);
	 * conn.setConnectTimeout(2000); conn.setReadTimeout(10000); if (msg != null &&
	 * msg.length() > 0) { conn.setRequestProperty("Content-Length",
	 * String.valueOf(msg.length())); BufferedOutputStream out = new
	 * BufferedOutputStream( conn.getOutputStream()); out.write(msg.getBytes(), 0,
	 * msg.length()); out.flush(); out.close(); } int responseCode =
	 * conn.getResponseCode(); Log.debug("Http response",
	 * String.valueOf(responseCode)); int length = conn.getContentLength(); byte
	 * bytes[]; BufferedInputStream in = null; if (length < 0) {
	 * ByteArrayOutputStream bout = new ByteArrayOutputStream(128); in = new
	 * BufferedInputStream(conn.getInputStream()); do { int b = in.read(); if (b ==
	 * -1) break; bout.write(b); } while (true); bytes = bout.toByteArray();
	 * 
	 * } bytes = new byte[length]; try { in = new
	 * BufferedInputStream(conn.getInputStream()); } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } int pos; int numBytesRead =
	 * 0; for (pos = 0; pos < length; pos += numBytesRead) { try { numBytesRead =
	 * in.read(bytes, pos, length - pos); } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } if (numBytesRead != -1)
	 * continue; break; } return;
	 * 
	 * }
	 */

}