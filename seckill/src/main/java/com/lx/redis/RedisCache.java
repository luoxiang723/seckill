package com.lx.redis;

import static com.lx.redis.RedisJsonUtil.deserialize;
import static com.lx.redis.RedisJsonUtil.deserializeArray;
import static com.lx.redis.RedisJsonUtil.serialize;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;


public class RedisCache {
	private RedisTemplate<String,String> redisTemplate;
	
	/**
	 * 向redis写入Object，永久有效不过期，需要过期请调用带有timeout参数的
	 * @param key
	 * @param obj
	 */
	public void set(String key,Object obj){
		redisTemplate.opsForValue().set(key,serialize(obj));
	}
	
	/**
	 * 向redis写入Object
	 * @param key
	 * @param obj
	 * @param timeout 过期时间
	 * @param timeUnit 过期时间的单位
	 */
	public void set(String key,Object obj,long timeout,TimeUnit timeUnit){
		redisTemplate.opsForValue().set(key,serialize(obj),timeout,timeUnit);
	}
	
	/**
	 * 从redis获取Object
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> T get(String key,Class<T> clazz){
		String value = redisTemplate.opsForValue().get(key);
		return deserialize(clazz, value);
	}
	
	/**
	 * 从redis获取List
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> List<T> getList(String key,Class<T> clazz){
		String value = redisTemplate.opsForValue().get(key);
		return deserializeArray(clazz, value);
	}
	/**
	 * 根据key删除redis缓存
	 * @param key
	 */
	public void delete(String key){
		redisTemplate.delete(key);
	}
	
	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	
	

}
