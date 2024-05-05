import io.github.stuff_stuffs.fake_world.client.api.FakeWorldClientApi;
import net.fabricmc.api.ClientModInitializer;

public class TestClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FakeWorldClientApi.registerFactory(TestCommon.BASIC, BasicClientOnlyWorld::new);
    }
}
