package org.clarke.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * A wrapper class for "=" delimited key-value-pair formatted configuration files.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Configuration {
	public static final String DEFAULT_CONF_DIR = "conf";
	public static final String DEFAULT_MAP_REGEX_DELIMITER = "@";
	public static final String DEV_CONFIG_ENV_VAR = "FOOTBALL_DEV_CONFIG";
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
	private String environmentVariable;
	private String propertiesName;
	private String confDir = DEFAULT_CONF_DIR;
	private Properties properties;

	/**
	 * Constructor with implicit environment variable {@value #DEV_CONFIG_ENV_VAR}
	 *
	 * @param propertiesName The name of the properties file, including the extension
	 */
	public Configuration(String propertiesName) {
		this(propertiesName, DEV_CONFIG_ENV_VAR);
	}

	/**
	 * @param propertiesName      The name of the properties file, including the extension
	 * @param environmentVariable The name of the environment variable which points to a configuration directory or the file named by propertiesName
	 */
	public Configuration(String propertiesName, String environmentVariable) {
		this.environmentVariable = environmentVariable;
		this.propertiesName = propertiesName;

		initializeProperties();
	}

	public boolean getBooleanValue(String key, boolean defaultValue) {
		boolean returnValue;
		String property = getProperty(key);

		if (property == null) {
			logger.info("info> Falling back to default value for " + key + ": " + defaultValue + ".");
			returnValue = defaultValue;
		} else {
			returnValue = Boolean.parseBoolean(property);
			logger.info("info> " + propertiesName + " configured the \"" + key + "\" property with value: " + returnValue);
		}

		return returnValue;
	}

	@Deprecated
	public String[] getCSVArrayValue(String key, String[] defaultValue) {
		String[] returnValue;
		String property = getProperty(key);

		if (property == null) {
			logger.info("info> Falling back to default value for " + key + ": " + Arrays.toString(defaultValue) + ".");
			returnValue = defaultValue;
		} else {
			returnValue = property.split(",");
			logger.info("info> " + propertiesName + " configured the \"" + key + "\" property with value: " + Arrays.toString(returnValue));
		}

		return returnValue;
	}

	public List<Boolean> getCSVBooleanList(String key, List<Boolean> defaultValue) {
		List<Boolean> returnValue;
		String property = getProperty(key);

		if (property == null) {
			logger.info("info> Falling back to default value for " + key + ": " + Arrays.toString(defaultValue.toArray()) + ".");
			returnValue = defaultValue;
		} else {
			returnValue = Arrays.stream(property.split(",")).map(Boolean::parseBoolean).collect(Collectors.toList());

			logger.info("info> " + propertiesName + " configured the \"" + key + "\" property with value: " + Arrays.toString(returnValue.toArray()));
		}

		return returnValue;
	}

	public List<Double> getCSVDoubleList(String key, List<Double> defaultValue) {
		List<Double> returnValue;
		String property = getProperty(key);

		if (property == null) {
			logger.info("info> Falling back to default value for " + key + ": " + Arrays.toString(defaultValue.toArray()) + ".");
			returnValue = defaultValue;
		} else {
			returnValue = new ArrayList<>();
			try {
				Arrays.stream(property.split(",")).map(Double::parseDouble).forEach(returnValue::add);
			} catch (NumberFormatException nfe) {
				logger.info("info> A list element was not an integer. Falling back to default value for " + key + ": " + defaultValue + ".");
			}

			logger.info("info> " + propertiesName + " configured the \"" + key + "\" property with value: " + Arrays.toString(returnValue.toArray()));
		}

		return returnValue;
	}

	public List<Integer> getCSVIntegerList(String key, List<Integer> defaultValue) {
		List<Integer> returnValue;
		String property = getProperty(key);

		if (property == null) {
			logger.info("info> Falling back to default value for " + key + ": " + Arrays.toString(defaultValue.toArray()) + ".");
			returnValue = defaultValue;
		} else {
			returnValue = new ArrayList<>();
			try {
				Arrays.stream(property.split(",")).map(Integer::parseInt).forEach(returnValue::add);
			} catch (NumberFormatException nfe) {
				logger.info("info> A list element was not an integer. Falling back to default value for " + key + ": " + defaultValue + ".");
			}

			logger.info("info> " + propertiesName + " configured the \"" + key + "\" property with value: " + Arrays.toString(returnValue.toArray()));
		}

		return returnValue;
	}

	public List<String> getCSVListValue(String key, List<String> defaultValue) {
		List<String> returnValue;
		String property = getProperty(key);

		if (property == null) {
			logger.info("info> Falling back to default value for " + key + ": " + Arrays.toString(defaultValue.toArray()) + ".");
			returnValue = defaultValue;
		} else {
			returnValue = Arrays.asList(property.split(","));
			logger.info("info> " + propertiesName + " configured the \"" + key + "\" property with value: " + Arrays.toString(returnValue.toArray()));
		}

		return returnValue;
	}

	public double getDoubleValue(String key, double defaultValue) {
		double returnValue;
		String property = getProperty(key);

		try {
			returnValue = Double.parseDouble(property);
			logger.info("info> " + propertiesName + " configured the \"" + key + "\" property with value: " + returnValue);
		} catch (NumberFormatException nfe) {
			logger.info("info> Falling back to default value for " + key + ": " + defaultValue + ".");
			returnValue = defaultValue;
		}

		return returnValue;
	}

	public int getIntValue(String key, int defaultValue) {
		int returnValue;
		String property = getProperty(key);

		try {
			returnValue = Integer.parseInt(property);
			logger.info("info> " + propertiesName + " configured the \"" + key + "\" property with value: " + returnValue);
		} catch (NumberFormatException nfe) {
			logger.info("info> Falling back to default value for " + key + ": " + defaultValue + ".");
			returnValue = defaultValue;
		}

		return returnValue;
	}

	/**
	 * Returns a Map formed by "{@code keyValueSeparatorRegex}" separated key-value pairs in a comma separated list
	 * (property=key{{@code keyValueSeparatorRegex}}value,key{{@code keyValueSeparatorRegex}}value,...)
	 *
	 * @param key                    The property name
	 * @param keyValueSeparatorRegex The regex used to split Map items
	 * @param defaultValue           The default Map to return if the property doesn't exist or is improperly formatted
	 * @return The Map formed by "{@code keyValueSeparatorRegex}" separated key-value pairs in a comma separated list
	 * (property=key{{@code keyValueSeparatorRegex}}value,key{{@code keyValueSeparatorRegex}}value,...)
	 */
	public Map<String, String> getStringMap(String key, String keyValueSeparatorRegex, Map<String, String> defaultValue) {
		Map<String, String> returnValue;
		String property = getProperty(key);

		if (property == null) {
			logger.info("info> Falling back to default value for " + key + ": ");
			defaultValue.forEach((k, v) -> System.out.print("{" + k + " : " + v + "}, "));
			returnValue = defaultValue;
		} else {
			returnValue = new HashMap<>();

			for (String csvMapItem : getCSVListValue(key, new ArrayList<>())) {
				String[] splitItem = csvMapItem.split(keyValueSeparatorRegex);
				if (splitItem.length == 2) {
					returnValue.put(splitItem[0], splitItem[1]);
				}
			}
		}

		return returnValue;
	}

	/**
	 * Returns a Map formed by {@value #DEFAULT_MAP_REGEX_DELIMITER} separated key-value pairs in a comma separated list
	 * (property=key{@value #DEFAULT_MAP_REGEX_DELIMITER}value,key{@value #DEFAULT_MAP_REGEX_DELIMITER}value,...)
	 *
	 * @param key          The property name
	 * @param defaultValue The default Map to return if the property doesn't exist or is improperly formatted
	 * @return The Map formed by {@value #DEFAULT_MAP_REGEX_DELIMITER} separated key-value pairs in a comma separated list
	 * (property=key{@value #DEFAULT_MAP_REGEX_DELIMITER}value,key{@value #DEFAULT_MAP_REGEX_DELIMITER}value,key{@value #DEFAULT_MAP_REGEX_DELIMITER}value,...)
	 */
	public Map<String, String> getStringMap(String key, Map<String, String> defaultValue) {
		return getStringMap(key, DEFAULT_MAP_REGEX_DELIMITER, defaultValue);
	}

	public String getStringValue(String key, String defaultValue) {
		String property = getProperty(key);
		if (property == null) {
			logger.info("info> Falling back to default value for " + key + ": " + defaultValue + ".");
			property = defaultValue;
		} else {
			logger.info("info> " + propertiesName + " configured the \"" + key + "\" property with value: " + property);
		}

		return property;
	}

	public void setConfDir(String confDir) {
		this.confDir = confDir;
	}

	private String getProperty(String key) {
		String property = properties.getProperty(key);
		if (property == null) {
			logger.info("info> " + propertiesName + " did not have the \"" + key + "\" property.");
		}

		return property;
	}

	private void initializeProperties() {
		logger.info("info> Finding configured properties...");
		String environmentVariableValue = System.getenv(environmentVariable);
		String propertiesFolder;

		if (environmentVariableValue == null) {
			propertiesFolder = confDir;
			logger.info("info> \"" + environmentVariable + "\" environment variable not found. Using \"" + confDir + "\" directory.");
		} else {
			if (environmentVariableValue.endsWith(propertiesName)) {
				logger.info("info> \"" + environmentVariable + "\" environment variable points to a properties file.");
				propertiesFolder = environmentVariableValue.substring(0, environmentVariableValue.lastIndexOf(File.separator));
			} else {
				File valueFile = new File(environmentVariableValue);
				if (valueFile.isDirectory()) {
					logger.info("info> \"" + environmentVariable + "\" environment variable points to a directory.");
					propertiesFolder = environmentVariableValue;
				} else {
					logger.info("info> \"" + environmentVariable + "\" environment variable does not point to a directory or a properties file.");
					propertiesFolder = confDir;
				}

				logger.info("info> Checking folder \"" + propertiesFolder + "\" for configuration of properties...");
			}
		}

		properties = new Properties();
		boolean loadedProps = false;
		try (FileReader propertiesReader = new FileReader(propertiesFolder + File.separator + propertiesName)) {
			properties.load(propertiesReader);
			loadedProps = true;
		} catch (IOException ioe) {
			logger.info("info> Failed to open configuration properties " + propertiesName +
				" from external file (" + ioe.getMessage() + ").");
		}

		if (!loadedProps) {
			try (InputStream stream = getClass().getResourceAsStream(propertiesFolder + File.separator + propertiesName)) {
				properties.load(stream);
				loadedProps = true;
			} catch (IOException ioe) {
				logger.info("info> Failed to open configuration properties " + propertiesName +
					" from folder in jar (" + ioe.getMessage() + ").");
			}
		}

		if (!loadedProps) {
			try (InputStream stream = getClass().getResourceAsStream(propertiesName)) {
				properties.load(stream);
				loadedProps = true;
			} catch (IOException ioe) {
				logger.info("info> Failed to open configuration properties " + propertiesName +
					" from jar root (" + ioe.getMessage() + ").");
			}
		}

		if (!loadedProps) {
			logger.info("ERROR> Failed to open configuration properties " + propertiesName +
				". This configuration will always return default values!");
		}
	}
}
