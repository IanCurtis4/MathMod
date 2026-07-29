package com.mathmod.program;

import com.mathmod.registry.ModDataComponents;
import com.mathmod.runes.ProgramGraph;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.logging.Logger;

/** The only six-component item transition.  Production and fault tests share this state machine. */
final class ScopedProgramComponentTransaction {
    private static final Logger LOG=Logger.getLogger(ScopedProgramComponentTransaction.class.getName());
    private static final ThreadLocal<FaultInjector> TEST_INJECTOR=new ThreadLocal<>();
    enum Phase { BEFORE, AFTER }
    @FunctionalInterface interface FaultInjector { void at(int component, Phase phase); }
    record State(ProgramGraph program, boolean hasProgram, ScopedSourceEnvelope source, boolean hasSource,
                 String name, boolean hasName, List<ResourceSelection> resources, boolean hasResources,
                 GuidedWorkspaceState guided, boolean hasGuided, List<String> actions, boolean hasActions) {
        static State capture(ItemStack stack) {
            ProgramGraph p=stack.get(ModDataComponents.PROGRAM.get()); ScopedSourceEnvelope s=stack.get(ModDataComponents.PROGRAM_SCOPED_SOURCE.get());
            String n=stack.get(ModDataComponents.PROGRAM_NAME.get()); List<ResourceSelection> r=stack.get(ModDataComponents.PROGRAM_RESOURCES.get());
            GuidedWorkspaceState g=stack.get(ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get()); List<String> a=stack.get(ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get());
            return new State(p,p!=null,s,s!=null,n,n!=null,r,r!=null,g,g!=null,a,a!=null);
        }
    }
    private ScopedProgramComponentTransaction() { }
    static boolean apply(ItemStack stack, State next) { return execute(stack,next,null); }
    static boolean applyForTests(ItemStack stack, State next, FaultInjector injector) { return execute(stack,next,injector); }
    static void setTestInjector(FaultInjector injector) { if(injector==null) TEST_INJECTOR.remove(); else TEST_INJECTOR.set(injector); }
    private static boolean execute(ItemStack stack, State next, FaultInjector injector) {
        State old=State.capture(stack); // both patches exist before any target mutation
        ItemStack probe=stack.copy();
        try { patch(probe,next,null); if(!State.capture(probe).equals(next)) return false; }
        catch(RuntimeException failure) { return false; }
        try {
            patch(stack,next,injector==null?TEST_INJECTOR.get():injector);
            if(!State.capture(stack).equals(next)) throw new IllegalStateException("component patch verification failed");
            return true;
        } catch(RuntimeException failure) {
            try {
                patch(stack,old,null);
                if(!State.capture(stack).equals(old)) throw new IllegalStateException("rollback component equality failed");
            } catch(RuntimeException rollbackFailure) {
                LOG.severe("L0 scoped component rollback invariant breach: " + rollbackFailure);
            }
            return false;
        }
    }
    private static void patch(ItemStack s, State n, FaultInjector injector) {
        put(s,ModDataComponents.PROGRAM.get(),n.program,n.hasProgram,injector,0);
        put(s,ModDataComponents.PROGRAM_SCOPED_SOURCE.get(),n.source,n.hasSource,injector,1);
        put(s,ModDataComponents.PROGRAM_NAME.get(),n.name,n.hasName,injector,2);
        put(s,ModDataComponents.PROGRAM_RESOURCES.get(),n.resources,n.hasResources,injector,3);
        put(s,ModDataComponents.PROGRAM_GUIDED_WORKSPACE.get(),n.guided,n.hasGuided,injector,4);
        put(s,ModDataComponents.PROGRAM_CUSTOM_ACTIONS.get(),n.actions,n.hasActions,injector,5);
    }
    private static <T> void put(ItemStack stack, DataComponentType<T> type, T value, boolean present, FaultInjector injector, int index) {
        if(injector!=null) injector.at(index,Phase.BEFORE);
        if(present) stack.set(type,value); else stack.remove(type);
        if(injector!=null) injector.at(index,Phase.AFTER);
    }
}
