package be.zeldown.horsenbt.internal.common.network.impl;

import java.util.UUID;
import java.util.function.BiConsumer;

import be.zeldown.horsenbt.lib.data.IAdditionalData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SCPacketSyncHorseNBT {

	public static BiConsumer<SCPacketSyncHorseNBT, PacketBuffer> encode = (packet, buffer) -> {
		buffer.writeBoolean(packet.self);
		buffer.writeUUID(packet.entity);
		buffer.writeUtf(packet.key);
		buffer.writeNbt(packet.nbt);
	};

	private boolean self;
	private UUID entity;
	private String key;
	private CompoundNBT nbt;

	public SCPacketSyncHorseNBT(final Entity entity, final IAdditionalData<?> data, final boolean self) {
		this.self = self;
		this.entity = entity.getUUID();
		this.key = data.getKey();
		this.nbt = new CompoundNBT();
		data.write(this.nbt);
	}

	public static SCPacketSyncHorseNBT decode(final PacketBuffer buffer) {
		final boolean self = buffer.readBoolean();
		final UUID entity = buffer.readUUID();
		final String key = buffer.readUtf();
		final CompoundNBT nbt = buffer.readNbt();
		return new SCPacketSyncHorseNBT(self, entity, key, nbt);
	}

}