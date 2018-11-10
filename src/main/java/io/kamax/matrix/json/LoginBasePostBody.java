package io.kamax.matrix.json;

public abstract class LoginBasePostBody {
    protected String password;
    protected String type = "m.login.password";
    protected String initialDeviceDisplayName = "";

    public LoginBasePostBody(String password) {
        this.password = password;
    }

    public String getInitialDeviceDisplayName() {
        return initialDeviceDisplayName;
    }

    public void setInitialDeviceDisplayName(String initialDeviceDisplayName) {
        this.initialDeviceDisplayName = initialDeviceDisplayName;
    }
}
