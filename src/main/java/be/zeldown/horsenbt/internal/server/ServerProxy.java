package be.zeldown.horsenbt.internal.server;

import be.zeldown.horsenbt.internal.common.CommonProxy;
import be.zeldown.horsenbt.internal.server.listener.HorseNBTListener;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ServerProxy extends CommonProxy {

	@Override
	public void onInit(final FMLCommonSetupEvent event) {
		super.onInit(event);

		super.addListener(HorseNBTListener.class);
	}

}