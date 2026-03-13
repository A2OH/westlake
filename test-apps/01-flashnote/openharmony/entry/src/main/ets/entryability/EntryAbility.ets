// MainActivity.java lifecycle → EntryAbility.ets
// A2OH-LIFECYCLE R1-R3: Activity → UIAbility, setContentView → windowStage.loadContent

import UIAbility from '@ohos.app.ability.UIAbility';
import AbilityConstant from '@ohos.app.ability.AbilityConstant';
import Want from '@ohos.app.ability.Want';
import window from '@ohos.window';
import { hilog } from '@kit.PerformanceAnalysisKit';

const TAG = 'EntryAbility';

export default class EntryAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    hilog.info(0x0000, TAG, 'onCreate');
    // Store the initial want so pages can read navigation params
    if (want.parameters?.['noteId']) {
      AppStorage.setOrCreate<string>('launchNoteId', want.parameters['noteId'] as string);
    }
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    // A2OH-LIFECYCLE R3: setContentView → loadContent
    hilog.info(0x0000, TAG, 'onWindowStageCreate');
    windowStage.loadContent('pages/MainPage', (err) => {
      if (err.code) {
        hilog.error(0x0000, TAG, 'Failed to load content: %{public}s', JSON.stringify(err));
      }
    });
  }

  onForeground(): void {
    // A2OH-LIFECYCLE R4: onResume → onForeground
    hilog.info(0x0000, TAG, 'onForeground');
  }

  onBackground(): void {
    // A2OH-LIFECYCLE R4: onPause/onStop → onBackground
    hilog.info(0x0000, TAG, 'onBackground');
  }

  onNewWant(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    // A2OH-LIFECYCLE R5: onNewIntent → onNewWant
    hilog.info(0x0000, TAG, 'onNewWant');
    if (want.parameters?.['noteId']) {
      AppStorage.setOrCreate<string>('launchNoteId', want.parameters['noteId'] as string);
    }
  }

  onDestroy(): void {
    hilog.info(0x0000, TAG, 'onDestroy');
  }
}
