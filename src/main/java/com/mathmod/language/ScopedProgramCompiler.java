package com.mathmod.language;

import com.mathmod.runes.ProgramGraph;
import com.mathmod.runes.ProgramValidator;
import com.mathmod.runes.RuneRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Pure compiler orchestration; it never persists, executes, or mutates an item. */
public final class ScopedProgramCompiler {
    private final ScopedRuneSnapshot snapshot;

    public ScopedProgramCompiler(RuneRegistry registry) {
        this(ScopedRuneSnapshot.capture(registry));
    }

    public ScopedProgramCompiler(ScopedRuneSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public ScopedCompileResult compile(ScopedProgramSource source) {
        ScopedCompileBudget budget = new ScopedCompileBudget();
        return compile(source, budget);
    }

    /** Package-private test seam; executes the exact public compile pipeline. */
    ScopedCompileResult compile(ScopedProgramSource source, ScopedCompileBudget budget) {
        RuneRegistry registry = snapshot.registry();
        try {
            ScopedTypeCheckResult checked = new ScopedTypeChecker(registry, budget).check(source);
            if (!checked.valid()) return new ScopedCompileResult(Optional.empty(), checked.issues(), budget.chargedSteps());
            ScopedLoweringResult lowered = new ScopedProgramLowerer(registry, budget).lowerChecked(source);
            if (!lowered.valid()) return new ScopedCompileResult(Optional.empty(), lowered.issues(), budget.chargedSteps());
            ProgramGraph graph = lowered.graph().orElseThrow();
            if (!new ProgramValidator(registry).validate(graph).valid()) {
                return new ScopedCompileResult(Optional.empty(), List.of(new ScopedLanguageIssue(
                        ScopedLanguageIssue.Code.LOWERED_GRAPH_INVALID, "$", "Lowered graph failed validation")), budget.chargedSteps());
            }
            return new ScopedCompileResult(Optional.of(graph), List.of(), budget.chargedSteps());
        } catch (ScopedCompileBudget.LimitExceeded exceeded) {
            return new ScopedCompileResult(Optional.empty(), List.of(new ScopedLanguageIssue(
                    ScopedLanguageIssue.Code.COMPILE_STEP_LIMIT, "$", "Scoped compilation exceeded its step limit")), budget.chargedSteps());
        }
    }
}
