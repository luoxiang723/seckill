package com.lx.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.lx.bean.SecKillOrderBean;
import com.lx.redis.RedisJsonUtil;
import com.lx.service.OrderSecKillService;
import com.lx.util.StringUtils;

@Service
public class OrderSecKillServiceImpl implements OrderSecKillService {
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	private static final String stockKeyPrefix = "Store_111_OrderSecKill_SKU_";
	private static final String secKillOrderKey = "Store_111_OrderSecKill_ORDER";
	private static final String secKillerSet = "secKiller_set";
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean secKill(final String userId,final String sku) {
		System.out.println(getClass().getName()+":secKill");
		SetOperations orderListOps = redisTemplate.opsForSet();
		orderListOps.add(secKillerSet, "");
		//orderListOps
		return (Boolean)redisTemplate.execute(new RedisCallback<Boolean>() {
			@SuppressWarnings("rawtypes")
			@Override
			public Boolean doInRedis(RedisConnection connection)
					throws DataAccessException {
				
				byte[] stockKeyBytes = redisTemplate.getKeySerializer().serialize(stockKeyPrefix+sku);
				
				byte[] orderKeyBytes = redisTemplate.getKeySerializer().serialize(secKillOrderKey);
				
				//乐观锁定库存,key为门店编码+sku编码
				connection.watch(stockKeyBytes);
				byte[] skuStockBytes = connection.get(stockKeyBytes);
				if(skuStockBytes == null){
					return false;
				}
				Long skuStock = Long.parseLong(new String(skuStockBytes));
				if(skuStock == null || skuStock < 1){
					redisTemplate.unwatch();
					return false;
				}
				
				//事务开始
				connection.multi();
				//库存-1
				connection.decrBy(stockKeyBytes, 1L);
				//设置value
				SecKillOrderBean secOrder = new SecKillOrderBean();
				secOrder.setUserId(userId);
				secOrder.setOrderNo("SO1000");
				secOrder.setSku(sku);
				secOrder.setQuantity(1);
				secOrder.setTime(System.currentTimeMillis());
				String orderStr = RedisJsonUtil.serialize(secOrder);
				if(StringUtils.isEmpty(orderStr)){
					return false;
				}
				connection.rPush(orderKeyBytes, orderStr.getBytes());
				//事务执行，如果watch的key被修改则返回null，执行成功返回修改后的数据，修改了两条就返回两条
				List result = connection.exec();
				
				if(CollectionUtils.isNotEmpty(result)){
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void saveSecKillOrder() {
		ListOperations<String, String> orderListOps = redisTemplate.opsForList();
		String secOrder = orderListOps.leftPop("Store_111_OrderSecKill_ORDER");
		if(secOrder != null){
			System.out.println(System.currentTimeMillis()+"-"+secOrder);
		}else{
			System.out.println(System.currentTimeMillis()+":没有订单");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initSkuStock(final String sku, final Long stock) {
		ValueOperations<String, String> skuOps = redisTemplate.opsForValue();
		skuOps.set(stockKeyPrefix+sku, stock.toString());
	}
}
