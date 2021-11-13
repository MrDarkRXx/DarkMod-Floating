package dark.rx;

import android.content.Context;

public class LoaderMod {

    static {
        System.loadLibrary("DarkMod");
    }

    static native void setDarkStart(Context ctx);

    public static void Start(Context ctx) {
        setDarkStart(ctx);
    }

}
