package com.fss.pos.base.commons.utils;

public class ThreadLocalUtil {

	private static final ThreadLocal<ThreadLocalData> threadLocal;

	static {
		threadLocal = new ThreadLocal<ThreadLocalData>();
	}

	public static void unset() {
		threadLocal.remove();
	}

	public static ThreadLocalData get() {
		return threadLocal.get();
	}

	public static String getRRN() {
		ThreadLocalData tld = threadLocal.get();
		return tld == null ? null : tld.getRrn();
	}

	public static String getMsp() {
		ThreadLocalData tld = threadLocal.get();
		return tld == null ? null : tld.getMsp();
	}

	public static void setRRN(String rrn) {
		ThreadLocalData tld = threadLocal.get();
		if (tld == null)
			tld = new ThreadLocalData();
		tld.setRrn(rrn);
		threadLocal.set(tld);
	}

	public static void setMsp(String rrn) {
		ThreadLocalData tld = threadLocal.get();
		if (tld == null)
			tld = new ThreadLocalData();
		tld.setMsp(rrn);
		threadLocal.set(tld);
	}

}
