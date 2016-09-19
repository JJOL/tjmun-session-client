package mx.x10.iowizportal.tjmunapp.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by JJOL on 05/11/2015.
 */
public class ResourceLibrary {

    public final static String SHADOW_BOX_B = "-fx-effect: dropshadow(three-pass-box, blue, 10, 0, 0, 0);";
    public final static String SHADOW_BOX_R = "-fx-effect: dropshadow(three-pass-box, red, 10, 0, 0, 0);";

    public static final Image CROSS_ICON = new Image("cross.png");
    public static final Image CHECK_ICON = new Image("check.png");
    public static final Image DEFAULT_FLAG = new Image("mun_flag.jpg");
    public static final Image PLUS_ICON  = new Image("plus.png");

    public static final int CENTRAL_COLUMN_WIDTH = 300;

    public static ImageView getTinyIcon(Image ico) {
        ImageView iconView = new ImageView(ico);
        iconView.setFitHeight(15);
        iconView.setFitWidth(15);
        return iconView;
    }

    public static String valueFromStyle(String style) {
        boolean hit = false;
        char[] chars = style.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < chars.length-1; i++) {
            if(hit)
                sb.append(chars[i]);
            if(chars[i] == '#')
                hit = true;
        }
        return sb.toString();
    }
}
