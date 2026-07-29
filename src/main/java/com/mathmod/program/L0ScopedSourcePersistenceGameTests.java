package com.mathmod.program;

import com.mathmod.MathMod;
import com.mathmod.registry.ModDataComponents;
import com.mathmod.registry.ModItems;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import com.mathmod.language.RuneTypeExpression;
import com.mathmod.language.ScopedExpression;
import com.mathmod.language.ScopedProgramSource;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.runes.RuneType;

/** Dedicated-server read-boundary evidence; no client or network behavior is claimed. */
@GameTestHolder(MathMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class L0ScopedSourcePersistenceGameTests {
 private L0ScopedSourcePersistenceGameTests() { }
 @GameTest(template="empty") public static void validFutureAndConflictReadsNeverRewriteGraph(GameTestHelper helper) {
  ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get());
  var graph=graph(); talisman.set(ModDataComponents.PROGRAM.get(),graph);
  String json="{\"expression\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}";
  talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),new ScopedSourceEnvelope(1,json.getBytes(StandardCharsets.UTF_8))); ItemStack before=talisman.copy();
  helper.assertTrue(ScopedProgramPersistence.read(talisman).status()==ScopedSourceRead.Status.CURRENT_VALID,"valid source must be available without compiling");
  helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"read must not rewrite");
  talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),new ScopedSourceEnvelope(1,new byte[]{(byte)0xC3,(byte)0x28})); before=talisman.copy();
  helper.assertTrue(ScopedProgramPersistence.read(talisman).status()==ScopedSourceRead.Status.CURRENT_UNREADABLE,"unreadable current payload is retained");
  helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"unreadable read must not rewrite");
  talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),new ScopedSourceEnvelope(77,new byte[]{1,2,3})); before=talisman.copy();
  helper.assertTrue(ScopedProgramPersistence.read(talisman).status()==ScopedSourceRead.Status.UNSUPPORTED_VERSION,"future payload is preserved");
  helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"future read must not rewrite");
  talisman.set(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get(),GuidedWorkspaceState.create("x",List.of()));
  helper.assertTrue(ScopedProgramPersistence.read(talisman).status()==ScopedSourceRead.Status.CONFLICT,"physical Guided/source coexistence wins before validity");
  helper.assertTrue(ProgramStorage.get(talisman).orElseThrow().equals(graph),"source read must retain authoritative graph"); helper.succeed();
 }
 @GameTest(template="empty") public static void existingInscriptionRoutesClearScopedSourceAtomically(GameTestHelper helper) {
  var graph=graph(); ItemStack graphOnly=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); graphOnly.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),new ScopedSourceEnvelope(77,new byte[]{4}));
  helper.assertTrue(ProgramStorage.saveValidated(graphOnly,graph).valid(),"graph-only inscription must validate");
  helper.assertTrue(graphOnly.get(ModDataComponents.PROGRAM_SCOPED_SOURCE.get())==null,"graph-only inscription must clear scoped source");
  ItemStack guided=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); guided.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),new ScopedSourceEnvelope(77,new byte[]{5}));
  helper.assertTrue(ProgramStorage.saveValidatedCustom(guided,graph,"guided",List.of(CustomSpellInvocation.defaults(CustomSpellAction.SELF),CustomSpellInvocation.defaults(CustomSpellAction.UP_VECTOR),CustomSpellInvocation.defaults(CustomSpellAction.PUSH_SELF))).valid(),"Guided inscription must validate");
  helper.assertTrue(guided.get(ModDataComponents.PROGRAM_SCOPED_SOURCE.get())==null,"Guided inscription must clear scoped source"); helper.succeed();
 }
 @GameTest(template="empty") public static void injectedBeforeAndAfterComponentFailuresRestoreCompleteSnapshot(GameTestHelper helper) {
  for(int failure=0;failure<6;failure++) for(ScopedProgramComponentTransaction.Phase phase:ScopedProgramComponentTransaction.Phase.values()) { final int injected=failure; ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); var graph=graph();
   talisman.set(ModDataComponents.PROGRAM.get(),graph);talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),new ScopedSourceEnvelope(77,new byte[]{(byte)failure}));talisman.set(ModDataComponents.PROGRAM_NAME.get(),"old");talisman.set(ModDataComponents.PROGRAM_RESOURCES.get(),List.of(new ResourceSelection("feather",1)));talisman.set(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get(),GuidedWorkspaceState.create("old",List.of()));talisman.set(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get(),List.of("mathmod:self"));
   var before=ScopedProgramComponentTransaction.State.capture(talisman); boolean committed=ScopedProgramComponentTransaction.applyForTests(talisman,new ScopedProgramComponentTransaction.State(null,false,null,false,null,false,null,false,null,false,null,false),(index,at)->{if(index==injected&&at==phase)throw new IllegalStateException("injected");});
   helper.assertFalse(committed,"injected component failure must reject transaction "+failure+phase);helper.assertTrue(before.equals(ScopedProgramComponentTransaction.State.capture(talisman)),"rollback must restore exact six-component snapshot and source bytes "+failure+phase);
  } helper.succeed();
 }
 @GameTest(template="empty") public static void staleLiveKnowledgeMutatesNothing(GameTestHelper helper) {
  ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); talisman.set(ModDataComponents.PROGRAM.get(),graph()); ItemStack before=talisman.copy();
  java.util.concurrent.atomic.AtomicInteger calls=new java.util.concurrent.atomic.AtomicInteger();
  MathModRuneBootstrap.bootstrap();
  var outcome=new ScopedFunctionalInscriptionService(MathModRuneBootstrap.registry()).inscribe(functionalPush(),"stale",new ScopedCommitAuthority(()->talisman,()->calls.getAndIncrement()==0?com.mathmod.knowledge.PlayerKnowledge.empty():com.mathmod.knowledge.PlayerKnowledge.empty().withSchemaVersion(4),()->false));
  helper.assertTrue(outcome==ScopedCommitResult.KNOWLEDGE_STALE,"knowledge changed after compilation must be stale");
  helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"stale knowledge must not mutate target"); helper.succeed();
 }
 @GameTest(template="empty") public static void cancelledFunctionalRequestMutatesNothing(GameTestHelper helper) {
  ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get());talisman.set(ModDataComponents.PROGRAM.get(),graph());ItemStack before=talisman.copy();
  var source=new com.mathmod.language.ScopedProgramSource(1,new com.mathmod.language.ScopedExpression.Literal(com.mathmod.language.RuneTypeExpression.value(com.mathmod.runes.RuneType.NUMBER),"1"),com.mathmod.language.RuneTypeExpression.value(com.mathmod.runes.RuneType.NUMBER),1);
  var outcome=new ScopedFunctionalInscriptionService(com.mathmod.runes.MathModRuneBootstrap.registry()).inscribe(source,"",new ScopedCommitAuthority(()->talisman,com.mathmod.knowledge.PlayerKnowledge.empty(),()->true));
  helper.assertTrue(outcome==ScopedCommitResult.REQUEST_CANCELLED,"cancelled request must fail before compile/commit");helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"cancelled request must not mutate target");helper.succeed();
 }
 @GameTest(template="empty") public static void cancellationAfterPureCompilationMutatesNothing(GameTestHelper helper) {
  ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); talisman.set(ModDataComponents.PROGRAM.get(),graph()); ItemStack before=talisman.copy();
  java.util.concurrent.atomic.AtomicInteger probes=new java.util.concurrent.atomic.AtomicInteger(); MathModRuneBootstrap.bootstrap();
  var outcome=new ScopedFunctionalInscriptionService(MathModRuneBootstrap.registry()).inscribe(functionalPush(),"",new ScopedCommitAuthority(()->talisman,com.mathmod.knowledge.PlayerKnowledge.empty(),()->probes.incrementAndGet()>=3));
  helper.assertTrue(outcome==ScopedCommitResult.REQUEST_CANCELLED,"cancellation after the compiler entry check must reject before commit");
  helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"post-compilation cancellation must not mutate target"); helper.succeed();
 }
 @GameTest(template="empty") public static void functionalSuccessWritesCompleteStateAndClearsGuided(GameTestHelper helper) {
  ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get());
  talisman.set(ModDataComponents.PROGRAM.get(),graph());
  talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),new ScopedSourceEnvelope(77,new byte[]{6,7}));
  talisman.set(ModDataComponents.PROGRAM_NAME.get(),"old");
  talisman.set(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get(),GuidedWorkspaceState.create("old",List.of()));
  talisman.set(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get(),List.of("mathmod:self_player"));
  MathModRuneBootstrap.bootstrap();
  var outcome=new ScopedFunctionalInscriptionService(MathModRuneBootstrap.registry()).inscribe(functionalPush(),"functional",new ScopedCommitAuthority(()->talisman,com.mathmod.knowledge.PlayerKnowledge.empty(),()->false));
  helper.assertTrue(outcome==ScopedCommitResult.SUCCESS,"functional coordinator must commit admitted source");
  helper.assertTrue(talisman.get(ModDataComponents.PROGRAM.get())!=null,"functional success writes graph");
  var read=ScopedProgramPersistence.read(talisman);
  helper.assertTrue(read.status()==ScopedSourceRead.Status.CURRENT_VALID,"functional success writes canonical readable source");
  helper.assertTrue("functional".equals(talisman.get(ModDataComponents.PROGRAM_NAME.get())),"functional success writes accepted shared name");
  helper.assertTrue(talisman.get(ModDataComponents.PROGRAM_RESOURCES.get())!=null,"functional success writes resources");
  helper.assertTrue(talisman.get(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get())==null&&talisman.get(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get())==null,"functional success clears both Guided representations atomically"); helper.succeed();
 }
 @GameTest(template="empty") public static void staleFunctionalTargetMutatesNothing(GameTestHelper helper) {
  ItemStack original=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); original.set(ModDataComponents.PROGRAM.get(),graph()); ItemStack before=original.copy();
  ItemStack replacement=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); java.util.concurrent.atomic.AtomicInteger calls=new java.util.concurrent.atomic.AtomicInteger();
  MathModRuneBootstrap.bootstrap();
  var outcome=new ScopedFunctionalInscriptionService(MathModRuneBootstrap.registry()).inscribe(functionalPush(),"stale",new ScopedCommitAuthority(()->calls.getAndIncrement()==0?original:replacement,com.mathmod.knowledge.PlayerKnowledge.empty(),()->false));
  helper.assertTrue(outcome==ScopedCommitResult.TARGET_STALE,"a replacement target must be rejected after compile");
  helper.assertTrue(ItemStack.isSameItemSameComponents(before,original),"stale target rejection must not mutate original"); helper.succeed();
 }
 @GameTest(template="empty") public static void staleRuneGenerationMutatesNothing(GameTestHelper helper) {
  ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); talisman.set(ModDataComponents.PROGRAM.get(),graph()); ItemStack before=talisman.copy();
  MathModRuneBootstrap.bootstrap(); com.mathmod.runes.RuneRegistry isolated=new com.mathmod.runes.RuneRegistry(); MathModRuneBootstrap.registry().definitions().forEach(isolated::register);
  java.util.concurrent.atomic.AtomicInteger calls=new java.util.concurrent.atomic.AtomicInteger();
  var outcome=new ScopedFunctionalInscriptionService(isolated).inscribe(functionalPush(),"",new ScopedCommitAuthority(()->{if(calls.getAndIncrement()>0)isolated.setEnabled("mathmod:push_self",false);return talisman;},com.mathmod.knowledge.PlayerKnowledge.empty(),()->false));
  helper.assertTrue(outcome==ScopedCommitResult.REGISTRY_GENERATION_STALE,"generation changed after compilation must be stale");
  helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"stale generation must not mutate target"); helper.succeed();
 }
 @GameTest(template="empty") public static void staleMaterialCatalogMutatesNothing(GameTestHelper helper) {
  ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); talisman.set(ModDataComponents.PROGRAM.get(),graph()); ItemStack before=talisman.copy(); MathModRuneBootstrap.bootstrap();
  java.util.concurrent.atomic.AtomicInteger reads=new java.util.concurrent.atomic.AtomicInteger(); java.util.List<com.mathmod.kubejs.RuneMaterialDefinition> original=ProgramResources.materials();
  java.util.function.Supplier<java.util.List<com.mathmod.kubejs.RuneMaterialDefinition>> live=()->reads.getAndIncrement()==0?original:java.util.List.of();
  var runes=MathModRuneBootstrap.registry(); var compiler=new ScopedServerCompileService(runes,live,com.mathmod.knowledge.KnowledgeDefinitions::snapshot);
  var outcome=new ScopedFunctionalInscriptionService(runes,compiler,live).inscribe(functionalPush(),"",new ScopedCommitAuthority(()->talisman,com.mathmod.knowledge.PlayerKnowledge.empty(),()->false));
  helper.assertTrue(outcome==ScopedCommitResult.MATERIALS_STALE,"material catalog changed after compilation must be stale"); helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"stale materials must not mutate target"); helper.succeed();
 }
 @GameTest(template="empty") public static void explicitClearRemovesCompleteProgramState(GameTestHelper helper) {
  ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); talisman.set(ModDataComponents.PROGRAM.get(),graph());
  talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),new ScopedSourceEnvelope(1,new byte[]{1})); talisman.set(ModDataComponents.PROGRAM_NAME.get(),"name");
  talisman.set(ModDataComponents.PROGRAM_RESOURCES.get(),List.of(new ResourceSelection("feather",1))); talisman.set(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get(),GuidedWorkspaceState.create("name",List.of())); talisman.set(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get(),List.of("mathmod:self_player"));
  helper.assertTrue(ProgramStorage.clear(talisman),"clear must report removed program");
  helper.assertTrue(ScopedProgramComponentTransaction.State.capture(talisman).equals(new ScopedProgramComponentTransaction.State(null,false,null,false,null,false,null,false,null,false,null,false)),"clear must atomically remove all six components"); helper.succeed();
 }
 @GameTest(template="empty") public static void storageCommitFailureIsNeverReportedValid(GameTestHelper helper) {
  ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); talisman.set(ModDataComponents.PROGRAM.get(),graph()); ItemStack before=talisman.copy();
  ScopedProgramComponentTransaction.setTestInjector((index,phase)->{if(index==0&&phase==ScopedProgramComponentTransaction.Phase.BEFORE)throw new IllegalStateException("injected");});
  try { helper.assertFalse(ProgramStorage.saveValidated(talisman,graph()).valid(),"failed component transaction must be invalid"); }
  finally { ScopedProgramComponentTransaction.setTestInjector(null); }
  helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"failed storage commit must restore snapshot"); helper.succeed();
 }
 @GameTest(template="empty") public static void itemCodecRoundTripRetainsOpaqueUnreadableAndFutureBytes(GameTestHelper helper) {
  for(ScopedSourceEnvelope source:List.of(new ScopedSourceEnvelope(1,new byte[]{(byte)0xC3,(byte)0x28}),new ScopedSourceEnvelope(-9,new byte[]{0,1,(byte)0xFF}))) {
   ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),source);
   var ops=RegistryOps.create(NbtOps.INSTANCE,helper.getLevel().registryAccess());
   ItemStack decoded=ItemStack.CODEC.parse(ops,ItemStack.CODEC.encodeStart(ops,talisman).getOrThrow()).getOrThrow();
   ScopedSourceEnvelope restored=decoded.get(ModDataComponents.PROGRAM_SCOPED_SOURCE.get());
   helper.assertTrue(source.equals(restored),"server item codec must retain opaque envelope bytes exactly");
  } helper.succeed();
 }
 @GameTest(template="empty") public static void completeReadStateMatrixNeverMutatesOrCompiles(GameTestHelper helper) {
  ItemStack talisman=new ItemStack(ModItems.PROGRAMMED_TALISMAN.get()); talisman.set(ModDataComponents.PROGRAM.get(),graph());
  helper.assertTrue(ScopedProgramPersistence.read(talisman).status()==ScopedSourceRead.Status.ABSENT,"real stack with neither source nor Guided is absent");
  java.util.List<ScopedSourceEnvelope> sources=List.of(new ScopedSourceEnvelope(1,"{\"expression\":{\"kind\":\"literal\",\"rune_type\":\"number\",\"value\":\"1\"},\"result_type\":{\"kind\":\"value\",\"rune_type\":\"number\"},\"budget_limit\":1}".getBytes(StandardCharsets.UTF_8)),new ScopedSourceEnvelope(1,new byte[]{(byte)0xc3,(byte)0x28}),new ScopedSourceEnvelope(9,new byte[]{1}));
  ScopedSourceRead.Status[] expected={ScopedSourceRead.Status.CURRENT_VALID,ScopedSourceRead.Status.CURRENT_UNREADABLE,ScopedSourceRead.Status.UNSUPPORTED_VERSION};
  for(int sourceIndex=0;sourceIndex<sources.size();sourceIndex++) { ScopedSourceEnvelope source=sources.get(sourceIndex);
   talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),source); ItemStack before=talisman.copy();
   helper.assertTrue(ScopedProgramPersistence.read(talisman).status()==expected[sourceIndex],"source-only must use exact classification"); helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"read must not compile, migrate, or mutate");
   talisman.set(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get(),GuidedWorkspaceState.create("guided",List.of())); before=talisman.copy();
   helper.assertTrue(ScopedProgramPersistence.read(talisman).status()==ScopedSourceRead.Status.CONFLICT,"physical Guided/source conflict precedes every source validity state"); helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"conflict read must not mutate"); talisman.remove(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get());
   talisman.set(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get(),List.of("future:legacy")); before=talisman.copy(); helper.assertTrue(ScopedProgramPersistence.read(talisman).status()==ScopedSourceRead.Status.CONFLICT,"legacy Guided actions are physical conflict"); helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"legacy conflict read must not mutate"); talisman.remove(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get());
   talisman.set(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get(),new GuidedWorkspaceState(1,"guided",List.of("future:unreplayable"))); before=talisman.copy(); helper.assertTrue(ScopedProgramPersistence.read(talisman).status()==ScopedSourceRead.Status.CONFLICT,"unreplayable Guided state is still physical conflict"); helper.assertTrue(ItemStack.isSameItemSameComponents(before,talisman),"unreplayable conflict read must not mutate"); talisman.remove(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get());
  }
  talisman.set(ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),sources.get(0)); ItemStack mismatch=talisman.copy(); helper.assertTrue(ScopedProgramPersistence.read(talisman).status()==ScopedSourceRead.Status.CURRENT_VALID,"graph/source mismatch is not diagnosed during read"); helper.assertTrue(ProgramStorage.get(talisman).orElseThrow().equals(graph()),"graph remains executable authority"); helper.assertTrue(ItemStack.isSameItemSameComponents(mismatch,talisman),"mismatch read neither compiles nor repairs"); helper.succeed();
 }
 private static ScopedProgramSource functionalPush() {
  var number=RuneTypeExpression.value(RuneType.NUMBER);
  var player=new ScopedExpression.RuneCall("mathmod:self_player",List.of());
  var x=new ScopedExpression.Literal(number,"0"); var y=new ScopedExpression.Literal(number,"0.35"); var z=new ScopedExpression.Literal(number,"0");
  var vector=new ScopedExpression.RuneCall("mathmod:vector_from_numbers",List.of(new ScopedExpression.Argument("x",x),new ScopedExpression.Argument("y",y),new ScopedExpression.Argument("z",z)));
  var push=new ScopedExpression.RuneCall("mathmod:push_self",List.of(new ScopedExpression.Argument("player",player),new ScopedExpression.Argument("vector",vector)));
  return new ScopedProgramSource(1,push,RuneTypeExpression.value(RuneType.UNIT),16);
 }
 private static com.mathmod.runes.ProgramGraph graph(){ CustomSpellWorkspace workspace=new CustomSpellWorkspace();workspace.loadInvocations(List.of(CustomSpellInvocation.defaults(CustomSpellAction.SELF),CustomSpellInvocation.defaults(CustomSpellAction.UP_VECTOR),CustomSpellInvocation.defaults(CustomSpellAction.PUSH_SELF)));return workspace.toGraph(); }
}
