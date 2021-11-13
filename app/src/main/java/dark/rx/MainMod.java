package dark.rx;

import android.app.Activity;
import android.os.Bundle;

public class MainMod extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoaderMod.Start(this);
    }
}