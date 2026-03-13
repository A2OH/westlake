// MainActivity.java UI → MainPage.ets
// A2OH-UI-REWRITE: XML layout + Java imperative → @Component declarative build()
// A2OH-UI-REWRITE: LinearLayout(vertical) → Column()
// A2OH-UI-REWRITE: ScrollView → Scroll / List
// A2OH-UI-REWRITE: EditText → TextInput
// A2OH-UI-REWRITE: findViewById + setText → @State reactive binding
// A2OH-UI-REWRITE: Toast → promptAction.showToast
// A2OH-UI-REWRITE: startActivity(Intent) → router.pushUrl

import router from '@ohos.router';
import promptAction from '@ohos.promptAction';
import common from '@ohos.app.ability.common';
import { Note } from '../model/Note';
import { NoteStore } from '../model/NoteStore';

// Settings stored in AppStorage for cross-page reactivity
// A2OH-DATA-LAYER R6: SharedPreferences("flashnote_settings") → AppStorage
PersistentStorage.persistProp<boolean>('dark_mode', false);
PersistentStorage.persistProp<number>('font_size', 16);

@Entry
@Component
struct MainPage {
  @State allNotes: Note[] = [];
  @State filteredNotes: Note[] = [];
  @State searchQuery: string = '';
  @StorageProp('dark_mode') darkMode: boolean = false;
  @StorageProp('font_size') fontSize: number = 16;
  private noteStore?: NoteStore;

  aboutToAppear(): void {
    const context = getContext(this) as common.UIAbilityContext;
    this.noteStore = new NoteStore(context);
    this.loadNotes();
  }

  // A2OH-LIFECYCLE R4: onResume → onPageShow
  onPageShow(): void {
    this.loadNotes();
  }

  private async loadNotes(): Promise<void> {
    if (this.noteStore) {
      this.allNotes = await this.noteStore.loadAll();
      this.filterNotes();
    }
  }

  private filterNotes(): void {
    if (this.searchQuery.length === 0) {
      this.filteredNotes = [...this.allNotes];
      return;
    }
    const lower = this.searchQuery.toLowerCase();
    this.filteredNotes = this.allNotes.filter((note) =>
      note.title.toLowerCase().includes(lower) ||
      note.content.toLowerCase().includes(lower)
    );
  }

  build() {
    Column() {
      // Title bar
      Row() {
        Text('FlashNote')
          .fontSize(22)
          .fontWeight(FontWeight.Bold)
          .fontColor(this.darkMode ? Color.White : Color.Black)
        Blank()
        // Settings button
        // A2OH-UI-REWRITE: Button + startActivity → Button + router.pushUrl
        Button('Settings')
          .fontSize(14)
          .onClick(() => {
            router.pushUrl({ url: 'pages/SettingsPage' });
          })
      }
      .width('100%')
      .padding({ left: 16, right: 16, top: 12, bottom: 8 })

      // Search bar
      // A2OH-UI-REWRITE: EditText + TextWatcher → TextInput + onChange
      TextInput({ placeholder: 'Search notes…', text: this.searchQuery })
        .width('100%')
        .margin({ left: 16, right: 16, bottom: 8 })
        .onChange((value: string) => {
          this.searchQuery = value;
          this.filterNotes();
        })

      // Notes list
      // A2OH-UI-REWRITE: ScrollView + programmatic LinearLayout → List + ForEach
      if (this.filteredNotes.length === 0) {
        Text('No notes yet. Tap "Add Note" to create one.')
          .fontColor(Color.Gray)
          .margin({ top: 48 })
          .fontSize(this.fontSize)
      } else {
        List({ space: 12 }) {
          ForEach(this.filteredNotes, (note: Note) => {
            ListItem() {
              Column() {
                // Title
                Text(note.title.length > 0 ? note.title : 'Untitled')
                  .fontSize(this.fontSize + 2)
                  .fontColor(this.darkMode ? Color.White : Color.Black)
                  .fontWeight(FontWeight.Medium)
                // Preview
                Text(note.getPreview())
                  .fontSize(this.fontSize - 2)
                  .fontColor(this.darkMode ? '#cccccc' : '#555555')
                  .margin({ top: 4 })
                // Date
                Text(note.getFormattedDate())
                  .fontSize(this.fontSize - 4)
                  .fontColor(Color.Gray)
                  .margin({ top: 4 })
              }
              .alignItems(HorizontalAlign.Start)
              .width('100%')
              .padding(16)
              .backgroundColor(this.darkMode ? '#16213e' : '#f0f0f0')
              .borderRadius(8)
            }
            .onClick(() => {
              // A2OH-UI-REWRITE: Intent + putExtra → router.pushUrl + params
              router.pushUrl({
                url: 'pages/NoteEditorPage',
                params: { noteId: note.id }
              });
            })
          }, (note: Note) => note.id)
        }
        .width('100%')
        .layoutWeight(1)
        .padding({ left: 16, right: 16 })
      }

      // Add Note button
      Button('Add Note')
        .width('90%')
        .margin({ top: 12, bottom: 16 })
        .onClick(() => {
          router.pushUrl({ url: 'pages/NoteEditorPage' });
        })
    }
    .width('100%')
    .height('100%')
    .backgroundColor(this.darkMode ? '#1a1a2e' : Color.White)
  }
}
