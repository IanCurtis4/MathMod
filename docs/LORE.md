# MathMod Lore Framework

## Epiphanies And Discoveries

An **epiphany** is a relation the mathemagician recognizes through repeated,
measurable work with correlated witnesses. It is not a random level-up or a
gift from one universal school.

A **discovery** is a field record recovered from the world: a manuscript,
comparison, conjecture, or worked proof left by one mathematical tradition.
Reading can transmit notation and construction knowledge, but a record never
casts a spell by itself.

The player's private field ledger remembers materials, observed correlations,
epiphanies, discoveries, runes, and theorems. The current P6 build fills it
through successful material-backed proofs and three village manuscripts.
Advanced constructions appear as conjectures until their route is completed.
See `PROGRESSION.md`.

This document is the canonical reference for player-facing lore. It exists to keep new gameplay, Patchouli text, GUI language, manuscripts, and world generation coherent without requiring a single author, lost empire, or fixed crossover canon.

## Core Premise

Mathemagic is a field discipline built around the Convergence: independent peoples repeatedly discovered that the world responds to careful, valid relations. The discipline is not a source of mana. It is a way to state a construction, prove that its inputs fit together, and use suitable material witnesses to support a local world change.

The setting intentionally uses fictional traditions. They may echo the real-world fact that arithmetic, algebra, and geometry were independently developed by many cultures, but they must not borrow a real culture's identity, sacred practice, or historical figure as shorthand for fantasy.

## Canon Layers

MathMod uses four layers so pack authors and future entries can add variety without contradicting the playable rules:

1. **Mechanical canon:** typed runes, validated bindings, material witnesses, catalysts, inscriptions, and server-authoritative effects. Player-facing text must agree with implemented behavior.
2. **Shared field canon:** the Convergence, the modern Mathemagician's method, and the existence of several independent traditions. These facts are stable even when individual records disagree.
3. **Attributed records:** quotations, diagrams, manuscript voices, and claims assigned to a tradition or unknown author. They may be incomplete or use different notation, but the UI must identify them as records rather than universal fact.
4. **Pack-local interpretation:** KubeJS materials, added traditions, local names, loot sources, and optional histories. These may extend the setting without redefining typed execution or claiming ownership of built-in traditions.

A conjecture is not a fifth truth layer. It is an explicit statement that a described application has not yet become a supported construction.

## Comparative Inspiration

The historical inspiration is the recurrence of mathematical discovery across distant real societies, not a one-to-one fantasy copy of any people. Arithmetic, geometry, algebraic reasoning, measurement, and positional methods developed through many independent and interacting traditions. MathMod turns that plurality into a fictional design rule:

- No real culture maps directly to one in-game tradition.
- No tradition owns a universal rune, type, or theorem.
- Surviving notation may emphasize a different decomposition of the same relation.
- Equivalent constructions must be comparable through typed inputs, bindings, and results, not declared equivalent only by prose.
- A new tradition needs a practical question, a characteristic method of recording it, and at least one gameplay concept it illuminates.

This allows manuscripts to preserve alternate demonstrations without ranking one route as primitive, original, or superior. Historical names and sacred or culturally specific symbols should appear only in properly researched educational context outside the fictional canon, never as decorative fantasy shorthand.

## Canonical Terms

### Convergence Naming Boundary

**The Convergence** is the canonical proper name for the recurring historical
discovery that mathematical relations can guide local change. It is not an
energy field.

A **convergent field** or **convergent effect** is ordinary mathematical and
operational language for vectors directed toward a sink or decreasing
potential. The lower-case term does not name a faction, event, resource, or
school. Patchouli and GUI copy must preserve this distinction in both English
and Portuguese.

Environmental correspondence fields planned for P13 are measured properties
of place. They may vary with dimension, biome, height, and derived seed noise,
but they neither explain The Convergence nor provide free casting energy.

- **Convergence:** the recurring discovery that the same valid relations work across distant places and eras.
- **Mathemagician:** a practitioner and field researcher who observes, composes, tests, and records useful constructions.
- **Rune:** a typed statement about a value or operation, not an arbitrary command word.
- **Graph:** the full demonstration assembled from connected runes.
- **Theorem:** a verified, repeatable graph shipped as an editable example.
- **Anchor theorem:** a verified world-carrier theorem chosen with Rune Chalk. The code may retain preset ids for compatibility, but the practitioner chooses a theorem and inscribes its proof.
- **Proof:** a graph considered through its validity and use. A demonstrated proof is valid in the Programmer; an inscribed proof is bound to a talisman or world carrier.
- **Anchor inscription:** the proof currently bound to a Rune Anchor. Erasing it removes the world-carrier proof without changing the theorem carried by the chalk.
- **Spell:** the named practical effect produced when an inscribed proof accepts its witnesses. A custom spell is a practitioner-owned field note built in the Laboratory.
- **Program:** the implementation and persistence model behind a proof. Use this term for codecs, technical diagnostics, and code-facing documentation, not as the default name of the player's inscription.
- **Witness material:** a consumable item that supports a successful local world change.
- **Catalyst:** a non-consumed item that stabilizes a construction, raises capacity, or permits a demanding relation.
- **Conjecture:** a documented but not-yet-implemented application. It must never be presented as a currently castable spell.

## Symbolic Grammar

MathMod notation is a compact account of state, not decoration:

- `f(x)` marks the programmer as the place where a construction transforms typed inputs into a result.
- `Q.E.D.` / `C.Q.D.` marks a proof only after the server has confirmed its inscription on the talisman.
- `∴` means “therefore” and appears only when an inscribed proof has accepted its witnesses and produced an effect in the world. A talisman pairs it with the spell name; an anchor pairs it with the world carrier.
- `Σ(items)` marks the complete material plan: fixed theorem requirements plus editable prepared materials. A preparation may begin from a theorem recommendation before the practitioner changes it.
- `target[input] <- source` states that a typed value from one proof step supplies a named input of another.
- `#n` refers to the numbered visible proof step, keeping compact notation tied to an inspectable rune.

Future symbols must earn a stable mechanical meaning, appear in a discoverable tooltip or manual entry, and remain understandable when color is unavailable. The GUI may present these marks at the moment they matter; historical explanation remains in Patchouli or manuscripts.

The current `f(x)` and `Σ(items)` header marks satisfy this rule through localized hover text, keyboard focus, and narration. Their explanations describe transformation and the complete material plan, not historical exposition.

Canonical design documentation uses `Σ(items)`, while localized player surfaces translate the readable argument label, such as `Σ(itens)` in PT-BR. Mathematical operators and technical ids remain stable; ordinary-language words inside notation do not receive an English-only exemption.

## Present Traditions

### Horizon Measurers

They described direction relative to an observer. Their surviving notation supports player frames, local vectors, and future transformations.

### Gatherers Of Means

They used repeated observations to find centers and representative positions. Their work supports centroids, target queries, and future distributions.

### Boundary Builders

They treated an area as a condition rather than a drawn shape. Their work supports regions, anchors, and future building patterns.

### Compounders Of Correspondence

They compared material transformations by the relations those transformations
preserved. The Bound Measure treats soul binding as a bounded positional
constraint and leads, through practice, to Vital Correspondence.

### Keepers Of The Remainder

They recorded which obligations survive a proof and which need not be paid
again. The Ledger Of Remainders teaches Parsimony; repeated comparison of
Axiomatic Ink and stable quartz resonance leads to Conservation.

These traditions are not factions, governments, or living NPC societies yet. They are historical lenses for documents and gameplay concepts.

## Theorem Provenance Matrix

The current catalog uses provenance as a teaching cue, not a claim of ownership. The tooltip line names the field method echoed by the modern reconstruction.

| Catalog theorems | Provenance cue | Mathematical emphasis |
| --- | --- | --- |
| Hop | Shared exercise | A minimal valid effect used across traditions to teach inscription and witnesses. |
| Dash, Arc Leap, Recoil, Right Angle, Planar Dash, Oblique Leap | Horizon Measurers | Observer-relative direction, local frames, normalization, and vector composition. |
| Blink, Ray Marker | Horizon and Boundary synthesis | A directed observation becomes a bounded hit or destination. |
| Ore Centroid, Life Centroid, Lift, Hostile Lift | Gatherers Of Means | Collections, filtering, nearest relations, and representative positions. |
| Vector Wave, Horizon Ward | Horizon and Means synthesis | A selected collection receives a direction derived from an observer. |

Boundary Builder provenance remains sparse in the talisman catalog because regions and anchor-relative patterns are not yet represented by a survival-ready editable theorem. The lore must not assign a theorem merely to fill that gap. A future Boundary theorem enters this table only after its generic region or anchor primitives, costs, and failure modes are implemented.

## Writing Rules

- Keep Patchouli entries short, practical, and written as notes from a shared field tradition.
- Let gameplay demonstrate a principle before lore claims it is common practice.
- Treat material properties from KubeJS or other mods as local conventions, never as mandatory crossover canon.
- Do not place chronology, exposition, or named historical figures in the programming GUI. Use one-line tooltips and contextual labels only.
- Preserve the distinction between pure computation, world queries, resource validation, and world effects in both lore and mechanics.

## Surface Responsibilities

| Surface | Narrative job | Maximum density |
| --- | --- | --- |
| Theorem hover and narration | Name one field lineage and connect formula to a surviving method. | One localized line. |
| Programmer graph | Teach types, bindings, formula, validity, and inscription state. | No chronology or historical paragraphs. |
| Resource screen | Explain witnesses, catalysts, requirements, and preparation truthfully. | Mechanical vocabulary with short material lore in tooltips. |
| Field Manual | Establish shared canon and teach implemented mechanics through short entries. | Two concise pages per concept by default. |
| Manuscript reader | Carry an attributed voice, alternate notation, conjecture, or comparison. | One to eight bounded pages with explicit provenance. |
| Villager, trades, and structures | Make research practices visible in the world. | Environmental cues and trade names; detailed history remains readable. |

## Field Manual Landing Contract

The landing spread is the player's narrative doorway, not a changelog. It must identify the book as an in-world field manual, name the reader's role as a Mathemagician, and offer one immediate playable route before introducing historical traditions.

- The first link targets the bilingual first-spell entry. A new player should reach theorem selection, inscription, witnesses, and casting before being asked to read the Convergence.
- The introduction may name the proof cycle in one short paragraph: theorem, inscription, witnesses, and effect. Detailed controls remain in the linked tutorial.
- Book title, subtitle, and landing text use dedicated language keys. The subtitle is not an ordinal software edition and must not expose MVP or roadmap vocabulary.
- New lore categories may appear on the right page, but the title nameplate and copy must remain visually bounded in EN and PT-BR.
- Future manuscript discovery may add a second contextual route after the first spell; it must not displace the stable beginner link or imply that random discovery is required for core mechanics.

## Planned Narrative Systems

### Delivery Order

1. **Field manual foundation (implemented):** introduce the Convergence, the Mathemagician's work, typed runes, witnesses, catalysts, three fictional field traditions, and equivalent proofs in bilingual Patchouli entries. The localized landing leads with the first playable spell; the complete PT-BR entry set is covered by the real-client visual matrix, with separate EN/PT-BR landing captures.
2. **Catalog provenance (implemented):** every built-in theorem carries structured provenance shown as one localized hover/narration line. It identifies a field lineage or synthesis without claiming ownership and leaves history in the Manual.
3. **First field records (implemented):** stable component ids, declarative
   discovery grants, cartographer-chest acquisition, duplicate handling, and
   definition-driven Patchouli navigation.
4. **Complete manuscript reading model (partially implemented):** traditions,
   attributed pages, a server-resolved bounded reader, missing/alias behavior,
   Patchouli navigation, and read-only theorem inspection now exist independently
   of progression definitions. A full login/reload display catalog and its
   reconnect/narrator acceptance remain separate work.
5. **Mathemagician profession (contract frozen):** P10 now defines the
   validated acquisition snapshot, bounded economy, and craftable
   Demonstration Table boundary; implementation still follows independently
   of world generation.
6. **Field study structure (contract frozen):** the optional village house
   remains disabled until profession behavior and manuscript loot pass without
   it.

Each phase needs its own implementation plan and tests. Later phases must not conceal spell implementations behind random discovery; a player who obtains a construction must still be able to inspect its graph, requirements, and failure modes.

### Narrative Milestone Matrix

| Milestone | Player-visible change | Required document changes before release |
| --- | --- | --- |
| Current foundation | The Field Manual introduces the Convergence, Mathemagicians, typed runes, witnesses, catalysts, and equivalent discoveries. The theorem catalog exposes one concise lineage cue. | Keep `LORE.md`, bilingual Patchouli entries, `UI_PREVIEWS.md`, and `UX_AUDIT.md` synchronized whenever terminology or provenance changes. |
| Stable records | Traditions, manuscripts, aliases, and theorem references load as validated data, but no new item or unlock appears. | Update `MANUSCRIPTS.md`, `EPICS.md`, `SAFETY.md`, and the exact availability statement in `KUBEJS.md`; add dedicated-server reload coverage. |
| Readable manuscripts | Implemented: a manuscript item opens attributed, bounded pages and survives missing or renamed definitions. | `MANUSCRIPTS.md`, `UI_PREVIEWS.md`, `UX_AUDIT.md`, and `SAFETY.md` record the Data Component, on-demand synchronization limit, fallback states, and migration. |
| Demonstrations | Implemented: valid manuscript references open a Manual entry or a local read-only theorem inspection without editing a talisman. | Conjecture-promotion rules remain in `MATHEMATICAL_GAMEPLAY_ROADMAP.md`; navigation and unavailable-state acceptance live in the UI documents. |
| Mathemagician profession | A configurable villager profession and Demonstration Table trade field supplies and validated records. | Freeze trade pools, prices, duplicate policy, feature flags, and server ownership in `MANUSCRIPTS.md`, `EPICS.md`, `KUBEJS.md`, and `SAFETY.md` before registering the profession. |
| Optional field study | A separately configurable village house makes the profession visible without becoming a prerequisite. | Document template/jigsaw integration, biome and rarity controls, loot markers, disable behavior, and dedicated-server generation tests in `EPICS.md`, `MANUSCRIPTS.md`, `SAFETY.md`, and `UI_PREVIEWS.md`. |

The milestone names describe release boundaries, not one combined feature. A later milestone cannot be marked implemented merely because its lore text or data schema exists.

### Current Manuscript Contract

The detailed technical contract is implemented through P6 in
`docs/MANUSCRIPTS.md`. Current reader behavior is intentionally smaller than a
global client catalog:

- Data-pack location: `data/<namespace>/mathmod/manuscripts/<path>.json`, with the file id becoming the manuscript id.
- Required data: schema version, tradition id, localized title key, one or more localized page keys, icon item, and rarity.
- Optional references: one Patchouli entry and one theorem/tutorial id. References are declarative navigation aids; they never execute or inscribe a graph.
- Reload behavior: server data is authoritative, duplicate ids follow normal data-pack priority, and invalid definitions are rejected with source-aware diagnostics instead of partially loading.
- Migration behavior: renamed ids use an explicit alias table for one supported migration window; removed definitions leave existing manuscript items readable through a localized missing-record fallback.
- Client synchronization: the held manuscript opens with only its bounded,
  server-resolved display view and references. Loot, trade, and unlock decisions
  remain server-side. A global display catalog after login/reload is future work.
- Patchouli references: the dedicated server validates their syntax; the client resolves asset availability and exposes a stable unavailable action when Patchouli or the entry is missing.

The current display-record set contains three playable records plus the
Weighted Gathering conjecture. Further lore-only records should include:

- A Horizon Measurer note about rotating a local direction around an observed axis.
- A Gatherer Of Means note about distributing one effect across several weighted targets.
- A Boundary Builder note about repeating an anchor-relative pattern.
- An unattributed comparison showing two traditions reaching equivalent constructions through different notation.

These samples establish voice, equivalence, and independent discovery without promising unimplemented spells. Acceptance for the data-model slice is a successful dedicated-server reload, deterministic duplicate precedence, bilingual content, rejected broken server-known references with useful diagnostics, graceful client handling of missing Patchouli assets, and no new execution path.

### Data-Driven Manuscripts

The current component-backed field records are lootable documents with
validated discovery definitions, attributed pages, tradition data, and optional
Patchouli/theorem navigation. A playable record grants only its declarative
server-owned discovery; its reader is never an execution or inscription path.
Weighted Gathering remains a lore-only conjecture, deliberately without a
discovery definition or theorem reference.

Potential sources are structure loot, archaeology-style finds, village trades, and pack-defined loot tables. The content must stay data-driven so modpacks can add local research traditions and materials.

The manuscript plan must specify:

- A namespaced manuscript id and a versioned JSON codec.
- Localized title, authorial voice, tradition, pages, icon, and rarity.
- Optional `patchouli_entry` and `theorem_id` references validated at data load.
- Acquisition selectors for loot tables and trades, with data-pack replacement instead of hard-coded modpack dependencies.
- A per-player discovery model only if discovery changes gameplay; lore-only reading must not require synchronization.
- KubeJS hooks for registering pack-specific traditions and manuscript content without arbitrary Java execution.
- Migration behavior for removed or renamed manuscript ids.

### Mathemagician Villager

Contracted future scope: a Mathemagician villager profession using a craftable
Demonstration Table workstation, with bounded supplies and validated manuscript
offers. A custom house can later be added through optional jigsaw-pool
injection. Exact authority, configuration, prices, reload behavior, and
worldgen independence are frozen in
`docs/P10_MANUSCRIPT_ACQUISITION_CONTRACT.md`.

The villager is not part of the current gameplay implementation. P10 has
decided its architectural boundaries; Terra must still implement and test the
workstation, profession, trade progression, and acquisition reload before Luna
adds the optional house.

Implement the profession and structure as separate slices:

- The Demonstration Table first acts as a point of interest and later may host a research UI; its initial recipe must use configurable tags.
- Trades progress from field supplies to manuscript exchange and theorem notes. Valuable effects must retain their normal witness/catalyst costs.
- Trade pools, prices, and manuscript availability need server configuration or data-driven definitions suitable for modpacks.
- The profession must be testable with vanilla village behavior before any custom house is introduced.
- The house requires a structure template, jigsaw pool integration, loot markers, biome compatibility, rarity controls, and a dedicated-server generation test.

### GUI And Discovery Boundaries

The programming GUI may use a tradition name in a one-line tooltip, manuscript provenance, or theorem subtitle. It must not contain paragraphs of history. Patchouli remains the place for context; the graph, types, costs, and failure diagnostics remain the place for instruction.

A manuscript may point to the relevant Patchouli page or preselect a theorem, but it must not silently edit the held talisman. Any future unlock state must say whether content is undiscovered, unavailable in the installed pack, or implemented but missing prerequisites.

Future GUI changes remain contextual:

- The Programmer already shows one structured line of built-in theorem provenance in hover and narration. A future manuscript may add a record-specific source only if it replaces rather than stacks another history line.
- The reading screen may offer explicit `Open Field Manual` or `Demonstrate Theorem` actions only when their validated references exist.
- The Inscribed and Resources flows keep their current instructional role and never become lore pages.
- Unknown, removed, or pack-disabled references render a stable unavailable state; they do not disappear or select a different theorem.

Every new narrative surface needs EN/PT-BR compact captures in `docs/UI_PREVIEWS.md`, a discoverability result in `docs/UX_AUDIT.md`, and corresponding terminology updates in this file.

### Documentation Change Map

- `docs/LORE.md`: canonical terms, traditions, narrative voice, manuscript schema semantics, and player-facing boundaries.
- `docs/MANUSCRIPTS.md`: codecs, reload lifecycle, stable ids, synchronization, migrations, implementation slices, and test matrix.
- `docs/EPICS.md`: implementation order, dependencies, acceptance, and explicit separation of manuscripts, profession, trades, and structure generation.
- `docs/KUBEJS.md`: only callable APIs; planned manuscript hooks remain clearly marked unavailable until implemented.
- `docs/MATHEMATICAL_GAMEPLAY_ROADMAP.md`: rules for promoting a conjecture into mechanics, generic runes, or an editable theorem.
- `docs/UI_PREVIEWS.md`: deterministic captures for manuscript reading, missing references, long localized pages, trades, and any Programmer provenance cue.
- `docs/UX_AUDIT.md`: first-time discovery, readability, keyboard/narration behavior, and whether lore cues interfere with proof construction.
- `docs/SAFETY.md`: server authority, reload validation, trade/loot trust boundaries, and any future unlock synchronization.

### Future Conjectures

Current manuscript seeds point to rotating gardens, terrain-aware paths, shared effect distributions, and reusable anchor-relative patterns. Each should become a tutorial or theorem only after its generic rune primitives, resource cost model, server limits, and failure modes are implemented.
