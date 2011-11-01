/*
 * Copyright 2011 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.redis.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;

/**
 * Default implementation of {@link ZSetOperations}.
 * 
 * @author Costin Leau
 */
class DefaultZSetOperations<K, V> extends AbstractOperations<K, V> implements ZSetOperations<K, V> {

	DefaultZSetOperations(RedisTemplate<K, V> template) {
		super(template);
	}

	
	public Boolean add(final K key, final V value, final double score) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(value);

		return execute(new RedisCallback<Boolean>() {
			
			public Boolean doInRedis(RedisConnection connection) {
				return connection.zAdd(rawKey, score, rawValue);
			}
		}, true);
	}

	
	public Double incrementScore(K key, V value, final double delta) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(value);

		return execute(new RedisCallback<Double>() {
			
			public Double doInRedis(RedisConnection connection) {
				return connection.zIncrBy(rawKey, delta, rawValue);
			}
		}, true);
	}

	
	public void intersectAndStore(K key, K otherKey, K destKey) {
		intersectAndStore(key, Collections.singleton(otherKey), destKey);
	}

	
	public void intersectAndStore(K key, Collection<K> otherKeys, K destKey) {
		final byte[][] rawKeys = rawKeys(key, otherKeys);
		final byte[] rawDestKey = rawKey(destKey);
		execute(new RedisCallback<Object>() {
			
			public Object doInRedis(RedisConnection connection) {
				connection.zInterStore(rawDestKey, rawKeys);
				return null;
			}
		}, true);
	}

	
	public Set<V> range(K key, final long start, final long end) {
		final byte[] rawKey = rawKey(key);

		Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {
			
			public Set<byte[]> doInRedis(RedisConnection connection) {
				return connection.zRange(rawKey, start, end);
			}
		}, true);

		return deserializeValues(rawValues);
	}

	
	public Set<V> reverseRange(K key, final long start, final long end) {
		final byte[] rawKey = rawKey(key);

		Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {
			
			public Set<byte[]> doInRedis(RedisConnection connection) {
				return connection.zRevRange(rawKey, start, end);
			}
		}, true);

		return deserializeValues(rawValues);
	}

	
	public Set<TypedTuple<V>> rangeWithScores(K key, final long start, final long end) {
		final byte[] rawKey = rawKey(key);

		Set<Tuple> rawValues = execute(new RedisCallback<Set<Tuple>>() {
			
			public Set<Tuple> doInRedis(RedisConnection connection) {
				return connection.zRangeWithScores(rawKey, start, end);
			}
		}, true);

		return deserializeTupleValues(rawValues);
	}

	
	public Set<TypedTuple<V>> reverseRangeWithScores(K key, final long start, final long end) {
		final byte[] rawKey = rawKey(key);

		Set<Tuple> rawValues = execute(new RedisCallback<Set<Tuple>>() {
			
			public Set<Tuple> doInRedis(RedisConnection connection) {
				return connection.zRevRangeWithScores(rawKey, start, end);
			}
		}, true);

		return deserializeTupleValues(rawValues);
	}

	
	public Set<V> rangeByScore(K key, final double min, final double max) {
		final byte[] rawKey = rawKey(key);

		Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {
			
			public Set<byte[]> doInRedis(RedisConnection connection) {
				return connection.zRangeByScore(rawKey, min, max);
			}
		}, true);

		return deserializeValues(rawValues);
	}


	
	public Set<V> reverseRangeByScore(K key, final double min, final double max) {
		final byte[] rawKey = rawKey(key);

		Set<byte[]> rawValues = execute(new RedisCallback<Set<byte[]>>() {
			
			public Set<byte[]> doInRedis(RedisConnection connection) {
				return connection.zRevRangeByScore(rawKey, min, max);
			}
		}, true);

		return deserializeValues(rawValues);
	}

	
	public Set<TypedTuple<V>> rangeByScoreWithScores(K key, final double min, final double max) {
		final byte[] rawKey = rawKey(key);

		Set<Tuple> rawValues = execute(new RedisCallback<Set<Tuple>>() {
			
			public Set<Tuple> doInRedis(RedisConnection connection) {
				return connection.zRangeByScoreWithScores(rawKey, min, max);
			}
		}, true);

		return deserializeTupleValues(rawValues);
	}

	
	public Set<TypedTuple<V>> reverseRangeByScoreWithScores(K key, final double min, final double max) {
		final byte[] rawKey = rawKey(key);

		Set<Tuple> rawValues = execute(new RedisCallback<Set<Tuple>>() {
			
			public Set<Tuple> doInRedis(RedisConnection connection) {
				return connection.zRevRangeByScoreWithScores(rawKey, min, max);

			}
		}, true);

		return deserializeTupleValues(rawValues);
	}

	
	public Long rank(K key, Object o) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(o);

		return execute(new RedisCallback<Long>() {
			
			public Long doInRedis(RedisConnection connection) {
				Long zRank = connection.zRank(rawKey, rawValue);
				return (zRank != null && zRank.longValue() >= 0 ? zRank : null);
			}
		}, true);
	}

	
	public Long reverseRank(K key, Object o) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(o);

		return execute(new RedisCallback<Long>() {
			
			public Long doInRedis(RedisConnection connection) {
				Long zRank = connection.zRevRank(rawKey, rawValue);
				return (zRank != null && zRank.longValue() >= 0 ? zRank : null);
			}
		}, true);
	}

	
	public Boolean remove(K key, Object o) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(o);

		return execute(new RedisCallback<Boolean>() {
			
			public Boolean doInRedis(RedisConnection connection) {
				return connection.zRem(rawKey, rawValue);
			}
		}, true);
	}

	
	public void removeRange(K key, final long start, final long end) {
		final byte[] rawKey = rawKey(key);
		execute(new RedisCallback<Object>() {
			
			public Object doInRedis(RedisConnection connection) {
				connection.zRemRange(rawKey, start, end);
				return null;
			}
		}, true);
	}

	
	public void removeRangeByScore(K key, final double min, final double max) {
		final byte[] rawKey = rawKey(key);
		execute(new RedisCallback<Object>() {
			
			public Object doInRedis(RedisConnection connection) {
				connection.zRemRangeByScore(rawKey, min, max);
				return null;
			}
		}, true);
	}

	
	public Double score(K key, Object o) {
		final byte[] rawKey = rawKey(key);
		final byte[] rawValue = rawValue(o);

		return execute(new RedisCallback<Double>() {
			
			public Double doInRedis(RedisConnection connection) {
				return connection.zScore(rawKey, rawValue);
			}
		}, true);
	}

	
	public Long count(K key, final double min, final double max) {
		final byte[] rawKey = rawKey(key);

		return execute(new RedisCallback<Long>() {
			
			public Long doInRedis(RedisConnection connection) {
				return connection.zCount(rawKey, min, max);
			}
		}, true);
	}

	
	public Long size(K key) {
		final byte[] rawKey = rawKey(key);

		return execute(new RedisCallback<Long>() {
			
			public Long doInRedis(RedisConnection connection) {
				return connection.zCard(rawKey);
			}
		}, true);
	}

	
	public void unionAndStore(K key, K otherKey, K destKey) {
		unionAndStore(key, Collections.singleton(otherKey), destKey);
	}

	
	public void unionAndStore(K key, Collection<K> otherKeys, K destKey) {
		final byte[][] rawKeys = rawKeys(key, otherKeys);
		final byte[] rawDestKey = rawKey(destKey);
		execute(new RedisCallback<Object>() {
			
			public Object doInRedis(RedisConnection connection) {
				connection.zUnionStore(rawDestKey, rawKeys);
				return null;
			}
		}, true);
	}
}