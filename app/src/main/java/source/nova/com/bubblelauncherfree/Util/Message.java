package source.nova.com.bubblelauncherfree.Util;

/**
 * Created by joshua on 09.07.16.
 */
public class Message {
    public static final String SIZE_CHANGE = "size_change";
    public static final String PADDING_CHANGE = "padding_change";
    public static final String LABEL_CHANGE = "label_changed";
    public static final String APP_DEINSTALL = "app_deinstall";
    public static final String APP_INSTALL = "app_install";
    public static final String WALLP_CHANGE = "wallp_change";
    public static final String BUTTON_BAR_CHANGE = "button_bar_change";
    public static final String STATUS_BAR_CHANGE = "status_bar_change";

    public String type;
    public int valueint;
    public boolean valuebool;
    public String valuestr;

    public Message(String type, int valueint){
        this.type = type;
        this.valueint = valueint;
    }

    public Message(String type, boolean valuebool){
        this.type = type;
        this.valuebool = valuebool;
    }

    public Message(String type, String valuestr){
        this.type = type;
        this.valuestr = valuestr;
    }

}
