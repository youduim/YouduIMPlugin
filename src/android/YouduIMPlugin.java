package im.xinda.youdu.plugins;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.alibaba.fastjson.JSON;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import im.xinda.youdu.broadcastreceiver.ScreenUtil;
import im.xinda.youdu.impl.YDApiClient;
import im.xinda.youdu.item.UISessionInfo;
import im.xinda.youdu.lib.notification.NotificationCenter;
import im.xinda.youdu.lib.notification.NotificationHandler;
import im.xinda.youdu.lib.task.Task;
import im.xinda.youdu.lib.task.TaskManager;
import im.xinda.youdu.model.YDLoginModel;
import im.xinda.youdu.model.YDSessionUIModel;
import im.xinda.youdu.model.YouduIM;
import im.xinda.youdu.presenter.ImagePresenter;
import im.xinda.youdu.ui.app.YouduApp;
import im.xinda.youdu.ui.dialog.HintTextDialog;
import im.xinda.youdu.ui.dialog.MaterialDialog;
import im.xinda.youdu.ui.dialog.TextDialog;
import im.xinda.youdu.ui.loader.ImageLoader;
import im.xinda.youdu.ui.loader.Thumbnail;
import im.xinda.youdu.ui.presenter.ActivityDispatcher;
import im.xinda.youdu.ui.presenter.LoginPresenter;
import im.xinda.youdu.utils.RUtils;
import im.xinda.youdu.utils.TaskCallback;
import im.xinda.youdu.utils.Utils;

/**
 * This class echoes a string called from JavaScript.
 */
public class YouduIMPlugin extends CordovaPlugin {
    private List<UISessionInfo> sessionInfos;
    private CallbackContext callbackContext;

    public CallbackContext getCallbackContext() {
        return callbackContext;
    }

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        NotificationCenter.clearHandlers(this);
        NotificationCenter.scanHandlers(this);
        if (action.equalsIgnoreCase("setServer")) {
            setCallbackContext(callbackContext);
            String host1 = args.getString(0);
            String host2 = args.getString(1);
            String port = args.getString(2);
            this.setServerInfo(host1, host2, port);
            return true;
        } else if (action.equalsIgnoreCase("loginWithAccount")) {
            setCallbackContext(callbackContext);
            String account = args.getString(0);
            String password = args.getString(1);
            this.loginWithAccount(account, password);
            return true;
        } else if (action.equalsIgnoreCase("loginWithLoginKey")) {
            setCallbackContext(callbackContext);
            String loginKey = args.getString(0);
            this.loginWithLoginKey(loginKey);
            return true;
        } else if (action.equalsIgnoreCase("getSessionList")) {
            setCallbackContext(callbackContext);
            this.getSessionList(callbackContext);
            return true;
        } else if (action.equalsIgnoreCase("gotoSession")) {
            String sessionId = args.getString(0);
            this.gotoSession(sessionId);
            return true;
        } else if (action.equalsIgnoreCase("gotoCreateSession")) {
            this.gotoCreateSession();
            return true;
        } else if (action.equalsIgnoreCase("getUnreadCount")) {
            setCallbackContext(callbackContext);
            return true;
        } else if (action.equalsIgnoreCase("logOut")) {
            setCallbackContext(callbackContext);
            logOut();
            return true;
        }
        return false;
    }

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        initApp();
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        YouduApp.setCurrentActivity(cordova.getActivity());
        YDApiClient.INSTANCE.restore();

    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        YouduApp.setCurrentActivity(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!Utils.isAppOnForeground()
                || !ScreenUtil.INSTANCE.getScreenOn()) {
            YDApiClient.INSTANCE.setIsInBackground(cordova.getActivity(), true);
        }
    }

    private void initApp() {
        YouduApp.onAppCreate(cordova.getActivity().getApplication());

    }

    private void setServerInfo(String host1, String host2, String port) {
        YouduIM.setServerSetting(host1, host2, port);
    }

    private void loginWithAccount(String account, String password) {
        TaskManager.getMainExecutor().post(new Task() {
            @Override
            protected void run() throws Exception {
                YouduIM.loginWithAccount(account, password);
            }
        });
    }

    private void loginWithLoginKey(String loginKey) {
        YouduIM.loginWithLoginKey(loginKey);
    }

    private void getSessionList(CallbackContext callbackContext) {
        TaskManager.getGlobalExecutor().post(new Task() {
            @Override
            protected void run() throws Exception {
                YDSessionUIModel.getInstance().fetchNewestSessionInfo(new TaskCallback<List<UISessionInfo>>() {
                    @Override
                    public void onFinished(List<UISessionInfo> sessionInfoList) {
                        YouduIMPlugin.this.sessionInfos = sessionInfoList;
                        onSessionListchange(sessionInfoList);

                    }
                });
            }
        });

    }


    @NotificationHandler(name = YDSessionUIModel.kSessionListChangeNotification)
    private void onSessionListchange(List<UISessionInfo> sessionInfoList) {
        String strSessionList = JSON.toJSONString(sessionInfoList);
        JSONArray sessionListArray = null;
        try {
            sessionListArray = new JSONArray(strSessionList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject sessionsInfoObject = new JSONObject();
        try {
            sessionsInfoObject.put("sessionList", sessionListArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getCallbackContext().sendPluginResult(makeSuccPluginResult(sessionsInfoObject));

        for (int i = 0; i < sessionInfoList.size(); i++) {
            UISessionInfo info = sessionInfoList.get(i);
            loadHeadForsession(info.getSessionId(), getCallbackContext());
        }
    }

    private void loadHeadForsession(String sessionId, CallbackContext callbackContext) {
        loadSessionHead(sessionId, new TaskCallback<String>() {
            @Override
            public void onFinished(String base64) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, base64);
                result.setKeepCallback(true);
                JSONObject imageInfo = new JSONObject();
                JSONObject object = new JSONObject();
                try {
                    object.put("sessionId", sessionId);
                    object.put("avatar", base64);
                    imageInfo.put("head", object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                callbackContext.sendPluginResult(makeSuccPluginResult(imageInfo));
            }
        });
    }

    private void loadSessionHead(String sessionId, TaskCallback<String> callback) {
        ImageLoader.getInstance().loadSessionIcon(new Thumbnail() {

            private String uri;

            @Override
            public void setBitmap(Bitmap bitmap, boolean b) {
                callback.onFinished(ImagePresenter.bitmap2StrByBase64(bitmap));
            }

            @Override
            public void setDrawable(Drawable drawable) {
                callback.onFinished(ImagePresenter.drawable2Base64(drawable));
            }

            @Override
            public void setSelect(boolean b) {

            }

            @Override
            public void setUri(String uri) {
                this.uri = uri;
            }

            @Override
            public String getUri() {
                if (uri != null)
                    return uri;
                return sessionId;
            }


        }, sessionId);

    }

    private void gotoSession(String sessionId) {
        ActivityDispatcher.gotoSession(cordova.getActivity(), sessionId);
    }

    private void gotoCreateSession() {
        ActivityDispatcher.gotoCreateSession(cordova.getActivity());
    }


    private void logOut() {
        TaskManager.getMainExecutor().post(new Task() {
            @Override
            protected void run() throws Exception {
                YDLoginModel.getInstance().logout();
                JSONObject logOut = new JSONObject();
                logOut.put("logout", "success");
                getCallbackContext().sendPluginResult(makeSuccPluginResult(logOut));
            }
        });
    }

    private PluginResult makeSuccPluginResult(JSONObject object) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, object);
        result.setKeepCallback(true);
        return result;
    }

    private PluginResult makeErrorPluginResult(JSONObject object) {
        PluginResult result = new PluginResult(PluginResult.Status.ERROR, object);
        result.setKeepCallback(true);
        return result;
    }

    @NotificationHandler(name = YDSessionUIModel.kSessionListTotalUnreadSizeChangeNotification)
    private void onTotalUnreadSizeChange(int size) {
        JSONObject unreadCount = new JSONObject();
        try {
            unreadCount.put("unreadCount", size);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getCallbackContext().sendPluginResult(makeSuccPluginResult(unreadCount));
    }

    @NotificationHandler(name = YDLoginModel.kLoginSuccNotification)
    private void loginSucc(long gid) {
        JSONObject loginSucc = new JSONObject();
        try {
            loginSucc.put("loginSucc", gid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getCallbackContext().sendPluginResult(makeSuccPluginResult(loginSucc));
    }

    @NotificationHandler(name = LoginPresenter.NOTIFICATION_LOGIN_FAILED)
    private void onLoginFailed(String title, String message) {
        MaterialDialog dialog = new TextDialog(cordova.getContext())
                .setContent(message)
                .setTitle(title)
                .setFirstButton(RUtils.INSTANCE.getString(im.xinda.youdu.ui.R.string.confirm));
        dialog.setDialogButtonClick(buttonName -> {
            // todo handle login failed
            JSONObject loginFailed = new JSONObject();
            JSONObject failedInfo = new JSONObject();
            try {
                failedInfo.put("title",title);
                failedInfo.put("message",message);
                loginFailed.put("loginFailed", failedInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCallbackContext().sendPluginResult(makeSuccPluginResult(loginFailed));
        });

        dialog.show();
    }

    @NotificationHandler(name = YDLoginModel.kOnConfirmKickOut)
    private void onComfirmKickOut(String message) {
        MaterialDialog textDialog = new HintTextDialog(cordova.getActivity())
                .setContent(message)
                .setFirstButton(cordova.getContext().getString(im.xinda.youdu.R.string.determine))
                .setDialogButtonClick(buttonName -> {
                    JSONObject kickOut = new JSONObject();
                    try {
                        kickOut.put("kickOut", message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getCallbackContext().sendPluginResult(makeSuccPluginResult(kickOut));
                });
        textDialog.setCancelable(false);
        textDialog.setCanceledOnTouchOutside(false);
        textDialog.show();
    }

}
