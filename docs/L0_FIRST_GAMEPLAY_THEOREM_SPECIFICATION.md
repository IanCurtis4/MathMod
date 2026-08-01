# L0 First Gameplay Theorem Specification

**Task:** `L0-SOL-06` — First Gameplay Theorem Specification  
**Owner:** Sol  
**Decision:** `ACCEPT`  
**Unblocks:** `L0-LU-01` — Functional Teaching and Bilingual Evidence  
**Does not yet unblock:** `L0-TM-05` — First Gameplay Theorem

## 1. Gate basis

This specification is based on the repository state and the accepted L0
contracts and gates:

- `docs/MATHMOD_PRODUCT_ARCHITECTURE_DELIVERY_SOURCE_OF_TRUTH.md`;
- `docs/L0_SCOPED_SOURCE_PERSISTENCE_AND_COMPILE_CONTRACT.md`;
- `docs/L0_SCOPED_SOURCE_WIRE_FORMAT_CONTRACT.md`;
- `docs/L0_SERVER_COMPILE_SERVICE_READINESS.md`;
- `docs/L0_ATOMIC_PERSISTENCE_READINESS.md`;
- `docs/L0_TM_03_FINAL_GATE_ACCEPTANCE.md`;
- `docs/L0_READONLY_FUNCTIONAL_PROJECTION_READINESS.md`;
- `docs/L0_TM_04_FINAL_GATE_ACCEPTANCE.md`.

The repository already provides the required pure vector runes, observations,
terminal effect, scoped-function AST, pure compiler, atomic functional
inscription authority and read-only source/checked/graph projection. This gate
selects one bounded theorem that uses only those accepted capabilities.

It introduces no Java implementation.

## 2. Frozen theorem identity and player outcome

The first L0 gameplay theorem is:

| Field | Frozen value |
|---|---|
| Semantic identity | `mathmod:factored_leap` |
| English name | `Factored Leap` |
| PT-BR name | `Salto fatorado` |
| Category | Movement |
| Existing icon rune | `mathmod:scale_vector` |
| Existing provenance | `HORIZON_MEASURERS` |
| Result type | `Unit` |
| Graph budget limit | `24` |
| Charged compile steps | `113` |
| Catalog transport button | `37`, internal and non-semantic |
| Full statement | `let halve(v)=v*0.5 in push(self,halve(look)+halve((0,1,0)))` |
| Catalog formula | `push(halve(look)+halve(up))` |

The namespaced theorem id is semantic. The numeric catalog button is only an
existing-menu transport selector and must never be persisted, narrated, used
as a knowledge identity or exposed as a public compatibility promise.

No alias, legacy id or migration mapping exists for this additive theorem.
The two formulas are presentation summaries, not executable or persisted
source. In the catalog shorthand, `up` denotes the authored `(0,1,0)` vector.

### 2.1 Player-visible behavior

On successful execution, the theorem applies one combined movement vector to
the executing player:

```text
0.5 * look_vector(self) + (0, 0.5, 0)
```

The result is a forward-and-upward leap. It uses the existing
`mathmod:push_self` effect and its existing execution semantics. This gate does
not define a new movement effect, velocity rule, collision rule or world
mutation path.

## 3. Canonical authored theorem

The canonical scoped source is semantically:

```text
let halve = lambda (vector: Vec3) ->
    scale_vector(vector, 0.5)

let self = self_player()
let forward = halve(look_vector(self))
let lift = halve(vector_from_numbers(0, 1, 0))

push_self(self, vector_add(forward, lift))
```

Binder hints are presentation only. The persistent expression uses the
accepted De Bruijn representation and schema-1 wire format. Alpha-equivalent
binder names are semantically equal, but the built-in fixture uses:

```text
halve
vector
self
forward
lift
```

The source result type is `Unit` and its graph budget limit is `24`. The global
compile-step ceiling remains the accepted 4,096-step server meter; the player
or client cannot supply or reset it.

### 3.1 Required reuse

The theorem must contain one explicit lambda definition and two distinct
applications of that same let-bound function:

1. `halve(look_vector(self))`;
2. `halve(vector_from_numbers(0, 1, 0))`.

Inlining two unrelated `scale_vector` calls does not satisfy this theorem.
Applying an identity function only for decoration does not satisfy it.
Duplicating or moving the terminal effect into functional code is forbidden.

## 4. Type, purity and lowering contract

The accepted runes and classifications are:

| Rune | Type role | Purity |
|---|---|---|
| `mathmod:constant_number` | `Number` literal lowering | `PURE` |
| `mathmod:vector_from_numbers` | `Number × Number × Number -> Vec3` | `PURE` |
| `mathmod:scale_vector` | `Vec3 × Number -> Vec3` | `PURE` |
| `mathmod:vector_add` | `Vec3 × Vec3 -> Vec3` | `PURE` |
| `mathmod:self_player` | `Player` | `OBSERVATION` |
| `mathmod:look_vector` | `Player -> Vec3` | `OBSERVATION` |
| `mathmod:push_self` | `Player × Vec3 -> Unit` | `EFFECT` |

The lambda body contains only `scale_vector`, its parameter and the canonical
finite literal `0.5`. Observations occur outside the lambda. The sole effect is
the root terminal `push_self`.

The source must lower to the existing `ProgramGraph` authority. Lambdas,
applications, lets and binder names do not survive as executable graph nodes.
No runtime closure, function value, interpreter or second execution authority
is authorized.

### 4.1 Required graph oracle

The lowered graph must have these semantic counts:

| Rune | Count |
|---|---:|
| `mathmod:self_player` | 1 |
| `mathmod:look_vector` | 1 |
| `mathmod:constant_number` | 5 |
| `mathmod:vector_from_numbers` | 1 |
| `mathmod:scale_vector` | 2 |
| `mathmod:vector_add` | 1 |
| `mathmod:push_self` | 1 |

Required totals:

```text
12 graph nodes
12 graph edges
21 budget used
24 budget limit
113 charged compile steps
output rune = mathmod:push_self
```

The five canonical number values are `0.5`, `0.5`, `0`, `1`, `0`. There is no
CSE: the two authored applications lower independently. The explicitly bound
`self` observation lowers once and may feed both `look_vector` and
`push_self`.

Node ids and list order are not semantic oracles. Tests compare rune identity,
constants, socket connectivity, purity, output and explicit sharing.

### 4.2 Repository validation

Sol constructed the exact De Bruijn AST above against the current
`BuiltInRunes` registry and executed it through the public
`ScopedProgramCompiler` pipeline. The repository compiler returned:

```text
valid = true
issues = []
charged steps = 113
nodes = 12
edges = 12
budget = 21 / 24
output = mathmod:push_self
```

The per-rune counts exactly match section 4.1. This is feasibility evidence,
not a replacement for the future focused test and dedicated-server GameTest.

## 5. Authority, persistence and precedence

### 5.1 Selection and compilation

- The client may select only the catalog transport value.
- The server resolves value `37` to `mathmod:factored_leap`.
- The server constructs the canonical source; no client-authored AST, graph,
  result, cost, knowledge or resource claim is accepted for this built-in
  theorem.
- Compilation uses the accepted immutable rune snapshot and server compile
  service.
- The complete source/result/name/resource candidate is built off-item.
- All accepted target, request, generation, knowledge and material rechecks
  occur immediately before the single atomic mutation.

### 5.2 Atomic binding

Successful inscription must persist the schema-1 source envelope and its exact
compiled graph through the existing six-component transaction. Source and
result are inseparable.

The ordinary graph-only preset route
`ProgramStorage.saveValidated(ItemStack, ProgramGraph)` is not valid for this
theorem because it intentionally clears scoped source. `factored_leap` must use
the accepted functional compile/commit authority.

Failure, cancellation, stale target, stale request, stale rune generation,
stale knowledge, stale material catalog, compile rejection, resource rejection
or commit failure mutates nothing. Rollback retains the exact prior item state,
including source bytes.

### 5.3 Reads and precedence

- `ProgramGraph` remains the sole executable authority.
- Read, tooltip, render, reconnect and inspection never compile, migrate,
  repair or mutate the source.
- A malformed, future or conflicting source never replaces the graph.
- A source/graph mismatch is read-only and visible.
- Guided and scoped representations never receive silent precedence.
- Existing graph-only presets and inscriptions remain graph-only.
- No existing item is retroactively assigned `factored_leap` source.

## 6. Knowledge and resource policy

No new knowledge kind, discovery, epiphany, schema or player attachment is
introduced.

The theorem has no separate theorem-level unlock in this slice. Server
compilation still requires the player to know every referenced rune under the
accepted live knowledge policy. The GameTest must prove both:

1. missing required rune knowledge rejects without mutation;
2. sufficient live knowledge succeeds, while a final knowledge change becomes
   stale without mutation.

The lowered graph requires the existing `motion = 1` attribute from
`mathmod:push_self`. Its rune budget is `21`, inside the frozen graph limit
`24`; no budget-bonus material is required merely to make the graph valid.

Material selection and recommendations remain owned by the live accepted
material catalog and `ProgramResources`/`InscriptionResourcePolicy`. Neither
Luna content nor the theorem fixture may hardcode a particular item as the
semantic cost. Existing compatible prepared resources are preserved by the
accepted precedence rules.

## 7. Frozen terminology and teaching claims

`L0-LU-01` must use these semantic pairs:

| EN-US | PT-BR |
|---|---|
| scoped source | fonte delimitada |
| authored source | fonte autoral |
| checked form | forma verificada |
| compiled graph | grafo compilado |
| pure function | função pura |
| parameter | parâmetro |
| lambda | lambda |
| application | aplicação |
| let binding | vínculo `let` |
| beta reduction | redução beta |
| terminal effect | efeito terminal |
| Factored Leap | Salto fatorado |

`let`, `lambda`, rune ids and formulas may appear as code/notation. Ordinary
prose must not call the scoped source an executable graph, claim that the
client compiles, or imply that a function survives at runtime.

Required teaching claims:

1. `halve` is defined once and applied to two different vectors;
2. the lambda body is pure;
3. observations supply server state outside the lambda;
4. beta reduction/lowering removes functional structure before execution;
5. the existing graph is what executes;
6. `push_self` is the only terminal effect;
7. source, checked form and compiled graph are distinct read-only projections;
8. errors and stale states do not partially inscribe an item.

Forbidden teaching claims:

- recursion;
- collection combinators;
- runtime closures;
- effect values inside functions;
- automatic CSE;
- client authority;
- graph-to-lambda reconstruction;
- source migration or repair on read;
- a new public extension API.

## 8. L0-LU-01 exact ownership

Luna may edit only:

```text
src/main/resources/assets/mathmod/lang/en_us.json
src/main/resources/assets/mathmod/lang/pt_br.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/en_us/entries/programming/beta_theorems.json
src/main/resources/assets/mathmod/patchouli_books/field_manual/pt_br/entries/programming/beta_theorems.json
docs/handoffs/L0_LU_01_HANDOFF.md
```

Luna may regenerate evidence only under:

```text
run/client/screenshots/mathmod-patchouli-matrix-programming-beta_theorems-*
run/client/screenshots/mathmod-rune-inspector-functional-*
```

The existing `beta_theorems` entry must remain eight pages so the accepted
Patchouli preview matrix needs no Java change. Luna may revise the first four
pages to teach scoped functions and the frozen theorem, but all existing
theorem catalog claims across the eight pages must remain represented.

Luna must add or verify these exact presentation keys:

```text
screen.mathmod.rune_programmer.preset_factored_leap
screen.mathmod.rune_programmer.factored_leap_hint
```

Luna may improve copy for existing functional projection and diagnostic keys,
but must not add a diagnostic code or change its semantic mapping.

No Java, test source, schema, Data Component, networking, item, menu, screen,
public API or semantic-id file is in Luna ownership.

## 9. L0-LU-01 evidence and handoff

The handoff must be:

```text
docs/handoffs/L0_LU_01_HANDOFF.md
```

It must include:

- exact changed-file inventory;
- EN/PT-BR terminology table;
- page-by-page Patchouli claim matrix;
- translation-key parity;
- narrator/error-copy audit;
- generated capture inventory and visual inspection;
- commands and exact focused counts;
- limitations and escalations;
- explicit confirmation that no semantic behavior or id was invented.

Required focused command:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat cleanTest test --no-build-cache `
  --tests com.mathmod.assets.PortugueseLocalizationQualityTest `
  --tests com.mathmod.client.PatchouliPreviewMatrixTest `
  --tests com.mathmod.integration.patchouli.PatchouliFieldManualTest `
  --tests com.mathmod.client.UiPreviewMatrixTest `
  --tests com.mathmod.ServerSideIsolationTest
```

Required standard build:

```powershell
$env:GRADLE_USER_HOME='C:\codex-gradle-a0'
.\gradlew.bat build
```

A new GameTest is neither required nor authorized for the content-only Luna
slice. Runtime theorem GameTests belong to `L0-TM-05`.

The visual evidence must show:

- all four `beta_theorems` spreads in EN-US and PT-BR at 1024x800;
- the existing functional Inspector in EN-US and PT-BR at 1024x800;
- the existing functional Inspector in PT-BR at 640x480;
- no clipping, overlap, mojibake, untranslated prose or raw semantic ids in
  ordinary player copy.

Luna must not claim a runtime `factored_leap` catalog card or successful
inscription capture before `L0-TM-05` implements it.

## 10. L0-TM-05 remains blocked

After this gate, `L0-TM-05` remains blocked on:

1. accepted `L0-LU-01` content/evidence;
2. a Sol-owned exact integration-readiness amendment for the cross-package
   menu-to-functional-inscription boundary.

Repository evidence shows that `RuneProgrammerMenu` is in
`com.mathmod.screen`, while `ScopedFunctionalInscriptionService` is deliberately
package-private in `com.mathmod.program`. This gate does not widen that service,
create a public facade or duplicate its authority path.

The later readiness amendment must select an internal integration shape and
exact ownership without:

- making functional inscription a supported public extension API;
- adding a second transaction or compile path;
- routing `factored_leap` through graph-only `ProgramStorage`;
- moving compile, knowledge, material or commit authority to the client.

Until that amendment and Luna acceptance exist, Terra Medium must not implement
the theorem.

## 11. Gate transition

This document freezes the missing first-theorem specification. Therefore:

```text
L0-SOL-06 DONE (ACCEPT)
    -> L0-LU-01 READY

L0-TM-05 BLOCKED
    -> accepted L0-LU-01
    -> exact internal integration-readiness amendment
```

No other downstream task becomes ready.
