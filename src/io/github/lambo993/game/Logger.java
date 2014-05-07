package io.github.lambo993.game;

import java.text.SimpleDateFormat;
import java.util.*;

public class Logger {

	private String name;
	private List<String> history = new ArrayList<String>();

	private Logger(String name) {
		this.name = name;
	}

	public static Logger getLogger(String name) {
		return new Logger(name);
	}

	public void log(Level lvl, String msg) {
		String log = "[" + getTime() + " " + lvl.getName() + "] " + msg;
		System.out.println(log);
		history.add(log);
	}

	public void severe(String msg) {
		log(Level.SEVERE, msg);
	}

	public void warning(String msg) {
		log(Level.WARNING, msg);
	}

	public void info(String msg) {
		log(Level.INFO, msg);
	}

	public void config(String msg) {
		log(Level.CONFIG, msg);
	}

	public void fine(String msg) {
		log(Level.FINE, msg);
	}

	public void finer(String msg) {
		log(Level.FINER, msg);
	}

	public void finest(String msg) {
		log(Level.FINEST, msg);
	}

	public List<String> getHistory() {
		return history;
	}

	public String getTime() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	public String getName() {
		return name;
	}

	public static enum Level {

		SEVERE("SEVERE", 1000),
		WARNING("WARNING", 900),
		INFO("INFO", 800),
		CONFIG("CONFIG", 700),
		FINE("FINE", 500),
		FINER("FINER", 400),
		FINEST("FINEST", 300);

		private final String name;
		private final int value;

		private Level(String name, int value) {
			if (name == null) {
				throw new NullPointerException();
			}
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public final int intValue() {
			return value;
		}
	}
}