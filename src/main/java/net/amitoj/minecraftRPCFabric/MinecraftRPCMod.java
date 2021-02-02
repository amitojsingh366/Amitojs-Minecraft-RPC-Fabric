package net.amitoj.minecraftRPCFabric;

import club.minnced.discord.rpc.*;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

import java.util.Timer;
import java.util.TimerTask;

public class MinecraftRPCMod implements ModInitializer {

	DiscordRPC lib = DiscordRPC.INSTANCE;
	String applicationId = "765845744027435019";
	String steamId = "";
	DiscordEventHandlers handlers = new DiscordEventHandlers();
	Long start_time = System.currentTimeMillis() / 1000;

	MinecraftClient mc = MinecraftClient.getInstance();

	Integer times = 0;
	Timer t = new Timer();
	@Override
	public void onInitialize() {

		handlers.ready = (user) -> System.out.println("Ready!");
		lib.Discord_Initialize(applicationId, handlers, true, steamId);

		basicPresence();
		new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				lib.Discord_RunCallbacks();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ignored) {
				}
			}
		}, "RPC-Callback-Handler").start();

		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				updatePresence();
			}
		}, 5000, 5000);
	}

	private void basicPresence() {

		DiscordRichPresence presence = new DiscordRichPresence();
		presence.startTimestamp = start_time; // epoch second
		presence.details = "In The Main Menu";
		presence.largeImageKey = "icon_720";
		presence.largeImageText = "Amitoj's Minecraft RPC";
		presence.instance = 1;
		lib.Discord_UpdatePresence(presence);

	}

	private void updatePresence() {
		if (mc.world != null) {
			times++;
			boolean issinglePlayer = mc.isInSingleplayer();
			DimensionType dimtype = mc.world.getDimension();
			Identifier dimKey = mc.world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).getId(dimtype);
			DiscordRichPresence presence = new DiscordRichPresence();
			if(mc.player!=null){
				ItemStack held_item = mc.player.getStackInHand(Hand.MAIN_HAND);
				String item_name = held_item.getName().getString();
				if (!item_name.equals("Air")) {
					presence.details = "Holding " + item_name;
				}
			}
			presence.startTimestamp = start_time;
			presence.largeImageKey = "icon_720";
			presence.largeImageText = "Amitoj's Minecraft RPC";
			presence.instance = 1;
			presence.partyId = "priv_party";
			presence.matchSecret = "abXyyz";
			presence.joinSecret = "moonSqikCklaw";
			presence.spectateSecret = "moonSqikCklawkLopalwdNq";
			if (!issinglePlayer) {
				String serverip = "";
				if (mc.getCurrentServerEntry() != null) {
					serverip = mc.getCurrentServerEntry().address.toUpperCase();
				}
				presence.state = "Multiplayer - " + serverip;
				presence.partyId = serverip;
				presence.matchSecret = serverip.toLowerCase();
			} else {
				presence.state = "Singleplayer";
				presence.partySize = 1;
				presence.partyMax = 1;
			}
			if (DimensionType.THE_NETHER_ID.equals(dimKey)) {
				presence.smallImageKey = "ghast_face";
				presence.smallImageText = "In The Nether";
			} else if (DimensionType.THE_END_ID.equals(dimKey)) {
				presence.smallImageKey = "enderman_face";
				presence.smallImageText = "In The End";
			} else {
				presence.smallImageKey = "zombie_face";
				presence.smallImageText = "In The Overworld";
			}
			lib.Discord_UpdatePresence(presence);

		} else {
			basicPresence();
		}
	}
}
