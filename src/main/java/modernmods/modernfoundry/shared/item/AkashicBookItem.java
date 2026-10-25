package modernmods.modernfoundry.shared.item;

/** Loaded only when Akashic Tome is installed. */
public class AkashicBookItem extends TinkerBookItem implements vazkii.akashictome.IModdedBook {
  public AkashicBookItem(Properties props, BookType type) {
    super(props, type);
  }
}
