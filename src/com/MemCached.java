package com;

import java.io.IOException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;

class MemCached{
	static MemcachedClient mc = null;
	@SuppressWarnings("unused")
	public MemCached() {
        try {
            MemcachedClient mc = new MemcachedClient(
            new ConnectionFactoryBuilder()
                .setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                .build(),AddrUtil.getAddresses("127.0.0.1:11211"));
        } catch (IOException e) {
            // handle exception
        }
	}

	public void setMC(String key, String value) {
		mc.set(key, 0, value);
	}
	
	public void setMC(String key, String value, int expireTime) {
		mc.set(key, expireTime, value);
	}
	
	public String get(String key) {
		return (String) mc.get(key);
	}
}