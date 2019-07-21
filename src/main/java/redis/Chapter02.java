package redis;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 1. 登陆session 实例
 * zset 默认从小到大排序 index 0 变大 对应分值从小变大
 *
 * 存储登录token与用户ID映射关系
 * hash key "login:" field token value user_id
 *
 * zcard 用户有序集合中元素的数量
 *
 * 存储最近登陆的用户令牌
 * zset key "recent" member token score timestamp
 *
 *
 * 存储用户最近浏览过的商品(每个用户对应一个集合)
 * zset 存储 key "viewed:token" member item score timestamp,每个用户对应一个有序集合 ，对应浏览过的商品
 *
 *
 *
 * 2.实现购物车
 * 每个用户一个购物车 保存商品以及商品数量
 * hash key "cart:token" field item value count(item)
 *
 *
 * 3.网页缓存 (减少前端负载)
 *
 *
 * 4.数据行缓存 (减少数据压力)
 */
@Slf4j
public class Chapter02 {
    public static final void main(String[] args)
        throws InterruptedException
    {
        new Chapter02().run();
    }

    public void run()
        throws InterruptedException
    {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        // session登陆
//        testLoginCookies(conn);
        //  购物车
//        testShopppingCartCookies(conn);
//        testCacheRows(conn);
//        testCacheRequest(conn);
    }

    public void testLoginCookies(Jedis conn)
        throws InterruptedException
    {
        log.info("\n----- testLoginCookies -----");
        String token = "";
        for (int i= 1 ; i < 40 ;i++){
            token = UUID.randomUUID().toString();
            updateToken(conn, token, "lilongsheng_"+i, "itemX"+i);
        }
        Map<String,String> tokensMap = conn.hgetAll("login:");
        log.info("We print current login users: " + JSON.toJSONString(tokensMap));
        log.info("We just logged-in/updated token: " + token);

        log.info("What username do we get when we look-up that token?");
        String r = checkToken(conn, token);
        log.info("user_id:{}",r);
        assert r != null;

        log.info("Let's drop the maximum number of cookies to 0 to clean them out");
        log.info("We will start a thread to do the cleaning, while we stop it later");

        CleanSessionsThread thread = new CleanSessionsThread(0);
        thread.start();
        Thread.sleep(1000);
        thread.quit();
        Thread.sleep(2000);
        if (thread.isAlive()){
            throw new RuntimeException("The clean sessions thread is still alive?!?");
        }

        long s = conn.hlen("login:");
        log.info("The current number of sessions still available is: " + s);
        assert s == 0;
    }

    public void testShopppingCartCookies(Jedis conn)
        throws InterruptedException
    {
        log.info("\n----- testShopppingCartCookies -----");
        String token = UUID.randomUUID().toString();

        log.info("We'll refresh our session...");
        updateToken(conn, token, "username", "itemX");
        log.info("And add an item to the shopping cart");
        addToCart(conn, token, "itemY", 3);
        Map<String,String> r = conn.hgetAll("cart:" + token);
        log.info("Our shopping cart currently has:");
        for (Map.Entry<String,String> entry : r.entrySet()){
            log.info("  " + entry.getKey() + ": " + entry.getValue());
        }

        assert r.size() >= 1;

        log.info("Let's clean out our sessions and carts");
        CleanFullSessionsThread thread = new CleanFullSessionsThread(0);
        thread.start();
        Thread.sleep(1000);
        thread.quit();
        Thread.sleep(2000);
        if (thread.isAlive()){
            throw new RuntimeException("The clean sessions thread is still alive?!?");
        }

        r = conn.hgetAll("cart:" + token);
        log.info("Our shopping cart now contains:");
        for (Map.Entry<String,String> entry : r.entrySet()){
            log.info("  " + entry.getKey() + ": " + entry.getValue());
        }
        assert r.size() == 0;
    }

    public void testCacheRows(Jedis conn)
        throws InterruptedException
    {
        log.info("\n----- testCacheRows -----");
        log.info("First, let's schedule caching of itemX every 5 seconds");
        scheduleRowCache(conn, "itemX", 5);
        log.info("Our schedule looks like:");
        Set<Tuple> s = conn.zrangeWithScores("schedule:", 0, -1);
        for (Tuple tuple : s){
            log.info("  " + tuple.getElement() + ", " + tuple.getScore());
        }
        assert s.size() != 0;

        log.info("We'll start a caching thread that will cache the data...");

        CacheRowsThread thread = new CacheRowsThread();
        thread.start();

        Thread.sleep(1000);
        log.info("Our cached data looks like:");
        String r = conn.get("inv:itemX");
        log.info(r);
        assert r != null;

        log.info("We'll check again in 5 seconds...");
        Thread.sleep(5000);
        log.info("Notice that the data has changed...");
        String r2 = conn.get("inv:itemX");
        log.info(r2);
        assert r2 != null;
        assert !r.equals(r2);

        log.info("Let's force un-caching");
        scheduleRowCache(conn, "itemX", -1);
        Thread.sleep(1000);
        r = conn.get("inv:itemX");
        log.info("The cache was cleared? " + (r == null));
        assert r == null;

        thread.quit();
        Thread.sleep(2000);
        if (thread.isAlive()){
            throw new RuntimeException("The database caching thread is still alive?!?");
        }
    }

    public void testCacheRequest(Jedis conn) {
        log.info("\n----- testCacheRequest -----");
        String token = UUID.randomUUID().toString();

        Callback callback = new Callback(){
            public String call(String request){
                return "content for " + request;
            }
        };

        updateToken(conn, token, "username", "itemX");
        String url = "http://test.com/?item=itemX";
        log.info("We are going to cache a simple request against " + url);
        String result = cacheRequest(conn, url, callback);
        log.info("We got initial content:\n" + result);

        assert result != null;

        log.info("To test that we've cached the request, we'll pass a bad callback");
        String result2 = cacheRequest(conn, url, null);
        log.info("We ended up getting the same response!\n" + result2);

        assert result.equals(result2);

        assert !canCache(conn, "http://test.com/");
        assert !canCache(conn, "http://test.com/?item=itemX&_=1234536");
    }

    /**
     * 通过令牌获取对应的用户
     * @param conn
     * @param token
     * @return
     */
    public String checkToken(Jedis conn, String token) {
        return conn.hget("login:", token);
    }

    /**
     * 用户购物车数据 hash
     * @param conn
     * @param token
     * @param item
     * @param count
     */
    public void addToCart(Jedis conn, String token, String item, int count) {
        // 添加购物车以及移除购物车
        if (count <= 0) {
            conn.hdel("cart:" + token, item);
        } else {
            conn.hset("cart:" + token, item, String.valueOf(count));
        }
    }

    /**
     * 更新用户令牌操作
     * @param conn
     * @param token
     * @param user
     * @param item
     */
    public void updateToken(Jedis conn, String token, String user, String item) {
        // 获取当前时间 单位毫秒
        long timestamp = System.currentTimeMillis() / 1000;
        // 登录用户与令牌之间的映射
        conn.hset("login:", token, user);
        // 最近登录用户的有序集合
        conn.zadd("recent:", timestamp, token);
        // 如果浏览商品不为空
        if (item != null) {
            // 用户最近浏览过的商品的集合
            conn.zadd("viewed:" + token, timestamp, item);
            // 限制最近浏览过商品个数25个
            conn.zremrangeByRank("viewed:" + token, 0, -26);
            //
            conn.zincrby("viewed:", -1, item);
        }
    }

    public void scheduleRowCache(Jedis conn, String rowId, int delay) {
        conn.zadd("delay:", delay, rowId);
        conn.zadd("schedule:", System.currentTimeMillis() / 1000, rowId);
    }

    public String cacheRequest(Jedis conn, String request, Callback callback) {
        if (!canCache(conn, request)){
            return callback != null ? callback.call(request) : null;
        }

        String pageKey = "cache:" + hashRequest(request);
        String content = conn.get(pageKey);

        if (content == null && callback != null){
            content = callback.call(request);
            conn.setex(pageKey, 300, content);
        }

        return content;
    }

    public boolean canCache(Jedis conn, String request) {
        try {
            URL url = new URL(request);
            HashMap<String,String> params = new HashMap<String,String>();
            if (url.getQuery() != null){
                for (String param : url.getQuery().split("&")){
                    String[] pair = param.split("=", 2);
                    params.put(pair[0], pair.length == 2 ? pair[1] : null);
                }
            }

            String itemId = extractItemId(params);
            if (itemId == null || isDynamic(params)) {
                return false;
            }
            Long rank = conn.zrank("viewed:", itemId);
            return rank != null && rank < 10000;
        }catch(MalformedURLException mue){
            return false;
        }
    }

    public boolean isDynamic(Map<String,String> params) {
        return params.containsKey("_");
    }

    public String extractItemId(Map<String,String> params) {
        return params.get("item");
    }

    public String hashRequest(String request) {
        return String.valueOf(request.hashCode());
    }

    public interface Callback {
        public String call(String request);
    }

    /**
     * 清除登陆session的多线程
     *
     * */
    public class CleanSessionsThread
        extends Thread
    {
        private Jedis conn;
        private int limit;
        private boolean quit;

        public CleanSessionsThread(int limit) {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
            this.limit = limit;
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            while (!quit) {
                // 最近登陆令牌的数量
                long size = conn.zcard("recent:");
                if (size <= limit){
                    try {
                        // 令牌数量未超过限制，暂停1秒，再重新检查
                        sleep(1000);
                    }catch(InterruptedException ie){
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                // 获取需要移除的令牌ID
                long endIndex = Math.min(size - limit, 100);
                // 有序集合按分值排序 从小到大 ，index 0 - endIndex 为分值小的数据
                Set<String> tokenSet = conn.zrange("recent:", 0, endIndex - 1);
                String[] tokens = tokenSet.toArray(new String[tokenSet.size()]);

                ArrayList<String> sessionKeys = new ArrayList<String>();
                for (String token : tokens) {
                    sessionKeys.add("viewed:" + token);
                }
                // 移除旧的代码
                // 从zset集合中批量删除key
                conn.del(sessionKeys.toArray(new String[sessionKeys.size()]));
                // 从hash中批量删除用户tokens
                conn.hdel("login:", tokens);
                conn.zrem("recent:", tokens);
            }
        }
    }

    /**
     * 比CleanSessionsThread增加了删除过期token时候同时删除购物车数据
     */
    public class CleanFullSessionsThread
        extends Thread
    {
        private Jedis conn;
        private int limit;
        private boolean quit;

        public CleanFullSessionsThread(int limit) {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
            this.limit = limit;
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            while (!quit) {
                long size = conn.zcard("recent:");
                if (size <= limit){
                    try {
                        sleep(1000);
                    }catch(InterruptedException ie){
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                long endIndex = Math.min(size - limit, 100);
                Set<String> sessionSet = conn.zrange("recent:", 0, endIndex - 1);
                String[] sessions = sessionSet.toArray(new String[sessionSet.size()]);

                ArrayList<String> sessionKeys = new ArrayList<String>();
                for (String sess : sessions) {
                    sessionKeys.add("viewed:" + sess);
                    // 新增代码 删除过期token时候对应的购物车数据一并删除
                    sessionKeys.add("cart:" + sess);
                }

                conn.del(sessionKeys.toArray(new String[sessionKeys.size()]));
                conn.hdel("login:", sessions);
                conn.zrem("recent:", sessions);
            }
        }
    }

    public class CacheRowsThread
        extends Thread
    {
        private Jedis conn;
        private boolean quit;

        public CacheRowsThread() {
            this.conn = new Jedis("localhost");
            this.conn.select(15);
        }

        public void quit() {
            quit = true;
        }

        public void run() {
            while (!quit){
                Set<Tuple> range = conn.zrangeWithScores("schedule:", 0, 0);
                Tuple next = range.size() > 0 ? range.iterator().next() : null;
                long now = System.currentTimeMillis() / 1000;
                if (next == null || next.getScore() > now){
                    try {
                        sleep(50);
                    }catch(InterruptedException ie){
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                String rowId = next.getElement();
                double delay = conn.zscore("delay:", rowId);
                if (delay <= 0) {
                    conn.zrem("delay:", rowId);
                    conn.zrem("schedule:", rowId);
                    conn.del("inv:" + rowId);
                    continue;
                }

                Inventory row = Inventory.get(rowId);
                conn.zadd("schedule:", now + delay, rowId);
                conn.set("inv:" + rowId, JSON.toJSONString(row));
            }
        }
    }

    public static class Inventory {
        private String id;
        private String data;
        private long time;

        private Inventory (String id) {
            this.id = id;
            this.data = "data to cache...";
            this.time = System.currentTimeMillis() / 1000;
        }

        public static Inventory get(String id) {
            return new Inventory(id);
        }
    }
}
