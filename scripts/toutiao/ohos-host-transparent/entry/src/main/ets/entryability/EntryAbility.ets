import UIAbility from '@ohos.app.ability.UIAbility';
import window from '@ohos.window';
import hilog from '@ohos.hilog';
import wlboot from 'libwlboot.so';

const DOMAIN = 0;
const TAG = 'WESTLAKE';

export default class EntryAbility extends UIAbility {
  onCreate(): void {
    hilog.info(DOMAIN, TAG, 'EntryAbility onCreate');
    try {
      const result = wlboot.boot();
      hilog.info(DOMAIN, TAG, `wlboot.boot() returned ${result}`);
    } catch (error) {
      hilog.info(DOMAIN, TAG, `wlboot.boot() threw ${error}`);
    }
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    hilog.info(DOMAIN, TAG, 'EntryAbility onWindowStageCreate');

    // Android windows are attached directly to the display when this launcher
    // stub cannot obtain a SceneBoard child session. Keep the ability alive as
    // their lifecycle owner, but do not cover those surfaces with an opaque
    // ArkUI placeholder.
    windowStage.loadContent('pages/Index', () => {
      try {
        const mainWindow = windowStage.getMainWindowSync();
        mainWindow.setWindowBackgroundColor('#00000000');
      } catch (error) {
        hilog.info(DOMAIN, TAG, `transparent host background failed: ${error}`);
      }
      hilog.info(DOMAIN, TAG, 'loadContent done');
    });
  }

  onForeground(): void {
    hilog.info(DOMAIN, TAG, 'EntryAbility onForeground');
  }
}
