package source.nova.com.bubblelauncherfree.Notification;

public class Notification {
    private String appPackage;
    private int count;

    public Notification(String appPackage, int count) {
        this.appPackage = appPackage;
        this.count = count;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString(){
        return appPackage +" "+ count;
    }
}
