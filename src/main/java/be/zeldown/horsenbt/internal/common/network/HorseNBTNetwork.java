package be.zeldown.horsenbt.internal.common.network;

import be.zeldown.horsenbt.internal.Constants;
import be.zeldown.horsenbt.internal.common.network.impl.SCPacketSyncHorseNBT;
import be.zeldown.horsenbt.internal.common.network.impl.SCPacketSyncHorseNBTHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class HorseNBTNetwork {

	private static final String PROTOCOL_VERSION = "1";

	private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Constants.MOD_ID, "main"),
			() -> HorseNBTNetwork.PROTOCOL_VERSION,
			HorseNBTNetwork.PROTOCOL_VERSION::equals,
			HorseNBTNetwork.PROTOCOL_VERSION::equals
			);

	public static void init() {
		HorseNBTNetwork.INSTANCE.registerMessage(0, SCPacketSyncHorseNBT.class, SCPacketSyncHorseNBT.encode, SCPacketSyncHorseNBT::decode, SCPacketSyncHorseNBTHandler::handle);
	}

	public static SimpleChannel inst() {
		return HorseNBTNetwork.INSTANCE;
	}

}