package main.java.redis;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import redis.clients.jedis.*;

import java.io.File;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *1.存储最新日志
 * list  key "rencent:info" value  "timestamp+message"
 * lpush 一端写入 、ltrim 维持列表固定大小
 *
 *2.最常见日志
 * zset key "common:info" member "message"  score 1 频次每次加1 自动按分值频次排序
 *rename方法 每隔一小时对日志进行一次归档，这样zset存储的即一小时内最常见日志频次
 *
 *3.计数器实现
 * hash 每一个hash来存储一个固定精度计数器
 * hash key "5:hits"  field "精度时间片5" value 该时间片内点击次数
 *
 *zset key "knows" member "5:hits" score 0
 * zset 存储正在使用的计数器，方便对计数器进行清理工作
 * 计算时间片方法  单位秒（now / 精度  ）* 精度 ，如1 、2、3、4 、5、6 ，精度为
 *
 *  hash 中取出来field为cutoff以前的时间片，可以先取出来所有key ，转为有序list，再二分查找到切分点，即可找到 再批量删除field
 *
 * 控制一段代码执行时间如60s,不足则休眠 thread.sleep(Math.max(程序执行时间，60))
 *
 *4.
 *
 * 5.服务的发现与配置 策略提醒：一般本地都会再配置一个本地缓存
 * 实现判断网站是否在维护开关配置，将维护标识存储在redis
 * key "is-under-maintenance" value "true" 存储一个键表示是否在维护键值对即可
 *
 * 实现存储一个应用或组件的连接配置信息 key 为组件标识 value 为配置信息hashmap存储的json串
 * key "config:redis:statics" value hashmap json string
 *
 *
 *
 */
@Slf4j
public class Chapter05 {
    public static final String DEBUG = "debug";
    public static final String INFO = "info";
    public static final String WARNING = "warning";
    public static final String ERROR = "error";
    public static final String CRITICAL = "critical";

    public static final Collator COLLATOR = Collator.getInstance();

    public static final SimpleDateFormat TIMESTAMP =
            new SimpleDateFormat("EEE MMM dd HH:00:00 yyyy");
    private static final SimpleDateFormat ISO_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:00:00");

    static {
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static final void main(String[] args)
            throws InterruptedException {
        new Chapter05().run();

    }

    public void run()
            throws InterruptedException {
        Jedis conn = new Jedis("localhost");
        conn.select(15);
//        testLogRecent(conn);
//        testLogCommon(conn);
//        testCounters(conn);
//        testStats(conn);
//        testAccessTime(conn);
//        testIpLookup(conn);
//        testIsUnderMaintenance(conn);
        testConfig(conn);
    }

    /**
     * 测试 最新日志存储器
     * @param conn
     */
    public void testLogRecent(Jedis conn) {

        log.info("\n----- testLogRecent -----");
        log.info("Let's write a few logs to the recent log");
        for (int i = 0; i < 5; i++) {
            logRecent(conn, "test", "this is message " + i);
        }
        List<String> recent = conn.lrange("recent:test:info", 0, -1);
        log.info(
                "The current recent message log has this many messages: " +
                        recent.size());
        assert recent.size() >= 5;
    }

    /**
     * 测试最常见日志 存储器
     * @param conn
     */
    public void testLogCommon(Jedis conn) {
        log.info("\n----- testLogCommon -----");
        log.info("Let's write some items to the common log");
        for (int count = 1; count < 6; count++) {
            for (int i = 0; i < count; i++) {
                logCommon(conn, "test", "message-" + count);
            }
        }
        Set<Tuple> common = conn.zrevrangeWithScores("common:test:info", 0, -1);
        log.info("The current number of common messages is: " + common.size());
        log.info("Those common messages are:");
        for (Tuple tuple : common) {
            log.info("  " + tuple.getElement() + ", " + tuple.getScore());
        }
        assert common.size() >= 5;
    }

    public void testCounters(Jedis conn)
            throws InterruptedException {
        log.info("\n----- testCounters -----");
        log.info("Let's update some counters for now and a little in the future");
        long now = System.currentTimeMillis() / 1000;
        for (int i = 0; i < 10; i++) {
            int count = (int) (Math.random() * 5) + 1;
            log.info("点击次数随机生成count{}",count);
            updateCounter(conn, "hits", count, now + i);
        }

        List<Pair<Integer, Integer>> counter = getCounter(conn, "hits", 1);
        log.info("We have some per-second counters: " + counter.size());
        log.info("These counters include:");
        for (Pair<Integer, Integer> count : counter) {
            log.info("  " + count);
        }
        assert counter.size() >= 10;

        counter = getCounter(conn, "hits", 5);
        log.info("We have some per-5-second counters: " + counter.size());
        log.info("These counters include:");
        for (Pair<Integer, Integer> count : counter) {
            log.info("  " + count);
        }
        assert counter.size() >= 2;

        log.info("Let's clean out some counters by setting our sample count to 0");
        CleanCountersThread thread = new CleanCountersThread(0, 2 * 86400000);
        thread.start();
        Thread.sleep(1000);
        thread.quit();
        thread.interrupt();
        counter = getCounter(conn, "test", 86400);
        log.info("Did we clean out all of the counters? " + (counter.size() == 0));
        assert counter.size() == 0;
    }

    public void testStats(Jedis conn) {
        log.info("\n----- testStats -----");
        log.info("Let's add some data for our statistics!");
        List<Object> r = null;
        for (int i = 0; i < 5; i++) {
            double value = (Math.random() * 11) + 5;
            r = updateStats(conn, "temp", "example", value);
        }
        log.info("We have some aggregate statistics: " + r);
        Map<String, Double> stats = getStats(conn, "temp", "example");
        log.info("Which we can also fetch manually:");
        log.info(JSON.toJSONString(stats));
        assert stats.get("count") >= 5;
    }

    public void testAccessTime(Jedis conn)
            throws InterruptedException {
        log.info("\n----- testAccessTime -----");
        log.info("Let's calculate some access times...");
        AccessTimer timer = new AccessTimer(conn);
        for (int i = 0; i < 10; i++) {
            timer.start();
            Thread.sleep((int) ((.5 + Math.random()) * 1000));
            timer.stop("req-" + i);
        }
        log.info("The slowest access times are:");
        Set<Tuple> atimes = conn.zrevrangeWithScores("slowest:AccessTime", 0, -1);
        for (Tuple tuple : atimes) {
            log.info("  " + tuple.getElement() + ", " + tuple.getScore());
        }
        assert atimes.size() >= 10;
    }

    public void testIpLookup(Jedis conn) {
        log.info("\n----- testIpLookup -----");
        String cwd = System.getProperty("user.dir");
        File blocks = new File(cwd + "/GeoLiteCity-Blocks.csv");
        File locations = new File(cwd + "/GeoLiteCity-Location.csv");
        if (!blocks.exists()) {
            log.info("********");
            log.info("GeoLiteCity-Blocks.csv not found at: " + blocks);
            log.info("********");
            return;
        }
        if (!locations.exists()) {
            log.info("********");
            log.info("GeoLiteCity-Location.csv not found at: " + locations);
            log.info("********");
            return;
        }

        log.info("Importing IP addresses to Redis... (this may take a while)");
//        importIpsToRedis(conn, blocks);
        long ranges = conn.zcard("ip2cityid:");
        log.info("Loaded ranges into Redis: " + ranges);
        assert ranges > 1000;

        log.info("Importing Location lookups to Redis... (this may take a while)");
//        importCitiesToRedis(conn, locations);
        long cities = conn.hlen("cityid2city:");
        log.info("Loaded city lookups into Redis:" + cities);
        assert cities > 1000;

        log.info("Let's lookup some locations!");
        for (int i = 0; i < 5; i++) {
            String ip =
                    randomOctet(255) + '.' +
                            randomOctet(256) + '.' +
                            randomOctet(256) + '.' +
                            randomOctet(256);
            log.info(Arrays.toString(findCityByIp(conn, ip)));
        }
    }

    public void testIsUnderMaintenance(Jedis conn)
            throws InterruptedException {
        log.info("\n----- testIsUnderMaintenance -----");
        log.info("Are we under maintenance (we shouldn't be)? " + isUnderMaintenance(conn));
        conn.set("is-under-maintenance", "yes");
        log.info("We cached this, so it should be the same: " + isUnderMaintenance(conn));
        Thread.sleep(1000);
        log.info("But after a sleep, it should change: " + isUnderMaintenance(conn));
        log.info("Cleaning up...");
        conn.del("is-under-maintenance");
        Thread.sleep(1000);
        log.info("Should be False again: " + isUnderMaintenance(conn));
    }

    public void testConfig(Jedis conn) {
        log.info("\n----- testConfig -----");
        log.info("Let's set a config and then get a connection from that config...");
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("db.username", "lilongsheng");
        config.put("db.password", 123456);
        setConfig(conn, "redis", "test", config);

        Jedis conn2 = redisConnection("test");
        log.info(
                "We can run commands from the configured connection: " + (conn2.info() != null));
    }

    public void logRecent(Jedis conn, String name, String message) {
        logRecent(conn, name, message, INFO);
    }

    /**
     * 存储最新日志列表
     * lpush 从队列一段插入来保持有序性
     * ltrim 来保证队列大小限制
     *
     *
     * @param conn
     * @param name
     * @param message
     * @param severity
     */
    public void logRecent(Jedis conn, String name, String message, String severity) {
        String destination = "recent:" + name + ':' + severity;
        Pipeline pipe = conn.pipelined();
        log.info("logRecent key:{},value:{}",destination,TIMESTAMP.format(new Date())+' ' + message);
        pipe.lpush(destination, TIMESTAMP.format(new Date()) + ' ' + message);
        pipe.ltrim(destination, 0, 99);
        pipe.sync();
    }

    public void logCommon(Jedis conn, String name, String message) {
        logCommon(conn, name, message, INFO, 5000);
    }

    /**
     * 常见日志列表 （一小时内 实时统计）
     * zset 来存储常见日志列表 分值为记录频率
     * rename 可以用来对集合进行归档
     *
     * @param conn
     * @param name
     * @param message
     * @param severity
     * @param timeout
     */
    public void logCommon(Jedis conn, String name, String message, String severity, int timeout) {
        // 存储近期的常见日志消息有序集合的key
        String commonDest = "common:" + name + ':' + severity;
        // 用来存储轮询周期时间的key 当前所处的小时数
        String startKey = commonDest + ":start";
        long end = System.currentTimeMillis() + timeout;
        // 设置超时时间为5s
        while (System.currentTimeMillis() < end) {
            // 监控记录当前小时数的key
            conn.watch(startKey);
            // 获取当前所处的小时数
            String hourStart = ISO_FORMAT.format(new Date());
            log.info("logCommon hourStart:{}",hourStart);
            String existing = conn.get(startKey);
            Transaction trans = conn.multi();
            // 如果常见日志列表里面存储的是上一小时的日志，将其归档处理
            if (existing != null && COLLATOR.compare(existing, hourStart) < 0) {
                // rename 命令会将commonDest键重命名为commonDest + ":last"键，里面的members也会归于新键
                // commonDest键将删除
                trans.rename(commonDest, commonDest + ":last");
                trans.rename(startKey, commonDest + ":pstart");
                // 重新设置常见日志 当前所属的小时数
                trans.set(startKey, hourStart);
            }
            log.info("logCommon key:{},member:{},value:{}",commonDest,message,1);
            // 收到的消息写入常见日志有序集合
            trans.zincrby(commonDest, 1, message);
            // 存储最新日志 并保留最新100条记录
            String recentDest = "recent:" + name + ':' + severity;
            trans.lpush(recentDest, TIMESTAMP.format(new Date()) + ' ' + message);
            trans.ltrim(recentDest, 0, 99);
            List<Object> results = trans.exec();
            // null response indicates that the transaction was aborted due to
            // the watched key changing.
            if (results == null) {
                continue;
            }
            return;
        }
    }

    public void updateCounter(Jedis conn, String name, int count) {
        updateCounter(conn, name, count, System.currentTimeMillis() / 1000);
    }

    /*
    计数器的时间精度 以秒为单位
     */
    public static final int[] PRECISION = new int[]{1, 5, 60, 300, 3600, 18000, 86400};

    /**
     * 更新计数器方法
     * ps:为了方便清理计数器包含的旧数据，所以用了zset来存储维护使用中的计数器
     * @param conn
     * @param name  计数器名字
     * @param count 访问次数
     * @param now   当前时间
     */
    public void updateCounter(Jedis conn, String name, int count, long now) {
        Transaction trans = conn.multi();
        // 为每一种精度都创建一个计数器
        for (int prec : PRECISION) {
            // 取的当前时间片的开始时间 即当前精度的开始时间(代表这个时间段)
            // 当前时间除以精度则当前时间变为以精度单位的一段一段时间
            // 如1、2、3、4、5、6等除以2精度，则为0、2、4、6，相当于结尾把除以精度得到的余数舍去
            long pnow = (now / prec) * prec;
            // 计数器的名字 如5:hits、60:hits等
            String hash = String.valueOf(prec) + ':' + name;
            // 维护计数器有序数列
            trans.zadd("known:", 0, hash);
            // 用户散列存储时间片的信息 更新计数器的访问次数
            trans.hincrBy("count:" + hash, String.valueOf(pnow), count);
        }
        trans.exec();
    }

    /**
     * 获取计数器数据
     * @param conn
     * @param name
     * @param precision
     * @return
     */
    public List<Pair<Integer, Integer>> getCounter(
            Jedis conn, String name, int precision) {
        // 计数器对应的键名
        String hash = String.valueOf(precision) + ':' + name;
        // 取出来计数器数据
        Map<String, String> data = conn.hgetAll("count:" + hash);
        // 存储计数器数据  键值对
        ArrayList<Pair<Integer, Integer>> results =
                new ArrayList<Pair<Integer, Integer>>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            results.add(new Pair<Integer, Integer>(
                    Integer.parseInt(entry.getKey()),
                    Integer.parseInt(entry.getValue())));
        }
        // 排序 按时间
//        Collections.sort(results);
        return results;
    }

    public List<Object> updateStats(Jedis conn, String context, String type, double value) {
        int timeout = 5000;
        String destination = "stats:" + context + ':' + type;
        String startKey = destination + ":start";
        long end = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < end) {
            conn.watch(startKey);
            String hourStart = ISO_FORMAT.format(new Date());

            String existing = conn.get(startKey);
            Transaction trans = conn.multi();
            if (existing != null && COLLATOR.compare(existing, hourStart) < 0) {
                trans.rename(destination, destination + ":last");
                trans.rename(startKey, destination + ":pstart");
                trans.set(startKey, hourStart);
            }

            String tkey1 = UUID.randomUUID().toString();
            String tkey2 = UUID.randomUUID().toString();
            trans.zadd(tkey1, value, "min");
            trans.zadd(tkey2, value, "max");

            trans.zunionstore(
                    destination,
                    new ZParams().aggregate(ZParams.Aggregate.MIN),
                    destination, tkey1);
            trans.zunionstore(
                    destination,
                    new ZParams().aggregate(ZParams.Aggregate.MAX),
                    destination, tkey2);

            trans.del(tkey1, tkey2);
            trans.zincrby(destination, 1, "count");
            trans.zincrby(destination, value, "sum");
            trans.zincrby(destination, value * value, "sumsq");

            List<Object> results = trans.exec();
            if (results == null) {
                continue;
            }
            return results.subList(results.size() - 3, results.size());
        }
        return null;
    }

    public Map<String, Double> getStats(Jedis conn, String context, String type) {
        String key = "stats:" + context + ':' + type;
        Map<String, Double> stats = new HashMap<String, Double>();
        Set<Tuple> data = conn.zrangeWithScores(key, 0, -1);
        for (Tuple tuple : data) {
            stats.put(tuple.getElement(), tuple.getScore());
        }
        stats.put("average", stats.get("sum") / stats.get("count"));
        double numerator = stats.get("sumsq") - Math.pow(stats.get("sum"), 2) / stats.get("count");
        double count = stats.get("count");
        stats.put("stddev", Math.pow(numerator / (count > 1 ? count - 1 : 1), .5));
        return stats;
    }

    private long lastChecked;
    private boolean underMaintenance;

    /**
     * 验证服务器是否维护
     * @param conn
     * @return
     */
    public boolean isUnderMaintenance(Jedis conn) {
        if (lastChecked < System.currentTimeMillis() - 1000) {
            lastChecked = System.currentTimeMillis();
            String flag = conn.get("is-under-maintenance");
            underMaintenance = "yes".equals(flag);
        }

        return underMaintenance;
    }

    public void setConfig(
            Jedis conn, String type, String component, Map<String, Object> config) {
        conn.set("config:" + type + ':' + component, JSON.toJSONString(config));
    }

    /*
    redis 存储的配置信息 本地缓存
     */
    private static final Map<String, Map<String, Object>> CONFIGS =
            new HashMap<String, Map<String, Object>>();
    /*
     记录每种配置信息 更新的最新时间
     */
    private static final Map<String, Long> CHECKED = new HashMap<String, Long>();

    /**
     * 获取redis 存储的配置信息并更新本地缓存 1s缓存
     * @param conn
     * @param type
     * @param component
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getConfig(Jedis conn, String type, String component) {
        // 缓存1秒
        int wait = 1000;
        // 存储配置的key
        String key = "config:" + type + ':' + component;

        Long lastChecked = CHECKED.get(key);
        // 本地已过期或者第一次执行
        if (lastChecked == null || lastChecked < System.currentTimeMillis() - wait) {
            CHECKED.put(key, System.currentTimeMillis());

            String value = conn.get(key);
            Map<String, Object> config = null;
            if (value != null) {
                config = (Map<String, Object>) JSON.toJavaObject(JSON.parseObject(value), new HashMap<String, Object>().getClass());
            } else {
                config = new HashMap<String, Object>();
            }
            // 更新本地配置信息
            CONFIGS.put(key, config);
        }

        return CONFIGS.get(key);
    }

    /*
    redis 连接本地缓存
     */
    public static final Map<String, Jedis> REDIS_CONNECTIONS =
            new HashMap<String, Jedis>();

    public Jedis redisConnection(String component) {
        Jedis configConn = REDIS_CONNECTIONS.get("config");
        if (configConn == null) {
            configConn = new Jedis("localhost");
            configConn.select(15);
            REDIS_CONNECTIONS.put("config", configConn);
        }

        String key = "config:redis:" + component;
        // 本地缓存获取
        Map<String, Object> oldConfig = CONFIGS.get(key);
        // redis获取配置信息
        Map<String, Object> config = getConfig(configConn, "redis", component);
        // 如果本地不等于redis配置信息
        if (!config.equals(oldConfig)) {
            Jedis conn = new Jedis("localhost");
            if (config.containsKey("db")) {
                conn.select(((Double) config.get("db")).intValue());
            }
            REDIS_CONNECTIONS.put(key, conn);
        }

        return REDIS_CONNECTIONS.get(key);
    }

//    public void importIpsToRedis(Jedis conn, File file) {
//        FileReader reader = null;
//        try{
//            String[] headers = new String[]{};
//            CSVFormat formator = CSVFormat.DEFAULT.withHeader(headers);
//            reader = new FileReader(file);
//            CSVParser parser = new CSVParser(reader,formator);
//            int count = 0;
//            List<String> lines = null;
//            while ((lines = parser.getRecords()) != null){
//                String startIp = lines.size() > 1 ? lines[0] : "";
//                if (startIp.toLowerCase().indexOf('i') != -1){
//                    continue;
//                }
//                int score = 0;
//                if (startIp.indexOf('.') != -1){
//                    score = ipToScore(startIp);
//                }else{
//                    try{
//                        score = Integer.parseInt(startIp, 10);
//                    }catch(NumberFormatException nfe){
//                        continue;
//                    }
//                }
//
//                String cityId = line[2] + '_' + count;
//                conn.zadd("ip2cityid:", score, cityId);
//                count++;
//            }
//        }catch(Exception e){
//            throw new RuntimeException(e);
//        }finally{
//            try{
//                reader.close();
//            }catch(Exception e){
//                // ignore
//            }
//        }
//    }

//    public void importCitiesToRedis(Jedis conn, File file) {
//        Gson gson = new Gson();
//        FileReader reader = null;
//        try{
//            reader = new FileReader(file);
//            CSVParser parser = new CSVParser(reader);
//            String[] line = null;
//            while ((line = parser.getLine()) != null){
//                if (line.length < 4 || !Character.isDigit(line[0].charAt(0))){
//                    continue;
//                }
//                String cityId = line[0];
//                String country = line[1];
//                String region = line[2];
//                String city = line[3];
//                String json = gson.toJson(new String[]{city, region, country});
//                conn.hset("cityid2city:", cityId, json);
//            }
//        }catch(Exception e){
//            throw new RuntimeException(e);
//        }finally{
//            try{
//                reader.close();
//            }catch(Exception e){
//                // ignore
//            }
//        }
//    }

    public int ipToScore(String ipAddress) {
        int score = 0;
        for (String v : ipAddress.split("\\.")) {
            score = score * 256 + Integer.parseInt(v, 10);
        }
        return score;
    }

    public String randomOctet(int max) {
        return String.valueOf((int) (Math.random() * max));
    }

    public String[] findCityByIp(Jedis conn, String ipAddress) {
        int score = ipToScore(ipAddress);
        Set<String> results = conn.zrevrangeByScore("ip2cityid:", score, 0, 0, 1);
        if (results.size() == 0) {
            return null;
        }

        String cityId = results.iterator().next();
        cityId = cityId.substring(0, cityId.indexOf('_'));
        return JSON.parseObject(conn.hget("cityid2city:", cityId), String[].class);
    }

    /**
     * 清楚计数器线程
     */
    public class CleanCountersThread
            extends Thread {
        private Jedis conn;
        private int sampleCount = 100;
        private boolean quit;
        private long timeOffset; // used to mimic a time in the future.

        public CleanCountersThread(int sampleCount, long timeOffset) {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
            this.sampleCount = sampleCount;
            this.timeOffset = timeOffset;
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            // 记录清理操作执行的次数
            int passes = 0;
            // 持续的对计数器清理
            while (!quit) {
                // 记录清理操作执行的时间
                long start = System.currentTimeMillis() + timeOffset;
                // 通过比较索引来遍历所有计数器
                int index = 0;
                while (index < conn.zcard("known:")) {
                    Set<String> hashSet = conn.zrange("known:", index, index);
                    index++;
                    if (hashSet.size() == 0) {
                        break;
                    }
                    // 只有一个元素 取出来即计数器key
                    String hash = hashSet.iterator().next();
                    // 取出来前面的精度
                    int prec = Integer.parseInt(hash.substring(0, hash.indexOf(':')));
                    // 程序每60s 更新一次，根据计数器的精度来判断是否有必要清理
                    int bprec = (int) Math.floor(prec / 60);
                    if (bprec == 0) {
                        bprec = 1;
                    }
                    if ((passes % bprec) != 0) {
                        continue;
                    }

                    String hkey = "count:" + hash;
                    // 通过计数器精度和样本数量 计算出我们要保留什么时间之前的样本
                    String cutoff = String.valueOf(
                            ((System.currentTimeMillis() + timeOffset) / 1000) - sampleCount * prec);
                    ArrayList<String> samples = new ArrayList<String>(conn.hkeys(hkey));
                    // 使用二分查找因此先排序
                    Collections.sort(samples);
                    int remove = bisectRight(samples, cutoff);
                    // remove 找到即需要删除样本
                    if (remove != 0) {
                        // 批量删除过期样本
                        conn.hdel(hkey, samples.subList(0, remove).toArray(new String[0]));
                        // 如果相当说明此时样本数和切点cutoff处一样，不需要清理
                        if (remove == samples.size()) {
                            conn.watch(hkey);
                            // 计数器长度如果为空，删除有序数据中数据
                            if (conn.hlen(hkey) == 0) {
                                Transaction trans = conn.multi();
                                trans.zrem("known:", hash);
                                trans.exec();
                                index--;
                            } else {
                                conn.unwatch();
                            }
                        }
                    }
                }

                passes++;
                // 控制程序60s执行一次，如果本次不够60s则休眠到60s
                long duration = Math.min(
                        (System.currentTimeMillis() + timeOffset) - start + 1000, 60000);
                try {
                    sleep(Math.max(60000 - duration, 1000));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        /**
         * 二分查找列表中某个值
         * @param values
         * @param key
         * @return
         */
        public int bisectRight(List<String> values, String key) {
            int index = Collections.binarySearch(values, key);
            return index < 0 ? Math.abs(index) - 1 : index + 1;
        }
    }

    public class AccessTimer {
        private Jedis conn;
        private long start;

        public AccessTimer(Jedis conn) {
            this.conn = conn;
        }

        public void start() {
            start = System.currentTimeMillis();
        }

        public void stop(String context) {
            long delta = System.currentTimeMillis() - start;
            List<Object> stats = updateStats(conn, context, "AccessTime", delta / 1000.0);
            double average = (Double) stats.get(1) / (Double) stats.get(0);

            Transaction trans = conn.multi();
            trans.zadd("slowest:AccessTime", average, context);
            trans.zremrangeByRank("slowest:AccessTime", 0, -101);
            trans.exec();
        }
    }
}
