package nep.timeline.cirno.master;

import android.os.FileObserver;
import nep.timeline.cirno.configs.ConfigFileObserver;
import nep.timeline.cirno.hooks.android.activity.ActivityManagerServiceHook;
import nep.timeline.cirno.hooks.android.activity.ActivityStatsHook;
import nep.timeline.cirno.hooks.android.alarms.AlarmManagerService;
import nep.timeline.cirno.hooks.android.anr.ANRErrorStateHook;
import nep.timeline.cirno.hooks.android.anr.ANRHelperHooks;
import nep.timeline.cirno.hooks.android.audio.AudioStateHook;
import nep.timeline.cirno.hooks.android.audio.PlayerBanHook;
import nep.timeline.cirno.hooks.android.audio.SendMediaButtonHook;
import nep.timeline.cirno.hooks.android.binder.SamsungBinderTransHook;
import nep.timeline.cirno.hooks.android.broadcast.BroadcastDeliveryHook;
import nep.timeline.cirno.hooks.android.broadcast.BroadcastIntentHook;
import nep.timeline.cirno.hooks.android.broadcast.BroadcastSkipHook;
import nep.timeline.cirno.hooks.android.input.InputMethodManagerService;
import nep.timeline.cirno.hooks.android.intent.PendingIntentHook;
import nep.timeline.cirno.hooks.android.location.ListenerRegisterHook;
import nep.timeline.cirno.hooks.android.location.ListenerUnregisterHook;
import nep.timeline.cirno.hooks.android.network.NetworkManagerHook;
import nep.timeline.cirno.hooks.android.process.ProcessAddHook;
import nep.timeline.cirno.hooks.android.process.ProcessRemoveHook;
import nep.timeline.cirno.hooks.android.recorder.RecorderEventHook;
import nep.timeline.cirno.hooks.android.recorder.ReleaseRecorderHook;
import nep.timeline.cirno.hooks.android.signal.SendSignalHook;
import nep.timeline.cirno.hooks.android.signal.SendSignalQuietHook;
import nep.timeline.cirno.hooks.android.vpn.VpnStateHook;
import nep.timeline.cirno.hooks.android.wakelock.WakeLockHook;
import nep.timeline.cirno.services.BinderService;

public class AndroidHooks {
    public static void start(ClassLoader classLoader) {
        // Config
        FileObserver fileObserver = new ConfigFileObserver();
        fileObserver.startWatching();

        // ANR
        new ANRErrorStateHook(classLoader);
        new ANRHelperHooks(classLoader);
        // Signal
        new SendSignalHook(classLoader);
        new SendSignalQuietHook(classLoader);
        // Audio
        new AudioStateHook(classLoader);
        new PlayerBanHook(classLoader);
        new SendMediaButtonHook(classLoader);
        // Location
        new ListenerRegisterHook(classLoader);
        new ListenerUnregisterHook(classLoader);
        // InputMethod
        new InputMethodManagerService(classLoader);
        // Network
        new NetworkManagerHook(classLoader);
        // Alarms
        new AlarmManagerService(classLoader);
        // Broadcast
        new BroadcastSkipHook(classLoader);
        new BroadcastIntentHook(classLoader);
        new BroadcastDeliveryHook(classLoader);
        // WakeLock
        new WakeLockHook(classLoader);
        // Activity
        new ActivityManagerServiceHook(classLoader);
        new ActivityStatsHook(classLoader);
        // Process
        new ProcessAddHook(classLoader);
        new ProcessRemoveHook(classLoader);
        // Binder
        new SamsungBinderTransHook(classLoader);
        // Recorder
        new RecorderEventHook(classLoader);
        new ReleaseRecorderHook(classLoader);
        // Vpn
        new VpnStateHook(classLoader);
        // Intent
        new PendingIntentHook(classLoader);
        // ReKernel
        BinderService.start(classLoader);
    }
}
