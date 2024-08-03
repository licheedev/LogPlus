package com.licheedev.myutils;

import android.util.Log;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LogPlus {

    private static int sCurrentLogLevel = Log.DEBUG;
    private static String sPrefix = null;
    private static int sOffset = 4;
    private static ILogger slogger = new DefaultLogger();

    @IntDef({ Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR })
    @Retention(RetentionPolicy.SOURCE)
    public @interface LogLevel {
    }

    /**
     * 设置Tag前缀，可以防止 RIL，IMS，AT，GSM，STK，CDMA，SMS 开头的log不会被打印
     * 参考 <a href="https://stackoverflow.com/questions/36468822/why-does-logcat-not-print-a-log-when-the-tag-starts-with-ims">Why does Logcat not print a log when the tag starts with "IMS"?</a>
     *
     * @param prefix Tag前缀，
     */
    public static void setTagPrefix(@Nullable String prefix) {
        if (prefix != null) {
            prefix = prefix.trim();
            if (prefix.length() > 0) {
                sPrefix = prefix;
            }
        }
    }

    /**
     * 设置打log等级过滤器，log等级≥logLevel的才会打印
     *
     * @param logLevel 打log等级
     */
    public static void setLogLevelFilter(@LogLevel int logLevel) {
        sCurrentLogLevel = logLevel;
    }

    /**
     * 设置从 Thread.currentThread().getStackTrace() 栈中找到打log调用处的offset，默认为4，如果需要在LogPlus外面套一层，则需要修改这个值
     *
     * @param offset 默认为4
     */
    public static void setInvokedPlaceOffset(int offset) {
        sOffset = offset;
    }

    /**
     * 设置logger实例
     *
     * @param logger
     */
    public void setLogger(ILogger logger) {
        if (logger == null) {
            return;
        }
        slogger = logger;
    }

    // Log.v
    public static void v(
        @Nullable String tag,
        String msg,
        @Nullable Throwable tr,
        int extraOffset
    ) {
        log(Log.VERBOSE, tag, msg, tr, extraOffset);
    }

    public static void v(@Nullable String tag, String msg, @Nullable Throwable tr) {
        log(Log.VERBOSE, tag, msg, tr, 0);
    }

    public static void v(@Nullable String tag, String msg) {
        log(Log.VERBOSE, tag, msg, null, 0);
    }

    public static void v(String msg, @Nullable Throwable tr) {
        log(Log.VERBOSE, null, msg, tr, 0);
    }

    public static void v(String msg) {
        log(Log.VERBOSE, null, msg, null, 0);
    }

    // Log.d
    public static void d(
        @Nullable String tag,
        String msg,
        @Nullable Throwable tr,
        int extraOffset
    ) {
        log(Log.DEBUG, tag, msg, tr, extraOffset);
    }

    public static void d(@Nullable String tag, String msg, @Nullable Throwable tr) {
        log(Log.DEBUG, tag, msg, tr, 0);
    }

    public static void d(@Nullable String tag, String msg) {
        log(Log.DEBUG, tag, msg, null, 0);
    }

    public static void d(String msg, @Nullable Throwable tr) {
        log(Log.DEBUG, null, msg, tr, 0);
    }

    public static void d(String msg) {
        log(Log.DEBUG, null, msg, null, 0);
    }

    // Log.i
    public static void i(
        @Nullable String tag,
        String msg,
        @Nullable Throwable tr,
        int extraOffset
    ) {
        log(Log.INFO, tag, msg, tr, extraOffset);
    }

    public static void i(@Nullable String tag, String msg, @Nullable Throwable tr) {
        log(Log.INFO, tag, msg, tr, 0);
    }

    public static void i(@Nullable String tag, String msg) {
        log(Log.INFO, tag, msg, null, 0);
    }

    public static void i(String msg, @Nullable Throwable tr) {
        log(Log.INFO, null, msg, tr, 0);
    }

    public static void i(String msg) {
        log(Log.INFO, null, msg, null, 0);
    }

    // Log.w
    public static void w(
        @Nullable String tag,
        String msg,
        @Nullable Throwable tr,
        int extraOffset
    ) {
        log(Log.WARN, tag, msg, tr, extraOffset);
    }

    public static void w(@Nullable String tag, String msg, @Nullable Throwable tr) {
        log(Log.WARN, tag, msg, tr, 0);
    }

    public static void w(@Nullable String tag, String msg) {
        log(Log.WARN, tag, msg, null, 0);
    }

    public static void w(String msg, @Nullable Throwable tr) {
        log(Log.WARN, null, msg, tr, 0);
    }

    public static void w(String msg) {
        log(Log.WARN, null, msg, null, 0);
    }

    // Log.e
    public static void e(
        @Nullable String tag,
        String msg,
        @Nullable Throwable tr,
        int extraOffset
    ) {
        log(Log.ERROR, tag, msg, tr, extraOffset);
    }

    public static void e(@Nullable String tag, String msg, @Nullable Throwable tr) {
        log(Log.ERROR, tag, msg, tr, 0);
    }

    public static void e(@Nullable String tag, String msg) {
        log(Log.ERROR, tag, msg, null, 0);
    }

    public static void e(String msg, @Nullable Throwable tr) {
        log(Log.ERROR, null, msg, tr, 0);
    }

    public static void e(String msg) {
        log(Log.ERROR, null, msg, null, 0);
    }

    private static void log(int logLevel, String tag, String msg, Throwable tr, int extraOffset) {

        if (logLevel < sCurrentLogLevel) {
            return;
        }
        StackTraceElement e = Thread.currentThread().getStackTrace()[sOffset + extraOffset];
        String fileName = e.getFileName();
        int lineNum = e.getLineNumber();
        String methodName = e.getMethodName();

        StringBuilder sb = new StringBuilder();

        sb.append(methodName)
            .append('(')
            .append(fileName)
            .append(':')
            .append(lineNum)
            .append(')')
            .append(msg);

        String msgEx = sb.toString();

        sb.delete(0, sb.length());

        // 注意，如果tag是"IMS"开头的(还有其他)，log会不打印，所以加个"前缀_"
        // 参考这里 http://stackoverflow.com/a/36469141/5324526
        if (sPrefix != null) {
            sb.append(sPrefix).append('_');
        }

        if (tag == null || tag.length() == 0 || tag.trim().length() == 0) {
            String className = e.getClassName();
            int index = className.lastIndexOf('.') + 1;
            sb.append(className, index, className.length());
        } else {
            sb.append(tag);
        }

        tag = sb.toString();

        slogger.doLog(logLevel, tag, msg, msgEx, tr);
    }

    /**
     * 调用 {@link Log} 进行打log
     *
     * @param logLevel
     * @param tag
     * @param msg
     * @param tr
     */
    public static void invokeAndroidLog(int logLevel, String tag, String msg, Throwable tr) {
        if (tr == null) {
            switch (logLevel) {
                case Log.VERBOSE:
                    Log.v(tag, msg);
                    break;
                case Log.DEBUG:
                    Log.d(tag, msg);
                    break;
                case Log.INFO:
                    Log.i(tag, msg);
                    break;
                case Log.WARN:
                    Log.w(tag, msg);
                    break;
                case Log.ERROR:
                    Log.e(tag, msg);
                    break;
            }
        } else {
            switch (logLevel) {
                case Log.VERBOSE:
                    Log.v(tag, msg, tr);
                    break;
                case Log.DEBUG:
                    Log.d(tag, msg, tr);
                    break;
                case Log.INFO:
                    Log.i(tag, msg, tr);
                    break;
                case Log.WARN:
                    Log.w(tag, msg, tr);
                    break;
                case Log.ERROR:
                    Log.e(tag, msg, tr);
                    break;
            }
        }
    }

    public interface ILogger {
        /**
         * 打log
         *
         * @param logLevel log等级
         * @param tag tag
         * @param msg msg
         * @param msgEx msg带文件位置
         * @param tr 异常
         */
        void doLog(
            @LogLevel int logLevel, String tag, String msg, String msgEx,
            @Nullable Throwable tr
        );
    }

    public static class DefaultLogger implements ILogger {

        @Override
        public void doLog(int logLevel, String tag, String msg, String msgEx, Throwable tr) {
            invokeAndroidLog(logLevel, tag, msgEx, tr);
        }
    }
}
