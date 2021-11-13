void AddButton(JNIEnv *env, jobject ctx, const char *name, int id){
    jclass BTN = env->GetObjectClass(ctx);
    jmethodID inter = env->GetMethodID(BTN,OBFUSCATE("addButton"),OBFUSCATE("(Ljava/lang/String;Ldark/rx/Floater$InterfaceBtn;)V"));

    jmethodID BTNS = env->GetMethodID(BTN,OBFUSCATE("BTN"),OBFUSCATE("(I)Ldark/rx/Floater$InterfaceBtn;"));

    env->CallVoidMethod(ctx, inter, env->NewStringUTF(name), env->CallObjectMethod(ctx, BTNS, id));
}

void AddSwich(JNIEnv *env, jobject ctx, const char *name, int id){
    jclass Main = env->GetObjectClass(ctx);
    jmethodID swich = env->GetMethodID(Main,OBFUSCATE("addSwitch"),OBFUSCATE("(Ljava/lang/String;Ldark/rx/Floater$InterfaceBool;)V"));

    jmethodID BOOL = env->GetMethodID(Main,OBFUSCATE("BOOL"),OBFUSCATE("(I)Ldark/rx/Floater$InterfaceBool;"));

    env->CallVoidMethod(ctx, swich, env->NewStringUTF(name), env->CallObjectMethod(ctx, BOOL, id));
}

void AddSkeedBar(JNIEnv *env, jobject ctx, const char *name, int min, int max, int id){
    jclass Main = env->GetObjectClass(ctx);
    jmethodID skeed = env->GetMethodID(Main,OBFUSCATE("addSeekBar"),OBFUSCATE("(Ljava/lang/String;IILdark/rx/Floater$InterfaceInt;)V"));

    jmethodID INT = env->GetMethodID(Main,OBFUSCATE("INT"),OBFUSCATE("(I)Ldark/rx/Floater$InterfaceInt;"));

    env->CallVoidMethod(ctx, skeed, env->NewStringUTF(name),min,max, env->CallObjectMethod(ctx, INT, id));
}

void AddCategory(JNIEnv *env, jobject ctx, const char *name){
    jclass Main = env->GetObjectClass(ctx);
    jmethodID AddCategory = env->GetMethodID(Main,OBFUSCATE("addCategory"),OBFUSCATE("(Ljava/lang/String;)V"));
    env->CallVoidMethod(ctx, AddCategory, env->NewStringUTF(name));
}

void setText(JNIEnv *env, jobject obj, const char* text){
    jclass textView = (*env).FindClass(OBFUSCATE("android/widget/TextView"));
    jmethodID setText = (*env).GetMethodID(textView, OBFUSCATE("setText"), OBFUSCATE("(Ljava/lang/CharSequence;)V"));

    jstring jstr = (*env).NewStringUTF(text);
    (*env).CallVoidMethod(obj, setText,  jstr);
}

