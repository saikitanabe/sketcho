package net.sevenscales.domain.utils;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.logging.client.LogConfiguration;

public class SLogger {
	private Logger logger;
	private static CustomErrorHandler customErrorHandler;
	private boolean onlyTime = false;
	private Stack<DebugTime> measurements = new Stack<DebugTime>();
	
	public static interface CustomErrorHandler {
		void handleError(String errormsg);
	}
	
	private SLogger(Class clazz) {
		logger = Logger.getLogger(clazz.getName());
	}
	
	public static void setCustomErrorHandler(CustomErrorHandler ceh) {
		customErrorHandler = ceh;
	}
	
	public void debug(String msg) {
		debug("{}", msg);
	}
	
	public void debug(String format, String... values) {
		if (LogConfiguration.loggingIsEnabled(Level.FINER) && !onlyTime) {
			logger.finer(format(format, values));
		}
	}
	
	public void debug(String format, Object... values) {
		if (LogConfiguration.loggingIsEnabled(Level.FINER) && !onlyTime) {
			String[] ints = vals(values);
			logger.finer(format(format, ints));
		}
	}
	
	public void debug2(String format, Object... values) {
		if (LogConfiguration.loggingIsEnabled(Level.FINE) && !onlyTime) {
			String[] ints = vals(values);
			logger.fine(format(format, ints));
		}
	}
	
	public void info(String format, Object... values) {
		if (LogConfiguration.loggingIsEnabled(Level.INFO) && !onlyTime) {
			String[] ints = vals(values);
			logger.info(format(format, ints));
		}
	}

	private String[] vals(Object[] values) {
		String[] ints = new String[values.length];
		int i = 0;
		for (Object v : values) {
			if (v != null) {
				ints[i++] = v.toString();
			} else {
				ints[i++] = "null";
			}
		}
		return ints;
	}

	public static String format(String format, String... values) {
		for (String v : values) {
			format = format.replaceFirst("\\{\\}", v);
		}
		return format;
	}

	public static SLogger createLogger(Class clazz) {
		return new SLogger(clazz);
	}
	
	public void error(String msg) {
		error("{}", msg);
	}
	
//	public void error(String msg, Exception e) {
//		String statcktrace = format(e);
//		logger.severe(format("{} {}\n{}", msg, e.toString(), statcktrace));
//	}
	
	public void error(String msg, Throwable e) {
		String statcktrace = format(e);
		String errormsg = format("{} {}\n{}", msg, e.toString(), statcktrace);
		logger.severe(errormsg);
		
		if (LogConfiguration.loggingIsEnabled() && customErrorHandler != null) {
			customErrorHandler.handleError(errormsg);
		}
	}
	
	public void error(String format, String... values) {
		logger.severe(format(format, values));
	}
	
	public String format(Throwable e) {
		String result = "";
		for (StackTraceElement s : e.getStackTrace()) {
			result += format("    {}.{} {}:{}\n", s.getClassName(), s.getMethodName(), s.getFileName(), Integer.toString(s.getLineNumber()));
//			String filename = s.getFileName().substring(s.getFileName().lastIndexOf('/') + 1);
//			String filename = s.getFileName();
//			filename = filename.substring(0, filename.indexOf('@'));
//			result += format("{{{{}:{}}}}\n", filename, Integer.toString(s.getLineNumber()));
//			result += s + "\n";
		}
		return result;
	}

	private static class DebugTime {
		String title;
		Long time;
		DebugTime(String title, Long time) {
			this.title = title;
			this.time = time;
		}
	}

	public void start(String titleFormat, String... values) {
		if (LogConfiguration.loggingIsEnabled(Level.FINER)) {
			DebugTime time = new DebugTime(format(titleFormat, values), System.currentTimeMillis());
			measurements.push(time);
		}
	}

	public void debugTime() {
		if (LogConfiguration.loggingIsEnabled(Level.FINER)) {
			String format = "{} took: {} milliseconds";
			DebugTime time = measurements.pop();
			Long diff = System.currentTimeMillis() - time.time;
			logger.finer(format(format, time.title, diff.toString()));
		}
	}
}
