package be.zeldown.horsenbt.internal.common.network.impl;

import java.util.function.Supplier;

import be.zeldown.horsenbt.lib.HorseNBT;
import be.zeldown.horsenbt.lib.cache.HorseNBTCache;
import be.zeldown.horsenbt.lib.data.AdditionalDataBuilder;
import be.zeldown.horsenbt.lib.data.IAdditionalData;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class SCPacketSyncHorseNBTHandler {

	public static void handle(final SCPacketSyncHorseNBT message, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
				SCPacketSyncHorseNBTHandler.handleClient(message, ctx);
			});
		});
		ctx.get().setPacketHandled(true);
	}

	@OnlyIn(Dist.CLIENT)
	private static void handleClient(final SCPacketSyncHorseNBT message, final Supplier<NetworkEvent.Context> ctx) {
		final net.minecraft.client.world.ClientWorld world = net.minecraft.client.Minecraft.getInstance().level;
		Entity entity = null;
		if (message.isSelf()) {
			entity = net.minecraft.client.Minecraft.getInstance().player;
		} else {
			for (final Entity e : world.entitiesForRendering()) {
				if (e.getUUID().equals(message.getEntity())) {
					entity = e;
					break;
				}
			}
		}

		final AdditionalDataBuilder<? extends Entity> data = HorseNBT.getDataList().get(message.getKey());
		if (data == null) {
			return;
		}

		if (entity == null) {
			HorseNBTCache.raw().remove(message.getEntity().toString());
			return;
		}

		final IAdditionalData<?> cache = data.copy().getSource();
		cache.read(message.getNbt());
		HorseNBTCache.put(entity, cache);
	}

}