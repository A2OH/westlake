// NoteEditorActivity.java → NoteEditorPage.ets
// A2OH-LIFECYCLE R1-R3: Activity → @Entry @Component page
// A2OH-UI-REWRITE: EditText → TextInput/TextArea, Button → Button, Toast → promptAction
// A2OH-DEVICE-API: AlarmManager + BroadcastReceiver → reminderAgentManager
// A2OH-DEVICE-API: NotificationChannel → notificationManager.addSlot

import router from '@ohos.router';
import promptAction from '@ohos.promptAction';
import common from '@ohos.app.ability.common';
import { Note } from '../model/Note';
import { NoteStore } from '../model/NoteStore';
import { ReminderManager } from '../common/ReminderManager';

interface EditorParams {
  noteId?: string;
}

@Entry
@Component
struct NoteEditorPage {
  @State title: string = '';
  @State content: string = '';
  @StorageProp('dark_mode') darkMode: boolean = false;
  @StorageProp('font_size') fontSize: number = 16;
  private noteStore?: NoteStore;
  private currentNote?: Note;
  private isNewNote: boolean = true;

  aboutToAppear(): void {
    const context = getContext(this) as common.UIAbilityContext;
    this.noteStore = new NoteStore(context);
    this.loadNote();
  }

  private async loadNote(): Promise<void> {
    // A2OH-LIFECYCLE R7: getIntent().getStringExtra → router.getParams()
    const params = router.getParams() as EditorParams | undefined;
    if (params?.noteId && this.noteStore) {
      const note = await this.noteStore.findById(params.noteId);
      if (note) {
        this.currentNote = note;
        this.title = note.title;
        this.content = note.content;
        this.isNewNote = false;
      }
    }
    if (!this.currentNote) {
      this.currentNote = new Note();
      this.isNewNote = true;
    }
  }

  private async saveNote(): Promise<void> {
    const trimTitle = this.title.trim();
    const trimContent = this.content.trim();

    if (trimTitle.length === 0 && trimContent.length === 0) {
      promptAction.showToast({ message: 'Please enter some text' });
      return;
    }

    if (this.currentNote && this.noteStore) {
      this.currentNote.title = trimTitle;
      this.currentNote.content = trimContent;
      this.currentNote.updatedAt = Date.now();
      await this.noteStore.saveNote(this.currentNote);
      this.isNewNote = false;
      promptAction.showToast({ message: 'Note saved' });
    }
  }

  private async setReminder(): Promise<void> {
    // Save first
    await this.saveNote();

    if (this.currentNote) {
      // Schedule reminder 1 hour from now
      const triggerTime = Date.now() + (60 * 60 * 1000);
      this.currentNote.reminderAt = triggerTime;

      if (this.noteStore) {
        await this.noteStore.saveNote(this.currentNote);
      }

      // A2OH-DEVICE-API: AlarmManager + PendingIntent + BroadcastReceiver
      //                → reminderAgentManager.publishReminder
      await ReminderManager.setReminder(
        this.currentNote.id,
        this.currentNote.title,
        triggerTime
      );

      promptAction.showToast({ message: 'Reminder set for 1 hour from now' });
    }
  }

  private async deleteNote(): Promise<void> {
    if (!this.isNewNote && this.currentNote && this.noteStore) {
      await this.noteStore.deleteNote(this.currentNote.id);
      promptAction.showToast({ message: 'Note deleted' });
    }
    // A2OH-LIFECYCLE R8: finish() → router.back()
    router.back();
  }

  build() {
    Column() {
      // Top bar with back button
      Row() {
        Button('< Back')
          .fontSize(14)
          .backgroundColor(Color.Transparent)
          .fontColor(this.darkMode ? Color.White : '#007AFF')
          .onClick(() => {
            router.back();
          })
        Text('Edit Note')
          .fontSize(20)
          .fontWeight(FontWeight.Bold)
          .fontColor(this.darkMode ? Color.White : Color.Black)
          .layoutWeight(1)
          .textAlign(TextAlign.Center)
        // Spacer to balance the back button
        Text('').width(60)
      }
      .width('100%')
      .padding({ left: 8, right: 8, top: 12, bottom: 8 })

      // Title input
      // A2OH-UI-REWRITE: EditText → TextInput (single line)
      TextInput({ placeholder: 'Title', text: this.title })
        .width('100%')
        .fontSize(this.fontSize + 2)
        .margin({ left: 16, right: 16, bottom: 8 })
        .onChange((value: string) => {
          this.title = value;
        })

      // Content input
      // A2OH-UI-REWRITE: EditText(multiLine) → TextArea
      TextArea({ placeholder: 'Write your note…', text: this.content })
        .width('100%')
        .layoutWeight(1)
        .fontSize(this.fontSize)
        .margin({ left: 16, right: 16 })
        .onChange((value: string) => {
          this.content = value;
        })

      // Action buttons
      Row() {
        Button('Save')
          .layoutWeight(1)
          .margin(4)
          .onClick(() => {
            this.saveNote();
          })
        Button('Remind')
          .layoutWeight(1)
          .margin(4)
          .onClick(() => {
            this.setReminder();
          })
        Button('Delete')
          .layoutWeight(1)
          .margin(4)
          .fontColor(Color.White)
          .backgroundColor(Color.Red)
          .onClick(() => {
            this.deleteNote();
          })
      }
      .width('100%')
      .padding({ left: 12, right: 12, top: 8, bottom: 16 })
    }
    .width('100%')
    .height('100%')
    .backgroundColor(this.darkMode ? '#1a1a2e' : Color.White)
  }
}
