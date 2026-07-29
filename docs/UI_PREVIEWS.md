# MathMod UI Previews

The development client can open and capture MathMod screens without manual input. The harness is disabled unless `MATHMOD_UI_PREVIEW` is present.

## Programmer

```powershell
$env:MATHMOD_UI_PREVIEW = 'programmer'
$env:MATHMOD_UI_PREVIEW_WORLD = 'New World'
./gradlew runClient --no-daemon
```

Output: `run/client/screenshots/mathmod-programmer-preview.png`.

Use `talisman-incomplete-tooltip` at the minimum 640x480 window with GUI scale 2 to hover an inscribed Hop talisman in the real inventory. The stored construction line must call it an inscribed proof rather than a generic program, while the separate spell-name line remains literal. Normal use and sneak-use must appear as two short action lines, both must name witnesses, and neither may claim that the current inventory is ready. The complete tooltip must remain inside the window with JEI present. The spell identity is gold, proof metadata is muted, normal enactment is teal, and resource preparation is blue.

Run this preview in both languages after changing resource attributes. Hop's requirement must render as `Motion 1` in English and `Movimento 1` in PT-BR; raw KubeJS ids must not leak into the ordinary item tooltip.

Output: `run/client/screenshots/mathmod-talisman-incomplete-tooltip-preview.png`.

Use `chalk-tooltip` at the same minimum viewport to hover Rune Chalk in the real inventory. The tooltip must identify the chosen anchor theorem, then expose change, inscription, and erasure as separate action rows. PT-BR must keep `Agachar + usar: Apagar inscrição` on one line; no player-facing row may call the theorem a preset or program. The theorem is gold, cycling is blue, inscription is teal, and erasure is coral.

Output: `run/client/screenshots/mathmod-chalk-tooltip-preview.png`.

Use `rune-anchor-tooltip` at 640x480 with GUI scale 2 to hover the Rune Anchor block item in the synchronized real inventory. Its first line must state the compact world-carrier sequence `theorem -> inscription -> effect`; the remaining rows must expose chalk inscription, empty-hand enactment, sneak empty-hand inspection, and sneak chalk erasure. Run EN/PT-BR with JEI present. Every action must remain complete inside the window, and the tooltip must identify the Rune Anchor rather than a stale server-synchronized talisman. The sequence is gold, inscription/enactment are teal, inspection is blue, and erasure is coral.

Output: `run/client/screenshots/mathmod-rune-anchor-tooltip-preview.png`.

Use `item-use-empty-programmer` to invoke the blank talisman through `ProgrammedTalismanItem.use` on the integrated server. The capture is valid only after the real item route opens the Rune Programmer; direct menu helpers are deliberately bypassed.

Use `item-sneak-use-resources` to invoke an inscribed Hop talisman through the same public item method while secondary use is active. The capture is valid only after the real route opens Resources and the synchronized screen identifies Hop/Pulo rather than an empty talisman.

Outputs: `run/client/screenshots/mathmod-item-use-empty-programmer-preview.png` and `run/client/screenshots/mathmod-item-sneak-use-resources-preview.png`. Run both in `en_us` and `pt_br` at 640x480 with GUI scale 2 after changing item-use routing, hand synchronization, or either menu-opening path.

At 1024x800 with GUI scale 2 and JEI present, this mode is also the blank-talisman first-contact check. Tabs must read left-to-right as Theorems, Lab/Laboratory, and Talisman/Talismã; Theorems must be active, Hop/Pulo must be selected, Inscribe must be available, and Resources/Clear must remain disabled. A projected or inscribed resource heading at the lower graph boundary must be hidden unless its first cost line fits with it; scrolling must still reveal the complete section.

In the blank Programmer state, disabled Resources and Clear use the shared neutral disabled accent instead of saturated gold or coral. Inscribe remains the only semantically colored enabled action; the original role color returns when each control becomes available.

Use `programmer-scrollbar-drag` to press the Theorem scrollbar track, drag it to the final section, release, and hover the resulting thumb. The catalog must scroll to Control while Hop/Pulo remains the selected demonstrated theorem. The harness inspects both the scroll value and selected theorem, rejecting a track click that leaks through to a card. The hovered thumb is ivory, its hit target is wider than its two-pixel visual track, and mixed category/card rows remain aligned.

Output: `run/client/screenshots/mathmod-programmer-scrollbar-drag-preview.png`. Run it in `en_us` and `pt_br` after changing scrollbar geometry or Programmer mouse routing.

Run `programmer`, `theorem-node-tooltip`, `laboratory-invalid`, and `keyboard-laboratory` in both `en_us` and `pt_br` after changing rune types or their presentation. Primary graph rows, assembly titles, binding explanations, validation diagnostics, Rune Form previews, and narration must use localized type names. Raw ids such as `vec3`, `effect_plan`, and generated node ids may appear only in the explicit technical tooltip line or as invisible search aliases. The Programmer resource summary must use the same localized material names as Resources.

Use `programmer-notation-tooltip` to hover the permanent `f(x)` mark. The mark must gain a compact outline and explain the typed input-to-result transformation in the active language. It is also a keyboard-focusable narrated element, while its resting state remains visually identical to a quiet header mark.

Output: `run/client/screenshots/mathmod-programmer-notation-tooltip-preview.png`.

Use `programmer-help-entry` with Patchouli present to open a real server-backed Programmer menu and activate the compact `?` beside `f(x)`. The server must close the programmer container, wait one tick for synchronization, and open the Field Manual directly on the first-spell spread. The harness aborts if the target does not open or if the programmer container remains active behind the book.

Output: `run/client/screenshots/mathmod-programmer-help-entry-preview.png`. The first page must call the castable inscription a proof, preserving Spell/Magia as the practical effect name instead of exposing program as the default player-facing term. The ordinary `programmer` and `minimum-viewport` captures also guard the resting `?`: it must retain its symbol rather than ellipsizing, fit between the localized title and `f(x)`, and remain separate from the tabs at standard and minimum supported viewports.

Use `keyboard-first-programmer` to verify the complete entry-focus contract. On a blank talisman, the first three Tab stops must follow the same Theorems, Lab/Laboratory, Talisman/Talismã progression shown left-to-right. The harness then traverses the rest of the cycle, requires the notation widget to remain reachable, and requires the next Tab to wrap back to Theorems.

Output: `run/client/screenshots/mathmod-keyboard-first-programmer-preview.png`.

## Resources

```powershell
$env:MATHMOD_UI_PREVIEW = 'resources'
$env:MATHMOD_UI_PREVIEW_WORLD = 'New World'
./gradlew runClient --no-daemon
```

Output: `run/client/screenshots/mathmod-resources-preview.png`.

The resting header must mirror the Programmer's contextual notation pattern: a compact `?` immediately precedes `Σ(items)` / `Σ(itens)`. Help is not a fourth operational action beside `<- Proof` / `<- Prova`, Clear, and Close. Standard EN/PT-BR and minimum PT-BR captures must keep the pair inside the frame without colliding with the localized title. The left navigation command uses a neutral tone and must fit without ellipsis.

An incomplete plan must place Status/Estado and its concrete Outstanding/Pendências diagnosis before Prepared Materials/Materiais preparados and the consumed, catalyst, and attribute totals. The first viewport, including the minimum supported viewport, must therefore answer what is missing without requiring the player to scroll through the preparation or explanation of the complete plan.

Use `resources-material-tooltip` to hover the first material row. The tooltip must expose selector, tier, budget, consumed/catalyst mode, explanatory role, every attribute, and the add action. It uses the shared semantic palette: gold identity, muted technical metadata, teal budget, coral/green material role, blue attribute heading, and muted attribute values and action guidance. At the minimum 640x480 window and GUI scale 2, the complete frame must retain visible space from every window edge and JEI footer; no attribute or action line may be removed to make it fit.

Attribute rows use their active-language display names. Stable material id and selector remain in the technical metadata, while unknown pack attribute ids fall back to a readable label rather than snake case.

Every resting catalog row must also show a teal `+` before its scrollbar, without waiting for hover. Prepared Materials uses one coral `-` on the first visible line of each selection. These are persistent action affordances, not separate hitboxes: the complete row remains clickable and keyboard-activatable, and text wrapping reserves their width.

Output: `run/client/screenshots/mathmod-resources-material-tooltip-preview.png`.

Run this mode in both `en_us` and `pt_br` at 640x480 after changing material attributes, tooltip copy, tooltip positioning, or the supported minimum viewport. Also run `theorem-node-tooltip` at the same minimum viewport after changing the shared contextual-tooltip renderer; its typed binding details and complete frame must remain visible.

Ordinary Resources previews suppress contextual material and selected-loadout tooltips just like ordinary Programmer previews. A mode ending in `-tooltip` opts back into contextual hover. This keeps resting-state captures inspectable even when the host cursor remains over a row, without changing hover behavior during normal gameplay.

Use `resources-clear-tooltip` on a mixed fixed-plus-prepared loadout. The harness activates Clear once, waits while requiring the complete preparation to remain unchanged, then hovers the armed `Confirm?` / `Confirma?` control. The compact label must fit without ellipsis, and the tooltip must explain that the second activation removes every prepared material while fixed Theorem costs remain in the inscription. Run it in EN and PT-BR at 640x480 with GUI scale 2.

Output: `run/client/screenshots/mathmod-resources-clear-tooltip-preview.png`.

Use `resources-notation-tooltip` to hover `Σ(items)` / `Σ(itens)`. The tooltip must define the complete casting plan as fixed requirements plus prepared materials, remain bounded with JEI present, and expose the same explanation through keyboard focus and narration.

Output: `run/client/screenshots/mathmod-resources-notation-tooltip-preview.png`.

Use `resources-long-name-tooltip` to persist the 32-character authored name `Hipotese da Convergencia Celeste`, open Resources, and hover only its clipped visible portion. The proof name must end in a bounded ellipsis before the Materials panel, while the tooltip recovers the complete authored text. A name that fits does not create an invisible full-row tooltip target.

Output: `run/client/screenshots/mathmod-resources-long-name-tooltip-preview.png`. Run it in both `en_us` and `pt_br`, plus PT-BR at the minimum supported viewport after changing panel widths, heading translations, clipping, or the authored-name limit.

Use `keyboard-first-resources` to require the Materials catalog as the first Tab stop, traverse through the complete resource-screen cycle, reach contextual help followed by the `Σ(items)` notation widget, and wrap back to Materials. The focused panel uses teal and its current material row uses ivory. This keeps keyboard entry on the preparation task instead of the navigation toolbar.

Output: `run/client/screenshots/mathmod-keyboard-first-resources-preview.png`.

Use `resources-back-to-proof` to open a real server-backed Resources menu, activate `<- Proof` / `<- Prova`, wait for the menu transition, and capture only after the Rune Programmer opens on the same inscribed talisman. The harness aborts if the destination does not arrive; its expected-screen state changes from Resources to Programmer only after the click is sent.

Output: `run/client/screenshots/mathmod-resources-back-to-proof-preview.png`. Run it in both languages after changing resource-menu button ids, the navigation label, or the Programmer/Resources transition.

Use `resources-help-entry` with Patchouli present to open a real server-backed Resources menu and activate the header `?` by its semantic widget message rather than a positional action index. The server must close the resource container, wait one tick for menu synchronization, and then open the Field Manual directly on the Resource Costs choice spread. The harness aborts if Patchouli never opens or if the resource container remains active behind the book.

Output: `run/client/screenshots/mathmod-resources-help-entry-preview.png`. Run it in both `en_us` and `pt_br` after changing the Resources-to-manual bridge, its localization, or the target entry/page.

Use `resources-add-remove` to open a real server-backed resource menu, record the first localized catalog material, click that visible row twice, wait for quantity 2 of its canonical server id, scroll the diagnostic-first left panel until the selected Prepared Materials row is wholly visible, remove one unit through that semantic row, and wait for quantity 1 before capture. The harness aborts if any synchronized transition is skipped. In PT-BR this deliberately proves that visible `Aço` maps to canonical `steel`; it must not assume catalog order follows ids or that Prepared Materials begins at a fixed Y coordinate. The material viewport must end after a complete row. The final prepared row retains its coral `-`, while catalog rows retain teal `+` controls without colliding with localized names or the scrollbar.

Use `resources-cleared` to start with two prepared material kinds and activate Clear through the real menu. The first activation must preserve the exact preparation and expose the localized confirmation label for at least six client ticks; only the second activation may send the clear request. The harness then waits for an empty synchronized preparation. The final Clear control must be disabled and explain on hover that there are no prepared materials to remove; fixed Theorem costs remain conceptually separate. A section heading at the lower viewport boundary must remain hidden unless its first content line also fits, so `Attributes` / `Atributos` never appears as an orphaned final row.

The final disabled Clear indicator uses the neutral disabled accent rather than coral. Its tooltip still carries the reason and scope, so availability is not communicated by color alone.

Outputs: `run/client/screenshots/mathmod-resources-add-remove-preview.png` and `run/client/screenshots/mathmod-resources-cleared-preview.png`.

Use `resources-scrollbar-drag` to open a server-backed Vector Wave/Onda vetorial preparation containing exactly one material, drag the Materials scrollbar to the final localized rows, release, and hover the thumb. The harness waits for synchronization, requires positive scroll, and rejects any changed material id or quantity. This guards the former click-through path where pressing the visible track added the material row beneath it.

Output: `run/client/screenshots/mathmod-resources-scrollbar-drag-preview.png`. Run it in `en_us` and `pt_br`; the hovered thumb is ivory and the final material card remains complete.

Use `keyboard-added-materials` to reach Prepared Materials through Tab, select its first row with Home, and remove one unit with Enter. Use `keyboard-material-catalog` to focus the material catalog, select its final localized row with End, record that row's stable id, and add it with Enter. Both modes use the real server menu, wait for the held talisman to synchronize to the recorded id, retain focus after mutation, and abort if the expected quantity is not observed. EN ends on Tin; PT-BR ends on Pó de Redstone, proving that keyboard order follows presentation rather than canonical ids.

Outputs: `run/client/screenshots/mathmod-keyboard-added-materials-preview.png` and `run/client/screenshots/mathmod-keyboard-material-catalog-preview.png`. Run both in `en_us` and `pt_br`; the focused panel uses teal, the current row uses ivory, and automatic reveal must keep the entire row inside its viewport.

Use `minimum-resources` at a 640x480 window with GUI scale 2 to exercise the Resources screen at the minimum supported 320x240 logical viewport. When the centered screen cannot retain a 64 px side reserve for an item overlay, its compact frame must reserve 26 logical pixels above and below instead. The last gold border pixel must remain visibly separate from JEI's footer controls, both scrollable panels must stay usable, and only complete material and preparation rows may render.

Output: `run/client/screenshots/mathmod-minimum-resources-preview.png`.

## Patchouli

Patchouli previews are optional and use Patchouli's own server command without adding a compile-time dependency. Place a compatible NeoForge Patchouli JAR in `run/client/mods`, then use `patchouli-landing` to open the Field Manual landing spread, `patchouli-current-state` to open the current-state entry, or `patchouli-resource-costs` to open the resource-choice spread.

Outputs: `run/client/screenshots/mathmod-patchouli-landing-preview.png`, `run/client/screenshots/mathmod-patchouli-current-state-preview.png`, and `run/client/screenshots/mathmod-patchouli-resource-costs-preview.png`. These modes abort when Patchouli is absent, wait for a real `GuiBook` screen, and should be run in both `en_us` and `pt_br` after documentation copy or layout changes.

Use `patchouli-parallel-proofs` to open the complete bilingual `Convergent Proofs` / `Provas convergentes` spread. Both pages must distinguish equivalent typed relations from identical notation, keep theorem provenance separate from ownership, and remain above Patchouli's page controls.

Output: `run/client/screenshots/mathmod-patchouli-parallel-proofs-preview.png`.

The Resource Costs choice spread must use Prepared Materials/Materiais preparados consistently with the GUI and retain its final fixed-Theorem-cost line above the page controls. The English page previously clipped that line after a terminology expansion; both localized real-client captures are required after changing this spread.

The landing preview is outside the entry matrix and must be run separately. It verifies the localized book name, neutral localized subtitle, short mathemagician introduction, and internal first-spell link. The title nameplate must remain on the left page without obscuring `Categories` / `Categorias`, and the introductory text must remain above the page controls.

The English current-state spread is also the stale-feature and bottom-margin check: Rune Chalk and Rune Anchors must be described in the present tense, and the final non-player-repulsion line must remain visibly above the page boundary.

Use `patchouli-custom-programmer-reset` to open the Laboratory erasure/reset spread directly. It must teach both two-activation safeguards without conflating their scopes: Erase removes the talisman inscription, while `0 -> 0?` proposes clearing only the local Laboratory sequence and authored spell name. The reset explanation must explicitly preserve the talisman inscription and remain above Patchouli's page controls in EN and PT-BR.

Output: `run/client/screenshots/mathmod-patchouli-custom-programmer-reset-preview.png`.

Use `patchouli-kubejs-materials` to open the final KubeJS material page directly. It must explain `setMaterialTranslationKey(id, key)`, retain stable ids/selectors as hover diagnostics rather than catalog labels, and remain above the page controls in EN and PT-BR.

Output: `run/client/screenshots/mathmod-patchouli-kubejs-materials-preview.png`.

Use `patchouli-matrix` to capture every PT-BR entry spread in one client session. The checked-in matrix currently contains 68 spreads across 32 entries and is required by a unit test to cover every `entries/**/*.json` page pair exactly once; each capture is named `mathmod-patchouli-matrix-<entry>-p<page>-preview.png`. The harness waits 30 client ticks after each real server-command navigation, captures one rendered frame, closes the page, and advances to the next target. If death or another interruption closes the book, the current target is reopened and retried up to five times instead of silently leaving a stale capture.

Use `patchouli-advanced-mathematics` to open the Sampled Calculus / Cyclic Symmetry spread directly. It must distinguish sampled calculus from symbolic functions and explain cyclic elements without presenting them as ordinary numbers. The following specialist-evidence page, covered by `patchouli-matrix`, names Quartz, Copper, Lapis Lazuli, and Prismarine Crystals as configurable examples rather than universal ingredients. Output: `run/client/screenshots/mathmod-patchouli-advanced-mathematics-preview.png`.

## A0-LU-01 Current-Surface Content Requirements

## L0-TM-04 Functional Projection

Use `rune-inspector-functional` to capture the read-only server projection in
EN and PT-BR at 1024x800, and PT-BR at 640x480 (GUI scale 2). The capture must
show all three labels: authored source not executable, checked canonical
binding from one server attempt and not persisted, and compiled graph as the
executable authority. It must also keep a mismatch or stale diagnostic visible
without obscuring the graph canvas. This is a presentation capture only: it
must not send a request, compile locally, save, repair or migrate the talisman.

These are content and evidence requirements for existing surfaces, not new
preview modes. They are owned by the later screen integrator when executable
coverage is needed.

- The current Guided surface remains player-visible as `Theorems` and
  `Laboratory`; no preview may present `Guided` as a new mode or persisted id.
- The `Rune Inspector` preview must identify the projection as read-only and
  must not label it `Advanced`.
- Formula shorthand, including `f(x)`, must be described as compact
  presentation. It must not be described as persisted Source or a textual
  Function editor.
- Current EN/PT-BR previews must use the same changed Patchouli entry pair and
  must reject raw unresolved translation keys, mojibake, clipping, or English
  leakage in PT-BR.
- Inspector focus/narration evidence must cover the selected node, rune,
  output type, purity, formula, cost, dynamic inputs, and the read-only state;
  it must not depend on color alone.
- Missing presentation evidence may show the bounded technical rune/form/
  category identity, but must not select another form, invent a localized
  identity, or change the graph.

The requirements above consume only existing surfaces and ids. They do not
authorize mutable Advanced editing, textual Source, Discipline selection, or
notation-profile selection.

## Preview Matrix

The current acceptance matrix is exposed by `UiPreviewMatrix` and is checked
by `UiPreviewMatrixTest`. Each case is a real `MATHMOD_UI_PREVIEW` mode and
records locale, viewport, GUI scale, and the required JEI environment:

| Mode | Locale | Viewport | Scale | Purpose |
| --- | --- | --- | --- | --- |
| `programmer` | EN/PT-BR | 1024x800 | 2 | Standard Programmer baseline |
| `minimum-viewport` | PT-BR | 640x480 | 2 | Compact boundary and text containment |
| `laboratory-parameter-dialog` | EN/PT-BR | 1024x800 | 2 | Numeric parameter entry |
| `laboratory-parameter-dialog` | PT-BR | 640x480 | 2 | Parameter dialog compact boundary |
| `patchouli-current-state` | EN/PT-BR | 1024x800 | 2 | In-game documentation entry |
| `patchouli-manuscript-record` | EN/PT-BR | 1024x800 | 2 | Bilingual manuscript conjecture spread |
| `patchouli-manuscript-record` | PT-BR | 640x480 | 2 | Compact manuscript spread boundary |
| `patchouli-matrix` | PT-BR | 1024x800 | 2 | Complete Patchouli spread matrix |
| `construct-preview` | EN/PT-BR | 1024x800 | 2 | P12 construct body and bounded launch preview |
| `construct-preview` | PT-BR | 640x480 | 2 | Compact construct preview boundary |
| `p9-defensive-resources` | EN/PT-BR | 1024x800 | 2 | P12 defensive resource and failure-state preview |
| `p9-defensive-resources` | PT-BR | 640x480 | 2 | Compact defensive-resource boundary |

The parameterized cases must show the Simpson form, its independent bounds
and samples, and the resulting cost summary without clipped fields or overlap.
The manuscript-reader cases remain intentionally absent from this executable
matrix until the dedicated reading screen exists. The current manuscript
preview covers the Patchouli record reached by the lore layer, not a claim that
the future item reader is implemented.

## P12 Runtime Evidence Previews

The P12 Luna slice reuses the real client preview harness for the player-facing
surfaces that correspond to the automated GameTests. `construct-preview` shows
the transient construct, its selected material, bounded motion, and collision
status. `p9-defensive-resources` shows the defensive theorem's fixed and
prepared witnesses, including the failure state where preflight rejects an
insufficient loadout.

Each mode is covered in EN and PT-BR at the standard viewport, with a PT-BR
640x480 boundary capture. These captures verify terminology, clipping, and
action hierarchy only. They do not replace the nine passing server GameTests,
dedicated-server smoke rows, or multiplayer checks in
`docs/P12_SURVIVAL_READINESS_CONTRACT.md`.

The P12 preview contract is intentionally bounded:

- no preview may claim that a construct breaks or replaces terrain;
- no resource preview may claim that an ambient attribute satisfies an item
  witness;
- a failed preparation or cast must keep its player-facing diagnosis visible;
- EN/PT-BR labels must preserve the same fixed-cost versus prepared-material
  distinction used by the Resources screen.

## Planned Manuscript Previews

These modes are specified for the future manuscript reading slice and do not exist yet:

- `manuscript-reading`: first bilingual built-in record, standard viewport.
- `minimum-manuscript-reading`: longest valid title/page in the compact viewport.
- `manuscript-missing-definition`: held id no longer resolves after reload.
- `manuscript-missing-patchouli`: pages remain readable while Field Manual navigation is disabled with an explanation.
- `manuscript-theorem-link`: explicit available and unavailable demonstration actions.
- `keyboard-manuscript-reading`: previous/next, close, and optional reference actions retain visible focus and narration.

Every mode runs in EN/PT-BR with JEI present. The reading surface may paginate or scroll text, but it must not resize type by viewport width, overlap navigation, expose execution controls, or silently replace a missing reference. See `docs/MANUSCRIPTS.md`.

## P6 Progression Network

`field-ledger-overview` and `field-ledger-epiphanies` now render the active P6
snapshot with three epiphanies and three discoveries. The latter advances one
Harmonic study counter before opening the tab, so it verifies mixed complete
and incomplete progress without client-side inference.

The PT-BR `patchouli-matrix` includes
`lore-bound_measure-p0`, `lore-ledger_of_remainders-p0`, and
`lore-weighted_gathering-p0`. These spreads prove
that each new component-backed manuscript has bounded bilingual lore and a
truthful next practice route. The future dedicated manuscript reader modes
listed above remain unimplemented.

## Laboratory

```powershell
$env:MATHMOD_UI_PREVIEW = 'laboratory'
$env:MATHMOD_UI_PREVIEW_WORLD = 'New World'
./gradlew runClient --no-daemon
```

Output: `run/client/screenshots/mathmod-laboratory-preview.png`. This variant loads a valid two-step local-frame spell so undo, reset, validation, categorized actions, and coordinate-frame nodes are visible.

At the 1600x900 wide viewport with JEI, the Laboratory toolbar must render the complete `Resources` / `Recursos` label. `Reso...` is a layout regression: only compact layouts may replace that command with the narrated `Σ` symbol. This boundary is verified in both languages at 1600x900 and in PT-BR at the compact 1024x800 viewport.

At compact height, the palette must end after a complete rune row. The scrollbar communicates additional content; no partial text may appear against the lower panel border.

Use `minimum-viewport` at a 640x480 window with GUI scale 2 to exercise the minimum supported 320x240 logical viewport. JEI may collapse its ingredient list, but the MathMod frame must remain inside the window and above JEI's footer controls. When the centered screen cannot retain a 64 px side reserve, the same 26 px vertical item-overlay reserve used by Resources applies. Laboratory category headings must appear only when their first rune also fits; the corresponding Theorem rule must reserve its full first 30 px card.

Output: `run/client/screenshots/mathmod-minimum-viewport-preview.png`.

Use `laboratory-binding-tooltip` to hover the first binding in the same construction. The tooltip must expand its symbolic endpoints into localized rune names, identify the flowing type, and retain the exact node ids for pack debugging.

Output: `run/client/screenshots/mathmod-laboratory-binding-tooltip-preview.png`.

Use `theorem-node-tooltip` to select Right Angle and hover its second proof node. The tooltip must show the localized rune, numbered input source, flowing type, and exact node/rune ids.

Output: `run/client/screenshots/mathmod-theorem-node-tooltip-preview.png`.

Use `theorem-formula-tooltip` to select Right Angle and hover the compact statement under the graph title. The complete statement may occupy two stable lines, preferring the outer effect-argument boundary instead of leaving a punctuation fragment or `...`; the typed tree must begin below the complete block. The tooltip must explicitly say that it is not editable source and that the numbered runes form the complete typed proof. A long selected theorem title must force the workflow seal back to its compact sigil instead of truncating the theorem name.

Every theorem formula must state its final world effect through the outermost `push`, `blink`, or `mark` verb. A formula beginning with an intermediate assignment such as `v=` or `p=` is a didactic regression even when its graph remains executable.

Output: `run/client/screenshots/mathmod-theorem-formula-tooltip-preview.png`.

Use `keyboard-theorem-statement` to reach the same statement semantically through Tab. Its teal focus outline must enclose only the two-line statement block, with no editor fill or caret, and the harness must reject a focus cycle that cannot reach `TheoremStatementWidget`.

Output: `run/client/screenshots/mathmod-keyboard-theorem-statement-preview.png`.

Use `type-legend-tooltip` to select Right Angle and hover the four-quadrant output-family mark in the graph header. The tooltip must name all four color families and state that exact types remain on node rows. Its secondary instruction must retain readable contrast at GUI scale 3 while the four semantic family colors remain visually dominant.

Output: `run/client/screenshots/mathmod-type-legend-tooltip-preview.png`.

## Invalid Laboratory Output

```powershell
$env:MATHMOD_UI_PREVIEW = 'laboratory-invalid'
$env:MATHMOD_UI_PREVIEW_WORLD = 'New World'
./gradlew runClient --no-daemon
```

Output: `run/client/screenshots/mathmod-laboratory-invalid-preview.png`. This variant activates the first visible Rune Form by semantic palette navigation, inserts only Self Player, and leaves a typed `Player` output. The harness requires Undo to become active before capture and aborts when no graph mutation occurred; it must never accept an empty Laboratory caused by stale click coordinates. The resulting screen verifies that the assembly communicates its intermediate type and keeps Inscribe disabled until the output becomes `Unit`.

## Inscription Flow

Use `inscription-pending` to click Inscribe and capture the server-confirmation wait state. Use `inscription-confirmed` to simulate the synchronized talisman and capture the automatic transition to the Talisman tab. Its action row must say `Replace` / `Substituir` instead of duplicating the `Theorems` / `Teoremas` tab label; at the compact viewport the same command may shorten to `Trocar`.

```powershell
$env:MATHMOD_UI_PREVIEW = 'inscription-confirmed'
$env:MATHMOD_UI_PREVIEW_WORLD = 'New World'
./gradlew runClient --no-daemon
```

Outputs: `run/client/screenshots/mathmod-inscription-pending-preview.png` and `run/client/screenshots/mathmod-inscription-confirmed-preview.png`.

Run `laboratory-invalid`, `inscription-pending`, `inscription-confirmed`, and `custom-name-reinscription` in PT-BR at the minimum 640x480 window with GUI scale 2 after changing palette geometry, graph feedback, inscription synchronization, authored-name wrapping, or semantic colors. The invalid construction uses the shared coral failure color, subordinate headings use muted text, and confirmed/reinscribed proofs use the shared green success color. Every state must remain distinct and fully contained.

Use `replace-proof-tooltip` to open an inscribed Hop/Pulo proof on the Talisman tab and hover its replacement command. The harness requires the localized standard or compact action to be active before capture. Its tooltip must explain that the command only opens Theorems and that the held talisman changes after a new proof is inscribed, not when the catalog opens.

Output: `run/client/screenshots/mathmod-replace-proof-tooltip-preview.png`.

Ordinary screen captures begin only after four consecutive stable client ticks and two complete `ScreenEvent.Render.Post` passes. The final framebuffer capture clears stale scissor state. Dynamic scenarios must keep this stabilization contract so a server-backed tab or name transition cannot produce a partial header that looks like a real layout regression.

## Proof Workflow Seal

Use `workflow-demonstrated-tooltip` with a blank talisman to verify the teal demonstrated state. Use `workflow-witnesses-tooltip` to open an inscribed Hop proof with an impossible diamond loadout and verify the coral missing-witness state. Use `workflow-ready-tooltip` to install the recommended Hop loadout plus a matching inventory witness and verify that the seal becomes green only when the real player cost plan succeeds.

Outputs: `run/client/screenshots/mathmod-workflow-demonstrated-tooltip-preview.png`, `run/client/screenshots/mathmod-workflow-witnesses-tooltip-preview.png`, and `run/client/screenshots/mathmod-workflow-ready-tooltip-preview.png`.

The seal sits before the proof title and must not displace the selected Theorem statement or collide with the output-family legend. Graph panels at least 176 logical pixels wide show a fixed-width sigil plus localized one-word state label only when the complete current title still fits; narrower panels or long titles keep the 10 px sigil. The Laboratory always keeps the compact sigil so `Assembly -> type` / `Montagem -> tipo` remains readable. Each state also exposes a localized two-line tooltip and narration. Its title uses the state color and its actionable explanation uses the shared muted color rather than Minecraft's default gray; ordinary previews suppress the tooltip so the resting header can be audited independently.

Run the resting `programmer` preview at 1024x800 with JEI in EN and PT-BR after changing workflow labels or graph-header geometry. `Demonstrated` and `Demonstrada` must fit without ellipsis, while `minimum-viewport` at 640x480 must retain the compact sigil and complete Laboratory output type.

## Inscription Action States

Use `already-inscribed-tooltip` to open an inscribed Hop theorem and hover its disabled Inscribed control. The tooltip must explain that the same proof is already bound, that another Theorem replaces it, and that the current resource preparation remains unchanged.

Use `resources-active-tooltip` to hover Resources on that same proof. The control must remain available while redundant inscription is disabled, and its tooltip must direct the player to prepare witnesses and catalysts for the inscribed spell.

Outputs: `run/client/screenshots/mathmod-already-inscribed-tooltip-preview.png` and `run/client/screenshots/mathmod-resources-active-tooltip-preview.png`.

Both modes replace the recommended Hop loadout with a custom diamond loadout, re-inscribe the identical graph through the real item data-component path, and abort the preview if that preparation changes. This keeps the visual audit tied to the persistence behavior it describes.

## Program Erasure

Use `clear-confirmation-tooltip` to open an inscribed Hop talisman through a real server-backed programmer menu, wait for the initialized screen, click Erase once, and hover the armed confirmation. The compact destructive control must show the complete Confirm label and its tooltip must name the graph, authored name, Laboratory sequence, and resource preparation in scope. The delayed click is intentional: an initialization-time synthetic click does not represent a player interaction and can race menu synchronization.

Use `clear-confirmed` to send the second click after the menu has synchronized. The harness waits for the client talisman to lose its program and aborts if that never happens. The resulting Theorems tab must retain Hop as a local demonstration, show Inscribe as available, and disable Resources and Erase.

Outputs: `run/client/screenshots/mathmod-clear-confirmation-tooltip-preview.png` and `run/client/screenshots/mathmod-clear-confirmed-preview.png`.

These are server-backed previews rather than client-only screen constructions, so their button packets exercise the real menu and held-item synchronization path.

## Cost Summary

Use `cost-summary` to open an inscribed Blink theorem whose fixed Ender Pearl witness is missing from the preview inventory. This verifies that the programmer names fixed items and abstract attributes separately instead of reporting an unexplained generic cost failure.

Output: `run/client/screenshots/mathmod-cost-summary-preview.png`.

At the minimum 640x480 window with GUI scale 2 and JEI present, this mode also captures the top of the Inscribed Proof text viewport. The proof name, origin, and first guidance lines must remain inside the left panel; overflow must show a teal scrollbar instead of crossing the panel border, gold frame, or JEI footer.

Use `saved-palette-scrolled` with the same Blink talisman to wheel-scroll that semantic text viewport to its final row before capture. Every localized line must remain available. The last guidance is state-dependent: a proof with missing witnesses directs the player to Resources, while a cast-ready proof directs the player to close the programmer and use the talisman. The old fixed five-line cap must not return.

Output: `run/client/screenshots/mathmod-saved-palette-scrolled-preview.png`.

Run both modes in `en_us` and `pt_br` at the minimum viewport after changing Inscribed copy, panel dimensions, localization, wrapping, scrollbar geometry, or item-overlay reserves. Wheel scrolling advances only across real line/spacer boundaries. With keyboard focus on the Inscribed Proof panel, Up/Down moves one boundary, Page Up/Page Down moves by a viewport, Home/End reaches the limits, and narration reads the complete proof summary plus the available navigation keys.

Use `saved-witnesses-guidance` to open an inscribed Hop proof whose deliberately wrong diamond preparation cannot satisfy its witnesses. The proof seal must be coral and the first visible guidance must direct the player to Resources. At 640x480 with GUI scale 2, the localized PT-BR action remains visible and the scrollbar preserves the rest of the explanation.

Output: `run/client/screenshots/mathmod-saved-witnesses-guidance-preview.png`.

Use `saved-ready` to open the same inscribed Hop proof with its Feather already present. This resting state must direct the player back into the world without allowing the optional Resources inspection to visually impersonate the next required action.

Output: `run/client/screenshots/mathmod-saved-ready-preview.png`.

Use `saved-ready-tooltip` to hover the blue Resources inspection in that state. Its localized tooltip must begin by marking the action optional and repeat the actual next step: close the programmer and use the talisman.

Output: `run/client/screenshots/mathmod-saved-ready-tooltip-preview.png`.

Run `saved-witnesses-guidance`, `saved-ready`, and `saved-ready-tooltip` in both `en_us` and `pt_br` after changing proof workflow, action tones, Resources guidance, or inscription readiness. The pair is a semantic contrast test: missing witnesses require a coral proof seal plus gold preparation action; a ready proof requires a green seal plus blue optional inspection. Gold Resources in the ready state is a regression even when the button remains mechanically usable.

Use `saved-ready-live-tooltip` for the synchronization boundary that static fixtures cannot prove. It opens a server-backed Hop talisman without its Feather, keeps the exact same Programmer instance open, and changes the server inventory through `missing -> ready -> missing -> ready`. The harness rejects any intermediate Resources tone other than `RESOURCE -> INSPECTION -> RESOURCE -> INSPECTION`, then hovers the final action. The capture must show the green ready seal, blue Resources inspection, world-cast guidance, and localized optional tooltip together. Run it in EN/PT-BR at 1024x800 with JEI.

Output: `run/client/screenshots/mathmod-saved-ready-live-tooltip-preview.png`.

## Disabled Theorem Editing

Use `edit-theorem-disabled-tooltip` with a preserved Blink theorem already inscribed. The harness positions the cursor over the disabled Edit control and waits for its tooltip, proving that the screen explains why only Laboratory sequences can be reloaded.

Output: `run/client/screenshots/mathmod-edit-theorem-disabled-tooltip-preview.png`.

## Disabled Empty Actions

Use `blank-clear-tooltip` with a blank talisman. The harness requires the visible Clear/Apagar control to be disabled before hovering it. The tooltip must explain that the talisman has no inscribed proof to erase; it must not reuse the armed-confirmation scope or imply that a destructive action is currently available.

Use `laboratory-empty-undo-tooltip` to open a blank Laboratory. The harness requires the visible Undo/Desfazer control to be disabled before hovering it. The tooltip must name the missing prerequisite, a Rune Form and therefore a Laboratory step, instead of merely describing what Undo normally does.

Outputs: `run/client/screenshots/mathmod-blank-clear-tooltip-preview.png` and `run/client/screenshots/mathmod-laboratory-empty-undo-tooltip-preview.png`.

Run both modes in `en_us` and `pt_br` at 1024x800 with GUI scale 2 and JEI present after changing Programmer action state, localization, tooltip positioning, or compact-toolbar geometry. These previews are semantic checks as well as visual checks: they abort if a hidden duplicate button is selected or if the target control is active.

## Keyboard Palette

Use `keyboard-theorem` or `keyboard-laboratory` to focus the manually rendered palette, move through enough rows to exercise automatic scrolling, and activate the selected entry with Enter. These modes verify the ivory keyboard-focus outline independently from the colored theorem/action state.

Outputs: `run/client/screenshots/mathmod-keyboard-theorem-preview.png` and `run/client/screenshots/mathmod-keyboard-laboratory-preview.png`.

The Laboratory variant also verifies that automatic keyboard scrolling keeps the focused rune fully inside the whole-row viewport. Run it in both `en_us` and `pt_br`: after the focused Boolean rune is activated, the invalid-proof diagnosis must name both the required `Unit` output and the current `bool` output without truncation or English leakage.

Use `theorem-catalog-control` to move keyboard focus to the final theorem without activating it. Together with `programmer` and `keyboard-theorem`, this covers the beginning, middle, and end of the catalog in EN and PT-BR: every card must show a complete unique catalog formula without `...`, while hover, narration, and the selected statement retain the exact full formula. Every Programmer preview also checks all 27 formulas against the real Minecraft-font width available in the current palette before capture.

After changing advanced mathematical runes, use `advanced-theorem-harmonic`, `advanced-theorem-orthogonal`, `advanced-theorem-quarter-turn`, and `advanced-theorem-quadrature` in EN/PT-BR. Their selected graphs must show localized rune/type names, complete two-line formulas, and resource requirements without raw `cyclic_element`, `resonance`, `continuity`, `orientation`, or `symmetry` ids. Use `advanced-laboratory-symmetry` to search the compact Laboratory by the stable `cyclic` alias; the result must expose the localized Symmetry category and quarter-turn Rune Form without clipping.

Outputs: `run/client/screenshots/mathmod-advanced-theorem-harmonic-preview.png`, `mathmod-advanced-theorem-orthogonal-preview.png`, `mathmod-advanced-theorem-quarter-turn-preview.png`, `mathmod-advanced-theorem-quadrature-preview.png`, and `mathmod-advanced-laboratory-symmetry-preview.png`.

Output: `run/client/screenshots/mathmod-theorem-catalog-control-preview.png`.

Use `theorem-formula-tooltip` to verify the selected theorem's compact-statement explanation and its one-line field lineage. The lineage must remain secondary to the statement, use the same structured provenance as keyboard narration, and fit in both EN/PT-BR without adding permanent explanatory text to the graph viewport.

Output: `run/client/screenshots/mathmod-theorem-formula-tooltip-preview.png`.

Use `compact-toolbar-tooltip` at 1024x800 with JEI to hover the compact `<-` action. The custom name and `Inscribe`/`Inscrever` label must remain complete; `<-`, `0`, and `Σ` must have stable 26 px hitboxes, while the tooltip exposes the localized full action instead of relying on symbolic guesswork. Reset confirmation changes only the fixed reset mark from `0` to `0?`; it must not resize or shift this toolbar.

Output: `run/client/screenshots/mathmod-compact-toolbar-tooltip-preview.png`.

Use `compact-palette-tooltip` to scroll the Laboratory to the geometry boundary and hover `self∈R`. Every visible compact macro must use a complete, unique formula rather than ellipsized prose; the hover must recover the localized `Self In Region`/`Jogador Na Regiao` name, `bool` output, and defaults guidance.

Output: `run/client/screenshots/mathmod-compact-palette-tooltip-preview.png`.

The same tooltip treats palette entries as Rune Forms rather than pretending each row is one primitive rune. On a blank proof, `self∈R` must identify region and position as inferred premises and report the exact rune/binding expansion for the current click.

Use `laboratory-form-reuse-tooltip` to load the existing Right Basis proof, filter the palette to `push_self`, and hover the only result. The tooltip must identify player and vector as reused current inputs and report an expansion of one rune and two bindings. The PT-BR tooltip must fit inside the 1024x800 window with JEI present.

Output: `run/client/screenshots/mathmod-laboratory-form-reuse-tooltip-preview.png`.

## Laboratory Search

## Authoring Registry Palette

Use `authoring-registry-palette` to open the Guided Laboratory through the normal programmer path and verify that the visible category/form order comes from the frozen authoring snapshot. Exercise pointer selection, keyboard traversal and Enter activation, a registry search query, a numeric descriptor dialog, and Inspector narration. Capture EN/PT-BR at 1024x800 with JEI and PT-BR at the 640x480 compact boundary; the technical fallback remains the form id/path when presentation is unavailable and must never substitute another form.

The bounded `A0-TM-03F3` path first activates non-parameterized `Self` with keyboard Enter and asserts the Guided workspace mutation. It then focuses the actual Laboratory search box, asserts Simpson is the filtered first form, pointer-activates it, and asserts the active dialog plus all five finite numeric defaults. The same registry form presentation policy supplies the rendered row, tooltip, and palette narrator title; its unit evidence covers the technical-name fallback because built-in metadata intentionally has complete translations. Actual captures are retained under `run/client/screenshots/mathmod-authoring-registry-palette-{en_us-1024x800,pt_br-1024x800,pt_br-640x480}-preview.png`, with matching harness logs under `run/client/logs/authoring-registry-palette-*.log`.

Use `laboratory-search` to focus the Laboratory search field and filter the rune catalog. Use `laboratory-search-localized` under PT-BR to search for `vetor` through translated labels. Use `laboratory-search-empty` to verify the explicit zero-result state and disabled palette navigation. A focused search field must use the shared teal outline, retain four pixels of horizontal text inset, and leave the unfocused name field on the subdued grid outline.

Outputs: `run/client/screenshots/mathmod-laboratory-search-preview.png`, `run/client/screenshots/mathmod-laboratory-search-localized-preview.png`, and `run/client/screenshots/mathmod-laboratory-search-empty-preview.png`. Run the localized and empty modes at 1024x800 with JEI for the compact layout; the ordinary search mode should also be captured at 1600x900 for the standard layout.

## Custom Spell Names

Use `custom-name-default` under PT-BR to open an inscribed unnamed Laboratory construction. The harness asserts that no English fallback was persisted and the Inscribed panel must present the localized default. Use `custom-name-explicit` to persist `Hipotese de Gauss`, open Laboratory, and verify that authored text is returned literally.

Use `laboratory-reset-confirmation-tooltip` through a real server-backed Programmer. It opens the named two-step construction, activates Reset/Zerar once, waits six ticks, and requires both the name and exact action sequence to remain unchanged. The compact danger mark becomes `0?`; its localized tooltip must name the Laboratory sequence and spell name as the reset scope while explicitly preserving the talisman's inscription.

Use `custom-name-reset` for the confirmed path. Only the second activation may clear the local name and sequence or send `RESET_CUSTOM_BUTTON`; the harness requires the localized server action-bar confirmation and also requires the held talisman to retain `Hipotese de Gauss`, its saved actions, and its inscription. The final reset control is disabled with a discoverable empty-state tooltip.

Outputs: `run/client/screenshots/mathmod-custom-name-default-preview.png`, `run/client/screenshots/mathmod-custom-name-explicit-preview.png`, `run/client/screenshots/mathmod-laboratory-reset-confirmation-tooltip-preview.png`, and `run/client/screenshots/mathmod-custom-name-reset-preview.png`.

Use `text-field-focus` under PT-BR at 1024x800 with JEI to focus `Hipotese de Gauss` without changing it. The name frame must become teal, the unfocused search frame must remain on the grid tone, and neither text nor caret may touch the chamfered outline or collide with the compact action row.

Output: `run/client/screenshots/mathmod-text-field-focus-preview.png`.

Use `custom-name-reinscription` for the server-backed name-only path. It opens `Hipotese Antiga`, reloads its two-step Laboratory proof, changes only the name to `Hipotese Renovada`, and inscribes again. The preview aborts unless graph, name, and action sequence all synchronize; the final Inscribed panel and success feedback must both show the complete renewed name. The `Q.E.D.` / `C.Q.D.` confirmation may wrap to additional graph lines, but it must not ellipsize the authored name or depend on the obscured world action bar.

Output: `run/client/screenshots/mathmod-custom-name-reinscription-preview.png`.

## Cast Failure

## Manuscript Reader

`manuscript-reader` covers the resolved reader in EN/PT-BR, including the
compact PT-BR 640x480 boundary. `manuscript-reader-missing` covers the explicit
unavailable record instead of substituting another manuscript. The Manual
control must disable when Patchouli or the declared target is unavailable; the
Theorem control must enter the read-only Inspector and never open the mutable
Programmer. These matrix cases are deterministic capture obligations; the
dedicated-server reload and narrator checks remain separate acceptance gates.

Use `cast-missing-item` to remain in the world, install a Hop talisman with an unavailable 64-diamond loadout, execute it server-side, and capture the translated action-bar diagnosis. This verifies that structured cost deficits survive the full execution-to-HUD path.

Output: `run/client/screenshots/mathmod-cast-missing-item-preview.png`.

Use `cast-missing-attribute` to inscribe Hop with an empty preparation and invoke the public talisman route on the integrated server. The harness waits for the exact localized action-bar component and rejects raw attribute ids: EN must report `Motion 1`, PT-BR must report `Movimento 1`, and both must direct the player to resource preparation.

Output: `run/client/screenshots/mathmod-cast-missing-attribute-preview.png`.

## World Anchor Journey

Use `anchor-journey` to exercise one continuous integrated-server route through Rune Chalk and a Rune Anchor:

1. Normal-use the chalk to change from Anchor Pulse to Sacrifice Pulse.
2. Inscribe the anchor, serialize it with `saveWithFullMetadata`, and verify `program_preset`.
3. Recreate a distinct block entity with `BlockEntity.loadStatic`, install it in the level, and require the Sacrifice Pulse identity plus its complete graph before any later interaction.
4. Sneak-use the anchor with an empty hand and verify the named inscription.
5. Normal-use without a witness and require the missing-amethyst diagnosis.
6. Place one Amethyst Shard, normal-use again, require successful execution, and verify exact witness consumption.
7. Sneak-use with chalk to erase the inscription.
8. Repeat the chalk erasure and require the distinct no-inscription diagnosis rather than another success message.
9. Inspect with an empty hand and require the empty state.

Every checkpoint waits for both the server-side invariant and the exact localized client action-bar component. The NBT reconstruction is a real save/load API roundtrip and replacement of the live block entity, but it is not a chunk unload or disk-I/O test; those remain dedicated persistence scenarios. The preview creates a short clear sightline before placing the anchor at eye height, so terrain cannot hide a mechanically successful result. Run it in EN and PT-BR with GUI scale 2; the final frame must contain the anchor, Rune Chalk, the complete empty-state message, and any particles still visible from the successful proof.

Output: `run/client/screenshots/mathmod-anchor-journey-preview.png`.

## First Spell Journey

The first-spell modes exercise one continuous server-backed journey rather than constructing isolated client screens:

1. Start with a blank talisman and exactly one Feather.
2. Open the real programmer menu and select the first Hop theorem.
3. Inscribe it and wait for the held talisman to synchronize.
4. Optionally open Resources to inspect the recommended one-Feather preparation.
5. Use the visible header `X` to return to the world, execute the talisman server-side, consume exactly one Feather, and verify upward movement.

Use `first-spell-inscribed` to stop after synchronized inscription. Because the harness already carries the recommended Feather, the proof must be cast-ready immediately: the seal is green and the Inscribed guidance directs the player to close the programmer and use the talisman rather than unconditionally opening Resources. The panel must use the current Theorem/Resources vocabulary, `Q.E.D.` or `C.Q.D.` must be visible, and the validation status must fit in the graph viewport.

Use `first-spell-close-tooltip` to hover the compact `X` after that ready inscription. It must sit immediately before `?` and `f(x)`, fit with the localized title at 640x480, and explain that returning to the world preserves both the talisman inscription and Laboratory work. Only this close tooltip may render: a hidden theorem statement with stale hover state must not leak its tooltip across the Talisman tab transition.

Use `first-spell-ready` to stop when the real cost plan succeeds. It must identify the active proof in the loadout heading, show one localized Feather/Pena under Prepared Materials/Materiais preparados and Total Consumed/Consumo total, localize exact-item and configured tag-material catalog rows, direct the player to close and use the talisman, and leave no orphaned section heading or recipe toast. This preview specifically guards that an automatically recommended Feather is described as prepared rather than falsely attributed to a player add action.

The same capture is the first-journey action-discovery check: Feather/Pena must carry a coral `-`, every visible catalog material must carry a teal `+`, and neither symbol may replace the existing hover explanation or reduce the full-row mouse and keyboard target.

Use `first-spell-cast` to finish the direct journey from the synchronized ready Programmer. The harness activates the visible, enabled header `X`, requires the Hop inscription to remain on the held talisman after closing, and only then follows the public item-use route. It aborts unless one Feather is consumed and the player's vertical movement increases by at least `0.3`. Successful execution must display `therefore` (`∴`) followed by the localized proof name and effect message.

Outputs: `run/client/screenshots/mathmod-first-spell-inscribed-preview.png`, `run/client/screenshots/mathmod-first-spell-close-tooltip-preview.png`, `run/client/screenshots/mathmod-first-spell-ready-preview.png`, and `run/client/screenshots/mathmod-first-spell-cast-preview.png`.

The preview-only server player is invulnerable during this journey so nearby mobs cannot stall GUI synchronization before capture. This protection is deterministic harness setup, not part of talisman execution; witness consumption, public item use, and movement assertions remain unchanged.

Use `advanced-harmonic-cast` for the corresponding advanced-mathematics journey. It selects Harmonic Step through the real keyboard catalog, inscribes its graph, closes the Programmer, and casts through the public talisman route. The harness aborts unless one Feather is consumed, one Quartz catalyst remains, and the resulting horizontal impulse is at least `0.4`. Output: `run/client/screenshots/mathmod-advanced-harmonic-cast-preview.png`.

Use `alchemy-vital-infusion-cast` for the alchemical mixed-resource journey. It selects Vital Infusion through the real keyboard catalog, inscribes its typed graph, closes the Programmer, and casts through the public talisman route. The harness aborts unless exactly two Vital Salt are consumed, the Homuncular Matrix remains, the registered Vital Infusion effect is present, and the player's maximum health increases while the effect is active. Output: `run/client/screenshots/mathmod-alchemy-vital-infusion-cast-preview.png`.

Use `metamagic-parsimony-cast` for the tier-IV accounting journey. It inscribes Axiom of Parsimony with two Axiomatic Ink, casts through the public talisman route, requires both units to be consumed before the effect is granted, then proves the active effect makes a one-ink follow-up plan valid. Output: `run/client/screenshots/mathmod-metamagic-parsimony-cast-preview.png`.

Use `metamagic-conservation-cast` for the catalyst journey. It inscribes Conservation Lemma with two Recursive Seals, casts through the public route, requires both catalysts to remain, and verifies the registered Conservation effect. Output: `run/client/screenshots/mathmod-metamagic-conservation-cast-preview.png`.

Use `patchouli-alchemical-effects` to open the first recipe spread in the bilingual Alchemical Effects entry. It verifies that the new standard crafting recipes render inside Patchouli rather than requiring a custom JEI category. Output: `run/client/screenshots/mathmod-patchouli-alchemical-effects-preview.png`.

The first-spell Patchouli spread follows the same conditional path: inscription may already produce a ready proof when preparation and inventory agree, while Resources remains the place to inspect or adjust costs. Its closing page must use an in-world mathemagician voice, frame Hop as a small inspectable proof, and avoid developer-facing descriptions of GUI scope or future editor features. `programmer-help-entry` verifies the changed instruction, while the PT-BR `patchouli-matrix` target `basics-can_i_make_spell-p4` verifies the closing page and its bounds.

## Coordinate Frame Theorem

```powershell
$env:MATHMOD_UI_PREVIEW = 'frame-theorem'
$env:MATHMOD_UI_PREVIEW_WORLD = 'New World'
$env:MATHMOD_UI_PREVIEW_WIDTH = '1024'
$env:MATHMOD_UI_PREVIEW_HEIGHT = '800'
./gradlew runClient --no-daemon
```

Output: `run/client/screenshots/mathmod-frame-theorem-preview.png`. This variant scrolls the theorem catalog to Right Angle and displays its typed local-frame graph. With the ATM10 JEI jar in `run/client/mods`, the 1024x800 variant verifies the real item overlay beside the adaptive compact layout.

The mode performs four real mouse-wheel steps and clicks the resulting card. Category headers are 16 px high while theorem cards are 30 px high, so the palette must scroll between actual row boundaries rather than by a uniform pixel offset. Every visible theorem must retain its complete icon, name, formula, outline, and hit target; neither edge may show or accept a partial card.

Run this mode in EN and PT-BR at 1024x800 and in EN at the minimum 640x480 window after changing statement geometry. The preview checks every full theorem formula against the current graph width before capture; no statement may require more than two lines. Right Angle must break after `push(self,`, keep the complete frame expression on line two, and preserve node hover alignment after the tree moves downward.

Use `basis-icon-family` to keep Right Angle, Planar Dash, and Oblique Leap together in the theorem catalog. Use `basis-icon-laboratory` to focus the matching Right Basis Vector, Forward Basis Vector, and Oblique Basis Vector actions. The two captures verify that the shared coordinate-frame motif remains recognizable while each direction has a distinct glyph.

Outputs: `run/client/screenshots/mathmod-basis-icon-family-preview.png` and `run/client/screenshots/mathmod-basis-icon-laboratory-preview.png`.

The harness installs a temporary talisman in the selected development world's player hand, opens the requested screen, waits for a stable render, arms after `ScreenEvent.Render.Post`, captures the completed framebuffer in `RenderFrameEvent.Post` so item overlays are included, and closes the client. Do not point it at a world whose inventory must remain untouched.

Contextual MathMod hover tooltips are suppressed in ordinary automated previews so an incidental host cursor cannot cover the audited state. A preview mode ending in `-tooltip` retains contextual hover rendering when the tooltip itself is the subject of the audit.
