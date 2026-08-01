# P12-FX-01 Autonomous Evidence Review

**Date:** 2026-08-01  
**Reviewer:** Sol  
**Decision:** `NEEDS_FIX` (`SNAPSHOT_FAILURE`)  
**Downstream:** `P12-DS` remains blocked

## Scope and evidence identity

The repository was clean before the immutable build. The reviewed source was:

```text
commit: fc615e89ba84591dc1d1b91b9ff5c442d2080ab7
MathMod: 0.2.0-beta.1
JAR SHA-256: 4562AC6B846B8FF74F68C06B75696E2B681FECF3A5D4ABA38337A08BF1E83B6C
Java: Oracle 21.0.8+12-LTS-250
Minecraft: 1.21.1
NeoForge: 21.1.234
```

`clean build --no-daemon` and a confirming `build --no-daemon` completed
successfully. The standalone server was materialized outside the repository
from the frozen artifacts and hashes recorded in
`docs/P12_FX_01_GATE_REVIEW.md`.

The fixture formatting correction recorded below was made only after that
clean build. It changes no configuration value or production artifact.

## Autonomous results

### Standalone baseline — pass

- exact frozen MathMod, NeoForge, KubeJS and Rhino artifacts were discovered;
- the KubeJS startup script loaded `1/1`, with zero warnings and zero errors;
- MathMod registered 104 runes;
- the server reached `Done`, stopped cleanly and saved all dimensions.

This is fixture evidence, not a DS-01 through DS-09 gameplay pass.

### Optional KubeJS absence — pass

A fresh standalone world started with KubeJS and Rhino absent. MathMod reached
`Done`, stopped cleanly and saved all dimensions. This proves that the optional
integration is not a hard server dependency; it does not prove DS-05 behavior.

### Configuration installation — pass

The first baseline launch showed that NeoForge canonicalizes the committed
TOML formatting and generated comments. The five fixture files were updated to
that canonical representation without changing any value. Each profile was
then copied into a fresh standalone world and observed before and after launch.

| Profile | SHA-256 before/after | `Done` | Config correction | Errors | Clean save |
| --- | --- | --- | --- | --- | --- |
| baseline | `86E69BF1828A97179919139858C67E5303330DC9043F4FE1CAC14985C24BEE77` | yes | no | 0 | yes |
| house-disabled | `86E69BF1828A97179919139858C67E5303330DC9043F4FE1CAC14985C24BEE77` | yes | no | 0 | yes |
| loot-disabled | `BD75F3D08D9D2CEEA6137BDE8A72DCF9DA097B370E5F1FD4C08315BA03C5AF6B` | yes | no | 0 | yes |
| profession-disabled | `506A5B191E5A3A7D78FBBECA1D68BE2BB91FC2C7A344CB741794FFFAD7F91637` | yes | no | 0 | yes |
| trades-disabled | `A33B2324F0964DB9C330E92B1EAD5181BA51B6758E5209C7823A485A6930651F` | yes | no | 0 | yes |

Baseline and house-disabled are intentionally identical because the accepted
baseline already disables house generation. These observations prove parser
and installation stability only; they do not prove DS-08 gameplay absence.

### DS-05 backend fixture semantics — pass, not a DS pass

The valid and missing-cross-reference fixture packs loaded together. The
authoritative log showed:

- the datapack manuscript shadows the KubeJS record for
  `example:constant_fields`;
- `p12:broken_reference` is rejected for its unknown tradition;
- the remaining candidate generation publishes coherently;
- `/reload` produces no KubeJS error.

This closes the fixture semantic question from FX-R2. No player-facing DS-05
scenario was claimed.

### DS-09 backend fixture load — pass, not a DS pass

The physical-profile fixture published six profiles on startup and a new
snapshot on `/reload`, with no physical-profile error. This proves that the
fixture reaches the backend publication boundary. It does not prove captured
old-flight versus future-flight behavior.

## Blocking finding: FX-R3

The corrected DS-02 sequence produced a real `SNAPSHOT_FAILURE`:

1. the valid same-resource replacement published three epiphanies and the
   distinguishing `successful_casts=3` definition;
2. the malformed higher-priority replacement was rejected with
   `Unsupported schema_version`;
3. the listener nevertheless published a reduced generation containing only
   two epiphanies;
4. authority therefore fell back to the built-in
   `vital_correspondence` value `successful_casts=2` instead of retaining the
   prior valid snapshot.

Repository inspection reproduces the cause:
`KnowledgeDefinitionReloadListener.loadDefinitions` catches a resource error
and omits that record, while `apply` still calls
`KnowledgeDefinitions.publishData` with the reduced map.

This violates DS-02 and the P12 rule that an invalid reload candidate leaves
the prior server state unchanged. A green build cannot override this runtime
counterexample.

## Invalid evidence excluded

An earlier datapack attempt used an operator-local path whose non-ASCII segment
was misencoded, so the packs were not copied. That attempt is discarded and is
not part of any result above. The valid rerun used a neutral external fixture
path and fresh world.

## Gate disposition

- `P12-FX-01`: `NEEDS_FIX` because the accepted fixture exposed FX-R3;
- `P12-TM-03`: `READY` under
  `docs/P12_KNOWLEDGE_RELOAD_CORRECTION_GATE.md`;
- `P12-DS`: remains `BLOCKED`;
- DS-06 and all truly two-client evidence: `BACKLOG` under
  `docs/P12_MULTIPLAYER_EVIDENCE_DEFERRAL.md`;
- no DS row is accepted by this document.

After P12-TM-03 is accepted, Sol must rebuild from a clean revision and rerun
the valid -> malformed -> removal sequence. Single-client rows can then resume
independently of the multiplayer backlog.
