package com.chua.common.support.lang.date;

import com.chua.common.support.lang.date.lunar.LunarTime;
import com.chua.common.support.lang.date.lunar.SolarTime;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.utils.StringUtils;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 时间分析
 *
 * @author CH
 * @since 2022-05-09
 */
public class DateTimeParser {
    private final String value;
    private final TimeNormalizer timeNormalizer;

    public DateTimeParser(String value) {
        this.value = value;
        this.timeNormalizer = new TimeNormalizer();
    }

    /**
     * 解析时间
     *
     * @return DateTime
     */
    public DateTime parse() {
        TimeUnit[] parse = timeNormalizer.parse(value);
        if (parse.length > 0) {
            return DateTime.of(parse[0].getTime());
        }
        return null;
    }


    static final class TimeNormalizer implements Serializable {

        private static final long serialVersionUID = 463541045644656392L;
        private static final Logger LOGGER = LoggerFactory.getLogger(TimeNormalizer.class);

        private String timeBase;
        private String oldTimeBase;
        private static Pattern patterns = null;
        private String target;
        private TimeUnit[] timeToken = new TimeUnit[0];

        private boolean isPreferFuture = true;

        @SneakyThrows
        public TimeNormalizer() {
            if (patterns == null) {
                String resource = "/TimeExp.m";
                InputStream in = null;
                try {
                    in = getClass().getResourceAsStream(this.getClass().getPackage().getName().replace(".", "/") + resource);
                    if (null == in) {
                        in = getClass().getResourceAsStream(resource);
                    }
                    ObjectInputStream objectInputStream = new ObjectInputStream(
                            new BufferedInputStream(new GZIPInputStream(in)));
                    patterns = readModel(objectInputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.print("Read model error!");
                } finally {
                    if (null != in) {
                        in.close();
                    }
                }
            }
        }

        /**
         * 参数为TimeExp.m文件路径
         *
         * @param path
         */
        public TimeNormalizer(String path) {
            if (patterns == null) {
                try {
                    patterns = readModel(path);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.print("Read model error!");
                }
            }
        }

        /**
         * 参数为TimeExp.m文件路径
         *
         * @param path
         */
        public TimeNormalizer(String path, boolean isPreferFuture) {
            this.isPreferFuture = isPreferFuture;
            if (patterns == null) {
                try {
                    patterns = readModel(path);
                    LOGGER.debug("loaded pattern:{}", patterns.pattern());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.err.print("Read model error!");
                }
            }
        }

        /**
         * TimeNormalizer的构造方法，根据提供的待分析字符串和timeBase进行时间表达式提取
         * 在构造方法中已完成对待分析字符串的表达式提取工作
         *
         * @param target   待分析字符串
         * @param timeBase 给定的timeBase
         * @return 返回值
         */
        public TimeUnit[] parse(String target, String timeBase) {
            this.target = target;
            this.timeBase = timeBase;
            this.oldTimeBase = timeBase;
            // 字符串预处理
            preHandling();
            timeToken = timeex(this.target, timeBase);
            return timeToken;
        }

        /**
         * 同上的TimeNormalizer的构造方法，timeBase取默认的系统当前时间
         *
         * @param target 待分析字符串
         * @return 时间单元数组
         */
        public TimeUnit[] parse(String target) {
            this.target = target;
            this.timeBase = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime());
            // Calendar.getInstance().getTime()换成new
            // Date？
            this.oldTimeBase = timeBase;
            preHandling();// 字符串预处理
            timeToken = timeex(this.target, timeBase);
            return timeToken;
        }

        //

        /**
         * timeBase的get方法
         *
         * @return 返回值
         */
        public String getTimeBase() {
            return timeBase;
        }

        /**
         * oldTimeBase的get方法
         *
         * @return 返回值
         */
        public String getOldTimeBase() {
            return oldTimeBase;
        }

        public boolean isPreferFuture() {
            return isPreferFuture;
        }

        public void setPreferFuture(boolean isPreferFuture) {
            this.isPreferFuture = isPreferFuture;
        }

        /**
         * timeBase的set方法
         *
         * @param s timeBase
         */
        public void setTimeBase(String s) {
            timeBase = s;
        }

        /**
         * 重置timeBase为oldTimeBase
         */
        public void resetTimeBase() {
            timeBase = oldTimeBase;
        }

        /**
         * 时间分析结果以TimeUnit组的形式出现，此方法为分析结果的get方法
         *
         * @return 返回值
         */
        public TimeUnit[] getTimeUnit() {
            return timeToken;
        }

        /**
         * 待匹配字符串的清理空白符和语气助词以及大写数字转化的预处理
         */
        private void preHandling() {
            target = StringPreHandlingModule.delKeyword(target, "\\s+"); // 清理空白符
            target = StringPreHandlingModule.delKeyword(target, "[的]+"); // 清理语气助词
            target = StringPreHandlingModule.numberTranslator(target);// 大写数字转化
            // TODO 处理大小写标点符号
        }

        /**
         * 有基准时间输入的时间表达式识别
         * <p>
         * 这是时间表达式识别的主方法， 通过已经构建的正则表达式对字符串进行识别，并按照预先定义的基准时间进行规范化
         * 将所有别识别并进行规范化的时间表达式进行返回， 时间表达式通过TimeUnit类进行定义
         *
         * @param tar      输入文本字符串
         * @param timebase 输入基准时间
         * @return TimeUnit[] 时间表达式类型数组
         */
        private TimeUnit[] timeex(String tar, String timebase) {
            Matcher match;
            int startline = -1, endline = -1;

            String[] temp = new String[99];
            int rpointer = 0;// 计数器，记录当前识别到哪一个字符串了
            TimeUnit[] timeUnits = null;

            match = patterns.matcher(tar);
            boolean startmark = true;
            while (match.find()) {
                startline = match.start();
                if (endline == startline) // 假如下一个识别到的时间字段和上一个是相连的 @author kexm
                {
                    rpointer--;
                    temp[rpointer] = temp[rpointer] + match.group();// 则把下一个识别到的时间字段加到上一个时间字段去
                } else {
                    if (!startmark) {
                        rpointer--;
                        rpointer++;
                    }
                    startmark = false;
                    temp[rpointer] = match.group();// 记录当前识别到的时间字段，并把startmark开关关闭。这个开关貌似没用？
                }
                endline = match.end();
                rpointer++;
            }
            if (rpointer > 0) {
                rpointer--;
                rpointer++;
            }
            timeUnits = new TimeUnit[rpointer];
            //时间上下文： 前一个识别出来的时间会是下一个时间的上下文，用于处理：周六3点到5点这样的多个时间的识别，第二个5点应识别到是周六的。*/
            TimePoint contextTp = new TimePoint();
            for (int j = 0; j < rpointer; j++) {
                timeUnits[j] = new TimeUnit(temp[j], this, contextTp);
                contextTp = timeUnits[j].timePoint;
            }
            //过滤无法识别的字段*/
            timeUnits = filterTimeUnit(timeUnits);
            return timeUnits;
        }

        /**
         * 过滤timeUnit中无用的识别词。无用识别词识别出的时间是1970.01.01 00:00:00(fastTime=-28800000)
         *
         * @param timeUnit
         * @return
         */
        public static TimeUnit[] filterTimeUnit(TimeUnit[] timeUnit) {
            if (timeUnit == null || timeUnit.length < 1) {
                return timeUnit;
            }
            List<TimeUnit> list = new ArrayList<>();
            for (TimeUnit t : timeUnit) {
                if (t.getTime().getTime() != -28800000) {
                    list.add(t);
                }
            }
            TimeUnit[] newT = new TimeUnit[list.size()];
            newT = list.toArray(newT);
            return newT;
        }

        private Pattern readModel(String file) throws Exception {
            ObjectInputStream in;
            String jar = "jar:file", file1 = "file:";
            if (file.startsWith(jar) || file.startsWith(file1)) {
                in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new URL(file).openStream())));
            } else {
                in = new ObjectInputStream(
                        new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
            }
            return readModel(in);
        }

        private Pattern readModel(ObjectInputStream in) throws Exception {
            Pattern p = (Pattern) in.readObject();
            LOGGER.debug("model pattern:{}", p.pattern());
            return Pattern.compile(p.pattern());
        }

        public static void writeModel(Object p, String path) throws Exception {
            ObjectOutputStream out = new ObjectOutputStream(
                    new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(path))));
            out.writeObject(p);
            out.close();
        }


    }


    static final class TimeUnit {
        private static final Pattern M_PATTERN = Pattern.compile("(月|\\.|\\-)");
        //有需要可使用
        //private static final Logger LOGGER = LoggerFactory.getLogger(TimeUnit.class);
        /**
         * 目标字符串
         */
        public String timeExpression = null;
        public String timeNorm = "";
        public int[] timeFull;
        public int[] timeOrigin;
        private Date time;
        private Boolean isAllDayTime = true;
        private boolean isFirstTimeSolveContext = true;

        TimeNormalizer normalizer = null;
        HolidayParser holidayParser = new HolidayParser();
        LunarParser lunarParser = new LunarParser();
        public TimePoint timePoint = new TimePoint();
        public TimePoint tpOrigin = new TimePoint();

        /**
         * 时间表达式单元构造方法
         * 该方法作为时间表达式单元的入口，将时间表达式字符串传入
         *
         * @param expTime 时间表达式字符串
         * @param n       TimeNormalizer
         */

        public TimeUnit(String expTime, TimeNormalizer n) {
            timeExpression = expTime;
            normalizer = n;
            timeNormalization();
        }

        /**
         * 时间表达式单元构造方法
         * 该方法作为时间表达式单元的入口，将时间表达式字符串传入
         *
         * @param expTime   时间表达式字符串
         * @param n         TimeNormalizer
         * @param contextTp 上下文时间
         */

        public TimeUnit(String expTime, TimeNormalizer n, TimePoint contextTp) {
            timeExpression = expTime;
            normalizer = n;
            tpOrigin = contextTp;
            timeNormalization();
        }

        /**
         * return the accurate time object
         */
        public Date getTime() {
            return time;
        }

        /**
         * 年-规范化方法
         * <p>
         * 该方法识别时间表达式单元的年字段
         */
        public void normSetYear() {
            /**假如只有两位数来表示年份*/
            String rule = "[0-9]{2}(?=年)";
            Pattern pattern = Pattern.compile(rule);
            Matcher match = pattern.matcher(timeExpression);
            if (match.find()) {
                timePoint.tUnit[0] = Integer.parseInt(match.group());
                if (timePoint.tUnit[0] >= 0 && timePoint.tUnit[0] < 100) {
                    if (timePoint.tUnit[0] < 30) /**30以下表示2000年以后的年份*/ {
                        timePoint.tUnit[0] += 2000;
                        //否则表示1900年以后的年份
                    } else {
                        timePoint.tUnit[0] += 1900;
                    }
                }

            }
            //不仅局限于支持1XXX年和2XXX年的识别，可识别三位数和四位数表示的年份*/
            rule = "[0-9]?[0-9]{3}(?=年)";

            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            //如果有3位数和4位数的年份，则覆盖原来2位数识别出的年份*/
            if (match.find()) {
                timePoint.tUnit[0] = Integer.parseInt(match.group());
            } else {
                String rule1 = "([早,前])(\\d+)年";
                Pattern pattern1 = Pattern.compile(rule1);
                Matcher match1 = pattern1.matcher(timeExpression);
                if (match1.find()) {
                    int i = Integer.parseInt(match1.group(2));
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - i);
                    timePoint.tUnit[0] = calendar.get(Calendar.YEAR) + 1900;

                    return;
                }

                rule1 = "([迟,后,晚,迟到])(\\d+)年";
                pattern1 = Pattern.compile(rule1);
                match1 = pattern1.matcher(timeExpression);
                if (match1.find()) {
                    int i = Integer.parseInt(match1.group(2));
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + i);
                    timePoint.tUnit[0] = calendar.get(Calendar.YEAR) + 1900;

                }
            }
        }

        /**
         * 月-规范化方法
         * <p>
         * 该方法识别时间表达式单元的月字段
         */
        public void normSetMonth() {
            String rule = "((10)|(11)|(12)|([1-9]))(?=月)";
            Pattern pattern = Pattern.compile(rule);
            Matcher match = pattern.matcher(timeExpression);
            if (match.find()) {
                timePoint.tUnit[1] = Integer.parseInt(match.group());

                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(1);
            } else {
                String value = holidayParser.parseMonth(timeExpression);
                if (!StringUtils.isBlank(value)) {
                    timePoint.tUnit[1] = Integer.parseInt(value);
                    //处理倾向于未来时间的情况  @author kexm*/
                    preferFuture(1);
                } else {
                    String rule1 = "([早,前])(\\d+)月";
                    Pattern pattern1 = Pattern.compile(rule1);
                    Matcher match1 = pattern1.matcher(timeExpression);
                    if (match1.find()) {
                        int anInt = Integer.parseInt(match1.group(2));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - anInt);
                        timePoint.tUnit[0] = calendar.get(Calendar.YEAR) + 1900;
                        timePoint.tUnit[1] = calendar.get(Calendar.MONTH) + 1;

                        //处理倾向于未来时间的情况  @author kexm*/
                        preferFuture(1);
                        return;
                    }

                    rule1 = "([迟,后,晚,迟到])(\\d+)月";
                    pattern1 = Pattern.compile(rule1);
                    match1 = pattern1.matcher(timeExpression);
                    if (match1.find()) {
                        int anInt = Integer.parseInt(match1.group(2));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + anInt);
                        timePoint.tUnit[0] = calendar.get(Calendar.YEAR) + 1900;
                        timePoint.tUnit[1] = calendar.get(Calendar.MONTH) + 1;

                        //处理倾向于未来时间的情况  @author kexm*/
                        preferFuture(1);
                    }
                }
            }
        }

        /**
         * 月-日 兼容模糊写法
         * <p>
         * 该方法识别时间表达式单元的月、日字段
         * <p>
         * add by kexm
         */
        public void normSetmonthFuzzyday() {
            String rule = "((10)|(11)|(12)|([1-9]))(月|\\.|\\-)([0-3][0-9]|[1-9])";
            Pattern pattern = Pattern.compile(rule);
            Matcher match = pattern.matcher(timeExpression);
            if (match.find()) {
                String matchStr = match.group();
                Pattern p = M_PATTERN;
                Matcher m = p.matcher(matchStr);
                if (m.find()) {
                    int splitIndex = m.start();
                    String month = matchStr.substring(0, splitIndex);
                    String date = matchStr.substring(splitIndex + 1);

                    timePoint.tUnit[1] = Integer.parseInt(month);
                    timePoint.tUnit[2] = Integer.parseInt(date);

                    //处理倾向于未来时间的情况  @author kexm*/
                    preferFuture(1);
                }
            }
        }

        /**
         * 日-规范化方法
         * <p>
         * 该方法识别时间表达式单元的日字段
         */
        public void normSetDay() {
            String rule = "((?<!\\d))([0-3][0-9]|[1-9])(?=(日|号))";
            Pattern pattern = Pattern.compile(rule);
            Matcher match = pattern.matcher(timeExpression);
            if (match.find()) {
                timePoint.tUnit[2] = Integer.parseInt(match.group());

                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(2);
            } else {
                String value = holidayParser.parseDay(timeExpression);
                if (!StringUtils.isBlank(value)) {
                    timePoint.tUnit[2] = Integer.parseInt(value);
                    //处理倾向于未来时间的情况  @author kexm*/
                    preferFuture(2);
                } else {
                    String rule1 = "([早,前])(\\d+)天";
                    Pattern pattern1 = Pattern.compile(rule1);
                    Matcher match1 = pattern1.matcher(timeExpression);
                    if (match1.find()) {
                        int anInt = Integer.parseInt(match1.group(2));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - anInt);
                        timePoint.tUnit[1] = calendar.get(Calendar.MONTH) + 1;
                        timePoint.tUnit[2] = calendar.get(Calendar.DAY_OF_MONTH);
                        //处理倾向于未来时间的情况  @author kexm*/
                        preferFuture(2);
                        return;
                    }

                    rule1 = "([迟,后,晚,迟到])(\\d+)天";
                    pattern1 = Pattern.compile(rule1);
                    match1 = pattern1.matcher(timeExpression);
                    if (match1.find()) {
                        int anInt = Integer.parseInt(match1.group(2));
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + anInt);
                        timePoint.tUnit[1] = calendar.get(Calendar.MONTH) + 1;
                        timePoint.tUnit[2] = calendar.get(Calendar.DAY_OF_MONTH);
                        //处理倾向于未来时间的情况  @author kexm*/
                        preferFuture(2);
                    }
                }
            }
        }

        /**
         * 时-规范化方法
         * <p>
         * 该方法识别时间表达式单元的时字段
         */
        public void normSetHour() {
            String rule = "(?<!(周|星期))([0-2]?[0-9])(?=(点|时))";

            Pattern pattern = Pattern.compile(rule);
            Matcher match = pattern.matcher(timeExpression);
            if (match.find()) {
                timePoint.tUnit[3] = Integer.parseInt(match.group());
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(3);
                isAllDayTime = false;
            }

            String rule1 = "([早,提前,前])(\\d+)(?=(点|时|小时))";
            Pattern pattern1 = Pattern.compile(rule1);
            Matcher match1 = pattern1.matcher(timeExpression);
            if (match1.find()) {
                int anInt = Integer.parseInt(match1.group(2));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - anInt);
                timePoint.tUnit[2] = calendar.get(Calendar.DAY_OF_MONTH);
                timePoint.tUnit[3] = calendar.get(Calendar.HOUR_OF_DAY);
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(3);
                return;
            }

            rule1 = "([晚,迟,迟到])(\\d+)(?=(点|时|小时))";
            pattern1 = Pattern.compile(rule1);
            match1 = pattern1.matcher(timeExpression);
            if (match1.find()) {
                int anInt = Integer.parseInt(match1.group(2));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + anInt);
                timePoint.tUnit[2] = calendar.get(Calendar.DAY_OF_MONTH);
                timePoint.tUnit[3] = calendar.get(Calendar.HOUR_OF_DAY);
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(3);
            }
            /*
             * 对关键字：早（包含早上/早晨/早间），上午，中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM的正确时间计算
             * 规约：
             * 1.中午/午间0-10点视为12-22点
             * 2.下午/午后0-11点视为12-23点
             * 3.晚上/傍晚/晚间/晚1-11点视为13-23点，12点视为0点
             * 4.0-11点pm/PM视为12-23点
             *
             * add by kexm
             */
            rule = "凌晨";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                //增加对没有明确时间点，只写了“凌晨”这种情况的处理 @author kexm*/
                if (timePoint.tUnit[3] == -1) {
                    timePoint.tUnit[3] = RangeTimeEnum.DAY_BREAK.getHourTime();
                }
                /**处理倾向于未来时间的情况  @author kexm*/
                preferFuture(3);
                isAllDayTime = false;
            }

            rule = "早上|早晨|早间|晨间|今早|明早";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                //增加对没有明确时间点，只写了“早上/早晨/早间”这种情况的处理 @author kexm*/
                if (timePoint.tUnit[3] == -1) {
                    timePoint.tUnit[3] = RangeTimeEnum.EARLY_MORNING.getHourTime();
                }
                /**处理倾向于未来时间的情况  @author kexm*/
                preferFuture(3);
                isAllDayTime = false;
            }

            rule = "上午";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                //增加对没有明确时间点，只写了“上午”这种情况的处理 @author kexm*/
                if (timePoint.tUnit[3] == -1) {
                    timePoint.tUnit[3] = RangeTimeEnum.MORNING.getHourTime();
                }
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(3);
                isAllDayTime = false;
            }

            rule = "(中午)|(午间)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                if (timePoint.tUnit[3] >= 0 && timePoint.tUnit[3] <= 10) {
                    timePoint.tUnit[3] += 12;
                }
                //增加对没有明确时间点，只写了“中午/午间”这种情况的处理 @author kexm*/
                if (timePoint.tUnit[3] == -1) {
                    timePoint.tUnit[3] = RangeTimeEnum.NOON.getHourTime();
                }
                /**处理倾向于未来时间的情况  @author kexm*/
                preferFuture(3);
                isAllDayTime = false;
            }

            rule = "(下午)|(午后)|(pm)|(PM)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                if (timePoint.tUnit[3] >= 0 && timePoint.tUnit[3] <= 11) {
                    timePoint.tUnit[3] += 12;
                }
                //增加对没有明确时间点，只写了“下午|午后”这种情况的处理  @author kexm*/
                if (timePoint.tUnit[3] == -1) {
                    timePoint.tUnit[3] = RangeTimeEnum.AFTERNOON.getHourTime();
                }
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(3);
                isAllDayTime = false;
            }

            rule = "晚上|夜间|夜里|今晚|明晚";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                if (timePoint.tUnit[3] >= 1 && timePoint.tUnit[3] <= 11) {
                    timePoint.tUnit[3] += 12;
                } else if (timePoint.tUnit[3] == 12) {
                    timePoint.tUnit[3] = 0;
                } else if (timePoint.tUnit[3] == -1) {
                    timePoint.tUnit[3] = RangeTimeEnum.NIGHT.getHourTime();
                }

                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(3);
                isAllDayTime = false;
            }

        }

        /**
         * 分-规范化方法
         * <p>
         * 该方法识别时间表达式单元的分字段
         */
        public void normSetMinute() {
            String rule = "([0-5]?[0-9](?=分(?!钟)))|((?<=((?<!小)[点时]))[0-5]?[0-9](?!刻))";

            Pattern pattern = Pattern.compile(rule);
            Matcher match = pattern.matcher(timeExpression);
            if (match.find()) {
                String empty = "";
                if (!match.group().equals(empty)) {
                    timePoint.tUnit[4] = Integer.parseInt(match.group());
                    //处理倾向于未来时间的情况  @author kexm*/
                    preferFuture(4);
                    isAllDayTime = false;
                }
            }


            String rule1 = "([早,提前,前])(\\d+)(?=(刻|点|分))";
            Pattern pattern1 = Pattern.compile(rule1);
            Matcher match1 = pattern1.matcher(timeExpression);
            if (match1.find()) {
                int anInt = Integer.parseInt(match1.group(2));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - anInt);
                timePoint.tUnit[3] = calendar.get(Calendar.HOUR_OF_DAY);
                timePoint.tUnit[4] = calendar.get(Calendar.MINUTE);
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(4);
                return;
            }

            rule1 = "([晚,迟,迟到])(\\d+)(?=(刻|点|分))";
            pattern1 = Pattern.compile(rule1);
            match1 = pattern1.matcher(timeExpression);
            if (match1.find()) {
                int anInt = Integer.parseInt(match1.group(2));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + anInt);
                timePoint.tUnit[3] = calendar.get(Calendar.HOUR_OF_DAY);
                timePoint.tUnit[4] = calendar.get(Calendar.MINUTE);
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(4);
            }

            // 加对一刻，半，3刻的正确识别（1刻为15分，半为30分，3刻为45分）*/
            rule = "(?<=[点时])[1一]刻(?!钟)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                timePoint.tUnit[4] = 15;
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(4);
                isAllDayTime = false;
            }

            rule = "(?<=[点时])半";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                timePoint.tUnit[4] = 30;
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(4);
                isAllDayTime = false;
            }

            rule = "(?<=[点时])[3三]刻(?!钟)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                timePoint.tUnit[4] = 45;
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(4);
                isAllDayTime = false;
            }
        }

        /**
         * 秒-规范化方法
         * <p>
         * 该方法识别时间表达式单元的秒字段
         */
        public void normSetSecond() {
            /*
             * 添加了省略“分”说法的时间
             * 如17点15分32
             * modified by 曹零
             */
            String rule = "([0-5]?[0-9](?=秒))|((?<=分)[0-5]?[0-9])";

            Pattern pattern = Pattern.compile(rule);
            Matcher match = pattern.matcher(timeExpression);
            if (match.find()) {
                timePoint.tUnit[5] = Integer.parseInt(match.group());
                isAllDayTime = false;
            }


            String rule1 = "([早,提前,前])(\\d+)(?=(秒))";
            Pattern pattern1 = Pattern.compile(rule1);
            Matcher match1 = pattern1.matcher(timeExpression);
            if (match1.find()) {
                int anInt = Integer.parseInt(match1.group(2));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - anInt);
                timePoint.tUnit[4] = calendar.get(Calendar.MINUTE);
                timePoint.tUnit[5] = calendar.get(Calendar.SECOND);
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(4);
                return;
            }

            rule1 = "([晚,迟,迟到])(\\d+)(?=(秒))";
            pattern1 = Pattern.compile(rule1);
            match1 = pattern1.matcher(timeExpression);
            if (match1.find()) {
                int anInt = Integer.parseInt(match1.group(2));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + anInt);
                timePoint.tUnit[4] = calendar.get(Calendar.MINUTE);
                timePoint.tUnit[5] = calendar.get(Calendar.SECOND);
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(4);
            }
        }

        /**
         * 特殊形式的规范化方法
         * <p>
         * 该方法识别特殊形式的时间表达式单元的各个字段
         */
        public void normSetTotal() {
            String rule;
            Pattern pattern;
            Matcher match;
            String[] tmpParser;
            String tmpTarget;

            rule = "(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]:[0-5]?[0-9]";
            String sq = ":";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                tmpParser = new String[3];
                tmpTarget = match.group();
                tmpParser = tmpTarget.split(sq);
                timePoint.tUnit[3] = Integer.parseInt(tmpParser[0]);
                timePoint.tUnit[4] = Integer.parseInt(tmpParser[1]);
                timePoint.tUnit[5] = Integer.parseInt(tmpParser[2]);
                //处理倾向于未来时间的情况  @author kexm*/
                preferFuture(3);
                isAllDayTime = false;
            } else {
                rule = "(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]";
                pattern = Pattern.compile(rule);
                match = pattern.matcher(timeExpression);
                if (match.find()) {
                    tmpParser = new String[2];
                    tmpTarget = match.group();
                    tmpParser = tmpTarget.split(sq);
                    timePoint.tUnit[3] = Integer.parseInt(tmpParser[0]);
                    timePoint.tUnit[4] = Integer.parseInt(tmpParser[1]);
                    //处理倾向于未来时间的情况  @author kexm*/
                    preferFuture(3);
                    isAllDayTime = false;
                }
            }
            /*
             * 增加了:固定形式时间表达式的
             * 中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM
             * 的正确时间计算，规约同上
             */
            rule = "(中午)|(午间)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                if (timePoint.tUnit[3] >= 0 && timePoint.tUnit[3] <= 10) {
                    timePoint.tUnit[3] += 12;
                }
                if (timePoint.tUnit[3] == -1) {
                    timePoint.tUnit[3] = RangeTimeEnum.NOON.getHourTime();
                }
                /**处理倾向于未来时间的情况  @author kexm*/
                preferFuture(3);
                isAllDayTime = false;

            }

            rule = "(下午)|(午后)|(pm)|(PM)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                if (timePoint.tUnit[3] >= 0 && timePoint.tUnit[3] <= 11) {
                    timePoint.tUnit[3] += 12;
                }
                if (timePoint.tUnit[3] == -1) {
                    timePoint.tUnit[3] = RangeTimeEnum.AFTERNOON.getHourTime();
                }
                preferFuture(3);
                isAllDayTime = false;
            }

            rule = "晚";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                if (timePoint.tUnit[3] >= 1 && timePoint.tUnit[3] <= 11) {
                    timePoint.tUnit[3] += 12;
                } else if (timePoint.tUnit[3] == 12) {
                    timePoint.tUnit[3] = 0;
                }
                if (timePoint.tUnit[3] == -1) {
                    timePoint.tUnit[3] = RangeTimeEnum.NIGHT.getHourTime();
                }
                preferFuture(3);
                isAllDayTime = false;
            }


            rule = "[0-9]?[0-9]?[0-9]{2}-((10)|(11)|(12)|([1-9]))-((?<!\\d))([0-3][0-9]|[1-9])";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                tmpParser = new String[3];
                tmpTarget = match.group();
                tmpParser = tmpTarget.split("-");
                timePoint.tUnit[0] = Integer.parseInt(tmpParser[0]);
                timePoint.tUnit[1] = Integer.parseInt(tmpParser[1]);
                timePoint.tUnit[2] = Integer.parseInt(tmpParser[2]);
            }

            rule = "((10)|(11)|(12)|([1-9]))/((?<!\\d))([0-3][0-9]|[1-9])/[0-9]?[0-9]?[0-9]{2}";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                tmpParser = new String[3];
                tmpTarget = match.group();
                tmpParser = tmpTarget.split("/");
                timePoint.tUnit[1] = Integer.parseInt(tmpParser[0]);
                timePoint.tUnit[2] = Integer.parseInt(tmpParser[1]);
                timePoint.tUnit[0] = Integer.parseInt(tmpParser[2]);
            }

            /*
             * 增加了:固定形式时间表达式 年.月.日 的正确识别
             * add by 曹零
             */
            rule = "[0-9]?[0-9]?[0-9]{2}\\.((10)|(11)|(12)|([1-9]))\\.((?<!\\d))([0-3][0-9]|[1-9])";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                tmpParser = new String[3];
                tmpTarget = match.group();
                tmpParser = tmpTarget.split("\\.");
                timePoint.tUnit[0] = Integer.parseInt(tmpParser[0]);
                timePoint.tUnit[1] = Integer.parseInt(tmpParser[1]);
                timePoint.tUnit[2] = Integer.parseInt(tmpParser[2]);
            }
        }

        /**
         * 设置以上文时间为基准的时间偏移计算
         */
        public void normSetBaseRelated() {
            String[] timeGrid = new String[6];
            timeGrid = normalizer.getTimeBase().split("-");
            int[] ini = new int[6];
            for (int i = 0; i < 6; i++) {
                ini[i] = Integer.parseInt(timeGrid[i]);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(ini[0], ini[1] - 1, ini[2], ini[3], ini[4], ini[5]);
            calendar.getTime();

            boolean[] flag = {false, false, false};//观察时间表达式是否因当前相关时间表达式而改变时间


            String rule = "\\d+(?=天[以之]?前)";
            Pattern pattern = Pattern.compile(rule);
            Matcher match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                int day = Integer.parseInt(match.group());
                calendar.add(Calendar.DATE, -day);
            }

            rule = "\\d+(?=天[以之]?后)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                int day = Integer.parseInt(match.group());
                calendar.add(Calendar.DATE, day);
            }

            rule = "\\d+(?=(个)?月[以之]?前)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[1] = true;
                int month = Integer.parseInt(match.group());
                calendar.add(Calendar.MONTH, -month);
            }

            rule = "\\d+(?=(个)?月[以之]?后)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[1] = true;
                int month = Integer.parseInt(match.group());
                calendar.add(Calendar.MONTH, month);
            }

            rule = "\\d+(?=年[以之]?前)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[0] = true;
                int year = Integer.parseInt(match.group());
                calendar.add(Calendar.YEAR, -year);
            }

            rule = "\\d+(?=年[以之]?后)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[0] = true;
                int year = Integer.parseInt(match.group());
                calendar.add(Calendar.YEAR, year);
            }

            String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
            String[] strings = s.split("-");
            if (flag[0] || flag[1] || flag[2]) {
                timePoint.tUnit[0] = Integer.parseInt(strings[0]);
            }
            if (flag[1] || flag[2]) {
                timePoint.tUnit[1] = Integer.parseInt(strings[1]);
            }
            if (flag[2]) {
                timePoint.tUnit[2] = Integer.parseInt(strings[2]);
            }
        }

        /**
         * 设置当前时间相关的时间表达式
         */
        public void normSetCurRelated() {
            String[] timeGrid = new String[6];
            timeGrid = normalizer.getOldTimeBase().split("-");
            int[] ini = new int[6];
            for (int i = 0; i < 6; i++) {
                ini[i] = Integer.parseInt(timeGrid[i]);
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.set(ini[0], ini[1] - 1, ini[2], ini[3], ini[4], ini[5]);
            calendar.getTime();

            boolean[] flag = {false, false, false};//观察时间表达式是否因当前相关时间表达式而改变时间

            String rule = "前年";
            Pattern pattern = Pattern.compile(rule);
            Matcher match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[0] = true;
                calendar.add(Calendar.YEAR, -2);
            }

            rule = "去年";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[0] = true;
                calendar.add(Calendar.YEAR, -1);
            }

            rule = "今年";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[0] = true;
                calendar.add(Calendar.YEAR, 0);
            }

            rule = "明年";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[0] = true;
                calendar.add(Calendar.YEAR, 1);
            }

            rule = "后年";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[0] = true;
                calendar.add(Calendar.YEAR, 2);
            }

            rule = "上(个)?月";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[1] = true;
                calendar.add(Calendar.MONTH, -1);

            }

            rule = "(本|这个)月";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[1] = true;
                calendar.add(Calendar.MONTH, 0);
            }

            rule = "下(个)?月";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[1] = true;
                calendar.add(Calendar.MONTH, 1);
            }

            rule = "大前天";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                calendar.add(Calendar.DATE, -3);
            }

            rule = "(?<!大)前天";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                calendar.add(Calendar.DATE, -2);
            }

            rule = "昨";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                calendar.add(Calendar.DATE, -1);
            }

            rule = "今(?!年)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                calendar.add(Calendar.DATE, 0);
            }

            rule = "明(?!年)";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                calendar.add(Calendar.DATE, 1);
            }

            rule = "(?<!大)后天";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                calendar.add(Calendar.DATE, 2);
            }

            rule = "大后天";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                calendar.add(Calendar.DATE, 3);
            }

            rule = "(?<=(上上(周|星期)))[1-7]?";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                int week;
                try {
                    week = Integer.parseInt(match.group());
                } catch (NumberFormatException e) {
                    week = 1;
                }
                if (week == 7) {
                    week = 1;
                } else {
                    week++;
                }
                calendar.add(Calendar.WEEK_OF_MONTH, -2);
                calendar.set(Calendar.DAY_OF_WEEK, week);
            }

            rule = "(?<=((?<!上)上(周|星期)))[1-7]?";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                int week;
                try {
                    week = Integer.parseInt(match.group());
                } catch (NumberFormatException e) {
                    week = 1;
                }
                if (week == 7) {
                    week = 1;
                } else {
                    week++;
                }
                calendar.add(Calendar.WEEK_OF_MONTH, -1);
                calendar.set(Calendar.DAY_OF_WEEK, week);
            }

            rule = "(?<=((?<!下)下(周|星期)))[1-7]?";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                int week;
                try {
                    week = Integer.parseInt(match.group());
                } catch (NumberFormatException e) {
                    week = 1;
                }
                if (week == 7) {
                    week = 1;
                } else {
                    week++;
                }
                calendar.add(Calendar.WEEK_OF_MONTH, 1);
                calendar.set(Calendar.DAY_OF_WEEK, week);
            }

            rule = "(?<=(下下(周|星期)))[1-7]?";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                int week;
                try {
                    week = Integer.parseInt(match.group());
                } catch (NumberFormatException e) {
                    week = 1;
                }
                if (week == 7) {
                    week = 1;
                } else {
                    week++;
                }
                calendar.add(Calendar.WEEK_OF_MONTH, 2);
                calendar.set(Calendar.DAY_OF_WEEK, week);
            }

            rule = "(?<=((?<!(上|下))(周|星期)))[1-7]?";
            pattern = Pattern.compile(rule);
            match = pattern.matcher(timeExpression);
            if (match.find()) {
                flag[2] = true;
                int week;
                try {
                    week = Integer.parseInt(match.group());
                } catch (NumberFormatException e) {
                    week = 1;
                }
                if (week == 7) {
                    week = 1;
                } else {
                    week++;
                }
                calendar.add(Calendar.WEEK_OF_MONTH, 0);
                calendar.set(Calendar.DAY_OF_WEEK, week);
                /**处理未来时间倾向 @author kexm*/
                preferFutureWeek(week, calendar);
            }

            String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
            String[] timeFin = s.split("-");
            if (flag[0] || flag[1] || flag[2]) {
                timePoint.tUnit[0] = Integer.parseInt(timeFin[0]);
            }
            if (flag[1] || flag[2]) {
                timePoint.tUnit[1] = Integer.parseInt(timeFin[1]);
            }
            if (flag[2]) {
                timePoint.tUnit[2] = Integer.parseInt(timeFin[2]);
            }

        }

        /**
         * 该方法用于更新timeBase使之具有上下文关联性
         */
        public void modifyTimeBase() {
            String[] timeGrid = new String[6];
            timeGrid = normalizer.getTimeBase().split("-");

            String s = "";
            if (timePoint.tUnit[0] != -1) {
                s += Integer.toString(timePoint.tUnit[0]);
            } else {
                s += timeGrid[0];
            }
            for (int i = 1; i < 6; i++) {
                s += "-";
                if (timePoint.tUnit[i] != -1) {
                    s += Integer.toString(timePoint.tUnit[i]);
                } else {
                    s += timeGrid[i];
                }
            }
            normalizer.setTimeBase(s);
        }

        /**
         * 时间表达式规范化的入口
         * <p>
         * 时间表达式识别后，通过此入口进入规范化阶段，
         * 具体识别每个字段的值
         */
        public void timeNormalization() {
            normSetYear();
            normSetMonth();
            normSetDay();
            normSetmonthFuzzyday();/**add by kexm*/
            normSetBaseRelated();
            normSetCurRelated();
            normSetHour();
            normSetMinute();
            normSetSecond();
            normSetTotal();
            modifyTimeBase();
            //农历
            normLunar();

            tpOrigin.tUnit = timePoint.tUnit.clone();

            String[] timeGrid = new String[6];
            timeGrid = normalizer.getTimeBase().split("-");

            int tunitpointer = 5;
            while (tunitpointer >= 0 && timePoint.tUnit[tunitpointer] < 0) {
                tunitpointer--;
            }
            for (int i = 0; i < tunitpointer; i++) {
                if (timePoint.tUnit[i] < 0) {
                    timePoint.tUnit[i] = Integer.parseInt(timeGrid[i]);
                }
            }
            String[] resultTmp = new String[6];
            resultTmp[0] = String.valueOf(timePoint.tUnit[0]);
            if (timePoint.tUnit[0] >= 10 && timePoint.tUnit[0] < 100) {
                resultTmp[0] = "19" + timePoint.tUnit[0];
            }
            if (timePoint.tUnit[0] > 0 && timePoint.tUnit[0] < 10) {
                resultTmp[0] = "200" + timePoint.tUnit[0];
            }

            for (int i = 1; i < 6; i++) {
                resultTmp[i] = String.valueOf(timePoint.tUnit[i]);
            }

            Calendar cale = Calendar.getInstance();            //leverage a calendar object to figure out the final time
            cale.clear();
            if (Integer.parseInt(resultTmp[0]) != -1) {
                timeNorm += resultTmp[0] + "年";
                cale.set(Calendar.YEAR, Integer.valueOf(resultTmp[0]));
                if (Integer.parseInt(resultTmp[1]) != -1) {
                    timeNorm += resultTmp[1] + "月";
                    cale.set(Calendar.MONTH, Integer.valueOf(resultTmp[1]) - 1);
                    if (Integer.parseInt(resultTmp[2]) != -1) {
                        timeNorm += resultTmp[2] + "日";
                        cale.set(Calendar.DAY_OF_MONTH, Integer.valueOf(resultTmp[2]));
                        if (Integer.parseInt(resultTmp[3]) != -1) {
                            timeNorm += resultTmp[3] + "时";
                            cale.set(Calendar.HOUR_OF_DAY, Integer.valueOf(resultTmp[3]));
                            if (Integer.parseInt(resultTmp[4]) != -1) {
                                timeNorm += resultTmp[4] + "分";
                                cale.set(Calendar.MINUTE, Integer.valueOf(resultTmp[4]));
                                if (Integer.parseInt(resultTmp[5]) != -1) {
                                    timeNorm += resultTmp[5] + "秒";
                                    cale.set(Calendar.SECOND, Integer.valueOf(resultTmp[5]));
                                }
                            }
                        }
                    }
                }
            }
            time = cale.getTime();

            timeFull = timePoint.tUnit.clone();
//		time_origin = _tp_origin.tunit.clone(); comment by kexm
        }

        /**
         * 农历-规范化方法
         * <p>
         * 该方法识别时间表达式单元的年字段
         */
        private void normLunar() {
            lunarParser.parse(timePoint.tUnit, timeExpression);
        }

        public Boolean getIsAllDayTime() {
            return isAllDayTime;
        }

        public void setIsAllDayTime(Boolean isAllDayTime) {
            this.isAllDayTime = isAllDayTime;
        }

        @Override
        public String toString() {
            return timeExpression + " ---> " + timeNorm;
        }

        /**
         * 如果用户选项是倾向于未来时间，检查checkTimeIndex所指的时间是否是过去的时间，如果是的话，将大一级的时间设为当前时间的+1。
         * <p>
         * 如在晚上说“早上8点看书”，则识别为明天早上;
         * 12月31日说“3号买菜”，则识别为明年1月的3号。
         *
         * @param checkTimeIndex _tp.tunit时间数组的下标
         */
        private void preferFuture(int checkTimeIndex) {
            /**1. 检查被检查的时间级别之前，是否没有更高级的已经确定的时间，如果有，则不进行处理.*/
            for (int i = 0; i < checkTimeIndex; i++) {
                if (timePoint.tUnit[i] != -1) {
                    return;
                }
            }
            /**2. 根据上下文补充时间*/
            checkContextTime(checkTimeIndex);
            /**3. 根据上下文补充时间后再次检查被检查的时间级别之前，是否没有更高级的已经确定的时间，如果有，则不进行倾向处理.*/
            for (int i = 0; i < checkTimeIndex; i++) {
                if (timePoint.tUnit[i] != -1) {
                    return;
                }
            }
            /**4. 确认用户选项*/
            if (!normalizer.isPreferFuture()) {
                return;
            }
            /**5. 获取当前时间，如果识别到的时间小于当前时间，则将其上的所有级别时间设置为当前时间，并且其上一级的时间步长+1*/
            Calendar c = Calendar.getInstance();
            if (this.normalizer.getTimeBase() != null) {
                String[] ini = this.normalizer.getTimeBase().split("-");
                c.set(Integer.valueOf(ini[0]).intValue(), Integer.valueOf(ini[1]).intValue() - 1, Integer.valueOf(ini[2]).intValue()
                        , Integer.valueOf(ini[3]).intValue(), Integer.valueOf(ini[4]).intValue(), Integer.valueOf(ini[5]).intValue());
//            LOGGER.debug(DateUtil.formatDateDefault(c.getTime()));
            }

            int curTime = c.get(TUNIT_MAP.get(checkTimeIndex));
            if (curTime < timePoint.tUnit[checkTimeIndex]) {
                return;
            }
            //准备增加的时间单位是被检查的时间的上一级，将上一级时间+1
            int addTimeUnit = TUNIT_MAP.get(checkTimeIndex - 1);
            c.add(addTimeUnit, 1);

//		_tp.tunit[checkTimeIndex - 1] = c.get(TUNIT_MAP.get(checkTimeIndex - 1));
            for (int i = 0; i < checkTimeIndex; i++) {
                timePoint.tUnit[i] = c.get(TUNIT_MAP.get(i));
                if (TUNIT_MAP.get(i) == Calendar.MONTH) {
                    ++timePoint.tUnit[i];
                }
            }

        }

        /**
         * 如果用户选项是倾向于未来时间，检查所指的day_of_week是否是过去的时间，如果是的话，设为下周。
         * <p>
         * 如在周五说：周一开会，识别为下周一开会
         *
         * @param weekday 识别出是周几（范围1-7）
         */
        private void preferFutureWeek(int weekday, Calendar c) {
            /**1. 确认用户选项*/
            if (!normalizer.isPreferFuture()) {
                return;
            }
            /**2. 检查被检查的时间级别之前，是否没有更高级的已经确定的时间，如果有，则不进行倾向处理.*/
            int checkTimeIndex = 2;
            for (int i = 0; i < checkTimeIndex; i++) {
                if (timePoint.tUnit[i] != -1) {
                    return;
                }
            }
            /**获取当前是在周几，如果识别到的时间小于当前时间，则识别时间为下一周*/
            Calendar curC = Calendar.getInstance();
            if (this.normalizer.getTimeBase() != null) {
                String[] ini = this.normalizer.getTimeBase().split("-");
                curC.set(Integer.valueOf(ini[0]).intValue(), Integer.valueOf(ini[1]).intValue() - 1, Integer.valueOf(ini[2]).intValue()
                        , Integer.valueOf(ini[3]).intValue(), Integer.valueOf(ini[4]).intValue(), Integer.valueOf(ini[5]).intValue());
            }
            int curWeekday = curC.get(Calendar.DAY_OF_WEEK);
            if (weekday == 1) {
                weekday = 7;
            }
            if (curWeekday < weekday) {
                return;
            }
            //准备增加的时间单位是被检查的时间的上一级，将上一级时间+1
            c.add(Calendar.WEEK_OF_YEAR, 1);
        }

        /**
         * 根据上下文时间补充时间信息
         */
        private void checkContextTime(int checkTimeIndex) {
            for (int i = 0; i < checkTimeIndex; i++) {
                if (timePoint.tUnit[i] == -1 && tpOrigin.tUnit[i] != -1) {
                    timePoint.tUnit[i] = tpOrigin.tUnit[i];
                }
            }
            //在处理小时这个级别时，如果上文时间是下午的且下文没有主动声明小时级别以上的时间，则也把下文时间设为下午*/
            if (isFirstTimeSolveContext && checkTimeIndex == 3 && tpOrigin.tUnit[checkTimeIndex] >= 12 && timePoint.tUnit[checkTimeIndex] < 12) {
                timePoint.tUnit[checkTimeIndex] += 12;
            }
            isFirstTimeSolveContext = false;
        }

        private static final Map<Integer, Integer> TUNIT_MAP = new HashMap<>();

        static {
            TUNIT_MAP.put(0, Calendar.YEAR);
            TUNIT_MAP.put(1, Calendar.MONTH);
            TUNIT_MAP.put(2, Calendar.DAY_OF_MONTH);
            TUNIT_MAP.put(3, Calendar.HOUR_OF_DAY);
            TUNIT_MAP.put(4, Calendar.MINUTE);
            TUNIT_MAP.put(5, Calendar.SECOND);
        }
    }

    static final class TimePoint {
        int[] tUnit = {-1, -1, -1, -1, -1, -1};
    }

    static enum RangeTimeEnum {
        //day end
        DAY_BREAK(3),
        //早
        EARLY_MORNING(8),
        //上午
        MORNING(10),
        //中午、午间
        NOON(12),
        //下午、午后
        AFTERNOON(15),
        //晚上、傍晚
        NIGHT(18),
        //晚、晚间
        LATE_NIGHT(20),
        //深夜,
        MID_NIGHT(23);

        private int hourTime = 0;

        /**
         * @param hourTime hourTime
         */
        RangeTimeEnum(int hourTime) {
            this.setHourTime(hourTime);
        }

        /**
         * @return the hourTime
         */
        public int getHourTime() {
            return hourTime;
        }

        /**
         * @param hourTime the hourTime to set
         */
        public void setHourTime(int hourTime) {
            this.hourTime = hourTime;
        }
    }

    static class StringPreHandlingModule {

        private static final Pattern B_PATTERN = Pattern.compile("0?[1-9]百[0-9]?[0-9]?");
        private static final Pattern W_PATTERN = Pattern.compile("(?<!(周|星期))0?[0-9]?十[0-9]?");
        private static final Pattern D_PATTERN = Pattern.compile("(?<=(周|星期))[末天日]");
        private static final Pattern N_PATTERN = Pattern.compile("[零一二两三四五六七八九]");
        private static final Pattern N1_PATTERN = Pattern.compile("[一二两三四五六七八九123456789]百[一二两三四五六七八九123456789](?!十)");
        private static final Pattern N2_PATTERN = Pattern.compile("[一二两三四五六七八九123456789]千[一二两三四五六七八九123456789](?!(百|十))");
        private static final Pattern A_PATTERN = Pattern.compile("[一二两三四五六七八九123456789]万[一二两三四五六七八九123456789](?!(千|百|十))");
        private static final Pattern NB_PATTERN = Pattern.compile("0?[1-9]千[0-9]?[0-9]?[0-9]?");
        private static final Pattern NBW_PATTERN = Pattern.compile("[0-9]+万[0-9]?[0-9]?[0-9]?[0-9]?");

        /**
         * 该方法删除一字符串中所有匹配某一规则字串
         * 可用于清理一个字符串中的空白符和语气助词
         *
         * @param target 待处理字符串
         * @param rules  删除规则
         * @return 清理工作完成后的字符串
         */
        public static String delKeyword(String target, String rules) {
            Pattern p = Pattern.compile(rules);
            Matcher m = p.matcher(target);
            StringBuffer sb = new StringBuffer();
            boolean result = m.find();
            while (result) {
                m.appendReplacement(sb, "");
                result = m.find();
            }
            m.appendTail(sb);
            String s = sb.toString();
            //System.out.println("字符串："+target+" 的处理后字符串为：" +sb);
            return s;
        }

        /**
         * 该方法可以将字符串中所有的用汉字表示的数字转化为用阿拉伯数字表示的数字
         * 如"这里有一千两百个人，六百零五个来自中国"可以转化为
         * "这里有1200个人，605个来自中国"
         * 此外添加支持了部分不规则表达方法
         * 如两万零六百五可转化为20650
         * 两百一十四和两百十四都可以转化为214
         * 一六零加一五八可以转化为160+158
         * 该方法目前支持的正确转化范围是0-99999999
         * 该功能模块具有良好的复用性
         *
         * @param target 待转化的字符串
         * @return 转化完毕后的字符串
         */
        public static String numberTranslator(String target) {
            Pattern p = A_PATTERN;
            Matcher m = p.matcher(target);
            StringBuffer sb = new StringBuffer();
            boolean result = m.find();
            while (result) {
                String group = m.group();
                String[] s = group.split("万");
                int num = 0;
                if (s.length == 2) {
                    num += wordToNumber(s[0]) * 10000 + wordToNumber(s[1]) * 1000;
                }
                m.appendReplacement(sb, Integer.toString(num));
                result = m.find();
            }
            m.appendTail(sb);
            target = sb.toString();

            p = N2_PATTERN;
            m = p.matcher(target);
            sb = new StringBuffer();
            result = m.find();
            while (result) {
                String group = m.group();
                String[] s = group.split("千");
                int num = 0;
                if (s.length == 2) {
                    num += wordToNumber(s[0]) * 1000 + wordToNumber(s[1]) * 100;
                }
                m.appendReplacement(sb, Integer.toString(num));
                result = m.find();
            }
            m.appendTail(sb);
            target = sb.toString();

            p = N1_PATTERN;
            m = p.matcher(target);
            sb = new StringBuffer();
            result = m.find();
            while (result) {
                String group = m.group();
                String[] s = group.split("百");
                int num = 0;
                if (s.length == 2) {
                    num += wordToNumber(s[0]) * 100 + wordToNumber(s[1]) * 10;
                }
                m.appendReplacement(sb, Integer.toString(num));
                result = m.find();
            }
            m.appendTail(sb);
            target = sb.toString();

            p = N_PATTERN;
            m = p.matcher(target);
            sb = new StringBuffer();
            result = m.find();
            while (result) {
                m.appendReplacement(sb, Integer.toString(wordToNumber(m.group())));
                result = m.find();
            }
            m.appendTail(sb);
            target = sb.toString();

            p = D_PATTERN;
            m = p.matcher(target);
            sb = new StringBuffer();
            result = m.find();
            while (result) {
                m.appendReplacement(sb, Integer.toString(wordToNumber(m.group())));
                result = m.find();
            }
            m.appendTail(sb);
            target = sb.toString();

            p = W_PATTERN;
            m = p.matcher(target);
            sb = new StringBuffer();
            result = m.find();
            while (result) {
                String group = m.group();
                String[] s = group.split("十");
                int num = 0;
                if (s.length == 0) {
                    num += 10;
                } else if (s.length == 1) {
                    int ten = Integer.parseInt(s[0]);
                    if (ten == 0) {
                        num += 10;
                    } else {
                        num += ten * 10;
                    }
                } else if (s.length == 2) {
                    if ("".equals(s[0])) {
                        num += 10;
                    } else {
                        int ten = Integer.parseInt(s[0]);
                        if (ten == 0) {
                            num += 10;
                        } else {
                            num += ten * 10;
                        }
                    }
                    num += Integer.parseInt(s[1]);
                }
                m.appendReplacement(sb, Integer.toString(num));
                result = m.find();
            }
            m.appendTail(sb);
            target = sb.toString();

            p = B_PATTERN;
            m = p.matcher(target);
            sb = new StringBuffer();
            result = m.find();
            while (result) {
                String group = m.group();
                String[] s = group.split("百");
                int num = 0;
                if (s.length == 1) {
                    int hundred = Integer.parseInt(s[0]);
                    num += hundred * 100;
                } else if (s.length == 2) {
                    int hundred = Integer.parseInt(s[0]);
                    num += hundred * 100;
                    num += Integer.parseInt(s[1]);
                }
                m.appendReplacement(sb, Integer.toString(num));
                result = m.find();
            }
            m.appendTail(sb);
            target = sb.toString();

            p = NB_PATTERN;
            m = p.matcher(target);
            sb = new StringBuffer();
            result = m.find();
            while (result) {
                String group = m.group();
                String[] s = group.split("千");
                int num = 0;
                if (s.length == 1) {
                    int thousand = Integer.parseInt(s[0]);
                    num += thousand * 1000;
                } else if (s.length == 2) {
                    int thousand = Integer.parseInt(s[0]);
                    num += thousand * 1000;
                    num += Integer.parseInt(s[1]);
                }
                m.appendReplacement(sb, Integer.toString(num));
                result = m.find();
            }
            m.appendTail(sb);
            target = sb.toString();

            p = NBW_PATTERN;
            m = p.matcher(target);
            sb = new StringBuffer();
            result = m.find();
            while (result) {
                String group = m.group();
                String[] s = group.split("万");
                int num = 0;
                if (s.length == 1) {
                    int tenthousand = Integer.parseInt(s[0]);
                    num += tenthousand * 10000;
                } else if (s.length == 2) {
                    int tenthousand = Integer.parseInt(s[0]);
                    num += tenthousand * 10000;
                    num += Integer.parseInt(s[1]);
                }
                m.appendReplacement(sb, Integer.toString(num));
                result = m.find();
            }
            m.appendTail(sb);
            target = sb.toString();

            return target;
        }

        /**
         * 方法numberTranslator的辅助方法，可将[零-九]正确翻译为[0-9]
         *
         * @param s 大写数字
         * @return 对应的整形数，如果不是大写数字返回-1
         */
        private static int wordToNumber(String s) {
            if ("零".equals(s) || "0".equals(s)) {
                return 0;
            } else if ("一".equals(s) || "1".equals(s)) {
                return 1;
            } else if ("二".equals(s) || "两".equals(s) || "2".equals(s)) {
                return 2;
            } else if ("三".equals(s) || "3".equals(s)) {
                return 3;
            } else if ("四".equals(s) || "4".equals(s)) {
                return 4;
            } else if ("五".equals(s) || "5".equals(s)) {
                return 5;
            } else if ("六".equals(s) || "6".equals(s)) {
                return 6;
            } else if ("七".equals(s) || "天".equals(s) || "日".equals(s) || "末".equals(s) || "7".equals(s)) {
                return 7;
            } else if ("八".equals(s) || "8".equals(s)) {
                return 8;
            } else if ("九".equals(s) || "9".equals(s)) {
                return 9;
            } else {
                return -1;
            }
        }
    }

    static class LunarParser {

        private static Pattern P = null;
        private static Pattern P1 = null;
        private static final Map<String, Integer[]> DAY = new ConcurrentHashMap<>();


        static {
            DAY.put("中秋节", new Integer[]{8, 15});
            DAY.put("端午节", new Integer[]{5, 5});
            DAY.put("清明节", new Integer[]{5, 5});
            DAY.put("春节", new Integer[]{1, 1});
            DAY.put("元宵节", new Integer[]{1, 15});
            DAY.put("七夕节", new Integer[]{7, 7});
            DAY.put("重阳节", new Integer[]{9, 9});
            P = Pattern.compile(Arrays.stream(LunarTime.JIE_QI_IN_USE).map(it -> "(" + it + ")").collect(Collectors.joining("|")) + "|春节");
            P1 = Pattern.compile(DAY.keySet().stream().map(it -> {
                if ("春节".equals(it)) {
                    return "(" + it + ")";
                }
                return "(" + it + ")|(" + it.replace("节", "") + ")";
            }).collect(Collectors.joining("|")));
        }

        /**
         * 解析弄里
         *
         * @param tUnit          时间
         * @param timeExpression 解析式
         */
        public void parse(int[] tUnit, String timeExpression) {
            int year = tUnit[0], month = tUnit[1], day = tUnit[2];
            year = year == -1 ? Calendar.getInstance().get(Calendar.YEAR) : year;
            month = month == -1 ? 1 : month;
            day = day == -1 ? 1 : day;
            String cj = "春节";
            LunarTime lunarTime = DateTime.of(year, month, day).toLunarTime();
            Matcher match = P.matcher(timeExpression);
            if (match.find()) {
                String group = match.group();
                if (cj.equals(group)) {
                    lunarTime = new LunarTime(year, 1, 1);
                    SolarTime solar = lunarTime.getSolar();
                    tUnit[0] = solar.getYear();
                    tUnit[1] = solar.getMonth();
                    tUnit[2] = solar.getDay();
                } else {
                    Map<String, SolarTime> jieQiMapping = lunarTime.getJieQiMapping();
                    SolarTime solar = jieQiMapping.get(group);
                    tUnit[0] = solar.getYear();
                    tUnit[1] = solar.getMonth();
                    tUnit[2] = solar.getDay();
                }
            } else {
                Matcher match1 = P1.matcher(timeExpression);
                if (match1.find()) {
                    String group = match1.group();
                    SolarTime solarTime = null;
                    String dz = "冬至节", cx = "除夕";
                    if (dz.contains(group) || cx.contains(group)) {
                        solarTime = new LunarTime(year, 12, 31).getSolar();
                    } else {
                        for (Map.Entry<String, Integer[]> entry : DAY.entrySet()) {
                            if (entry.getKey().contains(group)) {
                                solarTime = new LunarTime(year, entry.getValue()[0], entry.getValue()[1]).getSolar();
                                break;
                            }
                        }
                    }
                    if (null != solarTime) {
                        tUnit[0] = solarTime.getYear();
                        tUnit[1] = solarTime.getMonth();
                        tUnit[2] = solarTime.getDay();
                    }


                }

            }
        }
    }

    static class HolidayParser {
        public static final Map<String, String> MODULE = new ConcurrentHashMap<>();

        static {
            MODULE.put("元旦", "1,1");
            MODULE.put("麻风", "1,24");
            MODULE.put("海关", "1,26");
            MODULE.put("湿地", "2,2");
            MODULE.put("气象", "2,10");
            MODULE.put("情人", "2,14");
            MODULE.put("母语", "2,29");
            MODULE.put("海豹", "3,1");
            MODULE.put("民防", "3,1");
            MODULE.put("爱耳", "3,1");
            MODULE.put("志愿者", "3,5");
            MODULE.put("志愿者服务", "3,5");
            MODULE.put("雷锋", "3,5");
            MODULE.put("雷锋纪念", "3,5");
            MODULE.put("女生", "3,7");
            MODULE.put("妇女", "3,8");
            MODULE.put("植树", "3,12");
            MODULE.put("母亲河", "3,9");
            MODULE.put("白色", "3,14");
            MODULE.put("权益", "3,15");
            MODULE.put("航海", "3,17");
            MODULE.put("社会工作", "3,17");
            MODULE.put("愚人", "4,1");
            MODULE.put("儿童读书", "4,2");
            MODULE.put("自闭症", "4,2");
            MODULE.put("卫生", "4,7");
            MODULE.put("复活", "4,12");
            MODULE.put("世界地球", "4,22");
            MODULE.put("世界读书", "4,23");
            MODULE.put("海军建军", "4,23");
            MODULE.put("航天", "4,24");
            MODULE.put("知识产权", "4,26");
            MODULE.put("劳动", "5,1");
            MODULE.put("青年", "5,4");
            MODULE.put("哮喘", "5,5");
            MODULE.put("国际护士", "5,12");
            MODULE.put("儿童", "6,1");
            MODULE.put("环境", "6,5");
            MODULE.put("世界海洋", "6,8");
            MODULE.put("建党", "7,1");
            MODULE.put("建军", "8,1");
            MODULE.put("教师", "9,10");
            MODULE.put("国际爱牙", "9,20");
            MODULE.put("国际和平", "9,21");
            MODULE.put("国庆", "10,1");
            MODULE.put("万圣", "11,1");
            MODULE.put("世界儿童", "11,20");
            MODULE.put("感恩", "11,26");
            MODULE.put("世界艾滋病", "12,1");
            MODULE.put("全国交通安全", "12,2");
            MODULE.put("国际残疾人", "12,3");
            MODULE.put("澳门回归", "12,20");
            MODULE.put("平安夜", "12,24");
            MODULE.put("圣诞", "12,25");
        }

        /**
         * 月份
         *
         * @param timeExpression 待解析时间
         * @return 月
         */
        public String parseMonth(String timeExpression) {
            String parse = parse(timeExpression);
            if (!StringUtils.isBlank(parse)) {
                return parse.split(",")[0];
            }
            return null;
        }

        /**
         * 天
         *
         * @param timeExpression 待解析时间
         * @return 月
         */
        public String parseDay(String timeExpression) {
            String parse = parse(timeExpression);
            if (!StringUtils.isBlank(parse)) {
                return parse.split(",")[1];
            }

            return null;
        }

        /**
         * 天
         *
         * @param timeExpression 待解析时间
         * @return 月
         */
        public String parse(String timeExpression) {
            List<String> collect = MODULE.keySet().stream().map(it -> "(" + it + ")").collect(Collectors.toList());
            String join = Joiner.on("|").join(collect);
            String rule = "(" + join + ")([(?=节)|(?=日)]*)";
            Pattern pattern = Pattern.compile(rule);
            Matcher match = pattern.matcher(timeExpression);
            if (match.find()) {
                return MODULE.get(match.group());
            }

            return null;
        }
    }
}
