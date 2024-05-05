import io.github.stuff_stuffs.fake_world.common.api.FakeWorldData;
import io.github.stuff_stuffs.fake_world.common.api.FakeWorldDataType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class BasicFakeWorldData implements FakeWorldData {
    public static final PacketCodec<RegistryByteBuf, BasicFakeWorldData> CODEC = new PacketCodec<>() {
        @Override
        public BasicFakeWorldData decode(final RegistryByteBuf buf) {
            return new BasicFakeWorldData();
        }

        @Override
        public void encode(final RegistryByteBuf buf, final BasicFakeWorldData value) {

        }
    };

    @Override
    public FakeWorldDataType<?> type() {
        return TestCommon.BASIC;
    }
}
