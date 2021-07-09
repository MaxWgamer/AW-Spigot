package org.bukkit.craftbukkit.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.AbstractLogger;

public class ForwardLogHandler extends ConsoleHandler {
    private Map<String, Logger> cachedLoggers = new ConcurrentHashMap<String, Logger>();

    private Logger getLogger(String name) {
        Logger logger = cachedLoggers.get(name);
        if (logger == null) {
            logger = LogManager.getLogger(name);
            cachedLoggers.put(name, logger);
        }

        return logger;
    }

    @Override
    public void publish(LogRecord record) {
        Logger logger = getLogger(String.valueOf(record.getLoggerName())); // See SPIGOT-1230
        Throwable exception = record.getThrown();
        Level level = record.getLevel();
        String message = getFormatter().formatMessage(record);
        Marker marker = (exception == null ? null : AbstractLogger.EXCEPTION_MARKER);

        if (level == Level.SEVERE) {
            logger.error(marker, message, exception);
        } else if (level == Level.WARNING) {
            logger.warn(marker, message, exception);
        } else if (level == Level.INFO) {
            logger.info(marker, message, exception);
        } else if (level == Level.CONFIG) {
            logger.debug(marker, message, exception);
        } else {
            logger.trace(marker, message, exception);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
