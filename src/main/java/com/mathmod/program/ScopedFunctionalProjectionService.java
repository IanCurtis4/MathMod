package com.mathmod.program;

import com.mathmod.item.ProgrammedTalismanItem;
import com.mathmod.knowledge.KnowledgeDefinitionSnapshot;
import com.mathmod.knowledge.KnowledgeDefinitions;
import com.mathmod.knowledge.KnowledgeService;
import com.mathmod.knowledge.PlayerKnowledge;
import com.mathmod.kubejs.RuneMaterialDefinition;
import com.mathmod.language.RuneTypeExpression;
import com.mathmod.language.ScopedExpression;
import com.mathmod.language.ScopedProgramSource;
import com.mathmod.runes.MathModRuneBootstrap;
import com.mathmod.runes.ProgramGraph;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Server-only assembler for the immutable menu snapshot. It never writes the item or source. */
public final class ScopedFunctionalProjectionService {
    private ScopedFunctionalProjectionService() { }

    /** Captures, builds, and finally binds a projection to one server menu opening. */
    public static ScopedFunctionalProjection openingSnapshot(ServerPlayer player, InteractionHand hand) {
        return openingSnapshot(player, hand, () -> { });
    }

    static ScopedFunctionalProjection openingSnapshot(ServerPlayer player, InteractionHand hand, Runnable beforeFinalRecheck) {
        return openingSnapshot(player, hand, () -> { }, beforeFinalRecheck);
    }

    static ScopedFunctionalProjection openingSnapshot(ServerPlayer player, InteractionHand hand,
                                                       Runnable beforeCompile, Runnable beforeFinalRecheck) {
        ItemStack captured = player.getItemInHand(hand);
        ItemStack capturedContents = captured.copy();
        MathModRuneBootstrap.bootstrap();
        AuthoritySnapshot authority = liveAuthorities(player);

        ScopedFunctionalProjection candidate;
        try {
            candidate = build(captured, authority.knowledge(), beforeCompile);
        } catch (RuntimeException ignored) {
            return ScopedFunctionalProjection.unavailable();
        }

        beforeFinalRecheck.run();

        return acceptCandidate(captured, capturedContents, authority, candidate,
                captured.getItem() instanceof ProgrammedTalismanItem, player.getItemInHand(hand), liveAuthorities(player));
    }

    /** One shared final gate for production and transformed-runtime authority vectors. */
    static ScopedFunctionalProjection acceptCandidate(ItemStack captured, ItemStack capturedContents,
                                                      AuthoritySnapshot capturedAuthority, ScopedFunctionalProjection candidate,
                                                      boolean eligible, ItemStack currentTarget, AuthoritySnapshot currentAuthority) {
        boolean unchangedTarget = currentTarget == captured && ItemStack.isSameItemSameComponents(captured, capturedContents);
        return eligible && unchangedTarget && capturedAuthority.equals(currentAuthority)
                ? candidate : ScopedFunctionalProjection.unavailable(candidate.graphState());
    }

    static AuthoritySnapshot liveAuthorities(ServerPlayer player) {
        MathModRuneBootstrap.bootstrap();
        return new AuthoritySnapshot(KnowledgeService.get(player), MathModRuneBootstrap.registry().generation(),
                KnowledgeDefinitions.snapshot(), List.copyOf(ProgramResources.materials()));
    }

    record AuthoritySnapshot(PlayerKnowledge knowledge, long runeGeneration,
                             KnowledgeDefinitionSnapshot definitions, List<RuneMaterialDefinition> materials) { }

    public static ScopedFunctionalProjection build(ItemStack stack, PlayerKnowledge knowledge) {
        return build(stack, knowledge, () -> { });
    }

    /** Test seam counts the one accepted compile call without replacing the production compiler. */
    static ScopedFunctionalProjection build(ItemStack stack, PlayerKnowledge knowledge, Runnable beforeCompile) {
        ScopedSourceRead read=ScopedProgramPersistence.read(stack);
        ProgramGraph graph=ProgramStorage.get(stack).orElse(null);
        ScopedFunctionalProjection.GraphState graphState=graph==null?ScopedFunctionalProjection.GraphState.ABSENT:ScopedFunctionalProjection.GraphState.PRESENT;
        ScopedFunctionalProjection.SourceState sourceState=sourceState(read.status());
        if(read.status()!=ScopedSourceRead.Status.CURRENT_VALID) return new ScopedFunctionalProjection(1,sourceState,ScopedFunctionalProjection.AttemptState.NOT_RUN,graphState,ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,List.of(),List.of(),diagnostics(read),0);
        ScopedProgramSource source=read.source().orElseThrow(); List<ScopedFunctionalProjection.Row> authored=rows(source,false);
        MathModRuneBootstrap.bootstrap();
        beforeCompile.run();
        ScopedServerCompileResult result=new ScopedServerCompileService(MathModRuneBootstrap.registry()).compile(new ScopedServerCompileRequest(source,knowledge,ScopedCompileCancellation.NEVER));
        if(!result.successful()) {
            boolean language=!result.languageIssues().isEmpty();
            boolean authorityStale=result.serviceIssues().stream().anyMatch(issue -> issue.code()==ScopedServerCompileIssue.Code.REGISTRY_GENERATION_STALE);
            return new ScopedFunctionalProjection(1,sourceState,authorityStale?ScopedFunctionalProjection.AttemptState.AUTHORITY_STALE:language?ScopedFunctionalProjection.AttemptState.LANGUAGE_REJECTED:ScopedFunctionalProjection.AttemptState.ADMISSION_REJECTED,graphState,ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE,authored,List.of(),compileDiagnostics(result),result.chargedSteps());
        }
        ScopedFunctionalProjection.GraphRelation relation=graph==null?ScopedFunctionalProjection.GraphRelation.NOT_COMPARABLE:graph.equals(result.candidate().orElseThrow())?ScopedFunctionalProjection.GraphRelation.MATCH:ScopedFunctionalProjection.GraphRelation.MISMATCH;
        List<ScopedFunctionalProjection.Diagnostic> diagnostics=relation==ScopedFunctionalProjection.GraphRelation.MISMATCH?List.of(new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.MISMATCH,ScopedFunctionalProjection.Code.MISMATCH,"$")):List.of();
        return new ScopedFunctionalProjection(1,sourceState,ScopedFunctionalProjection.AttemptState.SUCCESS,graphState,relation,authored,rows(source,true),diagnostics,result.chargedSteps());
    }
    private static ScopedFunctionalProjection.SourceState sourceState(ScopedSourceRead.Status state) { return switch(state){case ABSENT->ScopedFunctionalProjection.SourceState.ABSENT;case CURRENT_VALID->ScopedFunctionalProjection.SourceState.CURRENT_VALID;case CURRENT_UNREADABLE,INVALID_ENVELOPE->ScopedFunctionalProjection.SourceState.CURRENT_UNREADABLE;case UNSUPPORTED_VERSION->ScopedFunctionalProjection.SourceState.UNSUPPORTED_VERSION;case CONFLICT->ScopedFunctionalProjection.SourceState.CONFLICT;}; }
    private static List<ScopedFunctionalProjection.Diagnostic> diagnostics(ScopedSourceRead read) { return switch(read.status()){case CONFLICT->List.of(new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.PERSISTENCE,ScopedFunctionalProjection.Code.CONFLICT,"$"));case CURRENT_UNREADABLE,INVALID_ENVELOPE->List.of(new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.PERSISTENCE,ScopedFunctionalProjection.Code.UNREADABLE,"$"));case UNSUPPORTED_VERSION->List.of(new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.PERSISTENCE,ScopedFunctionalProjection.Code.UNSUPPORTED,"$"));default->List.of();}; }
    private static List<ScopedFunctionalProjection.Diagnostic> compileDiagnostics(ScopedServerCompileResult result) { List<ScopedFunctionalProjection.Diagnostic> values=new ArrayList<>(); result.languageIssues().forEach(issue->values.add(new ScopedFunctionalProjection.Diagnostic(ScopedFunctionalProjection.Phase.LANGUAGE,ScopedFunctionalProjection.Code.LANGUAGE_REJECTED,issue.path()))); result.serviceIssues().forEach(issue->values.add(new ScopedFunctionalProjection.Diagnostic(issue.code()==ScopedServerCompileIssue.Code.REGISTRY_GENERATION_STALE?ScopedFunctionalProjection.Phase.STALE:ScopedFunctionalProjection.Phase.ADMISSION,issue.code()==ScopedServerCompileIssue.Code.REGISTRY_GENERATION_STALE?ScopedFunctionalProjection.Code.STALE:ScopedFunctionalProjection.Code.ADMISSION_REJECTED,issue.path()))); return List.copyOf(values); }
    private static List<ScopedFunctionalProjection.Row> rows(ScopedProgramSource source,boolean checked) { List<ScopedFunctionalProjection.Row> out=new ArrayList<>(); collect(source.expression(),checked,"$",0,out); add(out,new ScopedFunctionalProjection.Row("$.result",ScopedFunctionalProjection.RowKind.RESULT,source.resultType().type().id(),"",-1,0)); return List.copyOf(out); }
    private static void collect(ScopedExpression e,boolean checked,String path,int depth,List<ScopedFunctionalProjection.Row> out) {
        if(e instanceof ScopedExpression.Literal x) add(out,new ScopedFunctionalProjection.Row(path,ScopedFunctionalProjection.RowKind.LITERAL,x.encodedValue(),x.type().type().id(),-1,depth));
        else if(e instanceof ScopedExpression.ParameterReference x) add(out,new ScopedFunctionalProjection.Row(path,ScopedFunctionalProjection.RowKind.PARAMETER_REFERENCE,checked?"#"+x.deBruijnIndex():"parameter", "",x.deBruijnIndex(),depth));
        else if(e instanceof ScopedExpression.RuneCall x) { add(out,new ScopedFunctionalProjection.Row(path,ScopedFunctionalProjection.RowKind.RUNE_CALL,x.runeId(),"",-1,depth)); for(int i=0;i<x.arguments().size();i++){var a=x.arguments().get(i);add(out,new ScopedFunctionalProjection.Row(path+".arguments["+i+"]",ScopedFunctionalProjection.RowKind.RUNE_ARGUMENT,a.inputName(),"",-1,depth+1));collect(a.expression(),checked,path+".arguments["+i+"].expression",depth+1,out);}}
        else if(e instanceof ScopedExpression.Lambda x) { add(out,new ScopedFunctionalProjection.Row(path,ScopedFunctionalProjection.RowKind.LAMBDA,checked?"#binder":x.nameHint(),typeToken(x.parameterType()),-1,depth));collect(x.body(),checked,path+".body",depth+1,out);}
        else if(e instanceof ScopedExpression.Application x) { add(out,new ScopedFunctionalProjection.Row(path,ScopedFunctionalProjection.RowKind.APPLICATION,"","",-1,depth));collect(x.function(),checked,path+".function",depth+1,out);collect(x.argument(),checked,path+".argument",depth+1,out);}
        else { ScopedExpression.Let x=(ScopedExpression.Let)e;add(out,new ScopedFunctionalProjection.Row(path,ScopedFunctionalProjection.RowKind.LET,checked?"#binder":x.nameHint(),"",-1,depth));collect(x.value(),checked,path+".value",depth+1,out);collect(x.body(),checked,path+".body",depth+1,out); }
    }
    private static void add(List<ScopedFunctionalProjection.Row> rows, ScopedFunctionalProjection.Row row) { if(rows.size()>=256)throw new IllegalArgumentException("projection row limit"); rows.add(row); }
    private static String typeToken(RuneTypeExpression type) {
        return switch (type) {
            case RuneTypeExpression.ValueType value -> value.type().id();
            case RuneTypeExpression.FunctionType function -> "(" + typeToken(function.parameterType()) + "->" + typeToken(function.resultType()) + ")";
        };
    }
}
