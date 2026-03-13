# SKILL: android.os.Parcel

> Auto-generated from api_compat.db. Use this as the primary reference when shimming `android.os.Parcel`.

## Summary

| Property | Value |
|---|---|
| **Class** | `android.os.Parcel` |
| **Package** | `android.os` |
| **Total Methods** | 83 |
| **Avg Score** | 5.9 |
| **Scenario** | S3: Partial Coverage |
| **Strategy** | Implement feasible methods, stub the rest |
| **Direct/Near** | 39 (46%) |
| **Partial/Composite** | 41 (49%) |
| **No Mapping** | 3 (3%) |
| **Needs Native Bridge** | 0 |
| **Needs UI Rewrite** | 0 |
| **Has Async Gap** | 0 |
| **Related Skill Doc** | `A2OH-DEVICE-API.md` |
| **Expected AI Iterations** | 2-3 |
| **Test Level** | Level 1 + Level 2 (Headless) |

## Implementable APIs (score >= 5): 74 methods

| Method | Signature | Score | Type | Effort | OH Equivalent | OH Signature |
|---|---|---|---|---|---|---|
| `readFileDescriptor` | `android.os.ParcelFileDescriptor readFileDescriptor()` | 9 | direct | easy | `getFileDescriptor` | `getFileDescriptor(pipe: USBDevicePipe): number` |
| `writeFileDescriptor` | `void writeFileDescriptor(@NonNull java.io.FileDescriptor)` | 8 | direct | easy | `getFileDescriptor` | `getFileDescriptor(pipe: USBDevicePipe): number` |
| `hasFileDescriptors` | `boolean hasFileDescriptors()` | 8 | direct | easy | `getFileDescriptor` | `getFileDescriptor(pipe: USBDevicePipe): number` |
| `setDataSize` | `void setDataSize(int)` | 8 | near | easy | `setDate` | `setDate(date: Date, callback: AsyncCallback<void>): void` |
| `writeInt` | `void writeInt(int)` | 8 | near | easy | `write` | `write(data: number[]): Promise<void>` |
| `writeMap` | `void writeMap(@Nullable java.util.Map)` | 8 | near | easy | `write` | `write(data: number[]): Promise<void>` |
| `readByte` | `byte readByte()` | 8 | near | easy | `readText` | `readText(filePath: string,
  options?: {
    position?: number;
    length?: number;
    encoding?: string;
  }): Promise<string>` |
| `readInt` | `int readInt()` | 7 | near | easy | `read` | `read(): Promise<number[]>` |
| `readMap` | `void readMap(@NonNull java.util.Map, @Nullable ClassLoader)` | 7 | near | easy | `read` | `read(): Promise<number[]>` |
| `enforceInterface` | `void enforceInterface(String)` | 7 | near | easy | `setInterface` | `setInterface(pipe: USBDevicePipe, iface: USBInterface): number` |
| `writeByte` | `void writeByte(byte)` | 7 | near | easy | `write` | `write(data: number[]): Promise<void>` |
| `writeList` | `void writeList(@Nullable java.util.List)` | 7 | near | easy | `write` | `write(data: number[]): Promise<void>` |
| `writeLong` | `void writeLong(long)` | 7 | near | easy | `write` | `write(data: number[]): Promise<void>` |
| `writeSize` | `void writeSize(@NonNull android.util.Size)` | 7 | near | easy | `write` | `write(data: number[]): Promise<void>` |
| `writeString` | `void writeString(@Nullable String)` | 7 | near | easy | `writeSync` | `writeSync(fd: number,
  buffer: ArrayBuffer | string,
  options?: {
    offset?: number;
    length?: number;
    position?: number;
    encoding?: string;
  }): number` |
| `writeBundle` | `void writeBundle(@Nullable android.os.Bundle)` | 7 | near | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `dataSize` | `int dataSize()` | 7 | near | moderate | `size` | `size: number` |
| `readException` | `void readException()` | 7 | near | moderate | `readText` | `readText(filePath: string,
  options?: {
    position?: number;
    length?: number;
    encoding?: string;
  }): Promise<string>` |
| `readException` | `void readException(int, String)` | 7 | near | moderate | `readText` | `readText(filePath: string,
  options?: {
    position?: number;
    length?: number;
    encoding?: string;
  }): Promise<string>` |
| `readList` | `void readList(@NonNull java.util.List, @Nullable ClassLoader)` | 7 | near | moderate | `read` | `read(): Promise<number[]>` |
| `readLong` | `long readLong()` | 7 | near | moderate | `read` | `read(): Promise<number[]>` |
| `readTypedList` | `<T> void readTypedList(@NonNull java.util.List<T>, @NonNull android.os.Parcelable.Creator<T>)` | 7 | near | moderate | `readText` | `readText(filePath: string,
  options?: {
    position?: number;
    length?: number;
    encoding?: string;
  }): Promise<string>` |
| `writeArray` | `void writeArray(@Nullable Object[])` | 7 | near | moderate | `write` | `write(data: number[]): Promise<void>` |
| `writeBinderList` | `void writeBinderList(@Nullable java.util.List<android.os.IBinder>)` | 7 | near | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `writeFloat` | `void writeFloat(float)` | 7 | near | moderate | `write` | `write(data: number[]): Promise<void>` |
| `writeSizeF` | `void writeSizeF(@NonNull android.util.SizeF)` | 7 | near | moderate | `write` | `write(data: number[]): Promise<void>` |
| `writeStrongInterface` | `void writeStrongInterface(android.os.IInterface)` | 7 | near | moderate | `releaseInterface` | `releaseInterface(pipe: USBDevicePipe, iface: USBInterface): number` |
| `writeValue` | `void writeValue(@Nullable Object)` | 7 | near | moderate | `setValue` | `setValue(value: number): void` |
| `writeInterfaceToken` | `void writeInterfaceToken(String)` | 6 | near | moderate | `setInterface` | `setInterface(pipe: USBDevicePipe, iface: USBInterface): number` |
| `writeBinderArray` | `void writeBinderArray(@Nullable android.os.IBinder[])` | 6 | near | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `readBinderList` | `void readBinderList(@NonNull java.util.List<android.os.IBinder>)` | 6 | near | moderate | `readNdefTag` | `readNdefTag(): Promise<string>` |
| `writeIntArray` | `void writeIntArray(@Nullable int[])` | 6 | near | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `writeDouble` | `void writeDouble(double)` | 6 | near | moderate | `write` | `write(data: number[]): Promise<void>` |
| `writeException` | `void writeException(@NonNull Exception)` | 6 | near | moderate | `writePermission` | `readonly writePermission: string` |
| `writeTypedList` | `<T extends android.os.Parcelable> void writeTypedList(@Nullable java.util.List<T>)` | 6 | near | moderate | `writePermission` | `readonly writePermission: string` |
| `dataAvail` | `int dataAvail()` | 6 | near | moderate | `data` | `data: string[]` |
| `readBinderArray` | `void readBinderArray(@NonNull android.os.IBinder[])` | 6 | near | moderate | `readNdefTag` | `readNdefTag(): Promise<string>` |
| `readFloat` | `float readFloat()` | 6 | near | moderate | `read` | `read(): Promise<number[]>` |
| `readIntArray` | `void readIntArray(@NonNull int[])` | 6 | near | moderate | `readNdefTag` | `readNdefTag(): Promise<string>` |
| `writePersistableBundle` | `void writePersistableBundle(@Nullable android.os.PersistableBundle)` | 6 | partial | moderate | `writePermission` | `readonly writePermission: string` |
| `writeFloatArray` | `void writeFloatArray(@Nullable float[])` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `writeBoolean` | `void writeBoolean(boolean)` | 6 | partial | moderate | `write` | `write(data: number[]): Promise<void>` |
| `writeStringList` | `void writeStringList(@Nullable java.util.List<java.lang.String>)` | 6 | partial | moderate | `writeSync` | `writeSync(fd: number,
  buffer: ArrayBuffer | string,
  options?: {
    offset?: number;
    length?: number;
    position?: number;
    encoding?: string;
  }): number` |
| `setDataPosition` | `void setDataPosition(int)` | 6 | partial | moderate | `setHotspotConfig` | `setHotspotConfig(config: HotspotConfig): void` |
| `writeNoException` | `void writeNoException()` | 6 | partial | moderate | `writePermission` | `readonly writePermission: string` |
| `appendFrom` | `void appendFrom(android.os.Parcel, int, int)` | 6 | partial | moderate | `from` | `from(array: number[]): Buffer` |
| `readByteArray` | `void readByteArray(@NonNull byte[])` | 6 | partial | moderate | `readText` | `readText(filePath: string,
  options?: {
    position?: number;
    length?: number;
    encoding?: string;
  }): Promise<string>` |
| `readDouble` | `double readDouble()` | 6 | partial | moderate | `readNdefTag` | `readNdefTag(): Promise<string>` |
| `writeDoubleArray` | `void writeDoubleArray(@Nullable double[])` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `writeSerializable` | `void writeSerializable(@Nullable java.io.Serializable)` | 6 | partial | moderate | `writePermission` | `readonly writePermission: string` |
| `readFloatArray` | `void readFloatArray(@NonNull float[])` | 6 | partial | moderate | `readNdefTag` | `readNdefTag(): Promise<string>` |
| `writeSparseArray` | `<T> void writeSparseArray(@Nullable android.util.SparseArray<T>)` | 6 | partial | moderate | `writeSync` | `writeSync(fd: number,
  buffer: ArrayBuffer | string,
  options?: {
    offset?: number;
    length?: number;
    position?: number;
    encoding?: string;
  }): number` |
| `writeStringArray` | `void writeStringArray(@Nullable String[])` | 6 | partial | moderate | `writeSync` | `writeSync(fd: number,
  buffer: ArrayBuffer | string,
  options?: {
    offset?: number;
    length?: number;
    position?: number;
    encoding?: string;
  }): number` |
| `writeTypedObject` | `<T extends android.os.Parcelable> void writeTypedObject(@Nullable T, int)` | 6 | partial | moderate | `writeSync` | `writeSync(fd: number,
  buffer: ArrayBuffer | string,
  options?: {
    offset?: number;
    length?: number;
    position?: number;
    encoding?: string;
  }): number` |
| `readStringList` | `void readStringList(@NonNull java.util.List<java.lang.String>)` | 6 | partial | moderate | `getScanInfoList` | `getScanInfoList(): Array<WifiScanInfo>` |
| `setDataCapacity` | `void setDataCapacity(int)` | 6 | partial | moderate | `getDataSummary` | `getDataSummary(): Array<Summary>` |
| `writeStrongBinder` | `void writeStrongBinder(android.os.IBinder)` | 6 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `readBoolean` | `boolean readBoolean()` | 5 | partial | moderate | `readNdefTag` | `readNdefTag(): Promise<string>` |
| `readTypedArray` | `<T> void readTypedArray(@NonNull T[], @NonNull android.os.Parcelable.Creator<T>)` | 5 | partial | moderate | `readText` | `readText(filePath: string,
  options?: {
    position?: number;
    length?: number;
    encoding?: string;
  }): Promise<string>` |
| `writeTypedArrayMap` | `<T extends android.os.Parcelable> void writeTypedArrayMap(@Nullable android.util.ArrayMap<java.lang.String,T>, int)` | 5 | partial | moderate | `writePermission` | `readonly writePermission: string` |
| `readDoubleArray` | `void readDoubleArray(@NonNull double[])` | 5 | partial | moderate | `readNdefTag` | `readNdefTag(): Promise<string>` |
| `writeByteArray` | `void writeByteArray(@Nullable byte[])` | 5 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `writeByteArray` | `void writeByteArray(@Nullable byte[], int, int)` | 5 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `writeLongArray` | `void writeLongArray(@Nullable long[])` | 5 | partial | moderate | `writeNdefTag` | `writeNdefTag(data: string): Promise<void>` |
| `writeTypedArray` | `<T extends android.os.Parcelable> void writeTypedArray(@Nullable T[], int)` | 5 | partial | moderate | `writePermission` | `readonly writePermission: string` |
| `writeParcelableList` | `<T extends android.os.Parcelable> void writeParcelableList(@Nullable java.util.List<T>, int)` | 5 | partial | moderate | `writePermission` | `readonly writePermission: string` |
| `writeCharArray` | `void writeCharArray(@Nullable char[])` | 5 | partial | moderate | `write` | `write(data: number[]): Promise<void>` |
| `readStringArray` | `void readStringArray(@NonNull String[])` | 5 | partial | moderate | `readSync` | `readSync(fd: number,
  buffer: ArrayBuffer,
  options?: {
    offset?: number;
    length?: number;
    position?: number;
  }): number` |
| `readStrongBinder` | `android.os.IBinder readStrongBinder()` | 5 | partial | moderate | `readNdefTag` | `readNdefTag(): Promise<string>` |
| `dataCapacity` | `int dataCapacity()` | 5 | partial | moderate | `data` | `data: string[]` |
| `dataPosition` | `int dataPosition()` | 5 | partial | moderate | `data` | `data: string[]` |
| `readLongArray` | `void readLongArray(@NonNull long[])` | 5 | partial | moderate | `readNdefTag` | `readNdefTag(): Promise<string>` |
| `writeParcelable` | `void writeParcelable(@Nullable android.os.Parcelable, int)` | 5 | partial | moderate | `write` | `write(data: number[]): Promise<void>` |
| `writeTypedSparseArray` | `<T extends android.os.Parcelable> void writeTypedSparseArray(@Nullable android.util.SparseArray<T>, int)` | 5 | partial | moderate | `writePermission` | `readonly writePermission: string` |

## Stub APIs (score < 5): 9 methods

These methods have no feasible OH mapping. Stub them according to the stub strategy in the AI Agent Playbook.

| Method | Score | Type | Stub Strategy |
|---|---|---|---|
| `writeBooleanArray` | 5 | partial | Log warning + no-op |
| `readCharArray` | 5 | partial | Return safe default (null/false/0/empty) |
| `writeSparseBooleanArray` | 5 | partial | Log warning + no-op |
| `readBooleanArray` | 4 | partial | Return safe default (null/false/0/empty) |
| `writeParcelableCreator` | 4 | partial | Log warning + no-op |
| `writeParcelableArray` | 4 | partial | Log warning + no-op |
| `marshall` | 1 | none | throw UnsupportedOperationException |
| `recycle` | 1 | none | throw UnsupportedOperationException |
| `unmarshall` | 1 | none | throw UnsupportedOperationException |

## AI Agent Instructions

**Scenario: S3 — Partial Coverage**

1. Implement 74 methods that have score >= 5
2. Stub 9 methods using the Stub Strategy column above
3. Every stub must either: throw UnsupportedOperationException, return safe default, or log+no-op
4. Document each stub with a comment: `// A2OH: not supported, OH has no equivalent`
5. Test both working methods AND verify stubs behave predictably

## Dependencies

Check if these related classes are already shimmed before generating `android.os.Parcel`:

- `android.os.Bundle` (already shimmed)

## Quality Gates

Before marking `android.os.Parcel` as done:

1. **Compilation**: `javac` succeeds with zero errors
2. **API Surface**: All 83 public methods present (implemented or stubbed)
3. **Test Coverage**: At least 74 test methods for implemented APIs
4. **No Regression**: `test_pass >= baseline`, `test_fail <= baseline + 2`
5. **Mock Consistency**: Every OHBridge method has both declaration and mock
