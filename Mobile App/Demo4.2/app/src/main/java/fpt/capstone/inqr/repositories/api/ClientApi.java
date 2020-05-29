package fpt.capstone.inqr.repositories.api;

import android.content.Context;

import fpt.capstone.inqr.helper.PreferenceHelper;
import fpt.capstone.inqr.repositories.appService.AppService;

public class ClientApi extends BaseApi {

    private Context context;

    public ClientApi(Context context) {
        this.context = context;
    }

    public AppService getAppService() {
        String baseUrl = PreferenceHelper.getString(context, "BASE_URL");
        if (baseUrl == null) {
            baseUrl = ConfigApi.BASE_URL;
        }
        return this.getService(AppService.class, baseUrl);
    }
}
