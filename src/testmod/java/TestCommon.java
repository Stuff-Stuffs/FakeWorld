import io.github.stuff_stuffs.fake_world.common.api.FakeWorldApi;
import io.github.stuff_stuffs.fake_world.common.api.FakeWorldDataType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;

public class TestCommon implements ModInitializer {
    public static final FakeWorldDataType<BasicFakeWorldData> BASIC = new FakeWorldDataType<>(BasicFakeWorldData.CODEC);

    @Override
    public void onInitialize() {
        Registry.register(FakeWorldApi.FAKE_WORLD_DATA_TYPE_REGISTRY, new Identifier("test", "basic"), BASIC);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("test_fake_world").executes(context -> {
            FakeWorldApi.sendToFakeWorld(context.getSource().getPlayer(), context.getSource().getWorld(), RegistryKey.of(RegistryKeys.WORLD, new Identifier("test", "test")), new BasicFakeWorldData());
            return 0;
        })));
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(CommandManager.literal("test_fake_world_leave").executes(context -> {
            FakeWorldApi.leaveFakeWorld(context.getSource().getPlayer());
            return 0;
        })));
    }
}
