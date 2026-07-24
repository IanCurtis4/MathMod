# MathMod Epics

This roadmap keeps the project split into planning-sized chunks. Each epic should get its own implementation plan before serious code work starts.

## 1. NeoForge Foundation And Build

- Create and maintain the NeoForge 1.21.1 Gradle project.
- Keep the mod metadata, registries, resources, run configs, and build validation healthy.
- Keep Patchouli book resources loading when Patchouli is present in a modpack.
- Acceptance: `gradlew build` succeeds, the mod loads in `runClient`, and basic items appear in the creative tab.

## 2. In-Game Documentation Track

- Maintain a Patchouli field manual in parallel with implementation.
- Document what players can do now, what is planned but unavailable, and what pack authors can configure.
- Acceptance: each player-facing feature adds or updates at least one Patchouli entry.

## 3. Typed Rune Graph

- Define the rune type system, rune definitions, graph model, validator, and serialization contracts.
- Keep the first runtime model as a DAG with no loops or recursion.
- Acceptance: unit tests cover valid graphs, type errors, cycles, missing outputs, and budget overflow.

## 4. PSI-like GUI

- Build the first graph editor for composing typed runes.
- Show inputs, outputs, validation errors, and budget usage.
- Acceptance: a player can assemble and save a valid graph without editing files.

## 5. Programmed Talisman

- Store a validated graph on an item and execute it server-side.
- Target self-player spells first.
- Consume configured rune material requirements from the player's inventory on successful survival casts.
- Show a talisman resource menu with plural consumed items, catalysts, attributes, and effective budget.
- Acceptance: the talisman can run a small movement/raycast program from saved data.

## 6. Chalk And World Anchors

- Let chalk place or configure a simple rune anchor in the world.
- Use selected anchor theorems rather than microtile drawing.
- Preserve theorem identity on anchors and let players inspect the current inscription in-game.
- Let Rune Chalk carry a selected anchor theorem so players can choose before inscribing.
- Implemented terminology slice: the chalk tooltip exposes change/inscribe/erase actions, anchor feedback follows theorem -> proof -> inscription language, successful world-carrier proofs use `∴`, bilingual regressions reject leaked program/preset terminology, and minimum-viewport item captures guard the complete action hierarchy.
- Implemented interaction slice: one integrated-server journey now exercises the real chalk and block routes for theorem cycling, inscription, inspection, missing-sacrifice failure, successful witness consumption, erasure, repeated no-op erasure, and empty inspection. After inscription it serializes the block entity, recreates a distinct instance through `BlockEntity.loadStatic`, installs that instance back into the level, and requires the theorem id and complete graph to survive before continuing. Every localized action-bar message is verified in EN/PT-BR before capture; an already empty anchor no longer reports a false successful erasure.
- Implemented anchor-discovery slice: the Rune Anchor block item now identifies itself as a world carrier and exposes chalk inscription, empty-hand enactment, empty-hand inspection, and chalk erasure as separate bilingual action rows. Minimum-viewport inventory captures guard the complete hierarchy, and the tooltip harness now synchronizes the actual requested item instead of silently replacing non-chalk previews with a talisman.
- Next persistence slice: extend the proven in-memory NBT reconstruction into an actual chunk unload/save/reload cycle and then a dedicated-server run. Cover player reconnect, simultaneous interaction by two players, malformed or migrated persisted data, and pack-configured sacrifice selectors; require theorem identity, graph equality, witness accounting, and server-authoritative feedback after every boundary.
- Acceptance: an anchor stores a validated proof, preserves its theorem identity, and executes from its world position.

## 7. KubeJS Integration

- Expose material and rune registration hooks for modpack authors.
- Keep Java responsible for actual effect execution in the MVP.
- Support item-or-tag selectors for runtime sacrifice constants used by anchor presets.
- Let KubeJS tune built-in anchor preset sacrifice, drop, radius, and strength parameters.
- Let KubeJS register item attributes, budget bonuses, catalyst behavior, and rune attribute requirements.
- Acceptance: KubeJS can register item/tag material costs, tune budgets, and enable or disable runes.

## 8. MVP Movement And Raycast Runes

- Implement the first useful rune set: constants, math, player context, look vector, raycast, and movement.
- Promote Blink as the first raycast-gated teleport preset with collision checks.
- Avoid drops, damage, explosions, and entity mutation in the first pass.
- Acceptance: players can build simple self-target movement effects with visible validation.

## 9. Safety, Balance, And Server Testing

- Enforce budgets, server authority, no cycles, no arbitrary script execution, and clear failure modes.
- Use item requirements as the first real per-cast balance lever instead of mana or cooldowns.
- Use abstract resource attributes and budget materials as the first pack-extensible balance layer.
- Test client/server separation early.
- Acceptance: the same saved graph behaves consistently in single-player and dedicated server.

## 10. Future Language Depth

- Explore named functions, simplified typed lambda expressions, item sacrifices, drops, entities, explosions, timers, and multi-block rune structures.
- First promoted slice: rune anchors can gate a marker effect behind a nearby amethyst shard sacrifice.
- Second promoted slice: sacrifice-gated anchors can conjure a small fixed item drop.
- Third promoted slice: sacrifice-gated anchors can push nearby non-player living entities without damage.
- Promote features only after the MVP graph, GUI, and execution model are stable.

## 11. Beta Theorem Library And Mathemagic UI

- Release identity: `0.2.0-beta.1`.
- Replace the fixed preset button row with a data-driven theorem catalog.
- Group survival-ready programs into movement, sensing, and control families.
- Give each theorem a visible formula, rune icon, editable typed graph, and documented failure mode.
- Use a shared MathMod visual language for programmer and resource screens: chamfered controls, typed colors, proof-grid panels, and clear selected/hover states.
- Keep the catalog extensible without adding another screen button or server `switch` per spell.
- Implemented beta slice, now expanded to 19 theorem presets: scrollable catalog, localized hover explanations, typed graph cards, themed resource loadout, responsive standard/compact layouts with JEI/EMI side reservations, graph-node tooltips, automated in-client UI previews, and catalog-wide validation tests.
- Implemented interaction-audit slice: typed Laboratory actions and output heading, invalid-program Save lockout, two-step program deletion, resource-screen help, visible scrollbars, compact-layout regression coverage, and bilingual visual previews.
- Implemented inscription-flow slice: synchronized pending/success/timeout feedback, automatic Inscribed-tab transition, `Q.E.D.`/`C.Q.D.` proof identity, resource editing gated to the persisted graph, explicit projected/inscribed costs, and Patchouli terminology synchronized with the GUI.
- Implemented accessibility slice: keyboard-focusable theorem/Laboratory palettes, bounded cursor movement, automatic reveal-on-scroll, Enter/Space activation, semantic narration content, click sounds, Patchouli controls, and automated Tab-to-activation previews.
- Implemented cast-diagnostics slice: structured and ordered item deficits, distinct item/attribute/budget messages, argument-preserving execution results, exact deficit lines in both GUIs, semantic sneak-use guidance, bilingual HUD previews, and an updated first-spell Patchouli path.
- Implemented selector-presentation slice: exact item ids become localized item components across HUD and GUI, structured deficits remain pure domain data, technical tags/alternatives fall back safely, and raw selectors remain inspectable in resource tooltips.
- Implemented Laboratory-search slice: localized case/accent-insensitive matching across rune names, categories, types, and ids; one filtered ordering shared by mouse, keyboard, scroll, and narration; explicit zero-result behavior; compact JEI previews; and Patchouli guidance.
- Implemented custom-name slice: unnamed Laboratory proofs use a render-time localized fallback, authored names remain literal, reset removes the explicit name, long compact values begin at their first character and expose a full tooltip, and Proof/Inscribe terminology is shared with Patchouli.
- Implemented disabled-action guidance slice: preserved Theorems explain why Edit is unavailable and direct the player to assemble a new proof, with bilingual compact JEI previews and DPI-stable automated cursor placement.
- Implemented resource-semantics slice: editable prepared materials are distinct from total consumed/catalyst plans, Clear documents its exact scope, material roles replace opaque abbreviations, `Σ(items)` marks aggregation, and bilingual JEI previews verify the result.
- Implemented palette-boundary slice: Laboratory viewports fit complete rune rows, mouse and keyboard scrolling retain row alignment, residual panel space is not clickable, and bilingual compact JEI previews verify the lower boundary.
- Implemented theorem-notation slice: preserved graphs use localized `#n Rune -> type` cards, semantic input-binding tooltips, explicit final-result state, retained technical ids, line-based hover hit-testing, bilingual Patchouli guidance, and EN/PT JEI previews.
- Implemented output-family slice: exhaustive semantic color families, a compact focusable/narrated four-quadrant legend, bilingual Patchouli guidance, bounded button labels, a compact PT-BR `Lab` tab, and EN/PT scale-2 plus PT scale-3 previews.
- Implemented symbolic-icon slice: primitive rune textures were audited for uniqueness; right, forward, and oblique local-basis macros and theorems received a related but directionally distinct glyph family; catalog-wide uniqueness and asset coverage are enforced by tests; and EN/PT compact JEI previews verify recognition where labels truncate.
- Implemented formula-literacy slice: every selected Theorem retains its unique compact formula above the numbered proof, formula notation uses a consistent function grammar, contextual hover explains summary versus executable graph, Patchouli teaches representative tokens, and EN/PT compact JEI previews verify the bridge without displacing graph rows.
- Implemented first-cast workflow slice: a tested six-state proof seal separates graph validity, server inscription, witness readiness, and cast readiness; contextual bilingual tooltips and narration explain only the current state; talisman tooltips close the world-interaction loop; and Patchouli records the symbolic sequence without adding a GUI lore dump.
- Implemented held-item guidance correction: an inscribed talisman now says normal use casts only when witnesses are present, while failed item, attribute, and budget checks continue to direct the player to sneak-use resource preparation.
- Implemented held-item routing verification: blank, normal inscribed, and secondary inscribed interactions share one tested route rule; first-cast and missing-cost scenarios now invoke the public item method instead of bypassing it; and bilingual server-backed minimum-viewport previews prove the Programmer and Resources destinations.
- Implemented inscription-idempotency slice: re-inscribing an identical proof preserves custom resource preparation, redundant Inscribe actions become an explained Inscribed state, name-only Laboratory edits remain actionable, Resources guidance follows live state, and bilingual JEI previews exercise the real item persistence path.
- Implemented synchronized-transition slice: destructive confirmation labels fit compact layouts; pending inscription blocks erasure; stale feedback and armed confirmations are cancelled by conflicting actions; complete graph/name/Laboratory snapshots drive confirmation and held-item refresh; and server-backed EN/PT previews cover deletion plus name-only reinscription.
- Implemented resource-mutation slice: resource panels fit complete rows, material scroll and hit testing share the 24 px row grid, Clear follows synchronized preparation state, Patchouli documents add/remove/clear semantics, and server-backed EN/PT previews prove `0 -> 2 -> 1` plus full clearing.
- Implemented first-spell journey slice: one server-backed scenario now follows a blank talisman through Hop theorem selection, confirmed inscription, recommended one-Feather preparation, successful execution, exact witness consumption, and verified upward movement; stale Saved/Preset copy was replaced by Proof/Theorem language; `∴` marks the resulting world effect; and bilingual captures guard the compact validation status.
- Implemented theorem-card boundary slice: mixed 16 px category headers and 30 px theorem cards now share aligned wheel scrolling, keyboard reveal, rendering, and hit testing; formulas cannot survive as detached partial rows; unit tests cover mixed-height boundaries; and the real four-wheel-step preview passes in EN/PT with JEI.
- Implemented localized-validation slice: built-in graph and execution issues now preserve structured translation keys and arguments, complete wrapped diagnoses remain reachable in the graph viewport, and EN/PT keyboard previews state both the required `Unit` output and the actual `bool` output.
- Implemented compact-toolbar slice: Laboratory naming receives flexible width, the primary inscription command stays textual, and undo/reset/resources use the discoverable `<-`, `0`, and `Σ` notation with full localized tooltips and narration.
- Implemented compact-palette notation slice: all 38 Laboratory macros have unique bounded formulas for narrow rendering, while localized names and exact output types remain available through search, hover, narration, and graph rows; EN/PT JEI previews verify the notation bridge.
- Implemented inscription-feedback wrapping slice: pending, confirmed, and failed server feedback share the graph's width-aware line layout; authored proof names remain complete in EN/PT compact views instead of being reduced to an ellipsis.
- Implemented resource-section continuity slice: compact resource headings render only when their first content row also fits, preventing orphaned `Attributes` / `Atributos` labels while preserving the existing scroll grid and hitboxes.
- Implemented tooltip-contrast slice: all secondary instructional text in programmer and resource tooltips uses the shared theme-muted color instead of low-contrast dark gray; a pure contrast test and EN/PT scale-2/scale-3 previews guard the result.
- Implemented themed-text-field slice: Laboratory name and search editors separate content bounds from chamfered frames and share grid/hover/teal focus states; layout/theme tests plus standard EN and compact PT previews guard caret spacing and action-row containment.
- Implemented minimum-viewport category-continuity slice: Laboratory and Theorem headings reserve their first item before rendering, with homogeneous/mixed-height boundary tests and bilingual 640x480 previews guarding the supported 320x240 logical viewport.
- Implemented Rune-Form transparency slice: the guided palette is named for composed forms rather than primitive runes; each hover and narration preview distinguishes current inputs from inferred premises and reports the exact rune/binding expansion; bilingual compact JEI captures cover both inference and reuse.
- Implemented item-overlay footer-reserve slice: Programmer and Resources retain the usual side reservation where possible, then fall back to a tested 26 px vertical reserve at the minimum 320x240 logical viewport so centered frames end before JEI's footer controls; bilingual automated previews cover both screens. The original 24 px estimate was corrected after real captures showed the gold border sharing the footer's first pixel row.
- Implemented preview-hover and Resources-copy slice: both screens share a tested policy that removes incidental contextual tooltips from ordinary automated captures while retaining explicit tooltip modes; the visible PT-BR Resources vocabulary now uses correct diacritics and sentence case, with resting and material-tooltip JEI previews.
- Implemented Resources-keyboard slice: Prepared Materials and Materials join the Tab cycle with bounded Up/Down/Home/End navigation, Enter/Space mutation, automatic whole-row reveal, persistent synchronized focus, semantic narration, pure cursor tests, Patchouli controls, and server-backed EN/PT-BR JEI previews.
- Implemented PT-BR language-quality slice: Programmer, item/block, HUD, validation, rune, and Patchouli copy now use reviewed diacritics and sentence case; technical notation remains literal; stale anchor guidance was corrected; automated language checks guard regressions; and a real-client Patchouli matrix found and fixed title and footer overflow across the complete field manual.
- Implemented Patchouli-matrix slice: all 24 PT-BR entries produce an exact tested list of 47 even-page targets; one client session navigates, captures, retries interrupted pages, and exits automatically; the complete matrix passes at 1024x800 and GUI scale 2.
- Implemented first-cast context-continuity slice: the inscribed proof name remains visible in resource preparation and successful world feedback, readiness gives the next physical action without a tutorial panel, and the bilingual server-backed journey verifies the complete `proof -> Σ(items) -> ∴ effect` sequence.
- Implemented first-contact viewport slice: a blank talisman opens on a selected Hop/Pulo theorem with one meaningful primary action, while projected/inscribed resource headings require their first cost line before rendering; EN/PT-BR 1024x800 JEI captures guard the compact boundary.
- Implemented first-contact tab-progression slice: visual and keyboard order now follows Theorems -> Lab -> Talisman instead of presenting the persisted end state first; named layout accessors, pure standard/compact tests, bilingual JEI captures, a PT-BR minimum-viewport capture, and a three-stop keyboard assertion guard the hierarchy.
- Implemented dynamic-label and notation-discovery slice: textual buttons follow live state changes while compact icon actions retain stable glyphs; `f(x)` and `Σ(items)` are now focusable, narrated, and explained by bilingual contextual tooltips; the destructive-confirmation preview waits for the real initialized menu before clicking.
- Implemented shared-notation-header slice: Resources help moved out of the command row to form `?  Σ(items)`, matching the Programmer's `?  f(x)` relationship; one shared geometry governs both surfaces, Resources retains only proof-navigation/Clear/Close actions, semantic help lookup replaces a positional index, and EN/PT-BR standard plus PT-BR minimum captures guard the result.
- Implemented localized-notation slice: the resource sum mark is a translated component (`Σ(items)` / `Σ(itens)`) shared by rendering and preview hit-testing; its tooltip uses the same language, header geometry responds to translated width, PT-BR quality checks reject English `items`, and bilingual standard plus PT-BR minimum captures guard the mark.
- Implemented keyboard-entry hierarchy slice: notation marks remain narrated Tab stops but no longer precede the task controls; the active programmer tab has dynamic focus priority, Resources begins on its Materials catalog, and automated full-cycle checks require both notation reachability and deterministic wraparound.
- Implemented material-label slice: single exact-item materials use client-localized item names across catalog, selected loadout, and narration; tags and alternatives keep pack-defined ids; tooltips preserve technical ids/selectors; pure policy tests and bilingual JEI captures guard the distinction.
- Implemented contextual-manual bridge slice: the Resources `?` action opens Patchouli directly at the Resource Costs choice spread, remains unavailable when Patchouli is absent, validates the originating server menu, and defers book opening until the resource container has synchronized closed; a real-client preview rejects both missing navigation and a stale hidden container.
- Implemented first-contact manual bridge slice: a compact themed `?` beside `f(x)` opens the first-spell spread from the server-backed Programmer, remains disabled with guidance when Patchouli is absent, preserves task-first keyboard order, and shares the synchronized container-close scheduler with Resources. Standard/minimum PT-BR captures guard its header placement, while icon-specific padding prevents compact symbols from becoming ellipses.
- Implemented authored-name boundary slice: Resources keeps the full 32-character persistence allowance without crossing into Materials, uses an ellipsis that cannot exceed its remaining width, and reveals the complete name only when the player hovers the actually clipped name; EN/PT-BR standard and PT-BR minimum previews guard the boundary.
- Implemented resource-diagnosis hierarchy slice: incomplete plans move concrete item, attribute, and budget deficits beside Status/Estado under Outstanding/Pendências, while aggregate consumed, catalyst, and attribute evidence remains below; bilingual standard and PT-BR minimum captures guard first-viewport actionability.
- Implemented semantic resource-selection slice: added-material rendering, hit testing, reveal geometry, and server-backed mouse previews share selectable line metadata, so diagnosis reordering cannot redirect removal to a stale fixed coordinate; EN/PT mouse and keyboard synchronization scenarios pass.
- Implemented Inscribed-summary viewport slice: saved-proof identity and guidance now use a clipped, untruncated semantic line model with a visible scrollbar; mouse movement aligns to real text/spacer boundaries; keyboard focus supports Up/Down/Page Up/Page Down/Home/End with complete narration; and minimum 640x480 EN/PT-BR captures verify both the first and final localized rows above JEI's footer.
- Implemented Resources-navigation semantics slice: the misleading primary `Edit` command became neutral `<- Proof` / `<- Prova` navigation with a preservation tooltip and matching internal menu id; keyboard entry now starts on the Materials catalog; bilingual standard/minimum captures guard layout; and a server-backed transition preview proves that the same talisman returns to the Rune Programmer.
- Implemented bounded-tooltip slice: Programmer and Resources contextual tooltips now share a tested viewport policy that reserves the complete Minecraft tooltip frame, wraps overlong content, and preserves every semantic line; minimum 640x480 EN/PT-BR material captures plus the PT-BR theorem-node capture guard dense content against the JEI footer and window edges.
- Implemented state-directed Inscribed guidance slice: the saved-proof summary and narration derive their next action from the live proof workflow, sending missing witnesses to Resources and cast-ready proofs back into the world; EN cast-ready and minimum-viewport PT-BR missing-witness captures guard both branches.
- Implemented first-proof narrative continuity slice: Patchouli now presents Resources as conditional inspection when inscription is already ready, and its closing first-spell page teaches small proofs, premises, witnesses, results, and conjectures in an in-world mathemagician voice instead of describing GUI or MVP limitations.
- Implemented preparation-origin semantics slice: the editable resource list is now Prepared Materials/Materiais preparados because a new inscription can populate it from theorem recommendations before any manual add action; GUI help, keyboard narration, `Σ(items)`, Patchouli, lore, and roadmap terminology share that neutral model. Bilingual 640x480 ready-state captures guard the longer heading, and the resource-choice Patchouli spread was condensed after the English page exposed a clipped final line.
- Implemented dynamic-state preview-integrity slice: the invalid Laboratory scenario now uses semantic palette activation and rejects an unchanged graph instead of trusting a layout coordinate; PT-BR minimum-viewport captures verify invalid output, pending inscription, confirmed inscription, and complete authored-name reinscription without overlap.
- Implemented shared-semantic-palette slice: Programmer panels, Resources rows, buttons, proof seals, scrollbars, and tooltips now consume one named theme for neutral, selected, disabled, success, and failure states; contrast tests cover semantic text and a source guard rejects new screen-local ARGB colors.
- Implemented material-presentation slice: exact items retain localized registry names, built-in tag groups have bilingual catalog labels, KubeJS exposes `setMaterialTranslationKey`, unknown ids receive a readable fallback, and resource deficits reuse the player-facing material identity while hover preserves stable ids and selectors.
- Implemented localized-material-navigation slice: the Resources catalog collates display names in the active locale, mouse/keyboard rows map back to canonical server ids before mutation, narration uses the displayed name, and EN/PT-BR server-backed edge-selection previews reject index-order assumptions.
- Implemented tooltip-palette closure slice: theorem, graph, binding, workflow, Rune Form, type-legend, and material tooltips now use shared MathMod roles for identity, metadata, type, success, failure, and attributes; a source regression rejects vanilla named colors while preserving non-color text styles.
- Implemented workflow-seal discovery slice: common-width Theorem and Inscribed headers pair the mathematical state sigil with one localized word, width selection follows the real graph panel rather than the coarse compact flag, and the minimum viewport plus Laboratory retain the compact sigil to preserve proof titles and `Assembly -> type`.
- Implemented theorem-statement discovery slice: the compact formula became a focusable, narrated informational statement with hover/focus affordance and explicit non-editable-source guidance; responsive seal sizing now also preserves long theorem titles, and semantic previews cover mouse, keyboard, PT-BR copy, and the complete task-first focus cycle.
- Implemented dynamic-capture stabilization slice: ordinary GUI previews require four stable client ticks and two complete screen renders before framebuffer capture, then clear stale scissor state; synchronized transitions can no longer pass from a partially rendered first frame.
- Implemented first-use terminology slice: player-facing guidance now distinguishes Theorem examples, validated/inscribed proofs, and named spell effects while reserving program for technical implementation; the first-spell manual and compact talisman tooltip share that ladder, bilingual tests prevent regression, and the stale English chalk hint now documents the implemented anchor cycle.
- Implemented talisman-action hierarchy slice: blank and inscribed tooltips separate state from commands, normal use and sneak-use occupy distinct bilingual rows, both inscribed actions name witnesses, and the minimum-viewport real-inventory preview guards wrapping without pretending to know live inventory readiness.
- Implemented first-contact tooltip-palette slice: GUI and item tooltips now share one server-safe semantic text palette; gold identifies the mathematical or magical object, muted text carries proof metadata, teal marks primary enactment, blue marks inspection/preparation, and coral marks erasure. Talismans, Rune Chalk, and Rune Anchors use the same hierarchy in bilingual minimum-viewport captures.
- Implemented world-anchor journey slice: a deterministic in-world preview uses the public chalk and Rune Anchor interaction routes, checks the complete theorem -> proof -> witness -> effect -> erasure lifecycle, reconstructs and replaces the anchor block entity from its serialized NBT before enactment, validates preset and graph identity plus exact bilingual HUD feedback, and rejects terrain-obscured captures.
- Implemented resource-clear confirmation slice: clearing a preparation now uses a visible, narrated two-stage confirmation consistent with talisman erasure. The first activation preserves the exact loadout; the second sends the server request; related material mutations and screen transitions disarm it. Bilingual 640x480 captures guard the complete label and scope tooltip, and the integrated-server preview rejects premature mutation before synchronized clearing.
- Implemented Laboratory-reset confirmation slice: the irreversible local reset now requires two activations, exposes `0 -> 0?` as a stable symbolic proposal state, narrates the complete action, disables honestly when empty, and disarms on related edits or navigation. Bilingual integrated-server previews prove first-activation preservation, second-activation server confirmation, and strict separation between the cleared workspace and the untouched talisman inscription; Patchouli teaches the same scope.
- Implemented localized-type identity slice: every serialized rune type has a bilingual `type.mathmod.*` presentation; graph rows, assembly titles, bindings, validation, Rune Form previews, tooltips, search, and narration use player-facing mathematical names while technical ids remain available for codecs, KubeJS, search aliases, and diagnostic hover. Programmer resource summaries now share the Resources material-name policy.
- Implemented physical-carrier navigation slice: the third Programmer tab is now Talisman/Talismã rather than the adjective Inscribed/Inscrita. Its tooltip, Patchouli lore, keyboard contract, and compact bilingual previews state that it inspects the single proof bound to the held item, avoiding the false promise of a global saved-spell library while strengthening the magical object identity.
- Implemented localized-attribute identity slice: item, GUI, material tooltip, and world-failure surfaces retain structured resource deficits until component assembly and resolve built-in KubeJS attribute ids through bilingual `attribute.mathmod.*` names. Pack-defined ids have readable fallbacks and a documented resource-pack translation convention.
- Implemented theorem-conclusion notation slice: all 27 compact formulas now state the final world effect through an explicit conclusion verb such as `push(...)`, `blink(...)`, `mark(...)`, `heal(...)`, `apply(...)`, `bind(...)`, `infuse(...)`, or `exec(...)`; entries do not present an intermediate value as if it were the complete theorem. Tests enforce conclusion roots, and the bilingual Manual teaches outer verb versus nested evidence.
- Implemented Talisman replacement-language slice: the action row no longer duplicates the Theorems tab name for identical navigation. It states the player's intent as Replace/Substituir (compact Trocar), and its bilingual tooltip makes the mutation boundary explicit: browsing Theorems is safe, while the held proof changes only after a new inscription.
- Implemented wide Laboratory toolbar-fit slice: the Resources/Recursos action now consumes the unused right margin instead of truncating to `Reso...`; the compact layout deliberately retains the narrated `Σ` notation. Automated real-client captures verify EN/PT-BR at 1600x900 and compact PT-BR at 1024x800 with JEI.
- Implemented theorem-catalog formula-fit slice: narrow cards now use 27 explicit, unique symbolic summaries instead of clipping several theorems to an indistinguishable prefix; the full authoritative formula remains in the selected statement, tooltip, and narration, with preview coverage at the beginning, middle, and end of the catalog.
- Implemented full-theorem statement-fit slice: selected formulas now occupy a stable two-line semantic block rather than ellipsizing, graph rendering/scroll/hit-testing share the displaced origin, all 27 statements are checked with the real font, and EN/PT-BR plus minimum-viewport captures cover Right Angle and its second-node tooltip.
- Implemented ready-resource action-hierarchy slice: cast-ready proofs render Resources as blue optional inspection and explain the world-cast next step, while missing-witness proofs retain gold required-preparation emphasis. Deterministic EN/PT-BR previews cover both states and the localized ready tooltip.
- Implemented live-readiness synchronization slice: an open Programmer now observes inventory-derived workflow changes instead of waiting for inscription mutation. A server-backed bilingual preview preserves one screen while cycling `missing -> ready -> missing -> ready` and rejects any Resources tone that diverges from the seal or final optional tooltip.
- Implemented disabled-action hierarchy slice: inactive themed buttons now use one neutral accent instead of preserving saturated resource, inspection, primary, or destructive bars. The semantic colors return only when the action is available; a pure theme-policy test plus real-client Programmer EN and cleared-Resources PT-BR captures guard the distinction.
- Implemented interactive-scrollbar slice: every visible Programmer and Resources scrollbar now shares proportional geometry, an expanded hit target, ivory hover/drag feedback, track jump, direct drag, and row-boundary snapping. Scrollbar input is handled before rows, preventing theorem selection, Rune Form application, material addition, or prepared-material removal through the track; EN/PT-BR runtime previews reject those side effects.
- Implemented disabled-action explanation slice: blank Clear/Apagar and empty-Laboratory Undo/Desfazer now use state-specific reasons instead of describing unavailable mutations. A pure guidance policy separates empty, pending, and actionable states; bilingual real-client hover previews guard both meaning and fit.
- Implemented resource-row affordance slice: material cards now reserve a persistent teal `+` and prepared selections reserve one coral `-`, while their full rows remain mouse and keyboard targets. Pure geometry tests, a server-backed `2 -> 1` mutation, EN 1024x800, and PT-BR 640x480 captures guard text, scrollbar, hover, and action alignment.
- Implemented explicit-Programmer-exit slice: a compact header `X` now fulfills the ready-proof instruction to return to the world, explains that inscription and Laboratory work are preserved, and survives EN/PT-BR minimum-viewport captures beside `?` and `f(x)`. The same runtime audit fixed a stale hidden-theorem tooltip that could overlap the exit explanation after a tab change, and the first-spell harness now proves the direct `inscribe -> close -> cast` path.
- Corrected the English current-state entry so Rune Chalk and Rune Anchors are described as implemented world carriers rather than planned features.
- Track verified fixes, unresolved decisions, and the next acceptance matrix in `docs/UX_AUDIT.md`.
- Advanced-editor planning contract: `docs/ADVANCED_EDITOR.md` specifies coexistence with guided Rune Forms, typed sockets, parameters, undo, persistence modes, migration boundaries, accessibility, rollout, and acceptance.
- Next advanced-editor implementation slice: build the read-only canvas and prove graph inspection, keyboard focus, zoom/pan, and viewport behavior before enabling any mutation.
- Next interaction-audit slice: observe a first-time player completing `blank talisman -> theorem -> inscription -> optional resource inspection -> cast` without verbal guidance, record where discovery fails, and change only the smallest contextual GUI or Patchouli cue needed for each failure.
- Acceptance: at least 12 presets are valid, documented, selectable without overlap at the supported GUI scale, and include useful movement, sensing, and group-control gameplay.

## 12. Coordinate Frames And Transformations

- Add typed local coordinate frames and reusable local-to-world vector transformations.
- Keep mathematical transforms pure; world mutation remains in explicit effect runes.
- Teach the system through editable theorems and Laboratory macros rather than opaque built-in spells.
- Implemented foundation slice: `Frame`, `player_frame`, `transform_local_vector`, three local-movement theorems, three Laboratory basis-vector actions, custom rune icons, and bilingual Patchouli documentation.
- Next slice: generic rotations/reflections, anchor-relative frames, and affine transforms for reusable building patterns.
- Acceptance: players can construct and execute pitch-independent right, forward, and oblique movement from a typed player-relative basis.

## 13. Lore, Manuscripts, And Field Traditions

- Maintain the Convergence as the narrative frame: independent fictional traditions repeatedly discover valid mathematical constructions in the world.
- Keep player-facing lore in Patchouli and concise contextual tooltips; do not turn the programmer or resource screen into an exposition panel.
- Preserve canonical terms for runes, types, theorems, witnesses, catalysts, and conjectures in `docs/LORE.md`.
- Implemented foundation slice: bilingual Patchouli category and entries for the Convergence, Mathemagicians, typed runes, witnesses/catalysts, and the Horizon Measurers, Gatherers Of Means, and Boundary Builders; every PT-BR spread is covered by the automated real-client visual matrix.
- Implemented field-manual identity slice: localized in-world title, neutral subtitle, concise mathemagician introduction, and a direct first-spell link replace the developer-facing MVP landing copy; dedicated EN/PT-BR client captures guard nameplate width and page bounds.
- Implemented first-proof voice slice: the first-spell instructions acknowledge that a prepared inventory can make inscription immediately cast-ready, while the closing lesson frames Hop as the mathemagician's first inspectable proof and avoids developer-facing editor commentary.
- Implemented notation bridge: the Laboratory manual explains typed bindings, numbered proof steps, compact literal vectors, semantic colors, and technical-id tooltips.
- Implemented plural-discovery slice: the bilingual `Convergent Proofs` / `Provas convergentes` entry explains equivalent typed relations reached through different notation and distinguishes modern reconstruction from historical ownership.
- Implemented theorem-provenance slice: all 27 built-in theorems carry structured field lineage; theorem and formula hover plus keyboard narration expose one localized line without adding historical prose to the graph.
- Add data-driven manuscripts only after a dedicated implementation plan defines their loot, localization, optional tutorial links, and compatibility with modpack data.
- Implemented manuscript-planning slice: `docs/MANUSCRIPTS.md` freezes tradition and manuscript schemas, stable theorem ids, reload precedence, aliases, bounded synchronization, Patchouli client resolution, implementation slices, and the test matrix before any item is added.
- Implemented stable-identity foundation: Theorems and Laboratory Forms now
  have namespaced persistent ids, legacy Laboratory enum saves remain readable,
  and the player knowledge attachment supplies bounded alias migration.
- Implemented P3 Sol foundation: validated lore-only records, deterministic
  `data pack > KubeJS > built-in` source precedence, immutable snapshots,
  bounded flattened aliases, pure reference migration, source-aware
  diagnostics, and atomic publication. No lore record can grant, execute,
  inscribe, or mutate state.
- Implemented P3 Terra slice: Mojang codecs, a server reload listener,
  runtime item/theorem validation, and four built-in lore-only records now
  publish through the immutable snapshot boundary.
- Implemented P3 Luna slice: four traditions and four manuscript records now
  have bilingual localization keys, the Weighted Gathering conjecture has a
  dedicated two-page Patchouli entry, rejected-record fixtures cover codec
  boundaries, and the EN/PT-BR preview matrices include the new standard and
  compact cases.
- Remaining P3 verification: complete dedicated-server reload testing. The
  KubeJS surface is now available for its bounded manuscript declaration slice;
  live KubeJS reload validation remains a separate acceptance gate.
- Implemented P7 Terra slice: the manuscript KubeJS surface now exposes the
  contracted tradition, manuscript, and alias builders; it freezes one startup
  generation, validates KubeJS icons with the registry, and assembles the
  snapshot under fixed `built-in < KubeJS < data pack` precedence. Luna now
  provides the bilingual Patchouli teaching entry, canonical startup example,
  and documentation tests. Live dedicated-server reload remains.
- Acquisition slice: add the readable manuscript item and data component, then integrate configurable loot and trades. Duplicate handling, missing-definition fallback, server authority, and dedicated-server reload tests are acceptance requirements.
- Promotion slice: add an explicit route from a validated manuscript reference to a Patchouli tutorial or editable theorem demonstration, without hidden arbitrary execution or automatic talisman mutation.
- Future profession slice: register the Mathemagician and Demonstration Table, then validate configurable research trades before adding world generation.
- Future structure slice: optional village house with template/jigsaw integration, loot markers, biome and rarity controls, and dedicated-server generation tests.
- Keep profession, trades, and structure generation independently configurable so packs can adopt only the narrative systems they need.
- Acceptance: current mechanics have concise bilingual narrative context; future manuscripts and villager content remain explicitly planned rather than implied as implemented.

### Narrative Change Ledger

| Slice | Player-visible result | Primary implementation | Documentation and acceptance |
| --- | --- | --- | --- |
| N0: Shared canon | Convergence, Mathemagician, runes, witnesses, catalysts, and field traditions in the Manual. | Bilingual Patchouli lore category. | `LORE.md`; every entry covered by the Patchouli matrix. Implemented. |
| N1: Catalog lineage | Hovering or narrating a theorem names its field lineage or synthesis. | Structured `TalismanPreset.Provenance`, bilingual tooltip copy. | `LORE.md`, `UI_PREVIEWS.md`, `UX_AUDIT.md`; compact EN/PT-BR tooltip. Implemented. |
| N2: Stable records | Stable theorem/Laboratory identities, lore codecs, reload snapshot, source precedence, diagnostics, aliases, and bilingual built-ins are implemented. | Records, immutable snapshot, reload listener, runtime references, and preview fixtures; dedicated-server smoke remains. | `P3_MANUSCRIPT_SNAPSHOT_CONTRACT.md`, `MANUSCRIPTS.md`, `UI_PREVIEWS.md`, `SAFETY.md`; unit/build tests pass, dedicated-server test remains. In progress. |
| N3: Reading | A manuscript item opens bounded attributed pages and handles removed records. | Implemented Data Component, server-resolved on-demand view, read-only screen, aliases, and fallback states; full login/reload catalog and narrator/reconnect gates remain. | `MANUSCRIPTS.md`, `UI_PREVIEWS.md`, `UX_AUDIT.md`; EN/PT-BR and compact matrix coverage is implemented. |
| N4: Demonstration | Valid references open the Manual or a read-only theorem inspection. | Implemented server-validated Patchouli navigation plus client-local `ProgramSurface.theorem(...).inspect()`; no automatic inscription or mutable Programmer route. | `MANUSCRIPTS.md`, `MATHEMATICAL_GAMEPLAY_ROADMAP.md`; missing references remain explicit. |
| N5: Acquisition | Optional loot and Mathemagician trades surface records in survival. | Configurable loot selectors, profession, Demonstration Table, trade data. | `SAFETY.md`, `KUBEJS.md`; core theorems remain ungated and duplicates have policy. |
| N6: Field study | An optional village house makes the profession visible in world generation. | Structure template, jigsaw pool, loot markers, biome/rarity config. | `UI_PREVIEWS.md`, `SAFETY.md`; dedicated-server generation test and independent disable switch. |

Do not combine N2-N6 into one implementation change. Each slice has a separate rollback boundary and must update the documents named in the ledger before the next slice begins.

## 14. Advanced Mathematical Vocabulary

- Expand the language through reusable typed mathematics, never opaque effects with academic names.
- Implemented first slice: radian sine/cosine; finite difference and one-panel Simpson quadrature; vector cross product, projection, and reflection; and a typed bounded cyclic-group element with composition, inverse, and Y-rotation action.
- Implemented gameplay slice: Harmonic Step, Orthogonal Step, Quarter Turn, and Quadrature Leap all terminate in the existing `push_self` effect and expose their complete typed decomposition.
- Implemented Laboratory slice: six appended Rune Forms expose trigonometry, linear algebra, and cyclic action without changing persisted ordinals. Parameterized numerical-calculus forms now expose independent bounds and samples honestly, with bounded domains and a result-precision surcharge.
- Implemented resource slice: quartz/resonance, copper/continuity, lapis/symmetry, and prismarine/orientation are configurable tier-2 defaults; catalysts and consumed witnesses remain distinct.
- Implemented acceptance slice: unit tests cover mathematical laws and graph validation; real-client previews cover the advanced theorem catalog, Symmetry Laboratory discovery, bilingual Patchouli, and a complete Harmonic Step cast that consumes Feather, preserves Quartz, and proves horizontal motion.
- Implementation and future contract: `docs/ADVANCED_MATHEMATICS.md`.
- Next scalar slice: the approved P1 contract in `docs/P1_SCALAR_CONTRACT.md` fixes powers, roots,
  logarithms, inverse trigonometry, bounded interpolation, a minimal threshold bridge, explicit domain failures,
  output bounds, and Threshold Beacon before implementation.
- Next linear-algebra slice: typed matrices and affine maps, including singularity handling and a building/region demonstration rather than another fixed movement spell.
- Next calculus slice: bounded function or field values, sample-count cost, finite-difference gradient, and a world-derived theorem.
- Next symmetry slice: consider dihedral actions only after a useful region/building application and a distinct bounded typed representation exist.
- Remaining acceptance: add dedicated-server execution proof and replace the first-pass derived rune icons with distinct final glyphs.

## 15. Alchemical Effects And Entity Infusion

- Expand world mutation beyond movement and detection through typed, temporary entity transformations.
- Implemented effect-plan slice: restoration, Speed, Invisibility, Night Vision, Wither, Soul Constraint, and Vital Infusion construct `effect_plan` values; `combine_effect_plans` composes them; `execute_effect_plan` remains the only mutation boundary.
- Implemented gameplay slice: eight Alchemy theorems and eight appended Laboratory Rune Forms cover classic effects, hostile non-player control, temporary attribute infusion, and a compound mantle.
- Implemented material slice: seven craftable custom reagents provide restoration, vitality, haste, transmutation, concealment, sight, decay, soul, binding, and infusion evidence. Noctilucent Lens and Homuncular Matrix are catalysts; the remaining reagents are consumed.
- Implemented safety slice: bounded healing, duration, and levels; hostile plans exclude players; Soul Constraint has bounded pull; Vital Infusion uses temporary registered modifiers removed by the vanilla effect lifecycle.
- Implemented documentation slice: bilingual item, rune, theorem, category, provenance, effect, and attribute names plus a fourteen-page Patchouli entry with the seven recipes.
- Implementation and future contract: `docs/ALCHEMICAL_EFFECTS.md`.
- Next acceptance slice: real-client casts for a classic consumed reagent, Soul Constraint, and catalyst-preserving Vital Infusion; dedicated-server startup and execution proof.
- Next effect slice: cleansing, resistance, absorption, and explicitly targeted ally support.
- Permanent infusion remains deferred until an allowlisted, reversible, owned, migrated, and administratively configurable design is approved.
- Acceptance: all eight theorems execute through typed plans; resource preparation identifies every required correspondence; custom effects expire cleanly; hostile plans cannot target players.

## 16. Rune Tiers And Metamagic

- Give every rune an explicit tier and derive a proof's required tier from its most advanced rune.
- Require a sufficient-tier material that contributes to the proof rather than accepting unrelated progression filler.
- Implemented foundation slice: four rune tiers, contributing-witness validation, tier-aware recommendations, GUI tier status, and KubeJS setters for rune and material tiers.
- Implemented metamagic slice: Parcimônia reduces later attribute requirements; Conservação gives consumed units a bounded survival chance; both are server-authoritative MobEffects granted by ordinary typed effect-plan runes.
- Implemented anti-recursion rule: each player cast snapshots metamagic and one immutable cost plan before world execution, so a spell cannot finance itself.
- Implemented content slice: two tier-IV theorems, two appended Laboratory forms, Axiomatic Ink, Recursive Seal, recipes, icons, bilingual names, and Patchouli teaching.
- Implementation and future contract: `docs/METAMAGIC.md`.
- Implemented acceptance slice: real-client Parcimônia proves full self-cost plus a discounted follow-up; Conservação proves catalyst preservation and registered-effect application; catalog/resource captures guard tier layout; the dedicated GameTest distribution loads without client linkage.
- Next acceptance slice: effect expiry and reapplication, deterministic inventory-level conservation sampling, and registered world GameTests rather than startup-only server validation.
- Next framework slice: data-driven metamagic modifier descriptors only after stacking order, rounding, synchronization, and administrative caps are specified.
- Acceptance: tier failures are legible before casting; unrelated high-tier materials cannot unlock proofs; active modifiers never alter their own cast; no client controls consumption rolls.

## 17. Functional Observables And Calculus Language

- Evolve the graph into a bounded typed functional language while preserving an
  explicit final world-effect boundary.
- Implemented F0 language slice: rune definitions classify pure computation,
  world observation, and effects; supported closed scalar/vector subgraphs are
  normalized into the runtime cache without migrating saved graphs.
- Implemented F0 collection slice: `NumberList`, empty-safe entity sensing,
  velocity access, vector magnitudes, sum, mean, maximum, rounding, and
  bilingual distinct rune glyphs.
- Implemented F0 gameplay slice: Kinetic Transducer is a fifth chalk theorem;
  it samples motion in radius 8, emits `0..15` redstone from the Rune Anchor for
  ten seconds, persists expiry, updates neighbors, and clears on replacement.
- Implementation and future contract: `docs/FUNCTIONAL_LANGUAGE.md`.
- Next F1 slice: typed literal descriptors plus a read-only inspector for
  purity, normalized values, dynamic observations, and witness requirements.
  - Implemented F2 Sol architecture: generic function types, De Bruijn lexical
    references, bounded source AST, pure lambda boundary, collection work
    limits, and a no-rewrite migration policy that preserves version-1 graphs.
  - Implemented F2 Terra High review: exact typing judgments, De Bruijn
    shifting/substitution and alpha-equivalence counterexamples,
    sharing-preserving beta-to-let reduction, and a strict terminal effect
    boundary are frozen before runtime implementation.
  - Next F2 implementation slice: codecs, checker, reducer, graph lowering,
    optional source persistence, and one reusable-function theorem.
- Next F3 slice: scalar/vector fields, dimensions, bounded derivative,
  gradient, divergence, curl, and integration with visible sample-count cost.
- P8/F4 Sol contract frozen in `docs/P8_CONSTRUCTIVE_REGIONS_CONTRACT.md`:
  pure regions, deterministic candidate plans, exact item-counted fill plans,
  server-side escrow/rollback, transient mass-conserving construct bodies, and
  the compositional Cavalieri Projectile delivery path.
- Implemented P8 Terra High review in
  `docs/P8_GEOMETRY_SEMANTIC_REVIEW.md`: closed Boolean boundaries, bounded
  implicit predicates, radial-band profiles and solids of revolution,
  voxel-center inclusion, center of mass, scalar inertia, quantity
  interpretation, and degeneracy/compression counterexamples.
- Implemented P8 Terra B: terminal `fill_region` uses an exact simple block
  item, EMPTY_ONLY plans, loaded/world/interaction checks, escrow, commit-time
  state and permission revalidation, rollback, and delayed neighbor updates.
  It cannot use tag-only, fluid, NBT, block-entity, or callback-driven
  materials. Dedicated-server and claim-mod GameTests remain.
- Next P8 Terra C slice: transient construct body, conserved mass-equivalent,
  compression, spin, swept launch, bounded impact, and the Cavalieri Projectile
  theorem.
- Implemented P8 Terra A: Boolean and constant radial-band revolution
  descriptors now evaluate through the typed runtime, while a Minecraft-free
  candidate planner applies center sampling, stable `y,z,x` order, and explicit
  visit/candidate rejection. Exact material selection and world mutation remain
  outside this slice.
- Next F5 slice: finite item-backed sustained anchor leases, typed memory,
  hysteresis, and temporal observations without chunk force-loading.
- Remaining acceptance: real-client redstone capture and dedicated-server
  execution proof for signal activation and expiry.

## 18. Knowledge, Epiphanies, And Discoveries

- Turn mathematical progression into a private field ledger owned by each
  player rather than a global pack checklist.
- Keep payment and knowledge separate: witnesses pay for casting, while
  epiphanies and discoveries govern future construction and editing.
- Implemented P0 contract slice: `docs/PROGRESSION.md` freezes authority,
  migration, compatibility, identity, and the rule that already-inscribed
  proofs remain executable.
- Implemented P0 identity slice: all 29 Theorems and all Laboratory Forms use
  stable namespaced ids; old unqualified theorem lookups and persisted enum
  names remain readable.
- Implemented P1 knowledge slice: schema-versioned immutable per-player state
  stores materials, correlations, epiphanies, discoveries, runes, and
  theorems; each set is bounded, deterministic, persistent, copied on death,
  and synchronized only to its owner.
- Implemented P1 operations slice: kind-aware alias chains with cycle and
  conflict validation, automatic migration on data synchronization, and
  permission-level-2 `get`, `grant`, `revoke`, `clear`, and `migrate` commands.
- Implemented P2 vertical slice: successful casts study actual paid resource
  plans; two Feather and two Nether Quartz casts complete Harmonic
  Correspondence, grant trigonometric runes plus Harmonic Step, and report
  bounded server-owned progress.
- Implemented P3 vertical slice: The Rotated Horizon Field Manuscript grants
  cyclic runes plus Quarter Turn, remains readable after duplicate discovery,
  opens bilingual Patchouli pages, and appears through a one-in-three
  cartographer-chest subtable.
- Implemented construction gate: affected Theorems and Laboratory Forms remain
  visible as explained conjectures, while save/apply/inscribe is enforced
  server-side. Existing inscribed graphs continue to execute.
- Implemented compatibility migration: schema-1 players receive both first
  routes; new schema-2 ledgers begin with them undiscovered.
- Next P2/P3 expansion: add more correlated studies and manuscripts after the
  reloadable definition/alias registry is implemented.
- Implemented P4 registry slice: immutable definition and alias snapshots use
  `datapack > KubeJS > built-in` precedence; reload validates caps, grant
  references, manuscript ownership, and alias cycles before atomic
  publication.
- Implemented P4 KubeJS slice: declarative builders register epiphanies,
  material study requirements, discoveries, rune/theorem grants, and knowledge
  aliases without callbacks or direct player mutation.
- Implemented P5 field-ledger slice: a writable-book plus Rune-Chalk recipe
  creates a server-authored Caderno de Campo with overview, epiphany, and
  discovery tabs, bounded progress, route, and grant details.
- P5 boundary: the ledger is read-only and does not absorb the separate
  Mathemagician profession, trade, or village-structure milestones.
- Implemented P6 convergence slice: The Bound Measure and The Ledger Of
  Remainders manuscripts each reveal an advanced hypothesis; cross-tier
  practice then completes Vital Correspondence or Conserved Remainder.
- Implemented P6 acquisition/navigation slice: the one-in-three cartographer
  outcome now selects among three component-backed manuscripts, and each
  record opens its own declarative bilingual Patchouli entry.
- Implemented P6 compatibility slice: schema-2 players retain Soul Constraint,
  Vital Infusion, Axiom Of Parsimony, and Conservation Lemma; new schema-3
  ledgers earn them through play.
- Implemented P6 reading/navigation slice: component-backed manuscripts now
  open a bounded, server-resolved reader with alias and missing-record states;
  its Manual route is server-validated and its theorem route is an immutable
  local Inspector surface. The semantic review adds cross-record contract tests
  and explicitly preserves Weighted Gathering as non-castable conjecture.
- P6 boundary: trades, profession, structure, optional full login/reload
  display catalog synchronization, real narrator coverage, and dedicated-server
  reload verification remain independently planned.
- Next progression expansion: close the reload/narrator acceptance gates before
  adding profession or structure acquisition.
- Implementation and future contract: `docs/PROGRESSION.md` and
  `docs/MANUSCRIPTS.md`.
- Acceptance: reconnect, death, alias migration, and dedicated-server behavior
  preserve authoritative knowledge; old talismans execute even when their
  construction knowledge is absent.

## 19. Numerical Parameters And Result-Scaled Proofs

- Implemented first interactive-parameter slice: Number, Finite Difference,
  and Simpson Panel open a focused numeric form in the Laboratory.
- Parameterized Laboratory steps persist their bounded arguments beside stable
  action ids; legacy action-only inscriptions load with documented defaults.
- Simpson Panel exposes lower and upper bounds plus the three explicit samples
  required by the existing one-panel quadrature rune. It is numerical
  quadrature, not symbolic integration.
- Implemented server-authoritative magnitude scaling for normalized
  finite-difference and Simpson results: additional precision is
  `ceil(log2(abs(result)))`, zero through magnitude 1 and capped at 8.
- Next editor slice: select an existing graph node and edit its typed
  parameters in place instead of undoing and rebuilding the tail.
- Next calculus slice: bounded function values, sample count, composite
  quadrature, and visible domain/resolution cost after F2 scoped functions.
- Acceptance: malformed, non-finite, out-of-range, zero-step, and equal-bound
  inputs cannot enter the graph; client values are sanitized and costs are
  recomputed from the graph on the server.

## 20. P11 Derived Block Physics

- Keep exact item payment, P8 `massEquivalent`, and P11 physical mass as
  separate quantities. Physical profile reloads never re-price or rewrite a
  saved talisman.
- Completed Sol contract slice: schema-1 block/tag profiles and the
  `mathmod:default` policy, bounded numerical ranges, exact source precedence,
  ambiguity rejection, atomic immutable snapshots, snapshot-local LRU cache,
  stable diagnostics, startup-only KubeJS declarations, and the compatibility
  matrix are frozen in `docs/P11_DERIVED_BLOCK_PHYSICS_CONTRACT.md`.
- Compatibility default: existing P8 constructs use compression exponent
  `gamma = 0`; item escrow, source count, `massEquivalent`, launch caps, chunk
  policy, and terrain safety remain unchanged.
- Implemented Terra High review in `docs/P11_PHYSICS_SEMANTIC_REVIEW.md`:
  sampled-union canonical collision volume, canonical/contextual shape split,
  mass-weighted center/inertia tensor, gamma identities, legacy launch
  boundary, and counterexamples.
- Implemented P11 pure core in `com.mathmod.physics`: sampled volume,
  declarations/policy, profile precedence, fallback, snapshot-local cache,
  weighted center, tensor, compression, and pure tests.
- Implemented the P11 Terra runtime boundary: server-only canonical-state
  adaptation, profile/policy reload parsing, game-thread binding validation,
  and one atomic snapshot/cache publication. Invalid reload candidates retain
  the previous snapshot.
- Implemented P11 launch capture: a new construct flight stores the resolved
  material profile, aggregate and snapshot version. It therefore survives
  later profile reloads without changing P8 payment, `massEquivalent`, or
  legacy knockback.
- Implemented the reusable generated `empty.nbt` GameTest fixture and passed
  the first dedicated P11 test for canonical stone adaptation, bounded fallback
  resolution, and initial snapshot publication.
- Completed the P11 Luna surface: bilingual Patchouli teaching, starter
  physical declarations and policy, preview-matrix coverage, and explicit
  estimate labelling in the construct resource panel.
- Remaining P11 hardening is optional dedicated-server coverage for reload,
  flight, collision, and unloaded chunks; P9 must define a separate contract
  before consuming physical values for permanent effects.
- P11 begins after remaining P8 dedicated-server transaction/protection tests
  and before P9 permanent or physics-scaled material effects.

## 21. P9 Alchemical Player Policy

- Completed Terra High policy in `docs/P9_ALCHEMICAL_PLAYER_POLICY.md`:
  self-first player targeting, no player targets from anchors, one-target P9-A
  plans, pre-mutation item escrow, bounded defensive bands, metamagic limits,
  P11 separation, and a permanent-infusion gate.
- Implemented Terra Medium slice: the talisman path escrows consumed items
  before mutation and restores them on failure; cleansing, resistance, and
  absorption are player-facing and anchors reject P9 plans. Luna follows with
  recipes, Patchouli, and previews. Luna presentation/data slice is now also
  implemented; remaining work is deeper dedicated-server fixture coverage.
- P9 introduces no mana, cooldown, terrain transmutation, cross-player buffs,
  or permanent attributes in its first implementation slice.

## 22. P10 Manuscript Acquisition And Mathemagician

- Completed Sol architecture in
  `docs/P10_MANUSCRIPT_ACQUISITION_CONTRACT.md`: validated acquisition
  snapshot, bounded loot/trade economy, independent feature controls, safe
  surplus exchange, reload reconciliation, and optional worldgen.
- Implemented P10 Terra Medium core: acquisition codecs, immutable candidate
  snapshot, alias resolution, deterministic weighted selection, and pure
  feature configuration. No loot or villager behavior changed yet.
- Implemented P10 Terra High reload boundary: lore, acquisition candidates, and
  server configuration publish atomically as one generation; bootstrap uses
  default configuration until `SERVER` config loads, and a dedicated-server
  GameTest covers the initial publication. No gameplay consumer is enabled yet.
- Implemented the first P10 gameplay consumer: the cartographer chest now uses
  a bounded dynamic loot modifier, driven only by the validated acquisition
  pool and policy generation. Empty data and disabled loot are no-ops.
- Completed P10 Luna content: four built-in acquisition records, bounded future
  trade metadata, bilingual Patchouli teaching, dynamic-loot assets, and
  preview-matrix coverage. The baseline cartographer pool is now populated.
- Implemented the P10 profession slice: a craftable Demonstration Table, POI,
  Mathemagician profession, novice paper buyback, and deterministic manuscript
  offers from levels two through five. Acquisition is gated by server config
  and does not depend on a generated house.
- Implemented P10 offer reconciliation: marked manuscript offers are checked
  against the published snapshot for loaded, non-trading Mathemagicians.
  Valid offers keep their state; rejected entries are removed and missing
  deterministic career slots are filled without touching vanilla/modded offers.
- Remaining P10 work: live dedicated-server economy and world-generation
  tests. The field house is implemented but remains disabled by default.
- Implemented the optional house as a rare, config-gated field house in
  plains-village biomes. It places a Demonstration Table and a chest using the
  validated house loot pool. This deliberately avoids overwriting vanilla
  jigsaw pools; it is not road-attached until an append-only pool API or an
  approved mixin is introduced.

## 23. P12 Consolidation And Survival Readiness

- Consolidate the open P0-P11 dedicated-server, reload, reconnect, claim,
  narrator, economy, and first-use gates into one evidence matrix.
- Completed Terra High contract: `docs/P12_SURVIVAL_READINESS_CONTRACT.md`
  freezes evidence labels, GT-01 through GT-07 GameTest requirements, DS-01
  through DS-09 dedicated-server smoke rows, M-01 through M-03 manual checks,
  and the ambiguous-failure policy.
- Distinguish pure tests, GameTests, dedicated smoke tests, manual multiplayer,
  and optional integration evidence.
- Close P8 transaction/protection acceptance before enabling destructive
  survival gameplay.
- Completed Terra Medium automated slice: `gradlew test` and all nine required
  GameTests pass; P8 now proves rollback, preflight denial, and construct block
  collision, P9 proves missing-resource preflight, P10 proves independent
  feature flags, and P11 proves future snapshot publication. The construct
  collision test exposed and fixed a null collision-context crash.
- Completed Luna preview slice: bilingual standard and compact cases now cover
  `construct-preview` and `p9-defensive-resources`; the UI contract records
  that these captures are presentation evidence only and cannot replace
  dedicated-server or multiplayer acceptance.
- Next: complete the remaining GT variants and dedicated-server smoke rows;
  Luna then updates evidence presentation and bilingual previews.
- Model sequence: Terra High complete, Terra Medium automated slice complete,
  Luna afterward.
- Contracts: `docs/P12_SURVIVAL_READINESS_CONTRACT.md` and
  `docs/P12_P15_EVOLUTION_PLAN.md`.

## 24. P13 Environmental Correspondence Field

- Define a finite-dimensional attribute field over loaded world positions,
  derived deterministically from dimension, biome, height, and salted
  server-only seed noise.
- Completed Sol architecture in
  `docs/P13_ENVIRONMENTAL_FIELD_CONTRACT.md`: distinct attribute/scalar/spatial
  vector types, schema-one caps, atomic snapshots, world-secret persistence,
  seed privacy, reload migration, declarative KubeJS limits, P5 sample-plan
  reuse, and the non-mutating Dimensional Survey boundary are frozen.
- Completed Terra High semantic review in
  `docs/P13_ENVIRONMENTAL_FIELD_SEMANTIC_REVIEW.md`: layer algebra,
  continuity/discontinuity rules, finite-difference units, exact
  `salted_value_v1` golden vectors, Dimensional Survey signal/tie rules, and
  adversarial cases are frozen before codecs exist.
- Completed Terra Medium: `WorldFieldSecretData` persists
  the private 256-bit world key, three bounded environmental channels publish
  as P5 scalar providers, `salted_value_v1` has raw-double golden tests, and
  the anchor-only Dimensional Survey emits only bounded redstone. Declarative
  datapack reload, aliases, captured execution generations, typed projection,
  and player-safe anchor reports are now live. The dedicated GameTest server
  passes all 11 registered required tests.
- Keep attribute-space vectors separate from physical `Vec3` fields and expose
  each attribute channel as a bounded scalar field.
- Add a distinct vector-field type, immutable declarative snapshots, P5 sample
  planning, and a non-mutating Dimensional Survey theorem.
- Ambient attributes are observations, not mana and not substitutes for fixed
  witnesses, catalysts, exact items, or escrow.
- Model sequence: Sol, Terra High, Terra Medium, and Luna content/evidence
  complete for P13.
- Luna evidence includes the starter `mathmod:resonance` channel, bilingual
  correspondence entry, resonance glyph, and preview-matrix target. The
  contract remains experimental and labels starter coefficients as teaching
  data rather than physical law.
- Contracts: `docs/P13_ENVIRONMENTAL_FIELD_CONTRACT.md`,
  `docs/P13_ENVIRONMENTAL_FIELD_SEMANTIC_REVIEW.md`, and
  `docs/P12_P15_EVOLUTION_PLAN.md`.

## 25. P14 Transactional Block Mutation And Destruction

- Generalize world mutation through a new plan/receipt boundary rather than
  widening P8 EMPTY_ONLY fill semantics in place.
- Begin with bounded simple-block disintegration; add harvested drops only
  after tool, loot, XP, overflow, rollback, and mod compatibility are frozen.
- Reuse P8 candidate ordering/protection and P11 capped material bands without
  deriving permission or raw damage from physical metadata.
- First theorem: a short region-composed Euclidean Bore.
- Model sequence: Sol, Terra High, Terra Medium, Luna.
- Contract seed: `docs/P12_P15_EVOLUTION_PLAN.md`.

## 26. P15 Field Dynamics And Directed Effects

- Add reusable scalar potentials, vector fields, sampling, divergence, curl,
  falloff, and effect-plan composition.
- First deliver non-destructive gravity, tagged-item magnetism, repulsion, and
  vortex effects. Terrain-affecting implosions/explosions depend on P14.
- A destructive blast is a bounded field plus a block-mutation plan, never an
  opaque executor or an uncapped vanilla explosion shortcut.
- First theorems: Gravitational Well, Ferric Recall, Divergent Pulse, then the
  P14-gated Controlled Nova.
- Model sequence: Sol, Terra High, Terra Medium, Luna.
- Contract seed: `docs/P12_P15_EVOLUTION_PLAN.md`.
