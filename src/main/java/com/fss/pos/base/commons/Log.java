package com.fss.pos.base.commons;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;





import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fss.pos.base.commons.utils.ThreadLocalData;
import com.fss.pos.base.commons.utils.ThreadLocalUtil;
import com.fss.pos.base.services.fssconnect.FssConnect;

public class Log {

	private static Logger log = LogManager.getLogger("com.src.output");
	private static Logger elog = LogManager.getLogger("com.src.error");
	private static Logger debugLog = LogManager.getLogger("com.src.debug");
	private static Logger fsselog = LogManager.getLogger("com.src.fssc");

	public static void trace(final String message) {
		ThreadLocalData tld = ThreadLocalUtil.get();
		log.info((ThreadLocalUtil.getRRN() == null ? "" : ThreadLocalUtil
				.getRRN() + " : ")
				+ message);
		if (tld != null) {
			Boolean debugEnabled = StaticStore.loggerConfig.get(tld.getMsp());
			if (debugEnabled != null && debugEnabled)
				debugLog.info((ThreadLocalUtil.getRRN() == null ? ""
						: ThreadLocalUtil.getRRN() + " : ") + message);
		}
	}

	public static void info(final String key, final String message) {
		ThreadLocalData tld = ThreadLocalUtil.get();
		log.info((ThreadLocalUtil.getRRN() == null ? "" : ThreadLocalUtil
				.getRRN() + " : ")
				+ key + " : " + message);
		if (tld != null) {
			Boolean debugEnabled = StaticStore.loggerConfig.get(tld.getMsp());
			if (debugEnabled != null && debugEnabled)
				debugLog.info((ThreadLocalUtil.getRRN() == null ? ""
						: ThreadLocalUtil.getRRN() + " : ")
						+ key
						+ " : "
						+ message);
		}
	}

	public static void error(final String msg, final Throwable e) {
		elog.error(
				(ThreadLocalUtil.getRRN() == null ? "" : ThreadLocalUtil
						.getRRN()) + " : " + msg, e);
	}

	public static String logDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
		Date date = new Date();
		return formatter.format(date);
	}

	public static void fssc(final String msg) {
		String data = "";
		try {
			FssConnect fssc = new FssConnect(msg, StandardCharsets.ISO_8859_1);
			//fssc.setMessage(msg);
			//fssc.setMessage("XXXXXX");
			data = fssc.toString();
		} catch (Exception e) {
			Log.error("Logger fssc", e);
		}
		fsselog.info((ThreadLocalUtil.getRRN() == null ? "" : ThreadLocalUtil
				.getRRN() + " : ")
				+ data);
	}

	public static void debug(final String key, final String message) {
		ThreadLocalData tld = ThreadLocalUtil.get();
		if (tld == null) {
			debugLog.debug("DEBUG - " + key + " : " + message);
		} else {
			Boolean debugEnabled = StaticStore.loggerConfig.get(tld.getMsp());
			if (debugEnabled != null && debugEnabled)
				debugLog.debug((tld.getRrn() == null ? "" : tld.getRrn()
						+ " : ")
						+ "DEBUG - "+ key + " : " + message);
		}
	}
}
