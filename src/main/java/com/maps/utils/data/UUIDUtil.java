package com.maps.utils.data;

import java.util.UUID;

public class UUIDUtil {

	public static String getUUID(){
		UUID uuid = UUID.randomUUID();
		String uuidStr = uuid.toString().replaceAll("-", "");
		return uuidStr;
	}
}
