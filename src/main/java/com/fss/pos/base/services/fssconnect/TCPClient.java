package com.fss.pos.base.services.fssconnect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Log;


public class TCPClient {

	@Autowired
	private Config config;

	@SuppressWarnings("unused")
	public static String send(String requestData, String hostname, int port) {
		
		  Log.debug("Request Data", requestData);
		  Log.debug("Connection Deatils", "Host IP : " + hostname + "Port : " +
		  port);
		 
		StringBuilder response = new StringBuilder();
		String ss = new String();
		BufferedReader bufferedReader;
		BufferedWriter bufferedWriter;
		Socket foreignSocket = null;
		String respByte = null;
		try {

			//////////////////////////////////////////////////////////////
			foreignSocket = new Socket(hostname, port);
			// foreignSocket.setReuseAddress(true);
			foreignSocket.setKeepAlive(true);
			foreignSocket.setSendBufferSize(2048);
			foreignSocket.setReceiveBufferSize(2048);
			foreignSocket.setSoLinger(true, 0);
			foreignSocket.setSoTimeout(30*1000);
			///////////////////////////////////////////////////////////////
			
			
		/*	bufferedReader = new BufferedReader(
					new InputStreamReader(
							foreignSocket.getInputStream(),
							StandardCharsets.ISO_8859_1));*/
			
			bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(
							foreignSocket.getOutputStream(),
							StandardCharsets.ISO_8859_1));
		
			write(bufferedWriter, requestData, 4, 2);
		

			byte[] buff = new byte[1024];
			int NoOfBytes = foreignSocket.getInputStream().read(buff, 0,
					buff.length);
			
			respByte = new String(Base64.encodeBase64(buff),
					StandardCharsets.ISO_8859_1);

			bufferedReader = null;
			bufferedWriter = null;
			
			if (foreignSocket != null) {
				foreignSocket.close();
			}
			foreignSocket = null;

		} catch (Exception e) {
			Log.debug("Exception ::::::::::::: ", e.getMessage());
		}
		
		return respByte;
		
	}

	/** Convert graphical to decimal equivalent */
	static long toDecimal(char[] graphical) {
		int length = graphical.length;
		long decimal = 0;
		for (int i = 0; i < graphical.length; i++) {
			length--;
			decimal += graphical[i] * (long) Math.pow(16, 2 * length);
		}
		return decimal;
	}

	public static String leftPadZeros(final String ilosgStr,
			final int ilonuLength) {
		if (null == ilosgStr) {// if09
			return null;
		}// if09
		String olosgPadStr = new String(ilosgStr);
		if (ilonuLength < ilosgStr.length()) {// if10
			return ilosgStr.substring(0, ilonuLength);
		}// if10
		for (int i = ilosgStr.length(); i < ilonuLength; i++) {// for05
			olosgPadStr = '0' + olosgPadStr;
		}// for05
		return olosgPadStr;
	}

	/** Pads the char */
	static String pad(String str, char ch, int length, boolean append)
			throws Exception {
		String padStr = new String(str);
		if (length < str.length()) {
			return str.substring(0, length);
		}
		for (int i = str.length(); i < length; i++) {
			if (append) {
				padStr = padStr + ch;
			} else {
				padStr = ch + padStr;
			}
		}
		return padStr;
	}

	/** Convert decimal to graphical equivalent */
	static char[] toGraphical(long decimal, int length) {
		char[] graphical = new char[length];
		for (int i = 0; i < graphical.length; i++) {
			length--;
			graphical[i] = (char) (decimal / Math.pow(16, 2 * length));
			decimal %= Math.pow(16, 2 * length);
		}
		return graphical;
	}

	public String write(String strBuf, int transferType, int length)
			throws Exception {

		StringWriter str = new StringWriter();

		BufferedWriter bufferedWriter = new BufferedWriter(str);

		if (1 == transferType) {
			strBuf = pad(strBuf, ' ', length, true);
			bufferedWriter.write(strBuf); // Fixed length
		} else if (2 == transferType) {
			bufferedWriter.write(strBuf); // Variable length ends with new
											// line
			bufferedWriter.newLine();
		} else if (3 == transferType) {
			bufferedWriter.write(pad(String.valueOf(strBuf.length()), '0',
					length, false));
			bufferedWriter.write(strBuf); // Variable length starts with
											// Decimal length header
		} else if (4 == transferType) {
			Log.trace("Grapical Length:" + strBuf.length());
			bufferedWriter.write(pad(
					String.valueOf(toGraphical(strBuf.length(), length)), ' ',
					length, true));
			Log.trace("strBuf Length:" + strBuf);

			bufferedWriter.write(strBuf); // Variable length starts with
											// Graphical length header

		} else if (5 == transferType) {
			bufferedWriter.write(strBuf); // Variable length ends with new
											// line
		} else if (6 == transferType) { // Transfer type is added for Teller
										// testing by Aravindan. G on 30-12-2006
			bufferedWriter.write(leftPadZeros("" + strBuf.length(), 6));
			bufferedWriter.write(strBuf); // Variable length ends with new
											// line
		}

		bufferedWriter.flush();

		return str.getBuffer().toString();

	}

	public static void write(BufferedWriter bufferedWriter, String strBuf,
			int transferType, int length) throws Exception {
		if (1 == transferType) {
			strBuf = pad(strBuf, ' ', length, true);
			bufferedWriter.write(strBuf); // Fixed length
		} else if (2 == transferType) {
			bufferedWriter.write(strBuf); // Variable length ends with new
			bufferedWriter.newLine();
		} else if (3 == transferType) {
			Log.trace("strBuf Length:" + strBuf.length());
			bufferedWriter.write(pad(String.valueOf(strBuf.length()), '0',
					length, false));
			bufferedWriter.write(strBuf); // Variable length starts with
		} else if (4 == transferType) {
			// log.info("String.valueOf(toGraphical(strBuf.length(), length)"+String.valueOf(toGraphical(strBuf.length(),
			// length)));
			/*
			 * bufferedWriter.write(pad(
			 * String.valueOf(toGraphical(strBuf.length(), length)), ' ',
			 * length, true));
			 */

			/*
			 * Log.trace("Header length::: " +
			 * String.valueOf(toGraphical(strBuf.length(), length)));
			 * Log.trace("Header length::: " +
			 * String.valueOf(toGraphical(strBuf.length(), length)));
			 * Log.trace("Request ::: " + strBuf); Log.trace("Request ::: " +
			 * strBuf);
			 */

			bufferedWriter.write(strBuf); // Variable length starts with
		} else if (5 == transferType) {
			bufferedWriter.write(strBuf); // Variable length ends with new
		} else if (6 == transferType) { // Transfer type is added for Teller
			bufferedWriter.write(leftPadZeros("" + strBuf.length(), 6));
			bufferedWriter.write(strBuf); // Variable length ends with new
		}
		bufferedWriter.flush();
	}

	/** TCP/IP Read process */
	/*
	 * public static String read(BufferedReader bufferedReader, int
	 * transferType, int length) throws Exception {
	 * 
	 * int ret; char[] chrBuf; char[] chrBuffer; // String strBuf = null; String
	 * strBuf = ""; StringBuilder sb = new StringBuilder(); if (1 ==
	 * transferType) { chrBuf = new char[length]; // Fixed length ret =
	 * bufferedReader.read(chrBuf, 0, chrBuf.length); if (-1 == ret) { throw new
	 * Exception("Read Error: Socket closed"); } strBuf = new String(chrBuf); }
	 * else if (2 == transferType) {
	 * 
	 * strBuf = bufferedReader.readLine(); // Variable length ends with new //
	 * line if (null == strBuf) { throw new
	 * Exception("Read Error: Socket closed"); } } else if (3 == transferType) {
	 * chrBuf = new char[length]; // Variable length starts with Decimal //
	 * length header ret = bufferedReader.read(chrBuf, 0, chrBuf.length);
	 * 
	 * Log.debug("Read Buffer Length", String.valueOf(chrBuf) + ret);
	 * 
	 * if (-1 == ret) { throw new Exception("Read Error: Socket closed"); }
	 * 
	 * strBuf = new String(chrBuf); Log.debug("strBuf     ----- ", strBuf);
	 * 
	 * chrBuf = new char[Integer.parseInt(strBuf)]; bufferedReader.read(chrBuf,
	 * 0, chrBuf.length); strBuf = new String(chrBuf);
	 * Log.debug("Final Data Received", strBuf);
	 * 
	 * chrBuf = new char[length]; // Variable length starts with Decimal //
	 * length header ret = bufferedReader.read(chrBuf, 0, chrBuf.length); if (-1
	 * == ret) { throw new Exception("Read Error: Socket closed"); } strBuf =
	 * new String(chrBuf);
	 * 
	 * chrBuf = new char[Integer.parseInt(strBuf)]; bufferedReader.read(chrBuf,
	 * 0, chrBuf.length); strBuf = new String(chrBuf);
	 * 
	 * Log.debug("Final Data Received", strBuf);
	 * 
	 * return strBuf;
	 * 
	 * } else if (4 == transferType) {
	 * 
	 * chrBuf = new char[length]; // Variable length starts with Graphical //
	 * length header Log.trace("Response from host : chrBuf : " +
	 * chrBuf.toString() + " chrBuf.length : " + chrBuf.length); //
	 * Log.trace("Response from host : bufRead : "
	 * + bufferedReader.read(chrBuf, 0, chrBuf.length)); ret =
	 * bufferedReader.read(chrBuf, 0, chrBuf.length); if (-1 == ret) { throw new
	 * Exception("Read Error: Socket closed"); }
	 * 
	 *chrBuf = new char[(int) toDecimal(chrBuf)]; //
	 *  bufferedReader.read(chrBuf, 0,
	 * chrBuf.length);
	 * 
	 * strBuf = new String(chrBuf); Log.trace("Response from host : strBuf : " +
	 * strBuf + " chrBuf.length : " + chrBuf.length); } else if (5 ==
	 * transferType) {
	 * 
	 * byte ETX = 0x03; chrBuf = new char[1]; // Variable length starts with
	 * Graphical // length header
	 * 
	 * while (true) {
	 * 
	 * ret = bufferedReader.read(chrBuf, 0, 1);
	 * 
	 * if (-1 == ret) { throw new Exception("Read Error"); }
	 * 
	 * strBuf = strBuf + new String(chrBuf);
	 * 
	 * if (chrBuf[0] == (char) ETX) break; }
	 * 
	 * ret = bufferedReader.read(chrBuf, 0, 1); strBuf = strBuf + new
	 * String(chrBuf);
	 * 
	 * } else if (6 == transferType) { // Transfer type is added for Teller //
	 * testing by Aravindan. G on 30-12-2006 chrBuf = new char[length]; //
	 * Log.trace("Response from host : chrBuf : "+ chrBuf.toString() + //
	 * " chrBuf.length : " +chrBuf.length); // Variable length starts with
	 * Graphical // length header
	 * 
	 * ret = bufferedReader.read(chrBuf, 0, chrBuf.length);
	 * 
	 * for (char c : chrBuf) {
	 * 
	 * Log.debug("Response print i value", " : " + c + " : " +
	 * String.valueOf(c)); sb.append(String.valueOf(c)); }
	 * 
	 * // Log.debug("Response ret", " : " + ret); //
	 * Log.debug("Response ret chrBuf", String.valueOf(chrBuf)); if (-1 == ret)
	 * { throw new Exception("Read Error: Socket closed"); } //
	 * Log.debug("Response ret chrBuf 1", " : " ); // chrBuf = new char[(int)
	 * toDecimal(chrBuf)]; chrBuffer = new
	 * char[Integer.parseInt(String.valueOf(chrBuf))];
	 * Log.debug("Response ret chrBuf Len", " : " + chrBuffer.length);
	 * Log.debug("Response ret chrBuf 2", new String(chrBuf));
	 * bufferedReader.read(chrBuffer, 0, chrBuffer.length); //
	 * Log.debug("Response ret chrBuf value", String.valueOf(chrBuf)); //
	 * Log.debug("Response ret chrBuf 3", new String(chrBuf)); //
	 * sb.append(chrBuffer, 0, chrBuffer.length);
	 * 
	 * int i = 0; for (char c : chrBuffer) {
	 * 
	 * Log.debug("Response print i value", " : " + c + " : " +
	 * String.valueOf(c));
	 * 
	 * sb.append(String.valueOf(c)); i += 1; } // strBuf = chrBuffer;
	 * 
	 * Log.debug("Response print i value", " : " + i);
	 * Log.debug("Response ret chrBuf 4", sb.toString().length() + " : " +
	 * sb.toString());
	 * 
	 * 
	 * }else if (7 == transferType) {
	 * 
	 * 
	 * String strBuffer; chrBuf = new char[length];
	 * 
	 * ret = bufferedReader.read(chrBuf, 0, chrBuf.length);
	 * 
	 * //log.info("Read() startTime:::"+new
	 * SimpleDateFormat("HH:mm:ss:SSS").format(new Date()));
	 * 
	 * if (-1 == ret) { throw new Exception("Read Error1: Socket closed"); }
	 * 
	 * chrBuf = new char[(int) toDecimal (chrBuf)]; int msgLen = chrBuf.length;
	 * 
	 * strBuf = ""; int value = 0; Thread.sleep(5); while(true) { try { value =
	 * bufferedReader.read(); char c = (char) value; // converts int to
	 * character strBuffer = new String(Character.toString(c)); strBuf = strBuf
	 * + strBuffer; Log.debug("strBuf :: ",strBuf); if (strBuf.length() >=
	 * msgLen) { break; } } catch (SocketTimeoutException e) {
	 * Log.error("Exception occured : ",e); continue; } catch (Exception e) {
	 * Log.error("Exception occured strbuffer : ",e); break; } }
	 * 
	 * }
	 * 
	 * return sb.toString();
	 * 
	 * }
	 */

	/** TCP/IP Read process */
	@SuppressWarnings("unused")
	public static String read(BufferedReader bufferedReader, int transferType,
			int length) throws Exception {

		int ret;
		char[] chrBuf;
		char[] chrBuffer;
		// String strBuf = null;
		String strBuf = "";
		StringBuilder sb = new StringBuilder();
		if (1 == transferType) {
			chrBuf = new char[length]; // Fixed length
			ret = bufferedReader.read(chrBuf, 0, chrBuf.length);
			if (-1 == ret) {
				throw new Exception("Read Error: Socket closed");
			}
			strBuf = new String(chrBuf);
		} else if (2 == transferType) {

			strBuf = bufferedReader.readLine(); // Variable length ends with new
												// line
			if (null == strBuf) {
				throw new Exception("Read Error: Socket closed");
			}
		} else if (3 == transferType) {
			chrBuf = new char[length]; // Variable length starts with Decimal
										// length header
			ret = bufferedReader.read(chrBuf, 0, chrBuf.length);

			Log.debug("Read Buffer Length", String.valueOf(chrBuf) + ret);

			if (-1 == ret) {
				throw new Exception("Read Error: Socket closed");
			}

			strBuf = new String(chrBuf);
			Log.debug("strBuf     ----- ", strBuf);

			chrBuf = new char[Integer.parseInt(strBuf)];
			bufferedReader.read(chrBuf, 0, chrBuf.length);
			strBuf = new String(chrBuf);
			Log.debug("Final Data Received", strBuf);

		} else if (4 == transferType) {

			chrBuf = new char[length]; // Variable length starts with Graphical
										// length header
			Log.trace("Response from host : chrBuf : " + chrBuf.toString()
					+ " chrBuf.length : " + chrBuf.length);
			
			Log.trace("Response from host : bufRead : "
					+ bufferedReader.read(chrBuf, 0, chrBuf.length));
			ret = bufferedReader.read(chrBuf, 0, chrBuf.length);
			if (-1 == ret) {
				throw new Exception("Read Error: Socket closed");
			}

			
			chrBuf = new char[(int) toDecimal(chrBuf)];
			
			bufferedReader.read(chrBuf, 0, chrBuf.length);

			strBuf = new String(chrBuf);
			Log.trace("Response from host : strBuf : " + strBuf
					+ " chrBuf.length : " + chrBuf.length);
		} else if (5 == transferType) {

			byte ETX = 0x03;
			chrBuf = new char[1]; // Variable length starts with Graphical
									// length header

			while (true) {

				ret = bufferedReader.read(chrBuf, 0, 1);

				if (-1 == ret) {
					throw new Exception("Read Error");
				}

				strBuf = strBuf + new String(chrBuf);

				if (chrBuf[0] == (char) ETX)
					break;
			}

			ret = bufferedReader.read(chrBuf, 0, 1);
			strBuf = strBuf + new String(chrBuf);

		} else if (6 == transferType) {
			// Transfer type is added for Teller
			// testing by Aravindan. G on 30-12-2006
			chrBuf = new char[length];
			// Log.trace("Response from host : chrBuf : "+ chrBuf.toString() +
			// " chrBuf.length : " +chrBuf.length);
			// Variable length starts with Graphical
			// length header

		

			ret = bufferedReader.read(chrBuf, 0, chrBuf.length);

			for (char c : chrBuf) {
				Log.debug("Response print i value",
						" : " + c + " : " + String.valueOf(c));
				sb.append(String.valueOf(c));
			}

			// Log.debug("Response ret", " : " + ret);
			// Log.debug("Response ret chrBuf", String.valueOf(chrBuf));
			if (-1 == ret) {
				throw new Exception("Read Error: Socket closed");
			}
			// Log.debug("Response ret chrBuf 1", " : " );
			// chrBuf = new char[(int) toDecimal(chrBuf)];
			chrBuffer = new char[Integer.parseInt(String.valueOf(chrBuf))];
			// Log.debug("Response ret chrBuf Len", " : " + chrBuffer.length);
			// Log.debug("Response ret chrBuf 2", new String(chrBuf));
			bufferedReader.read(chrBuffer, 0, chrBuffer.length);
			// Log.debug("Response ret chrBuf value", String.valueOf(chrBuf));
			// Log.debug("Response ret chrBuf 3", new String(chrBuf));
			// sb.append(chrBuffer, 0, chrBuffer.length);

			int i = 0;
			for (char c : chrBuffer) {
				Log.debug("Response print i value",
						" : " + c + " : " + String.valueOf(c));
				sb.append(String.valueOf(c));
				i += 1;
			}
			// strBuf = chrBuffer;
			/*
			 * Log.debug("Response print i value", " : " + i);
			 * Log.debug("Response ret chrBuf 4", sb.toString().length() + " : "
			 * + sb.toString());
			 */

		}
		return sb.toString();

	}

	/*
	 * public static void main(String[] args) { InputStream is = null;
	 * InputStreamReader isr = null; BufferedReader br = null;
	 * 
	 * try {
	 * 
	 * // open input stream test.txt for reading purpose. is = new
	 * FileInputStream("d:/test.txt");
	 * 
	 * // create new input stream reader isr = new InputStreamReader(is);
	 * 
	 * // create new buffered reader br = new BufferedReader(isr);
	 * 
	 * // creates buffer char[] cbuf = new char[is.available()];
	 * 
	 * // reads characters to buffer, offset 2, len 10 int ret = 0; ret =
	 * br.read(cbuf, 0, 10); // for each character in the buffer
	 * 
	 * for (char c : cbuf) {
	 * 
	 * // if char is empty if (c == (char) 0) { c = '*'; }
	 * 
	 * // prints characters }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 */
}
