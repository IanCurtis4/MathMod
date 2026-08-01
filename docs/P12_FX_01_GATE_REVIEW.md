# P12-FX-01 Gate Review

**Date:** 2026-07-31  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX`  
**Downstream:** `P12-DS` remains `BLOCKED`

## Scope reviewed

Sol reviewed `docs/handoffs/P12_FX_01_HANDOFF.md`, every file under
`docs/fixtures/p12/**`, the relevant production reload authorities and the
real standalone launch boundary. No handoff claim was accepted from text
alone.

The fixture-only delta respects its ownership. No Java, production resource,
Gradle configuration, networking, Data Component, schema, client/UI or public
API change belongs to P12-FX-01.

## Findings

### FX-R1 — DS-02 malformed fixture does not test snapshot retention

The malformed record is installed as the new id
`mathmod:p12_malformed`. It does not replace the previously published
`mathmod:vital_correspondence` record from `p12-ds02-valid`.

`KnowledgeDefinitionReloadListener` rejects malformed JSON per resource and
continues building and publishing the remaining definition maps. Therefore a
new malformed id cannot prove that the prior replacement snapshot remains
authoritative. The current fixture can pass while the required DS-02
last-known-good behavior is never exercised.

Correction:

- make the malformed fixture target the exact resource id/path
  `mathmod:epiphanies/vital_correspondence` used by the valid replacement;
- freeze the sequence and pack priority: built-in -> valid replacement ->
  malformed override -> removal;
- require an authoritative observation that distinguishes the valid
  replacement (`successful_casts = 3`) from the built-in value
  (`successful_casts = 2`);
- do not claim retention if runtime falls back to built-in or publishes a
  mixed candidate. That result is a `SNAPSHOT_FAILURE`, not a fixture pass.

### FX-R2 — DS-05 expected result contradicts the P7 contract

The fixture README says that a manuscript with a missing tradition preserves
the prior publication. The frozen P7 contract instead says that a bad
cross-reference produces a source-aware diagnostic and omits only the
affected candidate; only a globally unpublishable generation preserves the
previous snapshot.

Correction:

- describe the missing-tradition vector as a local rejected-record vector;
- require proof that only `p12:broken_reference` is omitted, it grants no
  authority, and unrelated valid built-in/KubeJS/datapack winners remain
  coherent;
- do not claim whole-generation last-known-good behavior from this vector;
- retain the separate precedence proof for
  `example:constant_fields` and the built-in replacement proof for
  `mathmod:bound_measure`.

## Sol/operator work performed

Sol selected and hashed an externally compatible stack from artifact metadata:

| Artifact | Frozen selection | SHA-256 |
| --- | --- | --- |
| NeoForge installer | `net.neoforged:neoforge:21.1.234:installer` | `4B8D1632C6BC188FAA2F4D6DC53A53F43F8B188C3F208E5102FE2EE82DFAF8E9` |
| KubeJS | `kubejs-neoforge-2101.7.2-build.368.jar` | `01767BB677A9C4A8F318717C4C21BCA7E7EF80995603403A551068A0E064E740` |
| Rhino | `rhino-2101.2.7-build.85.jar` | `E0E9B0E78EDD380440266C0F4EA8D489DAC851EF075A4566A66A6DAE2F7BBB66` |

The KubeJS JAR declares NeoForge `[21.1.199,)` and Rhino
`[2101.2.7-build.81,)`; the selected stack satisfies both ranges. The KubeJS
JAR also embeds Better Advanced Tooltips `2101.1.0-build.1`, which appeared in
the observed mod list and is covered by the KubeJS artifact hash.

The local Java runtime was Oracle Java `21.0.8+12-LTS-250`.

The required clean build command completed successfully. The resulting
MathMod JAR SHA-256 was:

```text
4562AC6B846B8FF74F68C06B75696E2B681FECF3A5D4ABA38337A08BF1E83B6C
```

This hash is diagnostic only because `git status --porcelain` was not empty:
58 paths differed from `HEAD` before the build. It cannot identify an
immutable accepted revision.

A new standalone NeoForge server was installed outside the repository. It
observed:

- Minecraft `1.21.1` and NeoForge `21.1.234`;
- MathMod `0.2.0-beta.1`;
- KubeJS `2101.7.2-build.368` and Rhino `2101.2.7-build.85`;
- the fixture startup script loaded `1/1` with zero errors and zero warnings;
- 104 MathMod rune definitions registered;
- the server reached `Done (12.487s)` in survival mode with the expected mod
  list.

This is a sanitized diagnostic launch record, not DS evidence. No DS-01
through DS-09 pass is claimed. The diagnostic process was not used as a world
checkpoint.

## Remaining external blockers

The gate still lacks:

1. a clean immutable Git revision and a fresh JAR hash from that revision;
2. semantic runtime validation of the corrected valid/rejected datapack
   sequence;
3. installation and observation of every configuration profile in fresh
   worlds;
4. Client A and Client B joining as distinct authenticated accounts under
   `online-mode=true` and whitelist enforcement;
5. sanitized proof artifacts for those observations.

These are `ENVIRONMENT_FAILURE` dependencies until executed. They are not
product passes or failures.

## Bounded correction — P12-FX-01F

**Owner:** Terra Medium  
**Status:** `READY`

Allowed files:

```text
docs/fixtures/p12/README.md
docs/fixtures/p12/datapacks/p12-ds02-malformed/**
docs/handoffs/P12_FX_01_HANDOFF.md
docs/handoffs/P12_FX_01F_HANDOFF.md
```

Required correction evidence:

- the malformed DS-02 record uses the exact valid replacement id/path;
- README sequence, priority and distinguishing values are explicit;
- DS-05 wording follows the local-record rejection semantics of
  `docs/P7_KUBEJS_MANUSCRIPT_API_CONTRACT.md`;
- all JSON parses and `git diff --check` passes;
- no production or public surface changes.

Do not run or claim DS rows. After the corrected handoff, Sol resumes the
external semantic fixture proof. `P12-DS` remains blocked until all remaining
dependencies are demonstrated from a clean revision.
