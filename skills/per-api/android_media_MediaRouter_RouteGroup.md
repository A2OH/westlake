# SKILL: android.media.MediaRouter.RouteGroup

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.media.MediaRouter.RouteGroup`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.media.MediaRouter.RouteGroup` |
| **Package** | `android.media.MediaRouter` |
| **Total Methods** | 8 |
| **Avg Score** | 5.2 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 3 (37%) |
| **Partial/Composite** | 5 (62%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-MEDIA.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 4 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `getRouteCount` | `int getRouteCount()` | 8 | near | easy | `getCount` | `getCount(): number` |
| `setIconDrawable` | `void setIconDrawable(android.graphics.drawable.Drawable)` | 7 | near | easy | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |
| `getRouteAt` | `android.media.MediaRouter.RouteInfo getRouteAt(int)` | 7 | near | moderate | `getCount` | `getCount(): number` |
| `setIconResource` | `void setIconResource(@DrawableRes int)` | 5 | partial | moderate | `setDiscoverable` | `setDiscoverable(enable: boolean, callback: AsyncCallback<void>): void` |

## Stub APIs (score < 5): 4 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `removeRoute` | 4 | partial | Log warning + no-op |
| `removeRoute` | 4 | partial | Log warning + no-op |
| `addRoute` | 3 | composite | Log warning + no-op |
| `addRoute` | 3 | composite | Log warning + no-op |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 4 methods that have score >= 5
2. Stub 4 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.media.MediaRouter.RouteGroup`:


## Quality Gates

Before marking `android.media.MediaRouter.RouteGroup` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 8 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 4 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
