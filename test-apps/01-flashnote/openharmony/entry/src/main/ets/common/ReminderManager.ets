// ReminderReceiver.java + AlarmManager → reminderAgentManager
// A2OH-DEVICE-API: BroadcastReceiver → no manifest entry (R4 from CONFIG)
// AlarmManager + PendingIntent + BroadcastReceiver → @ohos.reminderAgentManager

import reminderAgentManager from '@ohos.reminderAgentManager';
import notificationManager from '@ohos.notificationManager';
import { hilog } from '@kit.PerformanceAnalysisKit';

const TAG = 'ReminderManager';

export class ReminderManager {

  static async setReminder(noteId: string, noteTitle: string, triggerTimeMs: number): Promise<void> {
    try {
      // Create a notification slot for reminders
      await notificationManager.addSlot(notificationManager.SlotType.CONTENT_INFORMATION);

      const delaySeconds = Math.max(1, Math.floor((triggerTimeMs - Date.now()) / 1000));

      const reminder: reminderAgentManager.ReminderRequestTimer = {
        reminderType: reminderAgentManager.ReminderType.REMINDER_TYPE_TIMER,
        triggerTimeInSeconds: delaySeconds,
        title: 'FlashNote Reminder',
        content: noteTitle || 'You have a note to review',
        expiredContent: 'Reminder expired',
        slotType: notificationManager.SlotType.CONTENT_INFORMATION,
        notificationId: hashCode(noteId),
        wantAgent: {
          pkgName: 'com.example.flashnote',
          abilityName: 'EntryAbility',
          parameters: {
            noteId: noteId
          }
        },
        actionButton: [
          {
            title: 'Open',
            type: reminderAgentManager.ActionButtonType.ACTION_BUTTON_TYPE_CUSTOM
          }
        ]
      };

      const reminderId = await reminderAgentManager.publishReminder(reminder);
      hilog.info(0x0000, TAG, 'Reminder set id=%{public}d for note=%{public}s', reminderId, noteId);
    } catch (e) {
      hilog.error(0x0000, TAG, 'Failed to set reminder: %{public}s', JSON.stringify(e));
    }
  }
}

function hashCode(str: string): number {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    const char = str.charCodeAt(i);
    hash = ((hash << 5) - hash) + char;
    hash = hash & hash; // Convert to 32-bit integer
  }
  return Math.abs(hash);
}
