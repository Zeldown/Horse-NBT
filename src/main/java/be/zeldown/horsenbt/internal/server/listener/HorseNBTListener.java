package be.zeldown.horsenbt.internal.server.listener;

import be.zeldown.horsenbt.lib.HorseNBT;
import be.zeldown.horsenbt.lib.cache.HorseNBTCache;
import be.zeldown.horsenbt.lib.data.AdditionalDataBuilder;
import be.zeldown.horsenbt.lib.data.AdditionalDataProperty;
import be.zeldown.horsenbt.lib.data.IAdditionalData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HorseNBTListener {

	@SubscribeEvent
	@SuppressWarnings("unchecked")
	public void onEntityJoin(final EntityJoinWorldEvent e) {
		if (e.getEntity().level.isClientSide) {
			return;
		}

		for (final AdditionalDataBuilder<? extends Entity> data : HorseNBT.getDataList().values()) {
			final String key = data.getSource().getKey();
			if (key == null || key.isEmpty()) {
				continue;
			}

			IAdditionalData<?> cache = HorseNBTCache.get(e.getEntity(), key);
			if (cache == null && data.isApplicable(e.getEntity().getClass())) {
				cache = HorseNBT.get(e.getEntity(), data.getSource().getClass());
			}

			if (cache == null) {
				continue;
			}

			if (data.hasProperty(AdditionalDataProperty.SYNCHRONIZED) || data.hasProperty(AdditionalDataProperty.SYNCHRONIZED_TRACKER)) {
				cache.sync();
			}
		}
	}

	@SubscribeEvent
	public void onEntityTrack(final StartTracking e) {
		if (!(e.getPlayer() instanceof ServerPlayerEntity)) {
			return;
		}

		for (final AdditionalDataBuilder<? extends Entity> data : HorseNBT.getDataList().values()) {
			final String key = data.getSource().getKey();
			if (key == null || key.isEmpty() || !data.hasProperty(AdditionalDataProperty.SYNCHRONIZED_TRACKER)) {
				continue;
			}

			final IAdditionalData<?> cache = HorseNBTCache.get(e.getEntity(), key);
			if (cache == null) {
				continue;
			}

			cache.sync((ServerPlayerEntity) e.getPlayer());
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unchecked")
	public void onEntityDeath(final LivingDeathEvent e) {
		if (e.getEntity().level.isClientSide) {
			return;
		}

		for (final AdditionalDataBuilder<? extends Entity> data : HorseNBT.getDataList().values()) {
			final String key = data.getSource().getKey();
			if (key == null || key.isEmpty() || data.hasProperty(AdditionalDataProperty.PERSISTANT)) {
				continue;
			}

			final IAdditionalData<?> cache = HorseNBTCache.get(e.getEntity(), key);
			if (cache == null) {
				continue;
			}

			HorseNBT.destroy(e.getEntity(), data.getSource().getClass());
			if (data.hasProperty(AdditionalDataProperty.SYNCHRONIZED) || data.hasProperty(AdditionalDataProperty.SYNCHRONIZED_TRACKER)) {
				cache.sync();
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(final PlayerLoggedOutEvent e) {
		if (e.getPlayer().level.isClientSide) {
			return;
		}

		for (final AdditionalDataBuilder<? extends Entity> data : HorseNBT.getDataList().values()) {
			final String key = data.getSource().getKey();
			if (key == null || key.isEmpty() || !data.hasProperty(AdditionalDataProperty.PERSISTANT)) {
				continue;
			}

			final IAdditionalData<?> cache = HorseNBTCache.get(e.getPlayer(), key);
			if (cache == null) {
				continue;
			}

			final CompoundNBT nbt = new CompoundNBT();
			cache.write(nbt);
			cache.save();
		}

		HorseNBTCache.remove(e.getPlayer());
	}

	@SubscribeEvent
	public void onSave(final PlayerEvent.SaveToFile e) {
		if (e.getPlayer().level.isClientSide) {
			return;
		}

		for (final AdditionalDataBuilder<? extends Entity> data : HorseNBT.getDataList().values()) {
			final String key = data.getSource().getKey();
			if (key == null || key.isEmpty() || !data.hasProperty(AdditionalDataProperty.PERSISTANT)) {
				continue;
			}

			final IAdditionalData<?> cache = HorseNBTCache.get(e.getPlayer(), key);
			if (cache == null) {
				continue;
			}

			final CompoundNBT nbt = new CompoundNBT();
			cache.write(nbt);
			cache.save();
		}
	}

}