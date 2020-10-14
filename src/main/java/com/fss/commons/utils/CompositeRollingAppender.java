package com.fss.commons.utils;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.logging.log4j.core.appender.FileAppender;



// Referenced classes of package com.aciworldwide.framework.log:
//            RollingCalendar

public class CompositeRollingAppender{

	/*public CompositeRollingAppender() {
		datePattern = "'.'yyyy-MM-dd";
		dateExpireInterval = -1L;
		scheduledFilename = null;
		nextCheck = System.currentTimeMillis() - 1L;
		now = new Date();
		rc = new RollingCalendar();
		checkPeriod = -1;
		maxFileSize = 0xa00000L;
		maxSizeRollBackups = 0;
		curSizeRollBackups = 0;
		maxTimeRollBackups = -1;
		curTimeRollBackups = 0;
		countDirection = -1;
		rollingStyle = 3;
		rollDate = true;
		rollSize = true;
		staticLogFileName = true;
	}

	public CompositeRollingAppender(Layout layout, String s, String s1)
			throws IOException {
		this(layout, s, s1, true);
	}

	public CompositeRollingAppender(Layout layout, String s, boolean flag)
			throws IOException {
		super(layout, s, flag);
		datePattern = "'.'yyyy-MM-dd";
		dateExpireInterval = -1L;
		scheduledFilename = null;
		nextCheck = System.currentTimeMillis() - 1L;
		now = new Date();
		rc = new RollingCalendar();
		checkPeriod = -1;
		maxFileSize = 0xa00000L;
		maxSizeRollBackups = 0;
		curSizeRollBackups = 0;
		maxTimeRollBackups = -1;
		curTimeRollBackups = 0;
		countDirection = -1;
		rollingStyle = 3;
		rollDate = true;
		rollSize = true;
		staticLogFileName = true;
	}

	public CompositeRollingAppender(Layout layout, String s, String s1,
			boolean flag) throws IOException {
		super(layout, s, flag);
		datePattern = "'.'yyyy-MM-dd";
		dateExpireInterval = -1L;
		scheduledFilename = null;
		nextCheck = System.currentTimeMillis() - 1L;
		now = new Date();
		rc = new RollingCalendar();
		checkPeriod = -1;
		maxFileSize = 0xa00000L;
		maxSizeRollBackups = 0;
		curSizeRollBackups = 0;
		maxTimeRollBackups = -1;
		curTimeRollBackups = 0;
		countDirection = -1;
		rollingStyle = 3;
		rollDate = true;
		rollSize = true;
		staticLogFileName = true;
		datePattern = s1;
		activateOptions();
	}

	public CompositeRollingAppender(Layout layout, String s) throws IOException {
		super(layout, s);
		datePattern = "'.'yyyy-MM-dd";
		dateExpireInterval = -1L;
		scheduledFilename = null;
		nextCheck = System.currentTimeMillis() - 1L;
		now = new Date();
		rc = new RollingCalendar();
		checkPeriod = -1;
		maxFileSize = 0xa00000L;
		maxSizeRollBackups = 0;
		curSizeRollBackups = 0;
		maxTimeRollBackups = -1;
		curTimeRollBackups = 0;
		countDirection = -1;
		rollingStyle = 3;
		rollDate = true;
		rollSize = true;
		staticLogFileName = true;
	}

	public void setDatePattern(String s) {
		datePattern = s;
	}

	public String getDatePattern() {
		return datePattern;
	}

	public void setDateExpireInterval(String s) {
		dateExpireInterval = toDateInterval(s, dateExpireInterval);
	}

	public static long toDateInterval(String s, long l) {
		if (s == null)
			return l;
		s = s.trim().toLowerCase();
		long l1 = 60000L;
		int i = s.length();
		if (s.endsWith("minutes") || s.endsWith("min") || s.endsWith("m")) {
			l1 = 60000L;
			i = s.indexOf("m");
		} else if (s.endsWith("hours") || s.endsWith("hour") || s.endsWith("h")) {
			l1 = 0x36ee80L;
			i = s.indexOf("h");
		} else if (s.endsWith("days") || s.endsWith("day") || s.endsWith("d")) {
			l1 = 0x5265c00L;
			i = s.indexOf("d");
		} else if (s.endsWith("weeks") || s.endsWith("week") || s.endsWith("w")) {
			l1 = 0x240c8400L;
			i = s.indexOf("w");
		}
		if (s != null) {
			String s1 = "";
			try {
				s1 = s.substring(0, i);
				return Long.valueOf(s1.trim()).longValue() * l1;
			} catch (NumberFormatException numberformatexception) {
				LogLog.error("[" + s1 + "] is not in proper int form.");
				LogLog.error("[" + s + "] not in expected format.",
						numberformatexception);
			}
		}
		return l;
	}

	public long getDateExpireInterval_inMilliseconds() {
		return dateExpireInterval;
	}

	public int getMaxSizeRollBackups() {
		return maxSizeRollBackups;
	}

	public long getMaximumFileSize() {
		return maxFileSize;
	}

	public void setMaxSizeRollBackups(int i) {
		maxSizeRollBackups = i;
	}

	public void setMaxFileSize(String s) {
		maxFileSize = OptionConverter.toFileSize(s, maxFileSize + 1L);
	}

	public void setMaximumFileSize(long l) {
		maxFileSize = l;
	}

	protected void setQWForFiles(Writer writer) {
		qw = new CountingQuietWriter(writer, errorHandler);
	}

	int computeCheckPeriod() {
		RollingCalendar rollingcalendar = new RollingCalendar();
		Date date = new Date(0L);
		if (datePattern != null) {
			for (int i = 0; i <= 5; i++) {
				String s = sdf.format(date);
				rollingcalendar.setType(i);
				Date date1 = new Date(rollingcalendar.getNextCheckMillis(date));
				String s1 = sdf.format(date1);
				if (s != null && s1 != null && !s.equals(s1))
					return i;
			}

		}
		return -1;
	}

	protected void subAppend(LoggingEvent loggingevent) {
		if (rollDate) {
			long l = System.currentTimeMillis();
			if (l >= nextCheck) {
				now.setTime(l);
				nextCheck = rc.getNextCheckMillis(now);
				rollOverTime();
			}
		}
		if (rollSize && fileName != null
				&& ((CountingQuietWriter) qw).getCount() >= maxFileSize)
			rollOverSize();
		super.subAppend(loggingevent);
	}

	public void setFile(String s) {
		baseFileName = s.trim();
		fileName = s.trim();
	}

	public synchronized void setFile(String s, boolean flag) throws IOException {
		s = initFileName();
		super.setFile(s, super.fileAppend, super.bufferedIO, super.bufferSize);
		initCountingQuietWriter(s, flag);
	}

	private void initCountingQuietWriter(String s, boolean flag) {
		if (flag) {
			File file = new File(s);
			((CountingQuietWriter) qw).setCount(file.length());
		}
	}

	private String initFileName() {
		String s = baseFileName;
		scheduledFilename = baseFileName.trim() + sdf.format(now) + ".log";
		if (!staticLogFileName) {
			s = scheduledFilename;
			if (countDirection > 0)
				scheduledFilename = s = s + '.' + ++curSizeRollBackups;
		}
		return s;
	}

	public int getCountDirection() {
		return countDirection;
	}

	public void setCountDirection(int i) {
		countDirection = i;
	}

	public int getRollingStyle() {
		return rollingStyle;
	}

	public void setRollingStyle(int i) {
		rollingStyle = i;
		switch (rollingStyle) {
		case 1: // '\001'
			rollDate = false;
			rollSize = true;
			break;

		case 2: // '\002'
			rollDate = true;
			rollSize = false;
			break;

		case 3: // '\003'
			rollDate = true;
			rollSize = true;
			break;

		default:
			errorHandler
					.error("Invalid rolling Style, use 1 (by size only), 2 (by date only) or 3 (both)");
			break;
		}
	}

	public boolean getStaticLogFileName() {
		return staticLogFileName;
	}

	public void setStaticLogFileName(boolean flag) {
		staticLogFileName = flag;
	}

	public void setStaticLogFileName(String s) {
		setStaticLogFileName(OptionConverter.toBoolean(s, true));
	}

	protected void existingInit() {
		curSizeRollBackups = 0;
		curTimeRollBackups = 0;
		File file = new File(baseFileName);
		String s = file.getAbsolutePath();
		File file1 = file.getParentFile();
		if (file1 == null)
			file1 = new File(".");
		LogLog.debug("existingInit: Searching for existing files in: " + file1);
		File afile[] = file1.listFiles();
		if (afile != null) {
			LogLog.debug("existingInit: Found " + afile.length
					+ " file in directory: " + file1.getAbsolutePath());
			for (int i = 0; i < afile.length; i++) {
				File file3 = afile[i];
				String s1 = file3.getAbsolutePath();
				LogLog.debug("existingInit: files[" + i + "] = " + s1
						+ " baseFileName = " + s);
				if (!s1.startsWith(s))
					continue;
				int j = s1.lastIndexOf(".");
				if (staticLogFileName) {
					if (j != s.length()) {
						LogLog.debug("existingInit: Skiping from backup count: "
								+ file3);
						continue;
					}
					LogLog.debug("existingInit: Found in backup count: "
							+ file3);
				}
				try {
					int k = Integer.parseInt(s1.substring(j + 1, s1.length()));
					if (k > curSizeRollBackups) {
						LogLog.debug("From file: " + file3
								+ " -> curSizeRollBackups set to: " + k);
						curSizeRollBackups = k;
					}
				} catch (Exception exception) {
					LogLog.debug("Encountered a backup file not ending in .x "
							+ file3);
				}
			}

		}
		LogLog.debug("curSizeRollBackups starts at: " + curSizeRollBackups);
		if (staticLogFileName && rollDate) {
			File file2 = new File(baseFileName);
			if (file2.exists()) {
				Date date = new Date(file2.lastModified());
				if (!sdf.format(date).equals(sdf.format(now))) {
					scheduledFilename = baseFileName + sdf.format(date);
					LogLog.debug("Initial roll over to: " + scheduledFilename);
					rollOverTime();
				}
			}
		}
		LogLog.debug("curSizeRollBackups after rollOver at: "
				+ curSizeRollBackups);
	}

	public void activateOptions() {
		LogLog.debug("version: 2003-09-10_19-37");
		if (datePattern != null) {
			now.setTime(System.currentTimeMillis());
			sdf = new SimpleDateFormat(datePattern);
			int i = computeCheckPeriod();
			rc.setType(i);
			nextCheck = rc.getNextCheckMillis(now);
		} else if (rollDate)
			LogLog.error("Either DatePattern or rollingStyle options are not set for ["
					+ name + "].");
		super.fileName = initFileName();
		LogLog.debug("super.fileName (1) = " + super.fileName);
		existingInit();
		LogLog.debug("super.fileName (2) = " + super.fileName);
		super.activateOptions();
		initCountingQuietWriter(fileName, super.fileAppend);
		LogLog.debug("options are activated");
	}

	public void rollOverTime() {
		curTimeRollBackups++;
		if (-1L < dateExpireInterval && staticLogFileName) {
			if (datePattern == null) {
				errorHandler.error("Missing DatePattern option in rollOver().");
				return;
			}
			String s = sdf.format(now);
			if (scheduledFilename.equals(fileName + s)) {
				errorHandler.error("Compare " + scheduledFilename + " : "
						+ fileName + s);
				return;
			}
			closeFile();
			for (int i = 1; i <= curSizeRollBackups; i++) {
				// String s1 = fileName + '.' + i;
				// String s2 = scheduledFilename + '.' + i;
			}

		}
		try {
			curSizeRollBackups = 0;
			scheduledFilename = fileName + sdf.format(now);
			setFile(baseFileName, false);
		} catch (IOException ioexception) {
			errorHandler.error("setFile(" + fileName + ", false) call failed.");
		}
	}

	public static void rollFile(String s, String s1) {
		File file = new File(s1);
		if (file.exists())
			LogLog.debug("deleting existing target file: " + file);
		File file1 = new File(s);
		if (file1.renameTo(file))
			LogLog.debug(s + " -> " + s1);
		else
			LogLog.debug("rename failed: " + s + " -> " + s1);
	}

	public void rollOverSize() {
		LogLog.debug("rollOverSize called");
		closeFile();
		LogLog.debug("rolling over count="
				+ ((CountingQuietWriter) qw).getCount());
		LogLog.debug("maxSizeRollBackups = " + maxSizeRollBackups);
		LogLog.debug("curSizeRollBackups = " + curSizeRollBackups);
		LogLog.debug("countDirection = " + countDirection);
		if (maxSizeRollBackups != 0)
			if (countDirection < 0) {
				if (curSizeRollBackups == maxSizeRollBackups)
					curSizeRollBackups--;
				for (int i = curSizeRollBackups; i >= 1; i--)
					rollFile(fileName + "." + i, fileName + '.' + (i + 1));

				curSizeRollBackups++;
				rollFile(fileName, fileName + ".1");
			} else if (countDirection == 0) {
				curSizeRollBackups++;
				now.setTime(System.currentTimeMillis());
				scheduledFilename = fileName + sdf.format(now);
				rollFile(fileName, scheduledFilename);
			} else {
				// int j;
				// if(curSizeRollBackups >= maxSizeRollBackups &&
				// maxSizeRollBackups > 0)
				// j = (curSizeRollBackups - maxSizeRollBackups) + 1;
				if (staticLogFileName) {
					curSizeRollBackups++;
					rollFile(fileName, fileName + '.' + curSizeRollBackups);
				}
			}
		try {
			setFile(baseFileName, false);
		} catch (IOException ioexception) {
			LogLog.error("setFile(" + fileName + ", false) call failed.",
					ioexception);
		}
	}

	static final int TOP_OF_TROUBLE = -1;
	static final int TOP_OF_MINUTE = 0;
	static final int TOP_OF_HOUR = 1;
	static final int HALF_DAY = 2;
	static final int TOP_OF_DAY = 3;
	static final int TOP_OF_WEEK = 4;
	static final int TOP_OF_MONTH = 5;
	static final int BY_SIZE = 1;
	static final int BY_DATE = 2;
	static final int BY_COMPOSITE = 3;
	static final String S_BY_SIZE = "Size";
	static final String S_BY_DATE = "Date";
	static final String S_BY_COMPOSITE = "Composite";
	private String datePattern;
	private long dateExpireInterval;
	private String scheduledFilename;
	private long nextCheck;
	Date now;
	SimpleDateFormat sdf;
	RollingCalendar rc;
	int checkPeriod;
	protected long maxFileSize;
	protected int maxSizeRollBackups;
	protected int curSizeRollBackups;
	protected int maxTimeRollBackups;
	protected int curTimeRollBackups;
	protected int countDirection;
	protected int rollingStyle;
	protected boolean rollDate;
	protected boolean rollSize;
	protected boolean staticLogFileName;
	protected String baseFileName;*/
}
