# SKILL: android.view.KeyEvent

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.KeyEvent`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.KeyEvent` |
| **Package** | `android.view` |
| **Total Methods** | 60 |
| **Avg Score** | 2.4 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 39 (65%) |
| **No Mapping** | 21 (35%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 52 |
| **Has Async Gap** | 52 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 60 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `KeyEvent` | 4 | partial | throw UnsupportedOperationException |
| `KeyEvent` | 4 | partial | throw UnsupportedOperationException |
| `KeyEvent` | 4 | partial | throw UnsupportedOperationException |
| `KeyEvent` | 4 | partial | throw UnsupportedOperationException |
| `KeyEvent` | 4 | partial | throw UnsupportedOperationException |
| `KeyEvent` | 4 | partial | throw UnsupportedOperationException |
| `KeyEvent` | 4 | partial | throw UnsupportedOperationException |
| `KeyEvent` | 4 | partial | throw UnsupportedOperationException |
| `changeAction` | 3 | composite | Store callback, never fire |
| `getAction` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDeadChar` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDeviceId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDisplayLabel` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDownTime` | 3 | composite | Return safe default (null/false/0/empty) |
| `getEventTime` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFlags` | 3 | composite | Return safe default (null/false/0/empty) |
| `getKeyCharacterMap` | 3 | composite | Return safe default (null/false/0/empty) |
| `getKeyCode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMatch` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMatch` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMaxKeyCode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMetaState` | 3 | composite | Return safe default (null/false/0/empty) |
| `getModifierMetaStateMask` | 3 | composite | Return safe default (null/false/0/empty) |
| `getModifiers` | 3 | composite | Return safe default (null/false/0/empty) |
| `getNumber` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRepeatCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getScanCode` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSource` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUnicodeChar` | 3 | composite | Return safe default (null/false/0/empty) |
| `getUnicodeChar` | 3 | composite | Return safe default (null/false/0/empty) |
| `isModifierKey` | 3 | composite | Return safe default (null/false/0/empty) |
| `isNumLockOn` | 3 | composite | Return safe default (null/false/0/empty) |
| `isPrintingKey` | 3 | composite | Return safe default (null/false/0/empty) |
| `keyCodeFromString` | 3 | composite | throw UnsupportedOperationException |
| `keyCodeToString` | 3 | composite | throw UnsupportedOperationException |
| `setSource` | 3 | composite | Log warning + no-op |
| `isCapsLockOn` | 3 | composite | Return safe default (null/false/0/empty) |
| `isScrollLockOn` | 3 | composite | Return safe default (null/false/0/empty) |
| `writeToParcel` | 2 | composite | Log warning + no-op |
| `changeFlags` | 1 | none | throw UnsupportedOperationException |
| `changeTimeRepeat` | 1 | none | Return safe default (null/false/0/empty) |
| `changeTimeRepeat` | 1 | none | Return safe default (null/false/0/empty) |
| `dispatch` | 1 | none | Return safe default (null/false/0/empty) |
| `hasModifiers` | 1 | none | Return safe default (null/false/0/empty) |
| `hasNoModifiers` | 1 | none | Return safe default (null/false/0/empty) |
| `isAltPressed` | 1 | none | Return safe default (null/false/0/empty) |
| `isCanceled` | 1 | none | Return safe default (null/false/0/empty) |
| `isCtrlPressed` | 1 | none | Return safe default (null/false/0/empty) |
| `isFunctionPressed` | 1 | none | Return safe default (null/false/0/empty) |
| `isGamepadButton` | 1 | none | Return safe default (null/false/0/empty) |
| `isLongPress` | 1 | none | Return safe default (null/false/0/empty) |
| `isMetaPressed` | 1 | none | Return safe default (null/false/0/empty) |
| `isShiftPressed` | 1 | none | Return safe default (null/false/0/empty) |
| `isSymPressed` | 1 | none | Return safe default (null/false/0/empty) |
| `isSystem` | 1 | none | Return safe default (null/false/0/empty) |
| `isTracking` | 1 | none | Return safe default (null/false/0/empty) |
| `metaStateHasModifiers` | 1 | none | Return safe default (null/false/0/empty) |
| `metaStateHasNoModifiers` | 1 | none | Return safe default (null/false/0/empty) |
| `normalizeMetaState` | 1 | none | throw UnsupportedOperationException |
| `startTracking` | 1 | none | Return dummy instance / no-op |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.KeyEvent`:


## Quality Gates

Before marking `android.view.KeyEvent` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 60 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
