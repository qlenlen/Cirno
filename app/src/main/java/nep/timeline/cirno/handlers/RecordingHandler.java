package nep.timeline.cirno.handlers;

import java.util.Set;

import nep.timeline.cirno.entity.AppRecord;
import nep.timeline.cirno.log.Log;
import nep.timeline.cirno.services.FreezerService;
import nep.timeline.cirno.threads.FreezerHandler;

public class RecordingHandler {
    public static final int RECORD_RIID_INVALID = -1;
    public static final int RECORDER_STATE_STARTED = 0;
    public static final int RECORDER_STATE_STOPPED = 1;

    public static void call(AppRecord appRecord, int event, int riid) {
        Set<Integer> set = appRecord.getAppState().getRecodingIds();
        if (event == RECORDER_STATE_STARTED)
            set.add(riid);
        else
            set.remove(riid);

        if (set.isEmpty()) {
            if (appRecord.getAppState().setRecording(false)) {
                Log.d("应用 " + appRecord.getPackageNameWithUser() + " 停止录音");
                FreezerHandler.sendFreezeMessageIgnoreMessages(appRecord, 3000);
            }
        } else if (appRecord.getAppState().setRecording(true)) {
            Log.d("应用 " + appRecord.getPackageNameWithUser() + " 开始录音");
            FreezerService.thaw(appRecord);
        }
    }
}
