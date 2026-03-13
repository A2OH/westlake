# SKILL: android.view.MotionEvent

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.view.MotionEvent`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.view.MotionEvent` |
| **Package** | `android.view` |
| **Total Methods** | 87 |
| **Avg Score** | 2.8 |
| **Scenario** | S6: UI Paradigm Shift |
| **Strategy** | ViewTree + ArkUI declarative rendering |
| **Direct/Near** | 0 (0%) |
| **Partial/Composite** | 78 (89%) |
| **No Mapping** | 9 (10%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 87 |
| **Has Async Gap** | 87 |
| **Related Skill Doc** | `A2OH-UI-REWRITE.md` |
| **Expected AI Iterations** | 3-5 |
| **Test Level** | Level 1 (Mock) + Level 2 (Headless ArkUI) |

## Stub APIs (score < 5): 87 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `actionToString` | 3 | composite | Store callback, never fire |
| `addBatch` | 3 | composite | Log warning + no-op |
| `addBatch` | 3 | composite | Log warning + no-op |
| `axisFromString` | 3 | composite | Return safe default (null/false/0/empty) |
| `axisToString` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAction` | 3 | composite | Return safe default (null/false/0/empty) |
| `getActionButton` | 3 | composite | Return safe default (null/false/0/empty) |
| `getActionIndex` | 3 | composite | Return safe default (null/false/0/empty) |
| `getActionMasked` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAxisValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `getAxisValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `getButtonState` | 3 | composite | Return safe default (null/false/0/empty) |
| `getClassification` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDeviceId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getDownTime` | 3 | composite | Return safe default (null/false/0/empty) |
| `getEdgeFlags` | 3 | composite | Return safe default (null/false/0/empty) |
| `getEventTime` | 3 | composite | Return safe default (null/false/0/empty) |
| `getFlags` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalAxisValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalAxisValue` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalEventTime` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalOrientation` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalOrientation` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalPointerCoords` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalPressure` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalPressure` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalSize` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalSize` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalToolMajor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalToolMajor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalToolMinor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalToolMinor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalTouchMajor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalTouchMajor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalTouchMinor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalTouchMinor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalX` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalX` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalY` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistoricalY` | 3 | composite | Return safe default (null/false/0/empty) |
| `getHistorySize` | 3 | composite | Return safe default (null/false/0/empty) |
| `getMetaState` | 3 | composite | Return safe default (null/false/0/empty) |
| `getOrientation` | 3 | composite | Return safe default (null/false/0/empty) |
| `getOrientation` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPointerCoords` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPointerCount` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPointerId` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPointerProperties` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPressure` | 3 | composite | Return safe default (null/false/0/empty) |
| `getPressure` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRawX` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRawX` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRawY` | 3 | composite | Return safe default (null/false/0/empty) |
| `getRawY` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSize` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSize` | 3 | composite | Return safe default (null/false/0/empty) |
| `getSource` | 3 | composite | Return safe default (null/false/0/empty) |
| `getToolMajor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getToolMajor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getToolMinor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getToolMinor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getToolType` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTouchMajor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTouchMajor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTouchMinor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getTouchMinor` | 3 | composite | Return safe default (null/false/0/empty) |
| `getX` | 3 | composite | Return safe default (null/false/0/empty) |
| `getX` | 3 | composite | Return safe default (null/false/0/empty) |
| `getXPrecision` | 3 | composite | Return safe default (null/false/0/empty) |
| `getY` | 3 | composite | Return safe default (null/false/0/empty) |
| `getY` | 3 | composite | Return safe default (null/false/0/empty) |
| `getYPrecision` | 3 | composite | Return safe default (null/false/0/empty) |
| `setAction` | 3 | composite | Log warning + no-op |
| `setEdgeFlags` | 3 | composite | Log warning + no-op |
| `setLocation` | 3 | composite | Log warning + no-op |
| `setSource` | 3 | composite | Log warning + no-op |
| `offsetLocation` | 2 | composite | Log warning + no-op |
| `writeToParcel` | 2 | composite | Log warning + no-op |
| `findPointerIndex` | 1 | none | Return safe default (null/false/0/empty) |
| `isButtonPressed` | 1 | none | Return safe default (null/false/0/empty) |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtain` | 1 | none | throw UnsupportedOperationException |
| `obtainNoHistory` | 1 | none | Return safe default (null/false/0/empty) |
| `recycle` | 1 | none | throw UnsupportedOperationException |
| `transform` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S6 — UI Paradigm Shift**

1. Create Java shim that builds a ViewNode description tree (NOT real UI)
2. Each setter stores the value in ViewNode.props map
3. Each container manages ViewNode.children list
4. Follow the Property Mapping Table in the AI Agent Playbook
5. Create headless ArkUI test validating component creation + properties
6. Test: addView/removeView, property propagation, event handler storage

## Dependencies

Check if these related classes are already shimmed before generating `android.view.MotionEvent`:

- `android.view.View` (already shimmed)

## Quality Gates

Before marking `android.view.MotionEvent` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 87 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 0 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
