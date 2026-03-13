# SKILL: android.util.Xml

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.util.Xml`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.util.Xml` |
| **Package** | `android.util` |
| **Total Methods** | 7 |
| **Avg Score** | 7.4 |
| **Scenario** | S2: Signature Adaptation |
| **Strategy** | Type conversion at boundary |
| **Direct/Near** | 4 (57%) |
| **Partial/Composite** | 3 (42%) |
| **No Mapping** | 0 (0%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `SHIM-INDEX.md` |
| **Expected AI Iterations** | 1-2 |
| **Test Level** | Level 1 (Mock only) |

## Implementable APIs (score >= 5): 6 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `parse` | `static void parse(String, org.xml.sax.ContentHandler) throws org.xml.sax.SAXException` | 10 | direct | trivial | `parseUUID` | `parseUUID(uuid: string): Uint8Array` |
| `parse` | `static void parse(java.io.Reader, org.xml.sax.ContentHandler) throws java.io.IOException, org.xml.sax.SAXException` | 10 | direct | trivial | `parseUUID` | `parseUUID(uuid: string): Uint8Array` |
| `parse` | `static void parse(java.io.InputStream, android.util.Xml.Encoding, org.xml.sax.ContentHandler) throws java.io.IOException, org.xml.sax.SAXException` | 10 | direct | trivial | `parseUUID` | `parseUUID(uuid: string): Uint8Array` |
| `findEncodingByName` | `static android.util.Xml.Encoding findEncodingByName(String) throws java.io.UnsupportedEncodingException` | 6 | near | moderate | `isEncoding` | `isEncoding(encoding: string): boolean` |
| `newSerializer` | `static org.xmlpull.v1.XmlSerializer newSerializer()` | 6 | partial | moderate | `newSEService` | `newSEService(type: 'serviceState', callback: Callback<ServiceState>): SEService` |
| `asAttributeSet` | `static android.util.AttributeSet asAttributeSet(org.xmlpull.v1.XmlPullParser)` | 5 | partial | moderate | `attributeValueCallbackFunction` | `attributeValueCallbackFunction?: (name: string, value: string) => boolean` |

## Stub APIs (score < 5): 1 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `newPullParser` | 5 | partial | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S2 — Signature Adaptation**

1. Create Java shim with type conversion at boundaries
2. Map parameter types: check the Gap Descriptions above for each method
3. For enum/constant conversions, create a mapping table in the shim
4. Test type edge cases: null, empty string, MAX/MIN values, negative numbers
5. Verify return types match AOSP exactly

## Dependencies

Check if these related classes are already shimmed before generating `android.util.Xml`:


## Quality Gates

Before marking `android.util.Xml` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 7 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 6 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
