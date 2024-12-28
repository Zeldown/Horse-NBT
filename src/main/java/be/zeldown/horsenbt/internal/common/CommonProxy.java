package be.zeldown.horsenbt.internal.common;

import be.zeldown.horsenbt.internal.ModProxy;
import be.zeldown.horsenbt.internal.common.network.HorseNBTNetwork;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommonProxy extends ModProxy {

	@Override
	public void onInit(final FMLCommonSetupEvent event) {
		HorseNBTNetwork.init();
	}

}