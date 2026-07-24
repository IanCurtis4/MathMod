# P0 Acceptance Checklist

P0 establishes the baseline that later gameplay work relies on. Automated
tests cover pure payload limits and the common/server source boundary. The
following checks require a real server or an independent player and are not
substituted by client preview captures.

## Dedicated Server And Reload

1. Start a dedicated NeoForge 1.21.1 server with MathMod and the same
   datapacks used by the client.
2. Join with a talisman, open the Rune Programmer, then replace the held
   talisman or close the menu before attempting a Laboratory edit. Confirm the
   server does not alter an inactive menu or a different held item.
3. Inscribe a parameterized calculus proof, prepare its witnesses, and cast
   it once. Run `/reload`, reconnect, and confirm the proof, parameters,
   knowledge definitions, and witness accounting remain valid.
4. Repeat the test with malformed or removed knowledge data. The server must
   retain the previous valid definition snapshot and report the rejected data
   in its log rather than disconnecting players or corrupting talismans.

## First-Use Observation

1. Give a new player the Field Manual, a blank talisman, and the one Feather
   needed by Hop. Do not explain the interface.
2. Observe whether they can reach the Hop theorem, inscribe it, prepare the
   witness, close the Programmer, and cast it without assistance.
3. Record the first blocking phrase or control, the elapsed time, and whether
   the player understood that a theorem becomes an inscribed proof on the
   held talisman.
4. Treat a failure as input for `docs/UX_AUDIT.md`; do not paper over it with
   a preview-only change.

## Exit Criteria

- Server rejects stale-menu and oversized custom-edit input without mutating a
  talisman.
- Reload and reconnect preserve valid state and safely reject invalid data.
- An independent player completes the first spell journey unassisted.
