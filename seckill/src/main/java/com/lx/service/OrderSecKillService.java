package com.lx.service;

public interface OrderSecKillService {
	
	/**
	 * 初始化sku库存
	 * @param sku
	 * @param stock
	 */
	public void initSkuStock(String sku,Long stock);
	
	/**
	 * 秒杀
	 * @param userId
	 * @param sku
	 * @return false秒杀失败，true秒杀成功
	 */
	public boolean secKill(String userId,String sku);
	
	/**
	 * 保存秒杀订单
	 */
	public void saveSecKillOrder();
}
