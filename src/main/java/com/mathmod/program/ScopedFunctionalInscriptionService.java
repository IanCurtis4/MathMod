package com.mathmod.program;
import com.mathmod.knowledge.KnowledgeDefinitions;
import com.mathmod.language.ScopedProgramSource;
import com.mathmod.runes.RuneRegistry;
import com.mathmod.item.ProgrammedTalismanItem;
import java.util.List;

/** Internal source/result-bound functional inscription coordinator. */
final class ScopedFunctionalInscriptionService {
 private final RuneRegistry runes;
 private final ScopedServerCompileService compiler;
 private final java.util.function.Supplier<List<com.mathmod.kubejs.RuneMaterialDefinition>> currentMaterials;
 ScopedFunctionalInscriptionService(RuneRegistry runes){this(runes,new ScopedServerCompileService(runes),ProgramResources::materials);}
 ScopedFunctionalInscriptionService(RuneRegistry runes, ScopedServerCompileService compiler, java.util.function.Supplier<List<com.mathmod.kubejs.RuneMaterialDefinition>> currentMaterials){this.runes=runes;this.compiler=compiler;this.currentMaterials=currentMaterials;}
 ScopedCommitResult inscribe(ScopedProgramSource source, String name, ScopedCommitAuthority authority) {
  if(authority.cancellation().cancelled())return ScopedCommitResult.REQUEST_CANCELLED;
  var stack=authority.target().get(); if(stack==null||!(stack.getItem() instanceof ProgrammedTalismanItem))return ScopedCommitResult.TARGET_STALE;
  var stackCopy=stack.copy();
  var before=ScopedProgramComponentTransaction.State.capture(stack);
  var capturedKnowledge=authority.knowledge().get();
  if(capturedKnowledge==null)return ScopedCommitResult.KNOWLEDGE_STALE;
  var result=compiler.compile(new ScopedServerCompileRequest(source,capturedKnowledge,authority.cancellation()));
  if(!result.successful())return result.serviceIssues().stream().anyMatch(i->i.code()==ScopedServerCompileIssue.Code.CANCELLED)?ScopedCommitResult.REQUEST_CANCELLED:ScopedCommitResult.COMPILE_REJECTED;
  // Build every candidate value before the single immediate precommit boundary.
  var graph=result.candidate().orElseThrow(); var resources=InscriptionResourcePolicy.resourcesToPersist(
    before.hasProgram()?java.util.Optional.of(before.program()):java.util.Optional.empty(),before.hasResources()?before.resources():List.of(),graph,result.recommendations());
  final ScopedSourceEnvelope sourceEnvelope;
  try { sourceEnvelope=ScopedSourceWireCodec.encode(source); } catch (RuntimeException failure) { return ScopedCommitResult.COMMIT_FAILED; }
  String acceptedName=ProgramNames.sanitizeOptional(name);
  boolean hasName=!acceptedName.isEmpty(); boolean hasResources=!resources.isEmpty();
  var candidate=new ScopedProgramComponentTransaction.State(graph,true,sourceEnvelope,true,hasName?acceptedName:null,hasName,hasResources?resources:null,hasResources,null,false,null,false);
  if(authority.cancellation().cancelled())return ScopedCommitResult.REQUEST_CANCELLED;
  if(authority.target().get()!=stack||!(stack.getItem() instanceof ProgrammedTalismanItem))return ScopedCommitResult.TARGET_STALE;
  if(stack.getCount()!=stackCopy.getCount()||!net.minecraft.world.item.ItemStack.isSameItemSameComponents(stackCopy,stack))return ScopedCommitResult.TARGET_STALE;
  if(!ScopedProgramComponentTransaction.State.capture(stack).equals(before))return ScopedCommitResult.TARGET_STALE;
  if(runes.generation()!=result.runeGeneration())return ScopedCommitResult.REGISTRY_GENERATION_STALE;
  if(KnowledgeDefinitions.snapshot()!=result.knowledgeDefinitions()||!capturedKnowledge.equals(result.playerKnowledge())||!capturedKnowledge.equals(authority.knowledge().get()))return ScopedCommitResult.KNOWLEDGE_STALE;
  if(!List.copyOf(currentMaterials.get()).equals(result.materialDefinitions()))return ScopedCommitResult.MATERIALS_STALE;
  return ScopedProgramComponentTransaction.apply(stack,candidate)?ScopedCommitResult.SUCCESS:ScopedCommitResult.COMMIT_FAILED;
 }
}
