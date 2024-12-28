package be.zeldown.horsenbt.lib.data;

import java.util.Objects;

import lombok.Getter;
import net.minecraft.entity.Entity;

@Getter
public class AdditionalDataBuilder<T extends Entity> {

	private final IAdditionalData<T> source;

	public AdditionalDataBuilder(final Class<? extends IAdditionalData<T>> source) {
		try {
			if (source.getConstructor() == null) {
				throw new IllegalArgumentException("The class " + source.getClass().getName() + " must have a default constructor");
			}

			this.source = source.getConstructor().newInstance();
		} catch (final Exception e) {
			throw new IllegalArgumentException("The class " + source.getClass().getName() + " must have a default constructor", e);
		}
	}

	public boolean hasProperty(final AdditionalDataProperty property) {
		if (this.source instanceof AdditionalData<?>) {
			return ((AdditionalData<?>) this.source).hasProperty(property);
		}

		if (this.source.getProperties() == null || this.source.getProperties().length == 0) {
			return false;
		}

		for (final AdditionalDataProperty p : this.source.getProperties()) {
			if (p == property) {
				return true;
			}
		}

		return false;
	}

	public boolean isApplicable(final Class<? extends Entity> clazz) {
		return this.source.getEntityType().isAssignableFrom(clazz);
	}

	@SuppressWarnings("unchecked")
	public AdditionalDataBuilder<T> copy() {
		return new AdditionalDataBuilder<>((Class<? extends IAdditionalData<T>>) this.source.getClass());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.source.getKey());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}

		return Objects.equals(this.source.getKey(), ((AdditionalDataBuilder<?>) obj).source.getKey());
	}

}