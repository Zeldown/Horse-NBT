package be.zeldown.horsenbt.lib.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import be.zeldown.horsenbt.lib.data.IAdditionalData;
import net.minecraft.entity.Entity;

public class HorseNBTCache {

	private static final Map<String, Map<String, IAdditionalData<?>>> CACHE = new ConcurrentHashMap<>();

	public static IAdditionalData<?> put(final Entity entity, final IAdditionalData<?> data) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null.");
		}

		if (data == null) {
			throw new IllegalArgumentException("Data cannot be null.");
		}

		HorseNBTCache.CACHE.computeIfAbsent(entity.getStringUUID(), k -> new ConcurrentHashMap<>()).put(data.getKey(), data);
		return data;
	}

	@SuppressWarnings("unchecked")
	public static <T extends IAdditionalData<E>, E extends Entity> T get(final E entity, final String key) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null.");
		}

		if (key == null) {
			throw new IllegalArgumentException("Data cannot be null.");
		}

		final Map<String, IAdditionalData<?>> map = HorseNBTCache.CACHE.get(entity.getStringUUID());
		if (map == null) {
			return null;
		}

		return (T) map.get(key);
	}

	public static <T extends IAdditionalData<E>, E extends Entity> void remove(final E entity, final String key) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null.");
		}

		if (key == null) {
			throw new IllegalArgumentException("Data cannot be null.");
		}

		final Map<String, IAdditionalData<?>> map = HorseNBTCache.CACHE.get(entity.getStringUUID());
		if (map == null) {
			return;
		}

		map.remove(key);
	}

	public static void remove(final Entity entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null.");
		}

		HorseNBTCache.CACHE.remove(entity.getStringUUID());
	}

	public static Map<String, Map<String, IAdditionalData<?>>> raw() {
		return HorseNBTCache.CACHE;
	}

}