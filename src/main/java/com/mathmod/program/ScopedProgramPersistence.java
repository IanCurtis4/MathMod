package com.mathmod.program;
import com.mathmod.registry.ModDataComponents;
import net.minecraft.world.item.ItemStack;
/** Read-only optional-source boundary; it never compiles, repairs or writes. */
final class ScopedProgramPersistence {
 private ScopedProgramPersistence(){}
 static ScopedSourceRead read(ItemStack stack){
  if(stack==null)return ScopedSourceRead.absent();
  ScopedSourceEnvelope envelope=stack.get(ModDataComponents.PROGRAM_SCOPED_SOURCE.get());
  if(envelope==null)return ScopedSourceRead.absent();
  if(stack.has(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get())||stack.has(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get()))return ScopedSourceRead.conflict(envelope);
  if(envelope.schemaVersion()!=1)return ScopedSourceRead.unsupported(envelope);
  return ScopedSourceWireCodec.decode(envelope);
 }
}
