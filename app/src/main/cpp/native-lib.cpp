#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_worka_eroyal_utils_JNIUtil_apiEndpoint(JNIEnv *env, jobject) {
    return env->NewStringUTF(
#ifdef DEV
            "http://deefecdd.ngrok.io/"
#endif
            #ifdef STAGING
            "http://7ebfa652b03b.ngrok.io/"
            #endif
            #ifdef PRODUCTION
            "http://api.ras.co.id/"
#endif
    );
}
