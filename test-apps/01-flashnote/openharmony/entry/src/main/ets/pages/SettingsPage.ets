// SettingsActivity.java → SettingsPage.ets
// A2OH-LIFECYCLE R1-R3: Activity → @Entry @Component page
// A2OH-UI-REWRITE: Switch → Toggle, SeekBar → Slider
// A2OH-DATA-LAYER R6: SharedPreferences → PersistentStorage + AppStorage

import router from '@ohos.router';

@Entry
@Component
struct SettingsPage {
  // A2OH-UI-REWRITE: Two-way binding to persistent storage
  // replaces prefs.edit().putBoolean("dark_mode", isChecked).apply()
  @StorageLink('dark_mode') darkMode: boolean = false;
  @StorageLink('font_size') fontSize: number = 16;

  build() {
    Column() {
      // Top bar
      Row() {
        Button('< Back')
          .fontSize(14)
          .backgroundColor(Color.Transparent)
          .fontColor(this.darkMode ? Color.White : '#007AFF')
          .onClick(() => {
            router.back();
          })
        Text('Settings')
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor(this.darkMode ? Color.White : Color.Black)
          .layoutWeight(1)
          .textAlign(TextAlign.Center)
        Text('').width(60)
      }
      .width('100%')
      .padding({ left: 8, right: 8, top: 12, bottom: 24 })

      // Dark mode toggle
      // A2OH-UI-REWRITE: Switch + setOnCheckedChangeListener → Toggle + onChange
      Row() {
        Text('Dark Mode')
          .fontSize(this.fontSize)
          .fontColor(this.darkMode ? Color.White : Color.Black)
          .layoutWeight(1)
        Toggle({ type: ToggleType.Switch, isOn: this.darkMode })
          .onChange((isOn: boolean) => {
            this.darkMode = isOn;
            // @StorageLink auto-persists via PersistentStorage — no manual .apply() needed
          })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: 16, bottom: 16 })

      Divider().margin({ left: 24, right: 24 })

      // Font size slider
      // A2OH-UI-REWRITE: SeekBar + OnSeekBarChangeListener → Slider + onChange
      Column() {
        Row() {
          Text('Font Size')
            .fontSize(this.fontSize)
            .fontColor(this.darkMode ? Color.White : Color.Black)
            .layoutWeight(1)
          Text(`${this.fontSize}sp`)
            .fontSize(14)
            .fontColor(Color.Gray)
        }
        .width('100%')

        Slider({
          value: this.fontSize,
          min: 12,   // minimum 12sp, same as Android Math.max(12, progress)
          max: 32,
          step: 1,
          style: SliderStyle.OutSet
        })
          .width('100%')
          .margin({ top: 8 })
          .onChange((value: number) => {
            this.fontSize = Math.round(value);
            // @StorageLink auto-persists via PersistentStorage
          })
      }
      .width('100%')
      .padding({ left: 24, right: 24, top: 16, bottom: 16 })

      Blank()
    }
    .width('100%')
    .height('100%')
    .backgroundColor(this.darkMode ? '#1a1a2e' : Color.White)
  }
}
