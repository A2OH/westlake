// Note.java → Note.ets
// A2OH-JAVA-TO-ARKTS: class with getters/setters → ArkTS class with public fields

import { util } from '@kit.ArkTS';

export class Note {
  id: string;
  title: string;
  content: string;
  createdAt: number;
  updatedAt: number;
  reminderAt: number; // 0 = no reminder

  constructor(id?: string, title?: string, content?: string,
              createdAt?: number, updatedAt?: number, reminderAt?: number) {
    this.id = id ?? util.generateRandomUUID();
    this.title = title ?? '';
    this.content = content ?? '';
    this.createdAt = createdAt ?? Date.now();
    this.updatedAt = updatedAt ?? this.createdAt;
    this.reminderAt = reminderAt ?? 0;
  }

  getFormattedDate(): string {
    const d = new Date(this.updatedAt);
    const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
    const month = months[d.getMonth()];
    const day = d.getDate().toString().padStart(2, '0');
    const year = d.getFullYear();
    const hours = d.getHours().toString().padStart(2, '0');
    const mins = d.getMinutes().toString().padStart(2, '0');
    return `${month} ${day}, ${year} ${hours}:${mins}`;
  }

  getPreview(): string {
    if (this.content.length <= 80) {
      return this.content;
    }
    return this.content.substring(0, 80) + '…';
  }

  toJSON(): object {
    return {
      id: this.id,
      title: this.title,
      content: this.content,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt,
      reminderAt: this.reminderAt
    };
  }

  static fromJSON(obj: Record<string, Object>): Note {
    return new Note(
      obj['id'] as string,
      obj['title'] as string,
      obj['content'] as string,
      obj['createdAt'] as number,
      obj['updatedAt'] as number,
      obj['reminderAt'] as number
    );
  }
}
