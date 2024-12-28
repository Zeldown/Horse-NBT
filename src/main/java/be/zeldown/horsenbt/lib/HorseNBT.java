package be.zeldown.horsenbt.lib;

import java.util.HashMap;
import java.util.Map;

import be.zeldown.horsenbt.lib.cache.HorseNBTCache;
import be.zeldown.horsenbt.lib.data.AdditionalDataBuilder;
import be.zeldown.horsenbt.lib.data.IAdditionalData;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;

public class HorseNBT {

	private static final Map<String, AdditionalDataBuilder<? extends Entity>> DATA_LIST = new HashMap<>();

	public static <T extends Entity> void register(final Class<? extends IAdditionalData<T>> clazz) {
		final AdditionalDataBuilder<T> additionalData = new AdditionalDataBuilder<>(clazz);
		final String key = additionalData.getSource().getKey();
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException(clazz.getSimpleName() + " does not have a key annotation.");
		}

		if (HorseNBT.DATA_LIST.containsKey(key)) {
			throw new IllegalArgumentException(clazz.getSimpleName() + " is already registered in the HorseNBT library.");
		}

		HorseNBT.DATA_LIST.put(key, additionalData);
	}

	@SuppressWarnings("unchecked")
	public static <E extends Entity, T extends IAdditionalData<E>> T get(final E entity, final Class<T> clazz) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null.");
		}

		final AdditionalDataBuilder<E> additionalData = new AdditionalDataBuilder<>(clazz);
		final String key = additionalData.getSource().getKey();
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException(clazz.getSimpleName() + " does not have a key annotation.");
		}

		IAdditionalData<E> cache = HorseNBTCache.get(entity, key);
		if (cache != null) {
			return (T) cache;
		}

		if (!additionalData.isApplicable(entity.getClass())) {
			throw new IllegalArgumentException(clazz.getSimpleName() + " is not compatible with the entity " + entity.getName() + " (" + entity.getClass().getName() + ")");
		}

		if (entity.getPersistentData().contains(key)) {
			final CompoundNBT nbt = entity.getPersistentData().getCompound(key);
			cache = additionalData.copy().getSource();
			cache.init(entity);
			cache.read(nbt);
			return (T) HorseNBTCache.put(entity, cache);
		}

		final CompoundNBT nbt = new CompoundNBT();
		cache = additionalData.copy().getSource();
		cache.init(entity);
		cache.write(nbt);
		cache.save();
		return (T) HorseNBTCache.put(entity, cache);
	}

	public static <E extends Entity, T extends IAdditionalData<E>> void destroy(final E entity, final Class<T> clazz) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null.");
		}

		final AdditionalDataBuilder<E> additionalData = new AdditionalDataBuilder<>(clazz);
		final String key = additionalData.getSource().getKey();
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException(clazz.getSimpleName() + " does not have a key annotation.");
		}

		if (!HorseNBT.DATA_LIST.containsKey(key)) {
			throw new IllegalArgumentException(clazz.getSimpleName() + " is not registered in the HorseNBT library.");
		}

		final IAdditionalData<E> cache = HorseNBTCache.get(entity, key);
		if (cache == null) {
			return;
		}

		cache.destroy(entity);
		entity.getPersistentData().remove(key);
		HorseNBTCache.remove(entity, key);
	}

	public static Map<String, AdditionalDataBuilder<? extends Entity>> getDataList() {
		return HorseNBT.DATA_LIST;
	}

}