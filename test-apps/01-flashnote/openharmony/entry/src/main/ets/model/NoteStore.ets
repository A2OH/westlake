// NoteStore.java → NoteStore.ets
// A2OH-DATA-LAYER RULE-D6: SharedPreferences → @ohos.data.preferences
// A2OH-DATA-LAYER RULE-D9: All data APIs are async

import dataPreferences from '@ohos.data.preferences';
import common from '@ohos.app.ability.common';
import { Note } from './Note';
import { hilog } from '@kit.PerformanceAnalysisKit';

const TAG = 'NoteStore';
const PREFS_NAME = 'flashnote_data';
const KEY_NOTES = 'notes_json';

export class NoteStore {
  private context: common.UIAbilityContext;

  constructor(context: common.UIAbilityContext) {
    this.context = context;
  }

  async loadAll(): Promise<Note[]> {
    try {
      const prefs = await dataPreferences.getPreferences(this.context, PREFS_NAME);
      const json = (await prefs.get(KEY_NOTES, '[]')) as string;
      const arr: Record<string, Object>[] = JSON.parse(json) as Record<string, Object>[];
      const notes: Note[] = arr.map((obj) => Note.fromJSON(obj));
      // Sort by updatedAt descending (newest first)
      notes.sort((a, b) => b.updatedAt - a.updatedAt);
      return notes;
    } catch (e) {
      hilog.error(0x0000, TAG, 'Error loading notes: %{public}s', JSON.stringify(e));
      return [];
    }
  }

  async saveAll(notes: Note[]): Promise<void> {
    try {
      const prefs = await dataPreferences.getPreferences(this.context, PREFS_NAME);
      const json = JSON.stringify(notes.map((n) => n.toJSON()));
      await prefs.put(KEY_NOTES, json);
      await prefs.flush(); // RULE-D6: flush() is required
    } catch (e) {
      hilog.error(0x0000, TAG, 'Error saving notes: %{public}s', JSON.stringify(e));
    }
  }

  async saveNote(note: Note): Promise<void> {
    const notes = await this.loadAll();
    const idx = notes.findIndex((n) => n.id === note.id);
    if (idx >= 0) {
      notes[idx] = note;
    } else {
      notes.push(note);
    }
    await this.saveAll(notes);
  }

  async deleteNote(noteId: string): Promise<void> {
    const notes = await this.loadAll();
    const filtered = notes.filter((n) => n.id !== noteId);
    await this.saveAll(filtered);
  }

  async findById(noteId: string): Promise<Note | undefined> {
    const notes = await this.loadAll();
    return notes.find((n) => n.id === noteId);
  }
}
