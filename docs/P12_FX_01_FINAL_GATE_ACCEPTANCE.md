# P12-FX-01 Final Gate Acceptance

**Date:** 2026-08-02  
**Reviewer:** Sol  
**Decision:** `ACCEPT`  
**Task:** `P12-FX-01` is `DONE`  
**Downstream:** single-client `P12-DS` is `READY`; `P12-DS-MP` remains `BACKLOG`

## Scope accepted

This gate combines the committed fixture bundle and F correction, the prior
standalone/configuration evidence, the accepted P12-TM-03/F/F2 atomic reload
correction and a fresh immutable standalone DS-02 recheck. It supersedes the
`SNAPSHOT_FAILURE` disposition in
`docs/P12_FX_01_AUTONOMOUS_EVIDENCE.md`; it does not rewrite that historical
counterexample.

No Java, production resource, networking, schema, Data Component, client/UI,
public API or gameplay surface was changed by this Sol gate.

## Immutable artifact identity

Before the build, `git status --porcelain` was empty. The source identity was:

```text
commit: ce64b9bbc1d3ef48d3231be13ebad1203d9eb7e7
commit time: 2026-08-02T17:55:46-03:00
MathMod: 0.2.0-beta.1
JAR: mathmod-0.2.0-beta.1.jar
JAR SHA-256: 9FF1CFE7D094BBB8E86E5739E9600C954A42ED9DC164EFD49EC6F6B74CFC725F
Java: Oracle 21.0.8+12-LTS-250
Minecraft: 1.21.1
NeoForge: 21.1.234
```

The frozen external artifacts remained:

| Artifact | Selection | SHA-256 |
| --- | --- | --- |
| NeoForge installer | `net.neoforged:neoforge:21.1.234:installer` | `4B8D1632C6BC188FAA2F4D6DC53A53F43F8B188C3F208E5102FE2EE82DFAF8E9` |
| KubeJS | `kubejs-neoforge-2101.7.2-build.368.jar` | `01767BB677A9C4A8F318717C4C21BCA7E7EF80995603403A551068A0E064E740` |
| Rhino | `rhino-2101.2.7-build.85.jar` | `E0E9B0E78EDD380440266C0F4EA8D489DAC851EF075A4566A66A6DAE2F7BBB66` |

The clean build command was:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat clean build --no-daemon
```

Result: `BUILD SUCCESSFUL` in 1m46s. HEAD remained the commit above and the
working tree remained empty after the build. The generated JAR and the copy in
the standalone server had the same new SHA-256. The previously installed JAR
had SHA-256
`4562AC6B846B8FF74F68C06B75696E2B681FECF3A5D4ABA38337A08BF1E83B6C`,
so the runtime replacement is distinguishable.

## Corrected standalone DS-02 recheck

The proof used a new world, installed the valid same-resource pack before
startup, then installed the malformed higher-priority pack, reloaded, removed
the malformed pack, reloaded, removed the valid pack and reloaded. The server
reached `Done` and stopped with process exit code 0.

The sanitized authoritative sequence was:

1. the server discovered and automatically loaded
   `file/p12-ds02-valid`;
2. the knowledge listener published 3 epiphanies, 3 discoveries and 0
   aliases, and the enabled-pack list contained the valid pack;
3. after installation, the enabled-pack list contained both the valid and
   malformed packs;
4. the malformed same-resource definition was rejected with
   `Unsupported schema_version`;
5. the coordinator logged that reload was rejected before publication and
   that the previous definition and alias snapshots remained active;
6. there was no knowledge success-publication log between malformed
   installation and malformed-pack removal;
7. disabling the malformed pack restored an ordinary successful publication
   with 3 epiphanies, 3 discoveries and 0 aliases;
8. disabling the valid pack and the final reload both completed normally;
9. the server stopped cleanly and saved every dimension.

The exact distinguishing value is proved by the accepted real-load GameTest
in `docs/P12_TM_03_FINAL_GATE_ACCEPTANCE.md`: the valid replacement publishes
`successful_casts=3`, every rejected candidate preserves the exact prior
definition and alias snapshot objects, and valid removal restores the built-in
value `2`. The standalone server independently proves that the packaged
runtime follows the rejection/no-publication path. The server's aggregate
count log is not misrepresented as a direct value inspector.

Raw runtime logs and the local proof script remain outside the repository.
This gate records only sanitized evidence and contains no machine path, player
identity, address, port or world seed.

## Prior fixture evidence retained

The following accepted observations from
`docs/P12_FX_01_AUTONOMOUS_EVIDENCE.md` remain applicable because the external
NeoForge/KubeJS/Rhino stack and committed fixture inputs did not change:

- standalone baseline reaches ready state and saves cleanly;
- MathMod also starts without the optional KubeJS/Rhino integration;
- all five configuration profiles parse and remain stable;
- DS-05 fixtures reach the intended precedence and local rejected-record
  backend boundaries;
- the DS-09 fixture reaches physical-profile publication.

These are fixture/backend proofs, not claims that the corresponding
player-facing DS rows passed.

## Gate disposition

- `P12-FX-01`: `DONE` with `ACCEPT`;
- FX-R3: closed in the packaged standalone runtime;
- `P12-DS`: `READY` for the single-client dedicated-server rows only;
- `P12-DS-MP`: remains `BACKLOG` under
  `docs/P12_MULTIPLAYER_EVIDENCE_DEFERRAL.md`;
- DS-06 is not passed, waived or inferred;
- `P12-M` remains `BLOCKED` on its independent-player and real-client
  observation requirements;
- no DS-01 through DS-09 gameplay row is accepted by this document.

The next authorized work is execution and evidence capture for the
single-client P12-DS rows. Any product defect found there returns the relevant
gate to `NEEDS_FIX`; a green build or this fixture acceptance cannot override
a runtime counterexample.
