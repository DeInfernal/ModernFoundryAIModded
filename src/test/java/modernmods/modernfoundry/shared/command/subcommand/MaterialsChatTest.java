package modernmods.modernfoundry.shared.command.subcommand;

import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import modernmods.modernfoundry.test.BaseMcTest;
import modernmods.modernfoundry.tools.stats.StatlessMaterialStats;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class MaterialsChatTest extends BaseMcTest {
  @Test
  void missingArrowHeadStatsEncodeAsSystemChat() {
    var id = new modernmods.modernfoundry.library.materials.stats.MaterialStatsId(modernmods.modernfoundry.TConstruct.getResource("arrow_head"));
    var error = MaterialsCommand.INVALID_STATS.create(id, Component.translatable("material.modernfoundry.iron"));
    var text = (Component)error.getRawMessage();
    var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
    try {
      ClientboundSystemChatPacket.STREAM_CODEC.encode(buf, new ClientboundSystemChatPacket(text, false));
      var decoded = ClientboundSystemChatPacket.STREAM_CODEC.decode(buf).content();
      var translated = (net.minecraft.network.chat.contents.TranslatableContents)decoded.getContents();
      assertThat(translated.getArgs()[0]).isEqualTo("modernfoundry:arrow_head");
    } finally { buf.release(); }
  }

  @Test
  void arrowHeadIronStatsEncodeAsSystemChat() {
    var text = MaterialsCommand.statsMessage(StatlessMaterialStats.ARROW_HEAD, Component.translatable("material.modernfoundry.iron"), 1f);
    var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));
    try {
      ClientboundSystemChatPacket.STREAM_CODEC.encode(buf, new ClientboundSystemChatPacket(text, false));
      var decoded = ClientboundSystemChatPacket.STREAM_CODEC.decode(buf);
      assertThat(decoded.content().getString()).isEqualTo(text.getString());
    } finally { buf.release(); }
  }
}
