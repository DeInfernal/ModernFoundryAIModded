package modernmods.modernfoundry.library.tools.definition.module.aoe;

/** Box and conditional/circular harvest definitions share the same per-tool mode UI. */
public interface SelectableAOEIterator extends AreaOfEffectIterator {
  BoxAOEIterator selectionBox();
}
