# FlashNote: Android → OpenHarmony Conversion Map

## File Mapping

| Android Source | OpenHarmony Target | Skill Applied |
|---|---|---|
| `AndroidManifest.xml` | `module.json5` | A2OH-CONFIG R1-R8 |
| `res/values/strings.xml` | `resources/base/element/string.json` | A2OH-CONFIG |
| `res/layout/*.xml` (3 files) | Deleted — inline in `build()` | A2OH-UI-REWRITE rule 1 |
| `Note.java` | `model/Note.ets` | A2OH-JAVA-TO-ARKTS |
| `NoteStore.java` | `model/NoteStore.ets` | A2OH-DATA-LAYER D6, D9 |
| `MainActivity.java` (lifecycle) | `entryability/EntryAbility.ets` | A2OH-LIFECYCLE R1-R5 |
| `MainActivity.java` (UI) | `pages/MainPage.ets` | A2OH-UI-REWRITE |
| `NoteEditorActivity.java` | `pages/NoteEditorPage.ets` | A2OH-UI-REWRITE, A2OH-DEVICE-API |
| `SettingsActivity.java` | `pages/SettingsPage.ets` | A2OH-UI-REWRITE |
| `ReminderReceiver.java` | `common/ReminderManager.ets` | A2OH-DEVICE-API |
| `FlashNoteApp.java` (Application) | Not needed (AbilityStage optional) | A2OH-LIFECYCLE R13 |

## API Conversion Details

### Lifecycle (A2OH-LIFECYCLE)
| Android API | OH API | Rule |
|---|---|---|
| `extends Activity` | `extends UIAbility` | R1 |
| `setContentView(R.layout.x)` | `windowStage.loadContent('pages/X')` | R3 |
| `onResume()` | `onForeground()` / `onPageShow()` | R4 |
| `onPause()` / `onStop()` | `onBackground()` | R4 |
| `onNewIntent(intent)` | `onNewWant(want)` | R5 |
| `getIntent().getStringExtra()` | `router.getParams()` | R7 |
| `finish()` | `router.back()` | R8 |
| `startActivity(Intent)` | `router.pushUrl({url, params})` | R9 |

### UI (A2OH-UI-REWRITE)
| Android API | OH API | Notes |
|---|---|---|
| XML `LinearLayout` (vertical) | `Column()` | Core paradigm shift |
| XML `ScrollView` | `List` with `ForEach` | For dynamic list |
| `EditText` (single line) | `TextInput()` | `.onChange()` |
| `EditText` (multi line) | `TextArea()` | `.onChange()` |
| `Button` | `Button()` | `.onClick()` |
| `Switch` | `Toggle({ type: ToggleType.Switch })` | `.onChange()` |
| `SeekBar` | `Slider({ min, max, step })` | `.onChange()` |
| `TextView` | `Text()` | `.fontSize()`, `.fontColor()` |
| `Toast.makeText().show()` | `promptAction.showToast({message})` | Import promptAction |
| `R.string.xxx` | `$r('app.string.xxx')` | Resource reference |
| `findViewById` + `setText` | `@State` reactive binding | No imperative mutation |
| `TextWatcher.afterTextChanged` | `TextInput.onChange()` | Declarative |
| Programmatic `addView` loop | `ForEach` in `List` | Declarative iteration |

### Data Layer (A2OH-DATA-LAYER)
| Android API | OH API | Rule |
|---|---|---|
| `getSharedPreferences(name, MODE)` | `dataPreferences.getPreferences(ctx, name)` | D6 |
| `prefs.getString(key, default)` | `await prefs.get(key, default)` | D6 |
| `edit().putString(k,v).apply()` | `await prefs.put(k,v); await prefs.flush()` | D6 |
| `prefs.getBoolean()` / `getInt()` | `PersistentStorage.persistProp()` + `@StorageLink` | D6 + UI binding |
| Synchronous API | All async (Promise) | D9 |

### Device API (A2OH-DEVICE-API)
| Android API | OH API | Rule |
|---|---|---|
| `AlarmManager.setExact()` | `reminderAgentManager.publishReminder()` | Timer reminder |
| `PendingIntent.getBroadcast()` | `wantAgent` in reminder request | No PendingIntent concept |
| `BroadcastReceiver.onReceive()` | Eliminated — reminder system handles it | R4 (CONFIG) |
| `NotificationCompat.Builder` | Built into reminder request `title`/`content` | Simplified |
| `NotificationChannel` | `notificationManager.addSlot()` | Slot-based |
| `NotificationManager.notify()` | Handled by reminderAgentManager | Automatic |

### Config (A2OH-CONFIG)
| Android Element | OH Element | Rule |
|---|---|---|
| `<activity>` MAIN/LAUNCHER | `abilities[0].skills: [{actions: ["ohos.want.action.home"]}]` | R6 |
| `android:launchMode="singleTask"` | `"launchType": "singleton"` | R7 |
| `<uses-permission> POST_NOTIFICATIONS` | `requestPermissions: [{name: "ohos.permission.PUBLISH_AGENT_REMINDER"}]` | R8 |
| `<receiver>` (BroadcastReceiver) | Not in module.json5 — handled in code | R4 |

### Java → ArkTS (A2OH-JAVA-TO-ARKTS)
| Java Pattern | ArkTS Pattern |
|---|---|
| `UUID.randomUUID().toString()` | `util.generateRandomUUID()` |
| `System.currentTimeMillis()` | `Date.now()` |
| `SimpleDateFormat` | Manual string formatting |
| `new ArrayList<>()` / `List<>` | `Array<T>` |
| `for (Note n : notes)` | `notes.filter()` / `notes.map()` |
| `Collections.sort(list, comparator)` | `array.sort()` |
| Java class + getters/setters | ArkTS class + public fields |
| `JSONObject` / `JSONArray` | `JSON.parse()` / `JSON.stringify()` |
| `null` checks | Optional chaining `?.` and `??` |
| `String.toLowerCase().contains()` | `string.toLowerCase().includes()` |
