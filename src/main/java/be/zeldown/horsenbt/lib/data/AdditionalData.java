package be.zeldown.horsenbt.lib.data;

import be.zeldown.horsenbt.internal.common.network.HorseNBTNetwork;
import be.zeldown.horsenbt.internal.common.network.impl.SCPacketSyncHorseNBT;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.PacketDistributor;

@Getter
public abstract class AdditionalData<T extends Entity> implements IAdditionalData<T> {

	private final String key;
	private final Class<T> entityType;
	private final AdditionalDataProperty[] properties;

	private T entity;

	public AdditionalData(final String key, final Class<T> entityType, final AdditionalDataProperty... properties) {
		this.key        = key;
		this.entityType = entityType;
		this.properties = properties;
	}

	@Override
	public void init(final T entity) {
		this.entity = entity;
	}

	@Override
	public final void sync() {
		if (this.entity == null || this.entity.level.isClientSide) {
			return;
		}

		final CompoundNBT compound = new CompoundNBT();
		this.write(compound);

		if (this.hasProperty(AdditionalDataProperty.SYNCHRONIZED) && this.entity instanceof ServerPlayerEntity) {
			HorseNBTNetwork.inst().send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) this.entity), new SCPacketSyncHorseNBT(this.entity, this, true));
		}

		if (this.hasProperty(AdditionalDataProperty.SYNCHRONIZED_TRACKER)) {
			HorseNBTNetwork.inst().send(PacketDistributor.TRACKING_ENTITY.with(() -> this.entity), new SCPacketSyncHorseNBT(this.entity, this, false));
		}
	}

	@Override
	public final void sync(final ServerPlayerEntity player) {
		if (player == null || this.entity == null || this.entity.level.isClientSide) {
			return;
		}

		final CompoundNBT compound = new CompoundNBT();
		this.write(compound);

		HorseNBTNetwork.inst().send(PacketDistributor.PLAYER.with(() -> player), new SCPacketSyncHorseNBT(this.entity, this, player == this.entity));
	}

	@Override
	public final void save() {
		if (this.entity == null || this.entity.level.isClientSide) {
			return;
		}

		final CompoundNBT compound = new CompoundNBT();
		this.write(compound);
		this.entity.getPersistentData().put(this.key, compound);
	}

	public final boolean hasProperty(final AdditionalDataProperty property) {
		if (this.properties == null || this.properties.length == 0) {
			return false;
		}

		for (final AdditionalDataProperty p : this.properties) {
			if (p == property) {
				return true;
			}
		}

		return false;
	}

	public final void update() {
		this.save();
		this.sync();
	}

}