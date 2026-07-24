# MathMod UI And Interaction Audit

This document tracks visual, behavioral, and teaching issues in the Rune Programmer, Talisman Resources, world feedback, and Patchouli field manual. It separates verified fixes from open design work.

## Verified Viewports

- 640x480 window, GUI scale 2, JEI 19.27.0.343: minimum Programmer and Resources layouts in English and PT-BR. Both centered frames end above the item-overlay footer while retaining complete scroll rows and reachable overflow.
- 1024x800 window, GUI scale 2, JEI 19.27.0.343: Resources resting state in English and PT-BR after shared contextual-hover suppression, plus the deliberate PT-BR material-tooltip state with corrected diacritics.
- 1024x800 window, GUI scale 2, JEI 19.27.0.343: programmer, coordinate-frame theorem, Laboratory, and resources in English; Laboratory and resources in PT-BR.
- 1024x800 window, GUI scale 2, JEI 19.27.0.343: inscription pending/confirmed in English, confirmed inscription and fixed-cost summary in PT-BR.
- Keyboard palette with JEI: Theorems at 1024x800 and 1600x900; Laboratory at 1024x800. The harness reaches the palette through Tab, scrolls with Down, and activates with Enter.
- Laboratory search with JEI: filtered results at 1600x900, plus filtered, localized PT-BR, and zero-result states at 1024x800. All fit their palette without crossing into the graph.
- Custom spell names with JEI at 1024x800 in PT-BR: localized unnamed inscription, literal authored name, bilingual server-backed `0 -> 0?` reset confirmation, and confirmed reset-to-unnamed states.
- Text-field focus with JEI: focused English search at 1600x900 and focused PT-BR authored name at 1024x800, covering both standard and compact field geometry.
- Server-backed name-only Laboratory reinscription with JEI at 1024x800 in English and PT-BR: renewed name, unchanged graph, and unchanged action sequence synchronize before success is shown.
- Disabled preserved-Theorem editing tooltip with JEI at 1024x800 in English and PT-BR.
- Mixed fixed-plus-prepared resource plan with JEI at 1024x800, plus a bilingual 640x480 armed Clear confirmation whose label and complete scope tooltip remain inside the window.
- Full material tooltip with JEI at 1024x800: English at GUI scale 2 and PT-BR at GUI scale 3, including role explanation, complete attribute list, and add action.
- Dense contextual tooltips with JEI at the minimum 640x480 window and GUI scale 2: the complete material tooltip passes in English and PT-BR, the theorem-node tooltip passes in PT-BR, and both retain their full frame plus a stable outer margin without dropping semantic lines.
- Laboratory palette with JEI at 1024x800 in English and PT-BR, plus a keyboard-scrolled state: only complete rune rows are rendered and actionable.
- Laboratory typed bindings with JEI at 1024x800 in English and PT-BR: compact notation, semantic type colors, localized hover explanations, and technical ids fit without crossing panel bounds.
- Right Angle theorem with JEI at 1024x800 in English and PT-BR: numbered localized nodes and semantic input-binding tooltips remain contained at GUI scale 2.
- Output-family legend on Right Angle with JEI at 1024x800: English and PT-BR at GUI scale 2, plus PT-BR at GUI scale 3.
- Coordinate-frame icon family with JEI at 1024x800: the three related theorem glyphs in English and the three Laboratory actions in English and PT-BR remain distinct at GUI scale 2.
- Mixed-height Theorem palette with JEI at 1024x800 in English and PT-BR: four mouse-wheel steps and a click keep complete 30 px theorem cards around 16 px category headers.
- Invalid Laboratory output with JEI at 1024x800 in English and PT-BR: the keyboard-driven Boolean construction reports the required `Unit` type and current `bool` type completely in both languages.
- Dynamic inscription states with JEI at the minimum 640x480 window in PT-BR: a real one-node invalid Laboratory construction keeps Inscribe disabled, pending verification disables mutations without covering the proof, confirmed Hop/Pulo inscription shows its complete `C.Q.D.` result, and name-only reinscription preserves the complete `Hipotese Renovada` name in both the saved panel and success feedback.
- Compact Laboratory toolbar with JEI at 1024x800 in PT-BR: the name field and `Inscrever` remain complete, while `<-`, `0`, and `Σ` use stable icon hitboxes and localized hover guidance.
- Compact Laboratory palette with JEI at 1024x800 in English and PT-BR: visible macros use complete formulas such as `|v|`, `v·look`, `d(v,self)`, and `self∈R`; hover restores the localized full name, output type, and defaults guidance.
- Rune Form expansion previews with JEI at 1024x800 in English and PT-BR: a blank proof exposes inferred region/position premises, while a loaded proof exposes reused player/vector inputs and the exact rune/binding delta.
- Theorem formula bridge with JEI at 1024x800: full selected Right Angle formula in English and a compact explanatory tooltip in PT-BR at GUI scale 2.
- Full selected theorem statements with JEI: Right Angle uses a semantic two-line `push(self,` / frame-expression break in EN and PT-BR at 1024x800 and in EN at 640x480; the tree, scrollbar, and second-node tooltip follow the displaced viewport, while the harness rejects any of the 19 formulas requiring a third line.
- Theorem catalog formulas with JEI at 1024x800 in English and PT-BR: beginning, middle, and final-card captures retain complete unique symbolic summaries; the preview harness checks all 27 expressions against the real 78 px Minecraft-font boundary before capture.
- Talisman replacement intent with JEI: PT-BR compact layouts at 640x480 and 1024x800 render `Trocar`, while the 1600x900 wide layout renders `Substituir`; English renders `Replace`. The bilingual tooltip stays bounded and states that catalog navigation is non-mutating until a new inscription.
- Laboratory resource action with JEI: the 1600x900 wide layout renders complete `Resources` and `Recursos` labels without touching the frame, while the 1024x800 PT-BR compact layout retains the separate narrated `Σ` action.
- Proof workflow seal with JEI at 1024x800: demonstrated and cast-ready states in English, missing-witness state in PT-BR, plus a resting Right Angle header without tooltip at GUI scale 2.
- Inscription action states with JEI at 1024x800: already-inscribed guidance in English and active-resource guidance in PT-BR, including runtime preservation of a custom material preparation.
- Program erasure with JEI at 1024x800: complete compact confirmation label and scope tooltip in English, plus a server-backed synchronized post-erasure state in PT-BR.
- Server-backed resource preparation with JEI at 1024x800: add twice, observe quantity 2, remove once, observe quantity 1, then arm Clear without mutation and confirm a separate mixed preparation to empty; both armed and final states are verified in English and PT-BR.
- Keyboard resource preparation with JEI at 1024x800 in English and PT-BR: Tab reaches Prepared Materials and Materials, Home/End move to deterministic boundaries, Enter mutates the real held talisman, focus survives synchronization, and automatic reveal retains complete rows.
- First-spell journey with JEI at 1024x800 in English and PT-BR: blank talisman to synchronized Hop/Pulo inscription, named one-Feather/Pena ready plan, successful named server cast, exact witness consumption, and upward movement.
- First-spell ready Resources at the minimum 640x480 window in English and PT-BR: Prepared Materials/Materiais preparados remains complete above one Feather/Pena, while the two panels, scrollbars, frame, and JEI footer remain separate.
- Resource-row action affordances with JEI at 1024x800 in English and at the minimum 640x480 window in PT-BR: every material card reserves a teal `+`, every prepared selection exposes one coral `-`, and both symbols remain separate from localized text and scrollbars.
- Incomplete Hop talisman tooltip in the real inventory at the minimum 640x480 window in English and PT-BR: the inscribed-proof identity and conditional witness guidance wrap fully inside the window and remain separate from JEI controls.
- Rune Chalk tooltip in the real inventory at the minimum 640x480 window in English and PT-BR: the chosen anchor theorem and three world actions remain complete, while the longer PT-BR erasure gesture stays on one row above JEI controls.
- State-directed Inscribed guidance with JEI: a cast-ready Hop proof at 1024x800 in English directs the player to close and cast, while a missing-witness Hop proof at the minimum 640x480 viewport in PT-BR exposes `Abra Recursos` and retains the complete wrapped explanation through scrolling.
- Live witness-readiness transition with JEI at 1024x800 in English and PT-BR: one unchanged Programmer instance follows server-synchronized `missing -> ready -> missing -> ready` inventory changes; proof seal, saved guidance, Resources tone, and optional tooltip remain in agreement after every transition.
- Blank-talisman first contact with JEI at 1024x800 in English and PT-BR: Theorems opens with Hop/Pulo selected, Inscribe is the only primary action available, and the graph viewport ends on a complete semantic section.
- First-contact tab hierarchy at 1024x800 in English and PT-BR, plus PT-BR at the minimum 640x480 window: Theorems, Lab, and Talisman/Talismã follow construction order from left to right, retain complete labels, and the compact frame remains above JEI's footer controls.
- Dynamic action labels with JEI at 1024x800 in English: an already-inscribed theorem displays `Inscribed`, and the first destructive click displays `Confirm?`; the compact Laboratory toolbar retains its fixed `<-`, `0`, and `Σ` glyphs.
- Disabled empty-action guidance with JEI at 1024x800 in English and PT-BR: blank-talisman Clear/Apagar explains that no proof is inscribed, while empty-Laboratory Undo/Desfazer names the missing Rune Form prerequisite.
- Header-notation tooltips with JEI at 1024x800 in English and PT-BR: `f(x)` and `Σ(items)` / `Σ(itens)` retain their resting positions, gain a bounded hover/focus outline, and explain their mechanical meaning without covering the frame or item overlay.
- Shared notation-header hierarchy with JEI at 1024x800 in English and PT-BR, plus PT-BR at the minimum 640x480 window: both primary screens render contextual help immediately before their mathematical mark; localized titles, `?`, and notation remain bounded and distinct.
- Localized resource notation with JEI at 1024x800 and the minimum 640x480 window: English renders `Σ(items)`, PT-BR renders `Σ(itens)`, and the translated width reflows the shared help/notation pair without collision. The PT-BR tooltip repeats the same localized mark.
- Keyboard entry order with JEI at 1024x800: a blank talisman focuses the active Theorems tab first, Resources focuses the Materials catalog first, both notation marks remain reachable later in the same Tab cycle, and focus wraps to the original task control.
- Programmer keyboard progression with JEI at 1024x800 in PT-BR: the first three Tab stops mirror the visual Theorems, Lab, Talisman/Talismã order before reaching the remaining controls and wrapping from notation to Theorems.
- Contextual Resources help with Patchouli at 1024x800 in PT-BR: the real `?` action closes its server-backed resource menu and opens the Resource Costs choice spread without leaving an active container behind the book.
- Contextual Programmer help with Patchouli at 1024x800 in PT-BR: the compact header `?` closes its server-backed programmer menu and opens the first-spell spread without leaving an active container behind the book.
- Programmer header at 1024x800 and the minimum 640x480 window in PT-BR: `?` retains its literal symbol, remains between the localized title and `f(x)`, and does not collide with tabs, JEI controls, or the frame.
- World HUD at 1024x800: server-side missing-item cast diagnosis in English and PT-BR.
- Reviewed PT-BR terminology at 1024x800 with JEI: keyboard Theorem, keyboard Laboratory, and missing-item HUD; plus the minimum 640x480 Programmer. Accented item, rune, action, validation, and status copy remains contained.
- Patchouli with its real NeoForge runtime at 1024x800 in PT-BR: the checked matrix now covers 45 spreads across 24 entries. Every title must remain inside its page and every final line above the non-scrollable page boundary.
- First-spell Patchouli continuity at 1024x800: the real Programmer help bridge exposes the conditional Resources instruction, and the PT-BR matrix closing spread renders `Uma prova pequena` in an in-world mathemagician voice without clipping.
- Patchouli landing spread with its real NeoForge runtime at 1024x800 in English and PT-BR: book identity, subtitle, introduction, and first-spell link are localized; the English nameplate no longer obscures `Categories`, and both introductions retain clear page-control margin.
- Patchouli current-state and world-anchor spreads with their real NeoForge runtime at 1024x800 in English and PT-BR: implemented chalk/anchor wording follows the theorem/proof/inscription ladder and retains clear bottom margin.
- The bilingual Weighted Gathering manuscript conjecture spread is covered by the checked Patchouli matrix, including the PT-BR compact viewport case.
- Patchouli Resource Costs choice spread with its real NeoForge runtime at 1024x800 in English and PT-BR: prepared-material terminology matches the GUI and the final fixed-cost sentence remains fully above the page boundary.
- Theorem formula hover at 1024x800 in English and PT-BR after structured provenance: lineage remains one secondary line and does not enter the graph viewport.
- Patchouli Convergent Proofs spread at 1024x800 in English and PT-BR: the first draft exposed a title collision across the spine; the final localized titles remain inside their pages and both text columns stay above navigation.
- 1024x800 window, GUI scale 3, JEI 19.27.0.343: coordinate-frame theorem, Laboratory, and resources.
- 640x480 window, GUI scale 2, JEI 19.27.0.343: minimum 320x240 logical Laboratory viewport in English and PT-BR.
- Pure layout tests: 512x400, 427x240, 341x266, and 320x240 logical viewports.

At GUI scale 3, the MathMod screen uses almost all available width and JEI hides its ingredient list because no useful side column remains. MathMod content stays inside the window and does not overlap the item overlay.

## Fixed Visual Issues

- Compact Laboratory actions no longer place Cost outside the right screen border.
- Wide Laboratory actions no longer give the textual Resources/Recursos command the same 46 px width as Undo and Reset. The final control now uses the available right margin, while compact layouts keep the discoverable `Σ` symbol and tooltip.
- Resource panel headings no longer intersect the horizontal divider.
- Resource, material, palette, and proof panels now draw scroll tracks and proportional thumbs only when content exceeds their viewport. The visible track has an expanded mouse target, the thumb becomes ivory on hover/drag, and both track jump and direct drag preserve complete row/card boundaries.
- Long Laboratory graph rows expose their complete text on hover.
- The font-ambiguous derivation turnstile between palette and proof was removed; panel structure and headings carry the relationship without an unreadable glyph.
- Laboratory action accents use output-type colors instead of category color alone.
- Standard and compact layouts retain their outer border, panel spacing, and JEI reservation.
- Laboratory palette viewports use whole-row heights and strict boundary checks, preventing the next rune's first pixels from leaking through the lower panel edge.
- Laboratory graph viewports render only complete lines, preventing partial status/resource text and invisible hover targets at the lower edge.
- Theorem graph cards use localized rune names and stable `#n` references instead of generated node and rune ids as their primary labels.
- Compact PT-BR tabs use the deliberate `Lab` label, and every themed button ellipsizes text inside its own bounds instead of drawing over adjacent controls.
- Compact Erase controls reserve enough width for the destructive confirmation label instead of reducing it to an ambiguous ellipsis.
- Resource text and material viewports render only complete rows. Material scrolling advances by the 24 px row height, and the residual panel strip is neither rendered nor clickable.
- Resource section headings are kept with their first content row. `Attributes` / `Atributos` no longer appears alone at the lower viewport boundary while its values remain hidden below the scroll cut.
- Programmer resource summaries follow the same continuity rule. `Projected resources` / `Recursos previstos` and the inscribed equivalent render only when their first cost line also fits, so the graph never ends on an orphaned resource heading.
- Textual MathButtons now render their current message instead of the construction-time label. Server-driven `Inscribe -> Inscribed` and local `Clear -> Confirm?` transitions therefore remain visually synchronized with tooltips and narration, while icon actions keep their deliberate fixed glyph.
- The compact graph-validity message now fits on one line in both languages instead of clipping its budget total in the PT-BR first-spell view.
- Theorem scrolling now stops only on real category/card boundaries. A card is rendered and clickable only when its icon, name, formula, outline, and full hit area fit inside the viewport.
- Validation details are no longer silently limited to two wrapped lines; the graph viewport and its scrollbar retain the complete diagnosis.
- The compact Laboratory toolbar no longer reduces four adjacent commands to unrelated ellipses. It preserves the primary action as text and uses the mathematical `<-`, `0`, and `Σ` shorthand only for actions with localized tooltips and full narration labels.
- Compact Laboratory rows no longer collapse long localized macro names into repeated ellipses. All 38 actions have unique bounded notation used only for compact rendering; both that visible notation and localized names are searchable, while full names remain authoritative for tooltips, narration, and the assembled graph.
- Server-confirmed inscription feedback now wraps through the graph viewport instead of ellipsizing the authored proof name. The complete renewed name remains visible in both `Q.E.D.` and `C.Q.D.` confirmations without relying on the dimmed world action bar behind the screen.
- Secondary tooltip text no longer uses Minecraft's low-contrast dark gray on a near-black background. Formula guidance, default-input notes, technical identifiers, material roles, empty attributes, add/remove actions, and the type-legend instruction now use the theme's muted tone with a tested contrast ratio above `4.5:1`.
- Semantic status colors no longer change between Programmer panels, Resources, buttons, proof seals, and tooltips. Success is shared green, failure and missing requirements are shared coral, subordinate structure is muted, and selection/disabled surfaces use named theme constants. Theorem, graph, workflow, Rune Form, type-legend, and material tooltips no longer introduce vanilla named colors; automated contrast and source-policy tests guard both literal and named-color regressions.
- Laboratory name and search fields no longer place borderless EditBox text directly against their decorative frame. Both use a tested internal inset and the same thematic state hierarchy: grid at rest, muted on hover, and teal on focus.
- Laboratory and Theorem category headings no longer appear alone at the bottom of a narrow palette. Each heading is rendered only when its first 16 px rune row or 30 px theorem card also fits; bilingual minimum-viewport captures guard the boundary.
- The Laboratory palette no longer labels guided macros as if every row were one primitive rune. `Rune Forms` / `Formas Runicas` names the actual interaction model without widening the panel beyond compact bounds.
- Minimum-width centered screens no longer extend behind JEI's bottom controls. Programmer and Resources preserve their normal height when a 64 px side reserve fits; otherwise their compact layouts reserve 26 logical pixels at the top and bottom for the item overlay. The former 24 px reserve left the gold border on the footer's first row; layout tests and bilingual 640x480 captures now guard a visible separation while preserving complete scroll rows.
- The Inscribed Proof summary no longer renders procedural text beyond its compact left panel or silently drops localized lines after a fixed cap. It is a clipped semantic viewport with a visible scrollbar, complete-line wheel movement, keyboard Up/Down/Page Up/Page Down/Home/End navigation, and dedicated narration. Minimum EN/PT-BR top-and-bottom captures prove that the final cost guidance remains reachable without touching the gold frame or JEI footer.
- Ordinary Resources previews no longer inherit a material tooltip from the host cursor. Programmer and Resources now share one tested preview-hover policy: ordinary modes expose the resting layout, while explicit `-tooltip` modes retain contextual inspection.
- The invalid-Laboratory preview no longer clicks a stale hard-coded palette coordinate and silently captures an empty workspace. It activates the first Rune Form through semantic palette focus and refuses to capture unless Undo proves that the graph actually changed.
- Dynamic previews no longer capture the first render immediately after a synchronized name, tab, or menu transition. Four stable ticks and two complete screen renders are required before the framebuffer is read, preventing a transient partial header from being recorded as a GUI defect.
- The PT-BR Resources surface now renders its title and core vocabulary with correct diacritics and sentence case, including `Recursos do Talismã`, `Preparação`, `Orçamento`, `Materiais preparados`, and total-plan headings.
- The remaining PT-BR Programmer, item, block, HUD, validation, rune, and Patchouli copy now uses reviewed diacritics and sentence case while technical ids, tags, and formulas remain literal only where they serve notation or diagnostics. Rune types and resource attributes have separate bilingual presentation keys.
- The complete PT-BR Patchouli matrix no longer clips titles or final lines. Four overwide titles were shortened, four overflowing text pages were condensed, and two near-limit pages gained bottom margin without removing player actions, lore concepts, or cost distinctions.
- A checked-in Patchouli target matrix derives every even page spread from every PT-BR entry. A real-client batch preview captures all 44 targets, retries navigation after death or an interrupted screen, and a unit test rejects missing, duplicated, or stale matrix coverage when pages change.
- The English Resource Costs choice page no longer clips its final fixed-Theorem-cost line. Its interaction copy was condensed without removing mouse, keyboard, hover, Clear, or total-plan guidance, and the PT-BR counterpart retains the same actions with safe bottom margin.
- Disabled themed buttons no longer retain a fully saturated semantic bar after their text, surface, and border become inactive. Their indicators share one neutral disabled accent, while enabled resource, inspection, primary, and destructive actions retain their gold, blue, teal, and coral roles. Real-client Programmer EN and cleared-Resources PT-BR captures verify the hierarchy.
- Source and runtime review found no current EN/PT-BR collision between localized Theorem/Laboratory category headings and their decorative rules. Material catalog and prepared-material rows also already distinguish mouse hover, keyboard cursor, and focused-panel states through separate fill, outline, and panel treatments, so neither area was changed without a reproducible defect.

## Fixed Behavioral And Teaching Issues

- The Laboratory Save action is enabled only when the current graph validates as an executable `Unit` program.
- Scrollbar input is resolved before row activation. Clicking or dragging the Theorem/Laboratory/Talisman palettes, proof graph, resource diagnosis, or material catalog scrollbar can no longer select a theorem, apply a Rune Form, add a material, or remove a prepared material beneath the visible track. Server-backed Resources and real Programmer drag previews reject those mutations after allowing synchronization time.
- The assembly heading reports its current output type, such as `Assembly -> player` or `Assembly -> unit`.
- Laboratory action tooltips report their result type and disclose that missing inputs may use current values or safe defaults.
- Every action's declared result type is checked against the graph it actually builds.
- Program deletion requires two clicks and states that it also removes the saved name, custom sequence, and resource loadout.
- The resource screen help control opens the Field Manual directly at the Resource Costs choice spread. It is disabled with an explicit tooltip when Patchouli is absent, and its server-side transition waits for the resource container to close before opening the book.
- Resources help no longer masquerades as a fourth command after `<- Proof`, Clear, and Close. It now forms the same `?` plus notation header pair as the Programmer, appears immediately before `Σ(items)` visually and by keyboard, and is located semantically by automated help navigation.
- The visible Resources mark no longer hard-codes the English word `items` in PT-BR. Symbol text, hover targeting, tooltip copy, and narration now share one localized component, while language-quality tests reject future `items` residue.
- The programmer header now offers the same concise contextual bridge at the point of first contact, opening the first-spell spread rather than placing tutorial prose inside the work surface.
- Compact icon actions use icon-specific horizontal padding, so narrow `?`, arrow, reset, and sum controls preserve their intended symbols instead of falling back to ellipses.
- MathMod contextual tooltips use a shared bounded positioner. Their content is width-wrapped when necessary, while the frame remains inside a real viewport margin instead of being clipped at the bottom or side edge.
- The Field Manual landing page no longer presents English MVP notes or a mixed-language ordinal edition. It identifies the book as an in-world mathemagic field manual, introduces the player as a mathemagician, and links directly to the first playable spell before offering historical context.
- Saved/Salva was first renamed to Inscribed/Inscrita, then refined to Talisman/Talismã after behavioral review showed that an adjective suggested a status or saved-spell library. The tab now names the physical held item; its panel still names the proof's inscribed state.
- The PT-BR Laboratory reset action is labeled `Zerar` instead of retaining the English `Reset`.
- Laboratory reset no longer destroys an irreversible local construction on first activation. Its fixed symbol changes from `0` to `0?` without moving the compact toolbar; the narrated message and bounded tooltip name sequence plus spell name as scope and preserve the talisman's inscription. Only the second activation clears client and server workspaces. Applying or undoing a Rune Form, editing the name, changing tabs, opening Resources, or starting inscription disarms the proposal; an empty workspace disables reset with an explicit reason.
- Disabled actions no longer describe an operation that cannot occur in the current state. Clear/Apagar distinguishes an empty talisman from pending confirmation and a stored proof; Undo/Desfazer distinguishes an empty Laboratory from an assembled step. The state policy is pure, bilingual, unit-tested, and exercised through real disabled-widget hover previews.
- Inscription now has an explicit pending state, blocks duplicate saves, waits for the expected graph to reach the held talisman, and transitions to Inscribed only after confirmation.
- Confirmed inscription uses the concise proof markers `Q.E.D.` and `C.Q.D.`; a failed synchronization times out with a visible retry message.
- Resource editing is enabled only when the displayed graph matches the graph inscribed on the talisman. Preview tooltips explain that the construction must be inscribed first.
- A cast-ready talisman no longer leaves Resources styled as required preparation. The control becomes a blue inspection action whose bilingual tooltip explicitly marks it as optional and returns the player to the real next step: close the Programmer and cast. Missing-witness states retain the gold Resources action and direct guidance to preparation.
- Readiness-dependent controls no longer update only when the inscription changes. The Programmer observes the derived workflow state while it remains open, so server-synchronized witness gains and losses immediately refresh Resources color and tooltip alongside the already-live proof seal and guidance.
- The graph distinguishes projected from inscribed resources and names missing fixed items and required abstract attributes before the budget total.
- Patchouli now uses the same Theorems, Laboratory, Talisman/Talismã, Reset/Zerar, and resource-flow terminology as the GUI.
- The English current-state entry no longer describes the implemented Rune Chalk and Rune Anchor as merely planned.

## Planned Narrative UX

- `docs/MANUSCRIPTS.md` keeps the first manuscript slice read-only: title, tradition, pages, and explicit optional navigation, with no inscription or execution action.
- Core theorems remain discoverable in the Programmer; manuscript loot cannot become a random gate for baseline casting.
- Missing records, Patchouli entries, and theorem references need distinct explained states rather than disappearing or selecting substitutes.
- The Programmer now exposes exactly one structured theorem-provenance line in theorem/formula hover and keyboard narration. History and chronology remain in Patchouli or the future manuscript reader.
- Provenance names a field lineage or synthesis, not an inventor. Hop is explicitly a shared exercise, and no catalog theorem is assigned to Boundary Builders merely to balance representation.
- Manuscript mouse, keyboard, narration, compact viewport, reload, and JEI coexistence require deterministic previews before the reading item ships.
- Theorem and Laboratory palettes now participate in Tab focus, support Up/Down/Home/End plus Enter/Space, keep the keyboard row visible while scrolling, and provide standard click sound feedback.
- Focus uses a teal palette outline plus an ivory row outline so keyboard position remains distinct from theorem selection and rune type/category colors.
- Palette narration reports the current entry, list position, theorem formula or action output type, and activation keys.
- Laboratory mouse scrolling advances by one complete rune row, and hit testing rejects the residual panel strip outside the aligned viewport.
- Laboratory search matches localized rune labels, localized categories, output types, and technical rune ids without case or accent sensitivity. Rendering, click hit-testing, scrolling, keyboard focus, and narration share the filtered ordering.
- A zero-result search renders an explicit message, disables palette activation, and cannot apply an invisible rune.
- Failed casts preserve structured deficits from the server plan and report a specific missing item selector, attribute, or budget amount in the action bar.
- Resource attributes remain structured until player-facing components are assembled. Talisman tooltips, Programmer summaries, Resources totals and deficits, material tooltips, and failed-cast messages therefore show `Movimento`, `Força`, or another active-language label rather than freezing KubeJS ids such as `motion` into English text. Unknown pack attributes receive a readable fallback and can provide `attribute.mathmod.<id>` through resources.
- Cost failure order is deterministic and prioritizes invalid selector, items, attributes, then budget. Multi-item text preserves plan order.
- Programmer and resource screens show exact inventory deficits separately from fixed requirements, avoiding the old ambiguous `missing fixed item cost`.
- Input guidance now says sneak-use / agachar e usar instead of assuming the Shift key. Patchouli's first-spell path uses the current Theorems, Inscribe, Resources, and Edit labels.
- Exact item selectors are rendered through the item's client-localized name in the HUD, programmer, resource costs, material catalog, added-material list, narration, and talisman tooltip. PT-BR runtime previews show `Pena`, `Fragmento de Ametista`, `Diamante`, and `Pérola do Ender`.
- Tags, unknown mod selectors, and alternatives remain technical notation; the resource material tooltip continues to expose the underlying item/tag selector for pack debugging.
- An unnamed Laboratory construction no longer persists the English `Custom Spell` fallback. Its display name is localized at render time, while player-authored names remain literal across inscription and reload.
- Loading a long authored name starts the compact editor at its beginning and exposes the complete value on hover. The localized unnamed hint is clipped to the field instead of crossing into the Inscribe control.
- Laboratory copy now uses Proof/Edit Proof/Inscribe and Prova/Editar Prova/Inscrever consistently instead of mixing Custom and Save terminology.
- The disabled Edit control on an inscribed preserved Theorem now explains that only Laboratory sequences can be reloaded. The tooltip directs the player back to Theorems to assemble a new proof instead of leaving a control that appears broken.
- Resource preparation separates editable Prepared Materials from the complete Total Consumed and Total Catalysts plan.
- Clear removes only prepared materials. Its tooltip and Patchouli entry explicitly state that fixed Theorem costs remain in the total plan.
- The editable list no longer claims every persisted selection was added by the player. New inscriptions may begin with theorem-recommended materials, so GUI headings, help, narration, `Σ(items)`, Patchouli, and lore now use Prepared Materials/Materiais preparados for both automatic defaults and later manual choices.
- Material rows use localized item names for single exact selectors, localized names for configured tag groups, and localized `spent`/`kept` roles instead of opaque ids and `C`/`K` abbreviations. Hover keeps the stable id, selector, and full attribute list inspectable.
- Laboratory bindings use `target[input] <- source` instead of exposing generated edge ids as the primary notation.
- Numbered references such as `#1` point back to visible Tree steps, while constructor subgraphs may fold into readable literals such as `vec(0.7, 0.08, 0)`.
- Binding color identifies the source output type. Hover text expands localized rune and input names, explains the transported type, and retains exact technical ids for debugging.
- Theorem node tooltips show output type, final-result state, numbered input sources, constants, and exact node/rune ids.
- Theorem hover hit-testing now uses the rendered graph-line model, so pending/success inscription feedback and spacers cannot shift a tooltip onto the wrong node.
- Rune output colors are defined by one exhaustive family mapping: scalar/logic, space/geometry, actors, and effects.
- A four-quadrant graph-header legend exposes those families through mouse hover, keyboard focus, and narration; textual node types keep the information independent of color.
- The original primitive rune textures and local-basis macro family remain unique. The first advanced-mathematics slice deliberately derives valid 16x16 assets from that vocabulary; dedicated sine/cosine, quadrature, cross-product, and cyclic-action glyphs remain an explicit visual follow-up in `docs/ADVANCED_MATHEMATICS.md`.
- Theorem and Laboratory catalog icon ids are required to be unique by automated tests, and the three generated basis assets have a reproducible Java source tool.
- A selected Theorem keeps its compact formula directly under its title, so changing focus from the catalog to the typed graph no longer discards the statement being demonstrated.
- The 19 formula summaries use unique, whitespace-free effect notation. Every statement now begins with its world conclusion, `push(...)`, `blink(...)`, or `mark(...)`; movement theorems no longer stop at intermediate assignments such as `v=...` or `p=...`. Nested terms such as `ray(self,12)` and `nearest(4)` describe the evidence inside that conclusion.
- The theorem catalog no longer clips several movement formulas to the indistinguishable `push(self,...)` prefix. Each theorem has a separate, unique card formula sized for the narrow palette, while the selected statement, hover, and narration preserve the exact full effect expression.
- The selected theorem statement no longer contradicts that promise by ellipsizing long full formulas. It reserves a stable two-line block, prefers the outer effect-argument separator for a meaningful break, and moves graph rendering, scrolling, and hit-testing to one shared lower origin.
- The compact theorem statement is now a focusable informational element rather than passive text. Hover/focus outlines expose it as inspectable without giving it an editor background or caret; tooltip and narration explicitly distinguish the non-editable summary from the complete numbered typed proof.
- A proof-state seal now distinguishes no proof, incomplete proof, local demonstration, pending inscription, missing witnesses, and cast-ready preparation without adding a permanent tutorial paragraph to the graph.
- The seal derives cast readiness from the real player resource plan, exposes a localized contextual tooltip and narration, and keeps the proof title, formula, and output-family legend in their existing header bounds.
- Programmed-talisman tooltips now separate state from commands and present both halves of the world interaction as short action rows: normal use casts with witnesses, while sneak-use opens their preparation; an empty talisman gets its own Programmer action row. The tooltip does not invent readiness because this item callback cannot reliably inspect the viewing player's complete inventory context.
- Rune Chalk now applies the same item-action hierarchy: it names the chosen anchor theorem, then separates change, inscription, and erasure into short rows. Anchor HUD and Manual copy use theorem, proof, and inscription according to state; `program` and `preset` remain implementation terms rather than competing player-facing concepts.
- Item interaction routes are now explicit and independently tested: a blank talisman opens the Programmer even during secondary use, normal use of an inscribed talisman casts, and secondary use of an inscribed talisman opens Resources. Bilingual minimum-viewport previews invoke the public item method on the integrated server and reject the wrong destination screen.
- Re-inscribing an identical graph now preserves its prepared materials and catalysts instead of silently replacing them with theorem recommendations. A different graph still starts from its own recommended preparation.
- The Inscribe control becomes an explicit disabled Inscribed state when the selected theorem, or the complete named Laboratory proof, already matches the talisman. Name-only Laboratory changes correctly re-enable inscription.
- Resources tooltips now follow the displayed state: pending inscription, no inscribed program, available projected preparation, or available preparation for the active spell. They no longer retain the initial disabled explanation after state changes.
- Erasing a program now cancels obsolete inscription feedback, waits for synchronized held-item changes when recalculating controls, and blocks Erase while another inscription is pending.
- After erasure, the selected Theorem remains visible as a local demonstration while Inscribe becomes available and Resources/Erase become unavailable. Patchouli distinguishes this operation from resetting the Laboratory workspace.
- Starting another inscription, opening Resources, selecting a theorem, or switching tabs disarms an unfinished two-click erasure so a later click cannot unexpectedly delete newer work.
- Inscription confirmation now compares the complete persisted identity: graph, optional authored name, and Laboratory action sequence. A name-only edit can no longer confirm against the old item merely because its topology is unchanged.
- The same complete inscription snapshot drives live held-item observation, so synchronized metadata changes refresh the saved panel even when the graph itself is identical.
- Resource addition, unit removal, and clearing are now exercised through a real server-backed menu instead of client-only screen fixtures.
- An incomplete resource plan presents its real item, attribute, and budget deficits immediately after Status/Estado under Outstanding/Pendências; aggregate consumed, catalyst, and attribute evidence follows instead of delaying the actionable diagnosis.
- Added-material mouse hit testing, rendering bounds, keyboard reveal, and the server-backed preview now derive from the same semantic selection lines. Reordering diagnostic sections can no longer make the removal test click an old fixed row.
- Clear follows the synchronized held stack: it disables after the last added material is removed and explains that the fixed Theorem plan is not an added loadout.
- Resource clearing now follows the same two-stage destructive contract as inscription erasure. The first mouse or keyboard activation only arms `Confirm?` / `Confirma?`, changes the narrated button message and scope tooltip, and preserves the complete preparation; only the second activation sends the server request. Adding or removing a material, leaving for the Programmer, losing the preparation, or rebuilding the screen disarms confirmation. Bilingual minimum-viewport captures guard the longer label and tooltip, while the server-backed clear journey waits six ticks between activations and rejects any first-activation mutation.
- A bilingual server-backed Laboratory reset journey now proves all three boundaries separately: the first activation preserves authored name and Rune Form sequence, the second receives the localized server confirmation and empties the workspace, and the held talisman's already inscribed proof remains byte-for-byte equivalent in name and saved actions. The Manual's “Erase or Reset?” spread teaches the same distinction and symbolic `0 -> 0?` transition.
- Built-in validation issues carry a stable translation key and ordered arguments while retaining an English fallback for logs and external integrations. The GUI can therefore name exact expected/current types in the active language instead of presenting a generic or truncated failure.
- A single server-backed preview journey now proves the intended first spell from blank talisman through theorem selection, inscription, resource preparation, witness consumption, and world movement.
- The proof name now survives the complete first-cast journey: Resources renders `Proof Loadout: Hop` / `Preparação: Pulo`, the ready state directs the player to close and use the talisman, and successful world feedback renders `∴ Hop takes effect` / `∴ Pulo produz efeito` instead of a generic result.
- A blank talisman now presents a coherent first decision without permanent tutorial prose: Theorems is active, a valid Hop/Pulo demonstration is selected, Inscribe is actionable, and Resources/Erase remain unavailable until their server-backed states exist.
- Programmer tabs now tell that same story from left to right and by keyboard: construct from a Theorem, move to the Laboratory for original work, then inspect the held Talisman. The final tab names the physical carrier instead of implying a global saved-spell library.
- The Talisman action row no longer repeats the `Theorems` tab label for the same navigation. It names the player's intent as `Replace` / `Substituir` (`Trocar` in the compact viewport), while its tooltip states that opening the catalog does not mutate the held inscription; replacement occurs only after a new proof is inscribed.
- Inscribed-panel copy no longer refers to the retired Presets/Saved vocabulary. It directs the player through Theorems and Resources while preserving the distinction between a demonstrated theorem and an inscribed proof.
- Inscribed guidance now follows the live proof workflow instead of prescribing Resources unconditionally. Missing witnesses direct the player to inspect preparation; a cast-ready proof directs the player back into the world to use the talisman. Visual text and narration share the same state-derived translation key.
- The Programmer header now provides a compact `X` before `?` and `f(x)`. It is the explicit counterpart to the cast-ready instruction to return to the world, and its tooltip states that both the talisman inscription and unsaved Laboratory work are preserved. EN and PT-BR captures at 640x480 guard the complete header, bounded tooltip, and direct `inscribe -> close -> cast` journey.
- Contextual Programmer tooltips must be gated by both their semantic tab and the live widget visibility. A real tab transition exposed that the hidden theorem statement retained stale hover state and rendered over the close tooltip; the renderer now rejects hidden statements, and the dedicated close-hover preview guards that regression.
- Patchouli's first-spell witness note names Hop's recommended Feather, explains that a witness is consumed only on successful execution, and relates resource readiness to agreement between graph, preparation, and inventory.
- Patchouli's first-spell path now treats Resources as conditional inspection rather than a mandatory step after every inscription. Its closing page also replaces developer-facing GUI/MVP language with an in-world lesson about small inspectable proofs and longer conjectures.
- Theorem formulas can no longer appear as orphaned fragments at a scroll edge; each shorthand remains visually attached to the theorem name and icon it summarizes.
- Rune Form hover and narration now preview the actual next graph mutation. They distinguish inputs reused from the current proof from safely inferred premises and report the exact added rune and binding counts without mutating the workspace.
- Prepared Materials and Materials now participate in the Tab cycle, support Up/Down/Home/End plus Enter/Space, preserve a visible panel and row cursor across synchronized mutations, and narrate the current material, position, and activation result. If the prepared list becomes empty while focused, focus transfers to the catalog instead of becoming stranded.
- Materials no longer rely on hover text to reveal that their rows are controls. A persistent teal `+` marks preparation in the catalog and one coral `-` marks removal on each visible prepared selection; the whole row remains the mouse/keyboard target, the symbols inherit row emphasis, and reserved geometry prevents names or summaries from rendering beneath them.
- The chalk hint no longer claims world anchors are merely planned in either locale. It teaches the implemented preset cycle, inscription, and sneak-use erasure behavior; a bilingual copy regression rejects stale `planned`/`MVP` language, and the current-state manual likewise describes the existing world carrier in the present tense.
- A bilingual integrated-server anchor journey now proves the complete physical route: cycle to Sacrifice Pulse, inscribe, inspect, reject enactment without a witness, consume one nearby Amethyst Shard on success, erase, reject repeated erasure as a no-op, and inspect the empty anchor. Immediately after inscription, the harness serializes the block entity, recreates a distinct instance with `BlockEntity.loadStatic`, replaces the live instance, and requires both the Sacrifice Pulse id and graph equality before continuing. Each transition requires the expected action-bar component, and the final visual stage clears intervening terrain so a hidden block cannot masquerade as a valid capture. Both block-first and item-first chalk routes distinguish a real erasure from an already empty anchor. This closes the in-memory NBT reconstruction boundary; actual chunk unload, disk save/reload, reconnect, and dedicated-server persistence remain explicit follow-up coverage.
- The Rune Anchor item now closes the world-interaction discovery gap without adding a screen or tutorial paragraph: its tooltip names the symbolic world-carrier sequence, then separates chalk inscription, empty-hand enactment, empty-hand inspection, and chalk erasure into physical action rows. Bilingual 640x480 JEI captures forced the PT-BR copy to be condensed until all gestures retained a safe window margin. The item-tooltip harness was also corrected after a real capture exposed that its server synchronization replaced every non-chalk item with a talisman.
- First-contact item tooltips no longer render identity, metadata, primary commands, secondary commands, and destructive commands as one undifferentiated white paragraph. A common server-safe palette now feeds both item components and the GUI theme: gold identity, muted proof detail, teal enactment/inscription, blue inspection/preparation, and coral erasure. Real 640x480 captures cover the PT-BR Talismã, Giz, and Âncora plus the final English anchor hierarchy; source regressions prevent item roles or GUI colors from drifting to separate palettes.
- A localization-quality test walks every PT-BR GUI and Patchouli string, rejects known unaccented spellings, English UI residue, mojibake, and Patchouli title-case connectors, and pins representative core vocabulary.
- Optional Patchouli preview modes open real book entries through Patchouli's server command without a compile-time dependency, allowing page overflow to be audited rather than inferred from valid JSON.
- The landing spread has its own bilingual real-client preview because the entry matrix does not cover book metadata, nameplate width, localized subtitles, or landing links.
- Resource materials backed by exact items use the item's localized name; built-in tag groups now use bilingual material names, and pack-authored groups may provide `setMaterialTranslationKey`. Catalog rows, prepared rows, missing-witness diagnostics, tooltips, and narration share that presentation, while stable ids and selectors remain available as technical hover details. Unknown groups receive a readable id-derived fallback instead of visible snake_case.
- The material catalog is collated by its active-language display names instead of server ids. Mouse and keyboard selections map the visible row back to the canonical id before sending the menu action; bilingual server-backed previews prove non-identity mappings for PT-BR `Aço -> steel` and `Pó de Redstone -> redstone`. Keyboard narration now names the localized row rather than pronouncing its technical id.
- Rune types now have a complete bilingual presentation namespace independent of their serialized ids. Graph titles, node rows, binding explanations, input/output tooltips, validation messages, Rune Form previews, search, and narration use localized concepts such as `Vetor`, `Efeito`, and `Plano de efeito`; raw ids such as `vec3` and generated node ids remain only in explicit technical tooltip details and addon-facing search.
- The Programmer's compact resource summary now resolves prepared material ids through the same presentation policy as Resources. A proof therefore names `Estanho` or the localized exact item instead of leaking `tin` or another registry id at the graph boundary.

## Current Symbolic Identity

- `f(x)` identifies the programmer as a transformation surface.
- `f(x)` and `Σ(items)` are focusable notation marks with localized hover text and narration; their meaning is now discoverable in the interface rather than existing only in design documentation.
- `?  f(x)` and `?  Σ(items)` share one tested right-aligned header geometry, identifying help as explanation of the adjacent mathematical surface rather than an unrelated operation.
- Keyboard entry follows the current task before secondary notation: the active programmer tab or the Resources Materials catalog receives first focus, while navigation commands and header symbols remain part of the complete cycle.
- Resources no longer labels a pure screen transition as `Edit` / `Editar`. The neutral `<- Proof` / `<- Prova` command names its destination, explains that preparation is preserved, and is verified through a real server-backed menu transition. This separates proof navigation from resource mutation and leaves teal emphasis for the material task.
- Output-family colors identify scalar/logic, spatial, actor, and effect domains; exact types remain textual. Category colors identify palette intent.
- Teal identifies the focused proof region; ivory identifies the keyboard cursor without replacing semantic type/category colors.
- `Q.E.D.` / `C.Q.D.` appears only after a server-confirmed inscription.
- `∴` appears only after an inscribed proof successfully accepts its required witnesses and produces a world effect. Talismans include the spell name; anchors identify the world carrier.
- `Σ(items)` identifies the aggregation of fixed Theorem costs and prepared materials into the total casting plan.
- Authored proof names may use all 32 persisted characters. Resources clips only the name portion inside the preparation panel, marks overflow with a width-bounded ellipsis, and exposes the complete name only from that visible clipped region.
- Witnesses and catalysts remain material roles, while graph validity and resource readiness remain separate states.
- `target[input] <- source` identifies typed value flow into a proof step; `#n` refers to the correspondingly numbered step in the Tree.
- `#n Rune -> Localized Type` identifies a preserved-Theorem node; the same `#n` is used by its hover bindings. Stable type ids remain available only in serialization, addon APIs, search aliases, and explicit technical diagnostics.
- `0` denotes the Laboratory's local zero state; `0?` denotes a proposed reset awaiting its second activation. Neither symbol erases the talisman's inscription.
- A shared frame-and-origin motif identifies local-coordinate constructions; the arrow direction distinguishes right, forward, and oblique vectors.
- A short gold statement mark introduces the selected formula; the numbered rune cards beneath it are the corresponding typed proof.
- The proof seal encodes progression rather than decoration: teal turnstile for a demonstrated proof, gold points during inscription, coral sum for missing witnesses, and a green terminal square for cast-ready. Where graph width permits, the sigil is paired with one localized state word; the minimum viewport and Laboratory preserve the compact mark to protect proof identity and output type.
- Resource-action color follows obligation rather than screen identity: gold means witness preparation is the next required step, while blue means the same Resources surface is available only for optional inspection of a ready plan.
- The introductory manual speaks as a field text for mathemagicians. Its first proof is justified through premises, witnesses, results, and conjectures rather than through editor limitations or development status.

Symbols must continue to encode behavior or state. Do not add ornamental notation that has no discoverable meaning.

## First-Use Flow Heuristic Audit

This is a source-and-runtime heuristic audit, not a substitute for observing a new player. It checks whether every state exposes one truthful next action and whether the terminology remains coherent across adjacent surfaces.

| Transition | Current evidence | Heuristic result |
| --- | --- | --- |
| Find the entry point | A blank talisman says that no proof is inscribed and normal use opens the Programmer. The public item route is covered by a server-backed preview. | Strong: the held item names both its state and immediate action. |
| Choose a first construction | Theorems opens first with Hop/Pulo selected. Inscribe is the only enabled primary mutation; Resources and Erase remain unavailable. | Strong: one useful default avoids an empty-editor dead end. |
| Understand what is being stored | The selected compact statement summarizes the construction, numbered rune rows expose the complete typed proof, and the proof seal distinguishes local demonstration from server inscription. The statement is inspectable by mouse and keyboard and explicitly says it is not editable source. | Improved: neither state nor statement meaning depends exclusively on a tiny hover target, though unaided comprehension still requires a human test. |
| Confirm inscription | Pending verification disables conflicting mutations. `Q.E.D.` / `C.Q.D.` appears only after the held talisman synchronizes, then the Inscribed panel gives the next action. | Strong: local validity and persisted state are not conflated. |
| Resolve material requirements | Missing witnesses direct the player to Resources. The resource screen places concrete deficits before preparation totals and reports Ready only when graph, preparation, and inventory agree. | Strong: the item economy is explicit and failure is recoverable. |
| Return to the world | A ready Inscribed proof and a ready resource plan both say to close and use the talisman. The item tooltip exposes normal use and sneak-use as separate action rows, and failed casts repeat the recovery gesture. | Strong mechanically and easier to scan; discovery without opening the tooltip still needs player observation. |
| Reach deeper explanation | The contextual `?` opens the short first-spell spread, which repeats the same theorem, proof, inscription, witness, and casting sequence without turning the work surface into a lore page. | Strong as optional support; Patchouli must not become required to recover from a GUI state. |

The current identity is deliberately dual rather than halfway between themes:

- Mathematics supplies validity, typed flow, formulas, graph structure, budget, `f(x)`, and `Σ(items)`.
- Magic supplies runes, talismans, inscriptions, witnesses, catalysts, named spells, and world effects.
- The lifecycle joins them: a Theorem demonstrates a proof, inscription binds it, witnesses support it, and `∴` announces its effect.
- Player-facing first-contact copy calls the validated construction a proof. Spell remains the name of its practical effect; program is reserved for codecs, persistence, and explicitly technical diagnostics.

No heuristic dead end remains in the inspected first-spell path. Confidence is high for behavioral continuity and terminology, but only medium for unaided discovery until a first-time player test covers formula interpretation, proof-seal hover, and normal-use versus sneak-use.

## Open UX Work

- Observe a first-time player reading a Field Manuscript at normal and compact
  GUI scale. They should recognize the difference between opening the Field
  Manual and inspecting a theorem graph, while neither action suggests that it
  can alter the held manuscript or talisman. The automated matrix covers
  EN/PT-BR, the missing-record state, and the compact boundary; a narrator pass
  and ATM10 overlay test remain open.

- Observe whether players understand the P6 chain from an unknown Field Ledger
  row to a cartographer manuscript, then from the manuscript's Patchouli hint
  to material-backed practice. The GUI exposes routes and progress, but only a
  player test can establish whether the distinction between discovery and
  epiphany is learned without verbal explanation.
- Verify the six-record Field Ledger and the manuscript Patchouli spread in PT-BR
  inside ATM10 at the player's actual GUI scale. Automated development-client
  captures cover bounds and JEI coexistence, not every pack overlay.

- The guided Rune Form composer remains the basic Laboratory mode because it has deterministic undo, stable persistence, and now makes its implicit expansion inspectable before activation. Direct node-and-edge editing remains a separate advanced-mode decision; it must not silently replace or reinterpret saved action sequences.
- Test server rejection, packet delay, and hand/slot changes during inscription on a dedicated server; automated confirmation currently simulates the synchronized held stack.
- Observe a first-time player completing `blank talisman -> theorem -> inscription -> optional resource inspection -> cast` without verbal guidance. The automated end-to-end journey and public item-route previews now prove that every transition works, that guidance changes with witness readiness, and that the item economy is honest. The labeled seal and explicit tooltip action rows reduce hidden-hover and paragraph-scanning dependencies, but only human observation can establish whether Theorems, Resources, and use/sneak-use are actually discovered.
- For that observation, record whether the player checks the talisman tooltip, time from inscription to first cast attempt, time from a missing-witness message to opening Resources, accidental resource-screen openings, and whether remapped Use/Sneak bindings change comprehension. Add a persistent world HUD hint only if players repeatedly fail after seeing both the tooltip and recovery message; otherwise keep instruction contextual.
- Extend the anchor journey across chunk unload/reload and a dedicated server before changing world-carrier persistence. Add two-player contention, reconnect inspection, and a non-amethyst KubeJS sacrifice selector so single-player timing and default configuration are not mistaken for the complete contract.
- Observe whether pack-authored material names need conventions beyond translation keys. Missing client translations fall back to a readable form of the stable id, while hover retains the exact selector for pack diagnostics.
- Observe whether `Unit` should remain teachable as the formal lambda-calculus type in advanced documentation while the ordinary GUI calls it `Effect` / `Efeito`. The current split favors first-use comprehension without changing the serialized `unit` type; any future notation view must present both names deliberately rather than reintroducing raw ids throughout the basic workflow.
- Observe whether first-time players correctly read compact statements as summaries rather than editable source. The statement now has mouse/keyboard affordance, narration, and explicit bilingual copy, but comprehension still needs a player test.
- Verify narration with the Minecraft narrator enabled and a real audio/screen-reader pass. The narration elements and focus cycle are implemented, but automated previews cannot prove spoken timing or clarity.
- Revisit the typed-color legend after more symbols exist. A symbol should remain meaningful without becoming decorative notation.
- Test PT-BR text in the full ATM10 instance at the player's actual GUI scale. Automated PT-BR previews pass in the development client, but do not reproduce every overlay and font condition in the complete pack.
- Decide whether legacy talismans explicitly named `Custom Spell` should receive an opt-in migration. They currently remain literal to avoid silently rewriting a player-authored name.

## Acceptance For The Next Audit

- No control exceeds its screen or panel at the supported viewport matrix.
- Every scrollable region visibly indicates overflow.
- Every visible scrollbar supports track jump and direct drag without activating content beneath it; wheel and keyboard navigation remain available.
- Destructive actions communicate scope and require confirmation.
- Future destructive controls must expose confirmation through their visible label and narration, preserve data on the first activation, cancel when a related mutation changes the target, and remain understandable without relying on coral color alone.
- Every disabled primary action has a discoverable reason.
- The player can distinguish sources, transformations, queries, plans, and effects without reading raw rune ids.
- Laboratory search returns the same ordered subset for mouse, keyboard, scrolling, and narration, including a safe zero-result state.
- Resource catalog order, hover, keyboard focus, narration, and activation use the same localized row sequence while server mutations retain canonical material ids.
- A first-time player can move from theorem selection to inscription, resource preparation, and casting using only the GUI plus short Patchouli references.
