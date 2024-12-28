package be.zeldown.horsenbt.internal;

import java.io.File;

import be.zeldown.horsenbt.internal.client.ClientProxy;
import be.zeldown.horsenbt.internal.common.CommonProxy;
import be.zeldown.horsenbt.internal.server.ServerProxy;
import lombok.Getter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Getter
@Mod(Constants.MOD_ID)
public class HorseNBTMod {

	private static HorseNBTMod instance;

	private CommonProxy proxy;
	private File configDir;

	public HorseNBTMod() {
		HorseNBTMod.instance = this;

		MinecraftForge.EVENT_BUS.register(this);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInit);
	}

	private void onInit(final FMLCommonSetupEvent event) {
		this.configDir = new File(new File(FMLLoader.getGamePath().toString(), "config"), Constants.MOD_ID);
		if (!this.configDir.exists() && !this.configDir.mkdirs()) {
			throw new RuntimeException("Failed to create config directory for " + Constants.MOD_ID);
		}

		this.build();
		this.proxy.onInit(event);

		MinecraftForge.EVENT_BUS.register(this.proxy);
	}

	private void build() {
		if (this.proxy == null) {
			this.proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
		}
	}

	public CommonProxy getProxy() {
		return this.proxy;
	}

	@OnlyIn(Dist.CLIENT)
	public ClientProxy getClient() {
		return (ClientProxy) this.proxy;
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	public ServerProxy getServer() {
		return (ServerProxy) this.proxy;
	}

	public static HorseNBTMod getInstance() {
		return HorseNBTMod.instance;
	}

}
