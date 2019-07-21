package redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 事务应用例子：此类实现了一个简单的用户商品买卖过程
 * hash 来存储每个用户的属性信息 key users:123
 * set 来存储每个用户的包裹(里面很多商品) key inventory:123
 * zset 来存储销售市场 key market
 *
 *
 * 使用到的方法有
 * watch 监控 数据集合变化
 *
 * set
 * sismember 判断某个成员是否存在
 *
 * hash
 * hincbry 给某个hash 中属性值加上一个数字
 *
 * zset
 * zscore 获取某个成员的分值
 *
 *
 *
 *
 *
 */
@Slf4j
public class Chapter04 {
    public static final void main(String[] args) {
        new Chapter04().run();
    }

    public void run() {
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        testListItem(conn, false);
        testPurchaseItem(conn);
        testBenchmarkUpdateToken(conn);
    }

    public void testListItem(Jedis conn, boolean nested) {
        if (!nested){
            log.info("\n----- testListItem -----");
        }

        log.info("We need to set up just enough state so that a user can list an item");
        String seller = "userX";
        String item = "itemX";
        String itemY = "itemY";
        // 给userX 包裹里面添加一个itemX 商品
        conn.sadd("inventory:" + seller, item);
        conn.sadd("inventory:" + seller, itemY);
        // 取出userX 包裹中所有物品
        Set<String> i = conn.smembers("inventory:" + seller);

        log.info("The user's inventory has:");
        for (String member : i){
            log.info("  " + member);
        }
        assert i.size() > 0;

        log.info("Listing the item...");
        // 把某用户的商品放入市场中售卖
        boolean l = listItem(conn, item, seller, 10);
        log.info("Listing the item succeeded? " + l);
        assert l;

        // 取出来销售市场正在卖的商品
        Set<Tuple> r = conn.zrangeWithScores("market:", 0, -1);
        log.info("The market contains:");
        for (Tuple tuple : r){
            log.info("  " + tuple.getElement() + ", " + tuple.getScore());
        }
        assert r.size() > 0;
    }

    public void testPurchaseItem(Jedis conn) {
        log.info("\n----- testPurchaseItem -----");
        testListItem(conn, true);

        log.info("We need to set up just enough state so a user can buy an item");
        //  从hash用户信息结构中设置 userY 钱包里面的钱设置为125 元
        conn.hset("users:userY", "funds", "125");
        // 从hash用户信息结构中 取出 userY 的属性信息
        Map<String,String> r = conn.hgetAll("users:userY");
        log.info("The user has some money:");
        for (Map.Entry<String,String> entry : r.entrySet()){
            log.info("  " + entry.getKey() + ": " + entry.getValue());
        }
        assert r.size() > 0;
        assert r.get("funds") != null;

        log.info("Let's purchase an item");
        boolean p = purchaseItem(conn, "userY", "itemX", "userX", 10);
        log.info("Purchasing an item succeeded? " + p);
        assert p;
        r = conn.hgetAll("users:userY");
        log.info("Their money is now:");
        for (Map.Entry<String,String> entry : r.entrySet()){
            log.info("  " + entry.getKey() + ": " + entry.getValue());
        }
        assert r.size() > 0;

        String buyer = "userY";
        Set<String> i = conn.smembers("inventory:" + buyer);
        log.info("Their inventory is now:");
        for (String member : i){
            log.info("  " + member);
        }
        assert i.size() > 0;
        assert i.contains("itemX");
        assert conn.zscore("market:", "itemX.userX") == null;
    }

    public void testBenchmarkUpdateToken(Jedis conn) {
        log.info("\n----- testBenchmarkUpdate -----");
        benchmarkUpdateToken(conn, 5);
    }

    /**
     * 把某用户的商品放入市场中售卖
     * @param conn
     * @param itemId
     * @param sellerId
     * @param price
     * @return
     */
    public boolean listItem(
            Jedis conn, String itemId, String sellerId, double price) {
        // 每个用户的包裹 key
        String inventory = "inventory:" + sellerId;
        // 售卖市场每个members的成员
        String item = itemId + '.' + sellerId;
        long end = System.currentTimeMillis() + 5000;

        while (System.currentTimeMillis() < end) {
            // 监控用户包裹发生的变化
            conn.watch(inventory);
            // 判断即将售卖的商品是否在用户包裹中，不在则解除监控
            if (!conn.sismember(inventory, itemId)){
                conn.unwatch();
                return false;
            }
            //开启事务
            Transaction trans = conn.multi();
            // 把销售的物品添加到销售市场里
            trans.zadd("market:", price, item);
            // 从用户包裹里拿出来物品
            trans.srem(inventory, itemId);
            //提交事务
            List<Object> results = trans.exec();
            // null response indicates that the transaction was aborted due to
            // the watched key changing.
            if (results == null){
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * 购买商品
     * @param conn
     * @param buyerId
     * @param itemId
     * @param sellerId
     * @param lprice
     * @return
     */
    public boolean purchaseItem(
            Jedis conn, String buyerId, String itemId, String sellerId, double lprice) {

        String buyer = "users:" + buyerId;
        String seller = "users:" + sellerId;
        String item = itemId + '.' + sellerId;
        String inventory = "inventory:" + buyerId;
        long end = System.currentTimeMillis() + 10000;
        // 出错后10s内重试
        while (System.currentTimeMillis() < end){
            // 监控销售市场数据 、购买者信息是否发生变化
            conn.watch("market:", buyer);
            // 验证销售市场中该物品价格是否发生变化、购买者的金额是否足够
            double price = conn.zscore("market:", item);
            double funds = Double.parseDouble(conn.hget(buyer, "funds"));
            if (price != lprice || price > funds){
                // 解除监控
                conn.unwatch();
                return false;
            }
            // 开启事务
            Transaction trans = conn.multi();
            // 将买物品的用户 余额加上物品价值
            trans.hincrBy(seller, "funds", (int)price);
            // 将买物品的用户 余额减去物品价值
            trans.hincrBy(buyer, "funds", (int)-price);
            // 将物品添加到购买者 包裹里
            trans.sadd(inventory, itemId);
            // 将物品从销售市场删除
            trans.zrem("market:", item);
            // 提交事务
            List<Object> results = trans.exec();
            // null response indicates that the transaction was aborted due to
            // the watched key changing.
            if (results == null){
                continue;
            }
            return true;
        }

        return false;
    }

    public void benchmarkUpdateToken(Jedis conn, int duration) {
        try{
            @SuppressWarnings("rawtypes")
            Class[] args = new Class[]{
                Jedis.class, String.class, String.class, String.class};
            Method[] methods = new Method[]{
                this.getClass().getDeclaredMethod("updateToken", args),
                this.getClass().getDeclaredMethod("updateTokenPipeline", args),
            };
            for (Method method : methods){
                int count = 0;
                long start = System.currentTimeMillis();
                long end = start + (duration * 1000);
                while (System.currentTimeMillis() < end){
                    count++;
                    method.invoke(this, conn, "token", "user", "item");
                }
                long delta = System.currentTimeMillis() - start;
                log.info(
                        method.getName() + ' ' +
                        count + ' ' +
                        (delta / 1000) + ' ' +
                        (count / (delta / 1000)));
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public void updateToken(Jedis conn, String token, String user, String item) {
        long timestamp = System.currentTimeMillis() / 1000;
        conn.hset("login:", token, user);
        conn.zadd("recent:", timestamp, token);
        if (item != null) {
            conn.zadd("viewed:" + token, timestamp, item);
            conn.zremrangeByRank("viewed:" + token, 0, -26);
            conn.zincrby("viewed:", -1, item);
        }
    }

    public void updateTokenPipeline(Jedis conn, String token, String user, String item) {
        long timestamp = System.currentTimeMillis() / 1000;
        Pipeline pipe = conn.pipelined();
        pipe.multi();
        pipe.hset("login:", token, user);
        pipe.zadd("recent:", timestamp, token);
        if (item != null){
            pipe.zadd("viewed:" + token, timestamp, item);
            pipe.zremrangeByRank("viewed:" + token, 0, -26);
            pipe.zincrby("viewed:", -1, item);
        }
        pipe.exec();
    }
}
