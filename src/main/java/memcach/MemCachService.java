package memcach;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Map;

/**
 * Created by liumin on 15/7/2.
 * redis服务类
 */

public class MemCachService {

    private static MemCachService memCachService;

    public ShardedJedisPool getShardedPool() {
        return shardedPool;
    }

    public void setShardedPool(ShardedJedisPool shardedPool) {
        this.shardedPool = shardedPool;
    }

    private static ShardedJedisPool shardedPool = null;

    MemCachService(){
        memCachService = this;
    }

    public static MemCachService getGet() {
        return memCachService;
    }

    /**
     *覆盖已有键值对
     *
     */
    void MemCachSet( String Key, String Vaule ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        shardedJedis.set( Key,Vaule );
        shardedPool.returnResourceObject( shardedJedis );
    }

    /**
     *通过KEY获取值
     *
     */
    public static  String  MemCachgGet( String Key ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        String value = shardedJedis.get( Key );
        shardedPool.returnResourceObject( shardedJedis );
        return value;
    }

    /**
     *检测一个键值是否存在
     *
     */
    public static boolean KeyIsExists( String Key ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        boolean IsExists = shardedJedis.exists(Key);
        shardedPool.returnResourceObject( shardedJedis );
        return IsExists;
    }

    /**
     *插入一个键值
     *
     */
    public static void InsertValue( String Key,String Value ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        shardedJedis.setnx( Key, Value );
        shardedPool.returnResourceObject( shardedJedis );
    }

    /**
     *在已有的键里追加值
     *
     */
    public void AppendValue( String Key,String Value ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        shardedJedis.append(Key, Value);
        shardedPool.returnResourceObject( shardedJedis );
    }

    /**
     *插入键值对时  附加失效时间
     *
     */
    public static void InsertValueWithTime( String Key,int time,String Value ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        shardedJedis.setex(Key, time, Value);
        shardedPool.returnResourceObject( shardedJedis );
    }

    /**
     *删除一对键值对
     *
     */
    public static void RemoveValue( String Key ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        shardedJedis.del( Key );
        shardedPool.returnResourceObject( shardedJedis );
    }

    /**
     *获取一个键值对的失效时间
     *
     */
    public static Long GetTimeOfKey( String Key, int Time ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        Long seconds = shardedJedis.ttl(Key);
        shardedPool.returnResourceObject( shardedJedis );
        return seconds;
    }

    /**
     *取消一个键值对的失效时间
     *
     */
    public static void CanleTimeOfKey( String Key ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        shardedJedis.persist(Key);
        shardedPool.returnResourceObject( shardedJedis );
    }

    public static String MemCachSetMap( String UserID,Map<String,String> map ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        shardedJedis.hmset( UserID,map );
        shardedPool.returnResourceObject( shardedJedis );
        return "SUCCESS";
    }

    public static Map<String,String> GetMemCachMap( String UserID ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        Map<String,String> map = shardedJedis.hgetAll( UserID );
        shardedPool.returnResourceObject( shardedJedis );
        return map;
    }

    public static String GetMemCachMapByMapKey( String UserID,String MapKey ){
        ShardedJedis shardedJedis = shardedPool.getResource();
        String map = shardedJedis.hget(UserID, MapKey );
        shardedPool.returnResourceObject( shardedJedis );
        return map;
    }

}
