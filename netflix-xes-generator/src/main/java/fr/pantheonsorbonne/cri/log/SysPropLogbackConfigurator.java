package fr.pantheonsorbonne.cri.log;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;

/**
 * Looks into System.properties for props -DLOG.loggerName=LEVEL to set Logback
 * levels at startup If LEVEL is empty (setting -DLOG.loggerName without level),
 * it erases a previously set level and will inherit from parent logger
 */
public class SysPropLogbackConfigurator {

	public static final String PROP_PREFIX = "LOG.";

	public static void apply() {
		System.getProperties().stringPropertyNames().stream().filter(name -> name.startsWith(PROP_PREFIX))
				.forEach(SysPropLogbackConfigurator::applyProp);
	}

	public static void applyOnce() {
		OnceInitializer.emptyMethodToForceInit();// force static init. applySysPropsToLogback will be called only once
	}

	private static void applyProp(final String name) {
		final String loggerName = name.substring(PROP_PREFIX.length());
		final String levelStr = System.getProperty(name, "");
		final Level level = Level.toLevel(levelStr, null);
		((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(loggerName)).setLevel(level);
	}

	private static class OnceInitializer {
		static {
			apply();
		}

		static void emptyMethodToForceInit() {
		};
	}
}