package fpt.capstone.inqr.repositories.api;

import fpt.capstone.inqr.repositories.appService.AppService;

public class ClientApi extends BaseApi {
    public AppService getAppService() {
        return this.getService(AppService.class, ConfigApi.BASE_URL);
    }
}
