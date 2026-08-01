# P12 Dedicated-Server Fixture Readiness

**Task:** `P12-SOL-02`  
**Date:** 2026-07-30  
**Owner:** Sol  
**Documentary decision:** `ACCEPT`  
**Execution decision:** `P12-DS` remains `BLOCKED`

> Operational supersession, 2026-08-01: the clean build, standalone backend
> observations, five configuration profiles, multiplayer deferral and the
> FX-R3 runtime failure are now recorded in
> `docs/P12_FX_01_AUTONOMOUS_EVIDENCE.md` and
> `docs/P12_MULTIPLAYER_EVIDENCE_DEFERRAL.md`. This document remains the frozen
> fixture contract; its older current-state inventory is not the active board.

## 1. Decision

The automated dependency is closed: `P12-TM-02` is `DONE` with `ACCEPT`, with
47 focused tests, 58 global GameTests and a green build recorded in
`docs/P12_TM_02_FINAL_GATE_ACCEPTANCE.md`.

The repository does not yet contain a reproducible real dedicated-server
fixture capable of executing DS-01 through DS-09. This is an
`ENVIRONMENT_FAILURE`, not a product pass or product failure. No existing
singleplayer, GameTest, ignored `run/server` directory or historical log may
substitute for `P12-DS`.

This document closes the Sol readiness analysis and freezes the exact fixture,
actors, isolation policy, evidence protocol, ownership and missing proofs.
`P12-FX-01` is the only task made `READY`. `P12-DS` remains blocked until Sol
accepts its handoff.

## 2. Repository evidence inspected

The following repository state is authoritative for this decision:

- `gradle.properties` freezes Minecraft `1.21.1`, NeoForge `21.1.234`,
  MathMod `0.2.0-beta.1` and Java 21 through the build toolchain;
- `build.gradle` defines one `runs.server` target at `run/server`, with
  `--nogui`, and one `runs.client` target at `run/client`;
- the current distributable output is named `mathmod-0.2.0-beta.1.jar`;
- `.gitignore` excludes `run/` and `runs/`;
- the only committed KubeJS launch input is
  `docs/examples/kubejs/mathmod_manuscripts.js`;
- `docs/KUBEJS.md` requires that example under `kubejs/startup_scripts`, but
  the repository freezes no KubeJS artifact, version, dependency set or hash;
- no committed dedicated-server `server.properties`, server mod manifest,
  P12 datapack fixture, configuration matrix or evidence directory exists;
- the ignored local server uses `online-mode=false`, `white-list=false`,
  `difficulty=easy`, `gamemode=survival`, `level-name=world` and
  `spawn-protection=16`;
- only one local client working directory exists;
- the current working tree differs materially from `HEAD`
  `b9c57cca2e506b17b74251ab14d4a2d8371c8e38`.

The ignored local server and its historical logs are diagnostic only. They do
not prove a clean world, the current source delta, intended KubeJS/data inputs,
two independently authenticated actors or a repeatable release artifact.

## 3. Frozen launch fixture

`P12-DS` must use a standalone dedicated-server fixture, not an integrated
singleplayer server and not the GameTest server.

### 3.1 Exact baseline

The accepted fixture manifest must record:

| Input | Required value |
| --- | --- |
| Java | 21, exact vendor/version recorded |
| Minecraft | `1.21.1` |
| NeoForge | `21.1.234` |
| NeoForge installer | coordinate/artifact name and SHA-256 |
| MathMod | `0.2.0-beta.1`, source commit and JAR SHA-256 |
| Server launch | NeoForge generated dedicated-server launch, `nogui` |
| KubeJS stack | exact compatible artifact names, versions and SHA-256 values |
| Data inputs | exact fixture ids and SHA-256 values |
| Configuration | named configuration profile and SHA-256 |

`runServer` may be used as an early diagnostic, but it cannot be the release
fixture unless its external mod classpath, configuration, clients and inputs
are independently frozen to the same standard. The preferred evidence target
is the standalone NeoForge server produced by the exact installer above.

### 3.2 Server policy

Every evidence run must use:

```properties
gamemode=survival
online-mode=true
white-list=true
enable-command-block=false
spawn-protection=0
```

The port and private address are operator-local and must not be committed.
The operator grants commands only through the server console. Client A and
Client B must be distinct authenticated accounts; duplicate offline identities
are invalid evidence.

### 3.3 Immutable build preflight

Before materializing a fixture:

```powershell
git status --porcelain
git rev-parse HEAD

$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat clean build --no-daemon
Get-FileHash -Algorithm SHA256 `
  C:\mathmod-build\MathMod\libs\mathmod-0.2.0-beta.1.jar
```

`git status --porcelain` must produce no output. The commit, MathMod artifact
hash and fixture manifest must remain unchanged throughout DS-01 through
DS-09. A later repository delta starts a new evidence batch.

## 4. Frozen fixture inputs

`P12-FX-01` must create a committed, copyable bundle under
`docs/fixtures/p12/`. It is evidence scaffolding only and may not introduce
runtime authority.

Required bundle:

```text
docs/fixtures/p12/README.md
docs/fixtures/p12/fixture-manifest.example.json
docs/fixtures/p12/server/server.properties
docs/fixtures/p12/config/baseline/mathmod-server.toml
docs/fixtures/p12/config/loot-disabled/mathmod-server.toml
docs/fixtures/p12/config/profession-disabled/mathmod-server.toml
docs/fixtures/p12/config/trades-disabled/mathmod-server.toml
docs/fixtures/p12/config/house-disabled/mathmod-server.toml
docs/fixtures/p12/kubejs/startup_scripts/mathmod_manuscripts.js
docs/fixtures/p12/datapacks/...
docs/fixtures/p12/evidence/P12_DS_EVIDENCE_TEMPLATE.md
```

The datapack set must include minimal, named and independently installable
fixtures for:

- DS-02 valid replacement, malformed knowledge candidate and removed
  knowledge record;
- DS-05 valid manuscript replacement, rejected cross-reference candidate and
  an id collision proving built-in `<` KubeJS `<` datapack precedence;
- DS-07 valid manuscript catalog and a replacement that removes exactly one
  marked offer;
- DS-09 valid replacement physical profile with a visibly distinct bounded
  value.

Each fixture must declare its intended row, installed location, exact resource
ids, expected candidate result and SHA-256. Rejected inputs must exercise the
real persistent codec/reload path. A hand-edited world, console mutation or
test-only publication hook is not acceptable.

The KubeJS example must remain declarative and semantically equivalent to the
canonical repository example. `P12-FX-01` must not change the public KubeJS API
or production code. The external KubeJS version is not currently frozen;
selecting, installing and hashing a compatible 1.21.1 stack is a mandatory
fixture proof.

## 5. World isolation and checkpoint policy

The ignored existing `run/server/world` must never be reused.

Use one newly created world per DS row, named by neutral row identifier, plus
one clean control and one fresh world per DS-08 flag profile. Do not commit a
world seed or private filesystem path.

```text
DS-01 fresh world
DS-02 fresh world with before-reload backup
DS-03 fresh world with before-reconnect backup
DS-04 fresh world with loaded/unloaded boundary checkpoint
DS-05 fresh world with startup, valid-reload and rejected-reload checkpoints
DS-06 fresh world for two clients
DS-07 fresh world with before/after offer-reload checkpoints
DS-08 control plus loot/profession/trades/house disabled fresh worlds
DS-09 fresh world with pre-reload flight and post-reload flight checkpoints
```

For every row:

1. stop the server cleanly before copying or restoring a world;
2. never restore only part of `world/`;
3. record the fixture manifest and configuration profile;
4. retain the raw server log outside Git;
5. commit only sanitized evidence;
6. restart from the exact checkpoint when a repeat is required.

No `/reload` substitutes for the fresh-world or restart requirements. No
restart substitutes for the explicit `/reload` rows.

## 6. Actors and scenario authority

| Row | Required actors | Required transitions |
| --- | --- | --- |
| DS-01 | operator + Client A | stale open menu, close, replacement item |
| DS-02 | operator + Client A | valid cast, `/reload`, reconnect, invalid data |
| DS-03 | operator + Client A | lowered functional proof, reconnect |
| DS-04 | operator + Client A | loaded sample, then unloaded/out-of-radius sample |
| DS-05 | operator + Client A | cold startup with KubeJS, valid and rejected reload |
| DS-06 | operator + Client A + Client B | self, hostile, anchor, empty, repeated resistance |
| DS-07 | operator + Client A | novice progression, open/closed menu reload |
| DS-08 | operator + Client A | clean control and each independent flag profile |
| DS-09 | operator + Client A | launch N, reload, launch N+1, collision/unloaded |

The operator may provision items, positioning and test data, but cannot invoke
test-only Java, GameTest helpers or direct publication APIs. All mutations under
acceptance must originate from ordinary server commands, reload, restart and
normal client gameplay.

DS-03 is applicable because the accepted L0 Factored Leap gameplay surface is
enabled in the current repository. Its graph is the authority; missing or
malformed source must not replace it.

## 7. Checkpoints and mandatory observations

Each row record must capture precondition, action, immediate result and
postcondition. At minimum:

- exact item identity/count and resource count before and after failed actions;
- active generation/version before and after reload;
- reconnect state as observed from the authoritative server;
- prior snapshot preservation after rejected candidates;
- old/new captured physical profile identity for DS-09;
- chunk loaded state before the unloaded-boundary action and after completion;
- absence of new chunk tickets, terrain mutation and cross-player effects;
- exact marked/unmarked offer identity, uses, maximum uses, price and demand;
- configuration flags and fresh-world identity for DS-08.

A green launch, absence of an exception or visual client result alone is
insufficient.

## 8. Evidence protocol

Accepted committed evidence lives under:

```text
docs/evidence/p12/<batch-id>/P12_DS_01.md
...
docs/evidence/p12/<batch-id>/P12_DS_09.md
docs/evidence/p12/<batch-id>/P12_DS_BATCH_SUMMARY.md
```

Each row uses the contract format:

```text
P12 evidence: <ID>
Build: <git revision>
Server: Minecraft 1.21.1, NeoForge 21.1.234, MathMod 0.2.0-beta.1
Data: <manifest/config/datapack/KubeJS identifiers>
Actors: <one client | two clients>
Result: PASS | FAIL | BLOCKED
Classification: <required when not PASS>
Artifacts: <sanitized artifact ids>
Notes: <one factual sentence>
```

The batch summary must list:

- Java, NeoForge installer, MathMod and KubeJS hashes;
- exact row order and restart/reload/reconnect boundaries;
- sanitized server-log identifiers;
- all failures, repeats and deviations;
- confirmation that no GameTest or singleplayer result was substituted.

Do not commit player names, IP addresses, ports, claim-owner names, world seeds,
access tokens, machine paths or unsanitized raw logs. Screenshots must crop or
redact those values without hiding the observed product state.

## 9. Failure disposition

Classify every non-pass using
`docs/P12_SURVIVAL_READINESS_CONTRACT.md`.

- Missing dependency, actor, fixture or reproducibility is
  `ENVIRONMENT_FAILURE`; repair the fixture and rerun.
- Authority, atomicity, snapshot, protection or bound failures block the
  affected product surface and require a minimal reproducible correction gate.
- Presentation failures remain product evidence and cannot be waived.
- Optional integration gaps never prove compatibility.

A failed row is not rerun over the same mutated world unless the row explicitly
tests persistence across that mutation. Preserve its checkpoint and log first.

## 10. Current blockers

`P12-DS` remains blocked by all of the following:

1. no committed standalone server/configuration fixture;
2. no exact compatible KubeJS artifact set, version or hash;
3. no committed valid/rejected P12 datapack matrix;
4. no proven two-client authenticated actor setup;
5. no committed evidence templates and sanitization procedure;
6. no clean immutable Git revision representing the accepted current product
   delta.

All six must close in one `P12-FX-01` handoff. Partial closure does not release
DS-01 through DS-09.

## 11. P12-FX-01 ownership

**Owner:** Terra Medium for repository fixture assembly; Sol + operator for
external artifact, account and launch proof.

**Status:** fixture correction accepted under
`docs/P12_FX_01F_GATE_ACCEPTANCE.md`; `P12-FX-01` remains blocked on external
Sol/operator evidence.

Allowed:

```text
docs/fixtures/p12/**
docs/handoffs/P12_FX_01_HANDOFF.md
```

Read-only:

```text
build.gradle
gradle.properties
all production and test Java
src/main/resources/**
networking
client/UI
Data Components and schemas
public APIs
run/**
```

The task may copy existing committed examples/data into fixture-only files.
It may not change product content or use ignored runtime state as deliverable.

The handoff must provide:

- complete file delta;
- exact artifact coordinates, versions and SHA-256 values;
- commands that materialize a new standalone server without a pre-existing
  world;
- proof that the server reaches ready state with the frozen mod list;
- proof that Client A and Client B can join as distinct authenticated actors;
- proof that every fixture installs and its expected valid/rejected candidate
  is observed;
- evidence paths and sanitization demonstration;
- limitations and external prerequisites.

The proof launch is fixture validation only. It must not claim any DS row pass.

## 12. Gate transition

Current:

```text
P12-TM-02 DONE (ACCEPT)
    -> P12-SOL-02 DONE (ACCEPT)
    -> P12-FX-01F DONE (ACCEPT)
    -> P12-FX-01 BLOCKED (ENVIRONMENT_FAILURE)
    -> P12-DS BLOCKED
    -> P12-M BLOCKED
```

After an accepted `P12-FX-01` handoff, Sol may change `P12-DS` to `READY`.
`P12-M` remains separately blocked on a stable build and its real independent
player/ATM10/accessibility observation fixture.
