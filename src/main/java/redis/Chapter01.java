package redis;


import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

import java.util.*;

/**
 * redis实现用户文章投票 并排名的程序
 *
 * 自动发布文章，发布完后用户对文章就行投票，每个用户对一个文章只能投一票，发布文章有效期为一周
 * 对发布文章按照发布时间、分值来进行动态排序
 * 同时支持对文章分类 不同类别的文章属于不同类
 *
 * 思路：hash 存储文章信息、set 存储已投用户列表、zset 存储文章排序信息(按分值、按发布时间)
 *
 *
 * 知识点：
 * 1.对hash中某个key 值更新    hincrBy
 * 2.获取hash中某个key 值       hget
 * 3.对zset中某个member值更新  zincrby
 * 4.获取zset中某个key 值         zscore
 * 5.求两个集合的交集、并集等  zinterstore
 * 6.对zset集合分页，根据索引实现  zrevrange
 *
 */
@Slf4j
public class Chapter01 {


    // 每天86400 秒
    private static final int ONE_WEEK_IN_SECONDS = 7 * 86400;
    private static final int VOTE_SCORE = 432;
    // 文章每页大小
    private static final int ARTICLES_PER_PAGE = 25;

    public static final void main(String[] args) {
        new Chapter01().run();
    }

    public void run() {
        // 连接本地redis
        Jedis conn = new Jedis("localhost");
        conn.select(15);

        // 发布文章
        String articleId = postArticle(
            conn, "username", "A title", "http://www.google.com");
        log.info("We posted a new article with id: " + articleId);
        log.info("Its HASH looks like:");

        // 根据文章ID 获取文章内容 hash对象
        Map<String,String> articleData = conn.hgetAll("article:" + articleId);
        for (Map.Entry<String,String> entry : articleData.entrySet()){
            log.info("  " + entry.getKey() + ": " + entry.getValue());
        }

        // 张三对文章投票
        articleVote(conn, "user_zhangsan", "article:" + articleId);
        // 获取指定文章的投票数量
        String votes = conn.hget("article:" + articleId, "votes");
        log.info("We voted for the article, it now has votes: " + votes);
        assert Integer.parseInt(votes) > 1;

        // 获取文章列表
        log.info("The currently highest-scoring articles are:");
        List<Map<String,String>> articles = getArticles(conn, 1);
        // 打印文章列表
        printArticles(articles);
        assert articles.size() >= 1;

        // 将一篇文章添加到一个或多个集合
        addGroups(conn, articleId, new String[]{"new-group"});
        log.info("We added the article to a new group, other articles include:");
        // 得到指定集合下面的指定页数文章列表
        articles = getGroupArticles(conn, "new-group", 1);
        printArticles(articles);
        assert articles.size() >= 1;
    }

    /**
     * 发布文章方法
     * @param conn redis 连接
     * @param user 用户标识
     * @param title 文章标题
     * @param link 文章链接
     * @return
     */
    public String postArticle(Jedis conn, String user, String title, String link) {
        // string 串integer 自增 生成文章ID
        String articleId = String.valueOf(conn.incr("article:"));

        // set 为每篇文章记录已投票用户白名单，防止重复投票，有效期为7天
        String voted = "voted:" + articleId;
        conn.sadd(voted, user);
        conn.expire(voted, ONE_WEEK_IN_SECONDS);

        // hmset 将文章文内保存为hashmap存储
        long now = System.currentTimeMillis() / 1000;
        String article = "article:" + articleId;
        HashMap<String,String> articleData = new HashMap<String,String>();
        articleData.put("title", title);//标题
        articleData.put("link", link);//链接
        articleData.put("user", user);//用户
        articleData.put("now", String.valueOf(now));//发布时间
        articleData.put("votes", "1");//投票或点赞数量
        conn.hmset(article, articleData);

        //zset 根据评分排序文章的有序集合
        conn.zadd("score:", now + VOTE_SCORE, article);
        //zset 根据时间排序文章的有序集合
        conn.zadd("time:", now, article);

        return articleId;
    }

    /**
     * 对文章投票
     * @param conn
     * @param user 用户
     * @param article 文章
     */
    public void articleVote(Jedis conn, String user, String article) {
        // 判断评分有序列表里面的指定文章是否过期
        long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
        if (conn.zscore("time:", article) < cutoff){
            return;
        }
        // 获取文章ID
        String articleId = article.substring(article.indexOf(':') + 1);
        //如果用户是第一次投票
        if (conn.sadd("voted:" + articleId, user) == 1) {
            //更新指定文章的评分
            conn.zincrby("score:", VOTE_SCORE, article);
            //更新指定篇文章的投票数量
            conn.hincrBy(article, "votes", 1);
        }
    }


    public List<Map<String,String>> getArticles(Jedis conn, int page) {
        return getArticles(conn, page, "score:");
    }

    /**
     * 获取指定页数的文章列表（按分值排序）
     * @param conn
     * @param page
     * @param order
     * @return
     */
    public List<Map<String,String>> getArticles(Jedis conn, int page, String order) {
        int start = (page - 1) * ARTICLES_PER_PAGE;
        int end = start + ARTICLES_PER_PAGE - 1;
        // 获取分值有序列表zset中文章主键列表
        Set<String> ids = conn.zrevrange(order, start, end);
        List<Map<String,String>> articles = new ArrayList<Map<String,String>>();
        for (String id : ids){
            // 根据文章主键列表来获取指定文章hash
            Map<String,String> articleData = conn.hgetAll(id);
            articleData.put("id", id);
            articles.add(articleData);
        }

        return articles;
    }

    /**
     * 讲一个文章添加到一个或多个组
     * @param conn
     * @param articleId
     * @param toAdd
     */
    public void addGroups(Jedis conn, String articleId, String[] toAdd) {
        String article = "article:" + articleId;
        for (String group : toAdd) {
            conn.sadd("group:" + group, article);
        }
    }

    public List<Map<String,String>> getGroupArticles(Jedis conn, String group, int page) {
        return getGroupArticles(conn, group, page, "score:");
    }

    /**
     * 得到一个组的所有文章
     * @param conn
     * @param group
     * @param page
     * @param order
     * @return
     */
    public List<Map<String,String>> getGroupArticles(Jedis conn, String group, int page, String order) {
        // 新集合key
        String key = order + group;
        if (!conn.exists(key)) {
            // 指定两个集合聚合时的操作，相同元素取值最大的
            ZParams params = new ZParams().aggregate(ZParams.Aggregate.MAX);
            // 合并两个集合 group:group 和 分值集合 score:
            conn.zinterstore(key, params, "group:" + group, order);
            // 合并后的集合缓存60s，为了提高效率
            conn.expire(key, 60);
        }
        return getArticles(conn, page, key);
    }

    private void printArticles(List<Map<String,String>> articles){
        for (Map<String,String> article : articles){
            log.info("  id: " + article.get("id"));
            for (Map.Entry<String,String> entry : article.entrySet()){
                if (entry.getKey().equals("id")){
                    continue;
                }
                log.info("    " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}
