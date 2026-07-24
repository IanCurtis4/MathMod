# P12 Survival Readiness Contract

Status: Terra High acceptance and ambiguity pass completed on 2026-07-22. Terra
Medium automated slice completed on 2026-07-22: `gradlew test` and all nine
required `runGameTestServer` cases passed.

## Purpose

P12 does not add a gameplay family. It establishes the evidence required before
the implemented P0-P11 systems may be described as safe for ordinary survival
use or used as authority for P14 terrain mutation.

This contract reconciles three distinct claims that must never be conflated:

| Label | Meaning | Evidence required |
| --- | --- | --- |
| `implemented` | The source, data, and bounded local behavior exist. | Code review plus relevant pure/unit tests. |
| `game-tested` | The behavior has run in NeoForge's GameTest server. | Passing `runGameTestServer` case(s) covering the stated runtime boundary. |
| `survival-ready` | The behavior has passed every applicable server, multiplayer, reload, and player-facing gate. | Unit tests, GameTests, dedicated-server smoke, and manual evidence required by this matrix. |
| `experimental` | The feature is implemented but one or more survival gates are still open. | It remains visible only with its limitations documented; it cannot grant a later system extra authority. |

Passing a pure test does not prove networking, registry reload, inventory
ownership, protection, or GUI usability. Passing a GameTest does not prove a
real client session, reconnect, or two-player interaction.

## Non-Negotiable P12 Rules

1. The server remains authoritative for graphs, costs, resources, snapshots,
   targets, offers, physical profiles, and all world mutation.
2. A failed validation, reload candidate, or preflight leaves the prior server
   state and player inventory unchanged unless an existing transaction contract
   explicitly records a handled partial commit.
3. A missing optional claim integration is not permission. The default policy
   must be tested independently and optional adapters must fail closed.
4. A client preview, client field value, client count, client material profile,
   or client offer list is never acceptance evidence.
5. P12 does not turn P8 fill authority into block-breaking authority, P11 mass
   into damage authority, or P13 ambient observations into mana.
6. A manual test records its server version, active datapacks/KubeJS scripts,
   feature configuration, actor count, exact scenario id, and result. A prose
   statement that a feature "seems to work" is useful feedback but not release
   evidence.

## Current Evidence Map

The test names below are evidence already present in the repository. They are
not promises that all release gates are already closed.

| Surface | Existing automated evidence | Current classification | Required remaining evidence |
| --- | --- | --- |
| P0 packet/menu boundary | `NetworkPayloadLimitsTest`, `ServerSideIsolationTest` | experimental | DS-01 stale menu/held item and DS-02 reload/reconnect. |
| P1 scalar and parameter forms | `ScalarOperationsTest`, `MathematicalOperationsTest`, `CustomSpellWorkspaceTest` | implemented | DS-02 parameterized proof survives reload/reconnect and recomputes server cost. |
| P2 programmer/inspector | workspace, screen-source, preview, and localization tests | implemented | M-01 first-use and M-02 actual ATM10 GUI-scale observation. |
| P3/P6 lore, manuscripts, knowledge | manuscript snapshot/reader/migration tests and knowledge tests | experimental | DS-02 valid and rejected lore reload, reconnect, missing-record reader; M-03 narrator pass. |
| P4 scoped functions | `ScopedTypeCheckerTest`, `ScopedStructureValidatorTest`, `ScopedProgramLowererTest`, migration tests | implemented | DS-03 one lowered functional proof on a dedicated server if the surface is enabled. |
| P5 fields/calculus | `FieldCalculusTest`, `SamplePlannerTest`, field/runtime tests | experimental | DS-04 Gradient Lantern, unloaded sample, exact cost, and reload behavior in a dedicated world. |
| P7 KubeJS manuscripts | KubeJS declaration/reload integration tests | experimental | DS-05 startup declaration plus `/reload` with valid and rejected data. |
| P8 fills/constructs | region/candidate/construct/preview/physics tests plus `P8GameTests` rollback, admission denial, and collision cases | experimental | GT-02 unsupported candidates, GT-03 unloaded flight, GT-04 spoofed payload, and dedicated smoke. |
| P9 player alchemy | `P9GameTests` anchor rejection and no-resource preflight plus policy/escrow tests | experimental | Remaining GT-05 target/refresh variants and DS-06 player-facing failure recovery. |
| P10 acquisition/profession/house | `P10GameTests` initial publication and independent disabled-feature configuration plus codec/offer tests | experimental | Remaining GT-06 reconciliation variants, DS-07 live trade career, DS-08 disabled-house fresh world. |
| P11 physical profiles | `P11GameTests` canonical profile/initial publication and future-snapshot publication plus pure profile tests | experimental | Remaining GT-07 captured-flight/unloaded variants and DS-09 future casts use the new snapshot only. |

P8, P9, P10, and P11 must remain `experimental` until their named runtime
boundaries have direct evidence. P14 may reuse their code, but cannot treat an
experimental boundary as permission to alter terrain.

## Required Execution Matrix

### Unit And Static Checks

Run before every P12 implementation batch:

```powershell
$env:GRADLE_USER_HOME='C:\gradle-home'
.\gradlew.bat test
```

Failures in validation, cost, snapshot, codec, or policy tests are release
blockers. Source-only tests are useful regression guards but do not replace the
runtime rows below.

### GameTest Server Rows

Run after tests whenever server runtime, reload, transactions, acquisition, or
profiles change:

```powershell
$env:GRADLE_USER_HOME='C:\gradle-home'
.\gradlew.bat runGameTestServer
```

| ID | Required scenario | Acceptance result | Terra Medium deliverable |
| --- | --- | --- | --- |
| GT-01 | P8 fill with exact items, then induce an admitted mid-commit failure. | Previous simple states restore in reverse order and escrow returns; no duplicate item appears. | New GameTest fixture and transaction assertion. |
| GT-02 | P8 candidate contains protected, unloaded, fluid, or block-entity position. | No placement, no resource loss, no chunk load, and a stable non-sensitive failure code. | Default-policy fixture plus optional adapter seam test. |
| GT-03 | Construct flight reaches an unloaded chunk and a collision target. | Flight stops/discards as contracted; no terrain mutation, ticket, or second owner flight. | Flight/unload/collision GameTests. |
| GT-04 | Spoofed P8 client choices for count, state, material, mass, or motion. | Server recomputes/ignores values and rejects mismatches before commit. | Packet or server-path GameTest. |
| GT-05 | P9 self cast, anchor cast, missing resource, dead target, and repeated defensive cast. | Target firewall holds; failed preflight mutates nothing; escrow restores; refresh is bounded. | Player/effect transaction GameTests. |
| GT-06 | P10 enabled/disabled loot, profession, trades, and house combinations plus marked-offer reconciliation. | Each feature flag is independent; valid offer state survives; only rejected marked offers change. | Merchant/config GameTests. |
| GT-07 | P11 reload, captured flight, collision, and unloaded chunk. | Existing flight retains its captured profile/version; future flight uses the replacement snapshot; no terrain change. | Profile/flight GameTests. |

**Automated evidence recorded on 2026-07-22:** all nine registered required
GameTests passed. This slice covers GT-01, the default admission half of GT-02,
the block-collision half of GT-03, the missing-resource preflight part of
GT-05, independent P10 feature configuration for GT-06, and future P11 snapshot
publication for GT-07. The remaining variants above are deliberately still
open; passing this batch does not make P8-P11 `survival-ready`.

**Luna preview evidence:** `construct-preview` and
`p9-defensive-resources` are registered in `UiPreviewMatrix` for EN/PT-BR at
1024x800 and PT-BR at 640x480. They validate localized presentation and
compact-viewport containment for the corresponding runtime surfaces. They do
not elevate the associated systems beyond `experimental` or replace the
dedicated-server rows.

GT-01 through GT-04 are the P14 entry gate. If a scenario cannot be expressed
in a GameTest because of an external optional mod, it still needs a default
policy GameTest and is recorded as an optional integration row rather than
silently omitted.

### Dedicated-Server Smoke Rows

Run a real `runServer` instance with the same NeoForge/Minecraft version and
the intended datapacks and KubeJS scripts. Join using a normal client. Use two
clients when a row says so. Preserve the server log and a short result note.

| ID | Scenario | Acceptance result |
| --- | --- | --- |
| DS-01 | Open Rune Programmer, replace or move the held talisman, then submit a Laboratory edit from the stale menu. Repeat after closing the menu. | The original and replacement talismans remain unchanged; the server rejects the stale mutation without disconnecting the player. |
| DS-02 | Inscribe a parameterized proof, satisfy its witnesses, cast it, run `/reload`, reconnect, and cast again. Repeat with malformed or removed knowledge data. | Parameters, graph, knowledge, and resource accounting remain coherent; invalid candidate data retains the previous snapshot and does not corrupt an item. |
| DS-03 | When a scoped function surface is enabled, execute one lowered pure functional proof before and after reconnect. | The compiled graph remains authoritative; malformed or unavailable source cannot replace it. |
| DS-04 | Inscribe and execute Gradient Lantern near an anchor, then make a required sample unloaded or outside its allowed radius. | Valid cast produces only its bounded signal; invalid sampling consumes nothing and preserves the previous anchor state. |
| DS-05 | Start with KubeJS manuscript declarations, then reload valid data and rejected data. | Built-in, KubeJS, and datapack precedence is stable; rejected data does not partially publish or grant authority. |
| DS-06 | In a two-player session, exercise P9 self, hostile, anchor, empty-resource, and repeat-resistance paths. | No P9 route affects another player; no failed cast consumes items; temporary effects stay bounded. |
| DS-07 | Progress a Mathemagician from novice through manuscript offers, reload data that removes one marked offer, and inspect both an open and closed menu. | Career offers remain deterministic; only loaded, closed menus reconcile; valid uses/prices persist; vanilla/modded offers remain untouched. |
| DS-08 | Create a fresh world with house generation disabled and with each P10 feature disabled in turn. | Base onboarding and profession behavior remain independent of the house; disabled features create no new behavior and do not unregister existing content. |
| DS-09 | Launch a P11 construct, reload physical data, then launch another construct and let both encounter collision/unloaded terrain. | The first flight retains its old snapshot; the second observes the new one; neither mutates terrain or force-loads chunks. |

The dedicated server is a smoke environment, not a benchmark. Large-scale
performance claims, claim-mod compatibility, and automated modpack regression
coverage remain separate future work.

### Manual Player Rows

| ID | Scenario | Acceptance result |
| --- | --- | --- |
| M-01 | Give an independent player the Field Manual, blank talisman, and one Feather. Give no explanation. | They inscribe, prepare, close, and cast Hop without verbal rescue. Record elapsed time and first blocker. |
| M-02 | Use the actual intended ATM10 GUI scale in EN and PT-BR with JEI visible. Visit Programmer, Resources, reader, Laboratory, and an invalid state. | No clipped/overlapping critical text, unreachable control, or color-only required action. |
| M-03 | Run keyboard and narrator paths through the reader, theorem inspector, resource panel, and a failed cast. | Focus order, state, and next action are understandable without a mouse; unsupported narrator details are recorded honestly. |

M-01 failure is product feedback, not a server failure. It keeps P0/P2 at
`experimental` until an issue is addressed or the onboarding expectation is
revised explicitly.

## Ambiguous Failure Policy

Every P12 result must be recorded in one category. This prevents a convenient
"probably fine" interpretation from promoting a system prematurely.

| Category | Meaning | Required disposition |
| --- | --- | --- |
| `AUTHORITY_FAILURE` | A client can alter server-owned graph, cost, target, snapshot, or world state. | Block release; reproduce with a minimal automated test before fixing. |
| `ATOMICITY_FAILURE` | A failed preflight or handled transaction loses/duplicates an item or leaves an unauthorized mutation. | Block release; retain logs/world state and add regression coverage. |
| `SNAPSHOT_FAILURE` | Reload exposes mixed generations, corrupts persisted data, or changes an in-flight captured plan. | Block release; preserve previous snapshot and add reload coverage. |
| `PROTECTION_FAILURE` | Default permission, unloaded-chunk, or adapter behavior permits an unsafe mutation. | Block P14 entry; default to fail closed. |
| `BOUND_FAILURE` | Radius, target, sample, packet, cost, or duration cap is exceeded or bypassed. | Block affected feature; add a boundary fixture. |
| `PRESENTATION_FAILURE` | A player cannot see/read/reach a required control, or localization/narration lies about state. | Keep the feature experimental; fix before survival-ready labeling. |
| `OPTIONAL_INTEGRATION_GAP` | A claim/modpack integration cannot be tested locally but default behavior is correct. | Document as unsupported/optional; never infer compatibility. |
| `ENVIRONMENT_FAILURE` | A test environment is missing a datapack, dependency, or reproducible fixture. | Do not count it as pass or fail; repair the fixture and rerun. |

## Evidence Record Format

Terra Medium records each executed row in a dated release note or issue using
this compact shape:

```text
P12 evidence: <ID>
Build: <git revision or local timestamp>
Server: NeoForge 1.21.1, MathMod <version>
Data: <datapack/KubeJS/config identifiers>
Actors: <one client/two clients/GameTest>
Result: PASS | FAIL | BLOCKED
Classification: <category when not PASS>
Artifacts: <server-log path, screenshot, or GameTest name>
Notes: <one factual sentence>
```

Do not place player names, real-world paths, claim owner names, world seed, or
private server addresses in committed evidence.

## Terra Medium Handoff

The next implementation slice must work in this order:

1. Add GT-01 through GT-07, beginning with P8 because it gates P14.
2. Run `test` and `runGameTestServer` after each isolated group.
3. Execute DS-01 through DS-09 only when the relevant fixture/data pack is
   ready; do not fabricate a pass from a singleplayer session.
4. Capture M-01 through M-03 with a real player and actual GUI scale.
5. Update `P0_ACCEPTANCE.md`, the owning P1/P3/P5/P7/P8/P9/P10/P11 contract,
   `PRIORITY_ASSESSMENT.md`, and `EPICS.md` only with evidence actually run.

P12 is complete when every row is either passed or deliberately retained as an
explicit experimental limitation. P14 requires GT-01 through GT-04 passed and
no open `AUTHORITY_FAILURE`, `ATOMICITY_FAILURE`, or `PROTECTION_FAILURE` in
the P8 mutation path.
