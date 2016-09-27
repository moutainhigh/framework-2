/**
 * 
 */
package com.hbasesoft.framework.common.utils.logger;

import com.hbasesoft.framework.common.utils.CommonUtil;

/**
 * <Description> <br>
 * 
 * @author wangwei<br>
 * @version 1.0<br>
 * @CreateDate 2015年6月21日 <br>
 * @see com.hbasesoft.framework.common.utils.logger <br>
 */
public class Logger {

    /**
     * sql语句日志名称，用于记录日志
     */
    private static final String SQL_LOG_NAME = "framework.sql.log";

    /**
     * logger
     */
    private org.apache.log4j.Logger logger;

    /**
     * sqlLoger
     */
    private static org.apache.log4j.Logger sqlLoger;

    /**
     * Logger
     * 
     * @param clazz <br>
     */
    public Logger(Class<?> clazz) {
        this.logger = org.apache.log4j.Logger.getLogger(clazz);
    }

    /**
     * Logger
     * 
     * @param name <br>
     */
    public Logger(String name) {
        this.logger = org.apache.log4j.Logger.getLogger(name);
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void error(Throwable t) {
        logger.error(t.getMessage(), t);
    }

    /**
     * Description: 普通异常日志<br>
     * 
     * @author 王伟<br>
     * @taskId <br>
     * @param message <br>
     */
    public void error(Object message) {
        logger.error(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public void error(Object message, Throwable t) {
        logger.error(message, t);
    }

    /**
     * 记录日志信息
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     * @param params 日志信息 <br>
     */
    public void error(Throwable t, String message, Object... params) {
        logger.error(CommonUtil.messageFormat(message, params));
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void warn(Throwable t) {
        logger.warn(t.getMessage(), t);
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void warn(Object message) {
        logger.warn(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public void warn(Object message, Throwable t) {
        logger.warn(message, t);
    }

    /**
     * 记录日志信息
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     * @param params 日志信息 <br>
     */
    public void warn(Throwable t, String message, Object... params) {
        logger.warn(CommonUtil.messageFormat(message, params), t);
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void debug(Object message) {
        logger.debug(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public void debug(Object message, Throwable t) {
        logger.debug(message, t);
    }

    /**
     * 记录日志信息
     * 
     * @param message 日志信息
     * @param params 日志信息
     */
    public void debug(String message, Object... params) {
        logger.debug(CommonUtil.messageFormat(message, params));
    }

    /**
     * 普通异常日志
     * 
     * @param message 日志信息
     */
    public void info(Object message) {
        logger.info(message);
    }

    /**
     * 带异常的日志信息
     * 
     * @param message 信息
     * @param t 异常
     */
    public void info(Object message, Throwable t) {
        logger.info(message, t);
    }

    /**
     * 记录日志信息
     * 
     * @param message 日志信息
     * @param params 日志信息
     */
    public void info(String message, Object... params) {
        logger.info(CommonUtil.messageFormat(message, params));
    }

    /**
     * sql日志
     * 
     * @param message 日志信息
     */
    public static void sqlInfoLog(String message) {
        if (sqlLoger == null) {
            sqlLoger = org.apache.log4j.Logger.getLogger(SQL_LOG_NAME);
        }
        sqlLoger.info(message);
    }

    /**
     * sql日志
     * 
     * @param t <br>
     * @param message 日志信息 <br>
     */
    public static void sqlErrorLog(String message, Throwable t) {
        if (sqlLoger == null) {
            sqlLoger = org.apache.log4j.Logger.getLogger(SQL_LOG_NAME);
        }
        sqlLoger.info(message);
        sqlLoger.error(message, t);
    }
}
