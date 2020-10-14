package com.fss.pos.host.iso8583.master;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.commons.codec.binary.Hex;

import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.utils.IsoUtil;

public class MasterMessage {

	public MasterMessage() {
		msgObj = new HashMap<Integer, byte[]>();
	}

	public void setElement(int pos, String value) {
		if (value == null) {
			Log.trace("Trying to set a null element at position (" + pos + ").");
			return;
		} else {
			msgObj.put(new Integer(pos), value.getBytes());
			return;
		}
	}

	public void setElement(int pos, byte value[]) {
		if (value == null) {
			Log.trace("Trying to set a null element at position (" + pos + ").");
			return;
		} else {
			msgObj.put(new Integer(pos), value);
			return;
		}
	}

	public String getStringElement(int pos) {
		Object o = msgObj.get(new Integer(pos));
		if (o == null || !(o instanceof byte[]))
			return null;
		else
			return new String((byte[]) o);
	}

	public byte[] getBytesElement(int pos) {
		Object o = msgObj.get(new Integer(pos));
		if (o == null || !(o instanceof byte[]))
			return null;
		else
			return (byte[]) o;
	}

	@SuppressWarnings("unused")
	private boolean validate(byte data[], MasterField fieldInfo) {
		if (fieldInfo.getMinLength() != fieldInfo.getMaxLength()
				&& fieldInfo.getLengthHeader() < 1) {
			Log.trace(fieldInfo.getName()
					+ " is a variable length field without "
					+ "a length header. Data: " + new String(data));
			return false;
		}
		switch (fieldInfo.getType()) {
		case 1: // '\001'
			for (int i = 0; i < data.length; i++)
				if (!Character.isDigit((char) data[i])) {
					Log.trace(fieldInfo.getName() + " is not numeric. Data: "
							+ new String(data));
					return false;
				}

			if (data.length > fieldInfo.getMaxLength()) {
				Log.trace("The length of " + fieldInfo.getName()
						+ " is too long. Data: " + new String(data));
				return false;
			} else {
				return true;
			}

		case 0: // '\0'
		case 6: // '\006'
			for (int i = 0; i < data.length; i++)
				if (!Character.isLetterOrDigit((char) data[i])
						&& !Character.isSpaceChar((char) data[i])) {
					Log.trace(fieldInfo.getName()
							+ " contains an invalid character ("
							+ (char) data[i] + "). Data: " + new String(data));
					return false;
				}

			return true;

		case 3: // '\003'
			return true;

		case 4: // '\004'
			return true;

		case 2: // '\002'
		case 7: // '\007'
			for (int i = 0; i < data.length; i++)
				if (!Character.isDefined((char) data[i])) {
					Log.trace(fieldInfo.getName()
							+ " contains an invalid character ("
							+ (char) data[i] + "). Data: " + new String(data));
					return false;
				}

			return true;

		case 5: // '\005'
			if (data.length > fieldInfo.getMaxLength()) {
				Log.trace("The length of " + fieldInfo.getName()
						+ " is too long. Data: " + new String(data));
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	protected byte[] formatElement(byte data[], MasterField fieldInfo, int pos) {
		boolean variableLength = false;
		String datas = new String(data);
		// if(!validate(data, fieldInfo))
		// return null;
		int maxlgth = fieldInfo.getMaxLength();
		int minlgth = fieldInfo.getMinLength();
		int lgthHdr = fieldInfo.getLengthHeader();
		if (maxlgth != minlgth)
			variableLength = true;

		switch (fieldInfo.getType()) {
		case 3: // '\003'
		default:
			break;

		case 5: // '\005'
		{
			if (variableLength) {
				datas = MasterIsoUtil.padLeft(datas, minlgth, '0');
				datas = MasterIsoUtil.zeroFill(
						Integer.toString(datas.length()), lgthHdr) + datas;
			} else {
				datas = MasterIsoUtil.padLeft(datas, maxlgth, '0');
			}
			byte dataBArry[];
			if (variableLength) {
				if (datas.length() % 2 == 0) {
					dataBArry = new byte[datas.length()];
					for (int i = 0; i < datas.length(); i++)
						dataBArry[i] = (byte) Integer.valueOf(
								datas.substring(i, i + 1)).intValue();

				} else {
					dataBArry = new byte[datas.length() + 1];
					dataBArry[datas.length()] = 15;
					for (int i = 0; i < datas.length(); i++)
						dataBArry[i] = (byte) Integer.valueOf(
								datas.substring(i, i + 1)).intValue();

				}
			} else if (datas.length() % 2 == 0) {
				dataBArry = new byte[datas.length()];
				for (int i = 0; i < datas.length(); i++)
					dataBArry[i] = (byte) Integer.valueOf(
							datas.substring(i, i + 1)).intValue();

			} else {
				dataBArry = new byte[datas.length() + 1];
				dataBArry[0] = 0;
				for (int i = 0; i < datas.length(); i++)
					dataBArry[i + 1] = (byte) Integer.valueOf(
							datas.substring(i, i + 1)).intValue();

			}
			byte resultBArry[] = new byte[dataBArry.length / 2];
			int j = 0;
			for (int i = 0; i < dataBArry.length; i += 2) {
				resultBArry[j] = (byte) ((dataBArry[i] << 4) + dataBArry[i + 1]);
				j++;
			}
			Log.trace("    Packed Hex Dump = "
					+ MasterIsoUtil.formatHexDump(resultBArry));
			return resultBArry;
		}

		case 1: // '\001'
		{
			if (variableLength) {
				datas = MasterIsoUtil.padLeft(datas, minlgth, '0');
				datas = MasterIsoUtil.zeroFill(
						Integer.toString(datas.length()), lgthHdr) + datas;
			} else {
				datas = MasterIsoUtil.padLeft(datas, maxlgth, '0');
			}
			break;
		}

		case 0: // '\0'
		case 2: // '\002'
		{
			int lgth = datas.length();
			if (lgth > maxlgth) {
				datas = datas.substring(0, maxlgth);
				lgth = maxlgth;
			}
			if (lgth < minlgth) {
				datas = MasterIsoUtil.padRight(datas, minlgth);
				lgth = minlgth;
			}
			if (variableLength)
				datas = MasterIsoUtil.zeroFill(Integer.toString(lgth), lgthHdr)
						+ datas;
			break;
		}

		case 6: // '\006'
		case 7: // '\007'
		{
			int lgth = datas.length();
			if (lgth > maxlgth) {
				datas = datas.substring(0, maxlgth);
				lgth = maxlgth;
			}
			if (!variableLength) {
				if (lgth < minlgth) {
					datas = MasterIsoUtil.padRight(datas, minlgth);
					lgth = minlgth;
				}
			} else {
				datas = MasterIsoUtil.zeroFill(Integer.toString(lgth), lgthHdr)
						+ datas;
			}
			break;
		}

		case 4: // '\004'
		{
			if (variableLength) {
				int lgth = data.length;
				datas = MasterIsoUtil.zeroFill(Integer.toString(lgth), lgthHdr);
				byte datab[] = new byte[data.length + datas.length()];
				System.arraycopy(datas.getBytes(), 0, datab, 0, datas.length());
				System.arraycopy(data, 0, datab, datas.length(), data.length);
				return datab;
			} else {
				return data;
			}
		}
		}

		if (pos == 64 || pos == 52)
			try {
				return Hex.decodeHex(datas.toCharArray());
			} catch (Exception e1) {
				throw new RuntimeException(e1.getCause());
			}
		else if (pos == 55) {
			try {
				String s = (datas.length() / 2) + "";
				String s1 = new String(s.getBytes("Cp1047"));
				s1 = IsoUtil.asciiChar2hex(s1);
				String message = IsoUtil.hex2AsciiChar(s1 + datas);
				return message.getBytes(StandardCharsets.ISO_8859_1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			try {
				return datas.getBytes("Cp1047");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e.getCause());
			}
		return null;
	}

	protected String parseElement(byte datab[], MasterField fieldInfo) {
		int maxlgth = fieldInfo.getMaxLength();
		int minlgth = fieldInfo.getMinLength();
		byte tempb[] = new byte[datab.length * 2];
		int j = 0;
		for (int i = 0; i < datab.length; i++) {
			tempb[j] = (byte) (48 + ((datab[i] & 0xff) >>> 4));
			j++;
			tempb[j] = (byte) (48 + (datab[i] & 0xf));
			j++;
		}

		String elementDatas;
		if (minlgth != maxlgth) {
			int varLen = (tempb[0] - 48) * 10 + (tempb[1] - 48);
			byte resultBArry[] = new byte[varLen];
			System.arraycopy(tempb, 2, resultBArry, 0, resultBArry.length);
			elementDatas = new String(resultBArry);
		} else if (maxlgth % 2 == 0) {
			elementDatas = new String(tempb);
		} else {
			byte resultBArry[] = new byte[maxlgth];
			System.arraycopy(tempb, 1, resultBArry, 0, maxlgth);
			elementDatas = new String(resultBArry);
		}
		return elementDatas;
	}

	protected int size() {
		return msgObj.size();
	}

	protected static final int AM_ALPHA_NUMERIC = 0;
	protected static final int AM_NUMERIC = 1;
	protected static final int AM_ALPHA_NUMERIC_SPECIAL = 2;
	protected static final int AM_X = 3;
	protected static final int AM_BYTE = 4;
	protected static final int AM_2805_NUMERIC = 5;
	protected static final int AM_2805_ALPHA_NUMERIC = 6;
	protected static final int AM_2805_ALPHA_NUMERIC_SPECIAL = 7;
	private HashMap<Integer, byte[]> msgObj;
}
