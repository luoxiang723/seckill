package com.lx.redis;

import java.lang.reflect.Field;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lx.redis.annotation.NoCacheJsonField;
import com.lx.util.ReflectUtil;

public class RedisJsonUtil {
	/**
	 * 对象序列化成json
	 * @param obj
	 * @return
	 */
	public static String serialize(Object obj) {
		PropertyFilter filter = getPropertyFilter();
		return toJSONBytesWithFilter(obj, filter, SerializerFeature.SortField);
	}
	
	/**
	 * json转对象
	 * @param type
	 * @param jsonStr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static  <T> T deserialize(Class<T> type, String jsonStr) {
		if (type == String.class) {
			return (T)jsonStr;
		} else {
			return JSON.parseObject(jsonStr, type, Feature.SortFeidFastMatch);
		}
	}
	
	/**
	 * json转list
	 * @param type
	 * @param listStr
	 * @return
	 */
	public static  <T> List<T> deserializeArray(Class<T> type, String listStr) {
		return JSON.parseArray(listStr,type);
	}

	private static final String toJSONBytesWithFilter(Object object, PropertyFilter filter, SerializerFeature... features) {
		SerializeWriter out = new SerializeWriter();

		try {
			JSONSerializer serializer = new JSONSerializer(out);
			serializer.getPropertyFilters().add(filter);

			for (com.alibaba.fastjson.serializer.SerializerFeature feature : features) {
				serializer.config(feature, true);
			}

			serializer.write(object);

			return out.toString();
		} finally {
			out.close();
		}
	}

	private static PropertyFilter getPropertyFilter() {
		PropertyFilter filter = new PropertyFilter() {
			@Override
			public boolean apply(Object object, String name, Object value) {
				Field field = ReflectUtil.getField(object, name);
				
				if (field == null) {
					return false;
				}
				
				NoCacheJsonField noCacheJsonField = field.getAnnotation(NoCacheJsonField.class);
				
				if (noCacheJsonField != null) {
					return false;
				} else {
					return true;
				}
			}
		};

		return filter;
	}
}
