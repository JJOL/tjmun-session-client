package mx.x10.iowizportal.tjmunapp.utils.consts;

import mx.x10.iowizportal.tjmunapp.ApplicationCore;

/**
 * Created by JJOL on 10/11/2015.
 */
public enum StaffPosition {
    COMMITEE_NAME("Committee", "Comit\u00E9"), TOPICS("Topics", "Temas"), CHAIRS("Chairs", "Moderadores"), CO_CHAIRS("Co-Chairs", "Co-Moderadores"), SUB_TOPIC("SubTopics", "SubTemas");
    private String ES_name;
    private String EN_name;

    StaffPosition(String EN_name, String ES_name) {
        this.EN_name = EN_name;
        this.ES_name = ES_name;
    }

    public String getName() {
        String name;
        switch (ApplicationCore.APP_LAN)
        {
            case SPANISH:
                name = ES_name;
                break;
            default:
                name = EN_name;
        }
        return name;
    }
}
