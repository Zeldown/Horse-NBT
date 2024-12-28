package be.zeldown.horsenbt.lib.data;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

public interface IAdditionalData<T extends Entity> {

	void init(final T entity);
	void destroy(final T entity);
	void read(final CompoundNBT nbt);
	void write(final CompoundNBT nbt);

	void sync();
	void sync(final ServerPlayerEntity player);
	void save();

	String getKey();
	Class<T> getEntityType();
	AdditionalDataProperty[] getProperties();

}