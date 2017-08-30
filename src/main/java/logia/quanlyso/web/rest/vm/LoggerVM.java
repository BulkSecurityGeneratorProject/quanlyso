package logia.quanlyso.web.rest.vm;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * View Model object for storing a Logback logger.
 *
 * @author Dai Mai
 */
public class LoggerVM {

	/** The name. */
	private String	name;

	/** The level. */
	private String	level;

	/**
	 * Instantiates a new logger VM.
	 *
	 * @param logger the logger
	 */
	public LoggerVM(Logger logger) {
		this.name = logger.getName();
		this.level = logger.getEffectiveLevel().toString();
	}

	/**
	 * Instantiates a new logger VM.
	 */
	@JsonCreator
	public LoggerVM() {
		// Empty public constructor used by Jackson.
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public String getLevel() {
		return this.level;
	}

	/**
	 * Sets the level.
	 *
	 * @param level the new level
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LoggerVM{" + "name='" + this.name + '\'' + ", level='" + this.level + '\'' + '}';
	}
}
