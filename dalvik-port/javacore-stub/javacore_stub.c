#define _GNU_SOURCE
#include <jni.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <signal.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <unistd.h>

/* libjavacore.so stub — minimal JNI natives for Dalvik VM boot. */

static void setInt(JNIEnv* e, jclass c, const char* n, int v) {
    jfieldID f = (*e)->GetStaticFieldID(e, c, n, "I");
    if (f) (*e)->SetStaticIntField(e, c, f, v);
}

/* ═══ OsConstants ═══ */
JNIEXPORT void JNICALL Java_libcore_io_OsConstants_initConstants(JNIEnv* e, jclass c) {
    setInt(e,c,"EACCES",EACCES); setInt(e,c,"EAGAIN",EAGAIN); setInt(e,c,"EBADF",EBADF);
    setInt(e,c,"EEXIST",EEXIST); setInt(e,c,"EINTR",EINTR); setInt(e,c,"EINVAL",EINVAL);
    setInt(e,c,"EIO",EIO); setInt(e,c,"EISDIR",EISDIR); setInt(e,c,"ENOENT",ENOENT);
    setInt(e,c,"ENOMEM",ENOMEM); setInt(e,c,"ENOSPC",ENOSPC); setInt(e,c,"EPERM",EPERM);
    setInt(e,c,"EPIPE",EPIPE); setInt(e,c,"ERANGE",ERANGE);
    setInt(e,c,"SIGKILL",SIGKILL); setInt(e,c,"SIGTERM",SIGTERM);
    setInt(e,c,"O_RDONLY",O_RDONLY); setInt(e,c,"O_WRONLY",O_WRONLY); setInt(e,c,"O_RDWR",O_RDWR);
    setInt(e,c,"O_CREAT",O_CREAT); setInt(e,c,"O_TRUNC",O_TRUNC); setInt(e,c,"O_APPEND",O_APPEND);
    setInt(e,c,"S_IFMT",0170000); setInt(e,c,"S_IFDIR",0040000); setInt(e,c,"S_IFREG",0100000);
    setInt(e,c,"AF_INET",2); setInt(e,c,"AF_UNIX",1); setInt(e,c,"SOCK_STREAM",1);
    setInt(e,c,"SEEK_SET",SEEK_SET); setInt(e,c,"SEEK_CUR",SEEK_CUR); setInt(e,c,"SEEK_END",SEEK_END);
}

/* ═══ ICU ═══ */
JNIEXPORT jobjectArray JNICALL Java_libcore_icu_ICU_getAvailableLocalesNative(JNIEnv* e, jclass c) {
    jclass s=(*e)->FindClass(e,"java/lang/String"); jobjectArray a=(*e)->NewObjectArray(e,1,s,NULL);
    (*e)->SetObjectArrayElement(e,a,0,(*e)->NewStringUTF(e,"en_US")); return a; }
JNIEXPORT void JNICALL Java_libcore_icu_ICU_setDefaultLocale(JNIEnv* e, jclass c, jstring l) {}
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getDefaultLocale(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,"en_US"); }
JNIEXPORT jboolean JNICALL Java_libcore_icu_ICU_initLocaleDataNative(JNIEnv* e, jclass c, jstring l, jobject d) {
    jclass lc=(*e)->GetObjectClass(e,d); if(!lc) return JNI_FALSE; jfieldID f;
    f=(*e)->GetFieldID(e,lc,"decimalSeparator","C"); if(f) (*e)->SetCharField(e,d,f,'.');
    f=(*e)->GetFieldID(e,lc,"groupingSeparator","C"); if(f) (*e)->SetCharField(e,d,f,',');
    f=(*e)->GetFieldID(e,lc,"zeroDigit","C"); if(f) (*e)->SetCharField(e,d,f,'0');
    f=(*e)->GetFieldID(e,lc,"percent","C"); if(f) (*e)->SetCharField(e,d,f,'%');
    f=(*e)->GetFieldID(e,lc,"minusSign","C"); if(f) (*e)->SetCharField(e,d,f,'-');
    f=(*e)->GetFieldID(e,lc,"patternSeparator","C"); if(f) (*e)->SetCharField(e,d,f,';');
    f=(*e)->GetFieldID(e,lc,"monetarySeparator","C"); if(f) (*e)->SetCharField(e,d,f,'.');
    f=(*e)->GetFieldID(e,lc,"exponentSeparator","Ljava/lang/String;"); if(f) (*e)->SetObjectField(e,d,f,(*e)->NewStringUTF(e,"E"));
    f=(*e)->GetFieldID(e,lc,"infinity","Ljava/lang/String;"); if(f) (*e)->SetObjectField(e,d,f,(*e)->NewStringUTF(e,"\xe2\x88\x9e"));
    f=(*e)->GetFieldID(e,lc,"NaN","Ljava/lang/String;"); if(f) (*e)->SetObjectField(e,d,f,(*e)->NewStringUTF(e,"NaN"));
    f=(*e)->GetFieldID(e,lc,"numberPattern","Ljava/lang/String;"); if(f) (*e)->SetObjectField(e,d,f,(*e)->NewStringUTF(e,"#,##0.###"));
    f=(*e)->GetFieldID(e,lc,"currencyPattern","Ljava/lang/String;"); if(f) (*e)->SetObjectField(e,d,f,(*e)->NewStringUTF(e,"$#,##0.00"));
    f=(*e)->GetFieldID(e,lc,"percentPattern","Ljava/lang/String;"); if(f) (*e)->SetObjectField(e,d,f,(*e)->NewStringUTF(e,"#,##0%"));
    f=(*e)->GetFieldID(e,lc,"integerPattern","Ljava/lang/String;"); if(f) (*e)->SetObjectField(e,d,f,(*e)->NewStringUTF(e,"#0"));
    f=(*e)->GetFieldID(e,lc,"currencySymbol","Ljava/lang/String;"); if(f) (*e)->SetObjectField(e,d,f,(*e)->NewStringUTF(e,"$"));
    f=(*e)->GetFieldID(e,lc,"internationalCurrencySymbol","Ljava/lang/String;"); if(f) (*e)->SetObjectField(e,d,f,(*e)->NewStringUTF(e,"USD"));
    return JNI_TRUE; }
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getCurrencyCode(JNIEnv* e, jclass c, jstring l) { return (*e)->NewStringUTF(e,"USD"); }
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getISO3Country(JNIEnv* e, jclass c, jstring l) { return (*e)->NewStringUTF(e,"USA"); }
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getISO3Language(JNIEnv* e, jclass c, jstring l) { return (*e)->NewStringUTF(e,"eng"); }
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getScript(JNIEnv* e, jclass c, jstring l) { return (*e)->NewStringUTF(e,""); }
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getBestDateTimePatternNative(JNIEnv* e, jclass c, jstring l, jstring s) { return (*e)->NewStringUTF(e,"yyyy-MM-dd HH:mm:ss"); }
JNIEXPORT jobjectArray JNICALL Java_libcore_icu_ICU_getISOLanguagesNative(JNIEnv* e, jclass c) {
    jclass s=(*e)->FindClass(e,"java/lang/String"); jobjectArray a=(*e)->NewObjectArray(e,1,s,NULL);
    (*e)->SetObjectArrayElement(e,a,0,(*e)->NewStringUTF(e,"en")); return a; }
JNIEXPORT jobjectArray JNICALL Java_libcore_icu_ICU_getISOCountriesNative(JNIEnv* e, jclass c) {
    jclass s=(*e)->FindClass(e,"java/lang/String"); jobjectArray a=(*e)->NewObjectArray(e,1,s,NULL);
    (*e)->SetObjectArrayElement(e,a,0,(*e)->NewStringUTF(e,"US")); return a; }
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getIcuVersion(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,"58.2"); }
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getUnicodeVersion(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,"9.0"); }
JNIEXPORT jstring JNICALL Java_libcore_icu_ICU_getCldrVersion(JNIEnv* e, jclass c) { return (*e)->NewStringUTF(e,"30"); }

/* ═══ File ═══ */
JNIEXPORT jstring JNICALL Java_java_io_File_realpath(JNIEnv* e, jclass c, jstring p) {
    const char* s=(*e)->GetStringUTFChars(e,p,NULL); if(!s) return p;
    char b[4096]; char* r=realpath(s,b); (*e)->ReleaseStringUTFChars(e,p,s);
    return r ? (*e)->NewStringUTF(e,b) : p; }
JNIEXPORT jboolean JNICALL Java_java_io_File_setLastModifiedImpl(JNIEnv* e, jclass c, jstring p, jlong t) { return JNI_TRUE; }

/* ═══ System ═══ */
JNIEXPORT jlong JNICALL Java_java_lang_System_currentTimeMillis(JNIEnv* e, jclass c) {
    struct timeval tv; gettimeofday(&tv,NULL); return (jlong)tv.tv_sec*1000+tv.tv_usec/1000; }
JNIEXPORT jlong JNICALL Java_java_lang_System_nanoTime(JNIEnv* e, jclass c) {
    struct timeval tv; gettimeofday(&tv,NULL); return (jlong)tv.tv_sec*1000000000LL+(jlong)tv.tv_usec*1000; }
JNIEXPORT jstring JNICALL Java_java_lang_System_mapLibraryName(JNIEnv* e, jclass c, jstring n) {
    const char* s=(*e)->GetStringUTFChars(e,n,NULL); if(!s) return n;
    char b[256]; snprintf(b,sizeof(b),"lib%s.so",s); (*e)->ReleaseStringUTFChars(e,n,s);
    return (*e)->NewStringUTF(e,b); }
JNIEXPORT jobject JNICALL Java_java_lang_System_initProperties(JNIEnv* e, jclass c, jobject p) {
    if(!p) return NULL; jclass pc=(*e)->GetObjectClass(e,p);
    jmethodID set=(*e)->GetMethodID(e,pc,"setProperty","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");
    if(!set){(*e)->ExceptionClear(e); return p;}
    #define P(k,v) (*e)->CallObjectMethod(e,p,set,(*e)->NewStringUTF(e,k),(*e)->NewStringUTF(e,v))
    P("java.vm.name","Dalvik"); P("file.separator","/"); P("line.separator","\n");
    P("path.separator",":"); P("file.encoding","UTF-8"); P("user.language","en");
    P("user.region","US"); P("user.dir","/"); P("user.home","/"); P("user.name","root");
    P("os.arch","arm"); P("os.name","Linux"); P("os.version","4.19");
    P("java.io.tmpdir","/tmp"); P("java.library.path","/data/a2oh");
    P("java.home","/system"); P("java.ext.dirs",""); P("java.class.path",".");
    #undef P
    return p; }
JNIEXPORT jobjectArray JNICALL Java_java_lang_System_specialProperties(JNIEnv* e, jclass c) {
    return (*e)->NewObjectArray(e,0,(*e)->FindClass(e,"java/lang/String"),NULL); }

/* ═══ Runtime ═══ */
JNIEXPORT jlong JNICALL Java_java_lang_Runtime_freeMemory(JNIEnv* e, jobject s) { return 64*1024*1024; }
JNIEXPORT jlong JNICALL Java_java_lang_Runtime_totalMemory(JNIEnv* e, jobject s) { return 64*1024*1024; }
JNIEXPORT jlong JNICALL Java_java_lang_Runtime_maxMemory(JNIEnv* e, jobject s) { return 256*1024*1024; }

/* ═══ JNI_OnLoad ═══ */
static int tryReg(JNIEnv* e, const char* cls, const JNINativeMethod* m, int n) {
    jclass c=(*e)->FindClass(e,cls); if(!c){(*e)->ExceptionClear(e); return 0;}
    int r=(*e)->RegisterNatives(e,c,m,n); if(r!=0) (*e)->ExceptionClear(e); return r==0; }

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* e; if((*vm)->GetEnv(vm,(void**)&e,JNI_VERSION_1_6)!=JNI_OK) return JNI_ERR;
    {JNINativeMethod m[]={{"initConstants","()V",(void*)Java_libcore_io_OsConstants_initConstants}};
     tryReg(e,"libcore/io/OsConstants",m,1);}
    {JNINativeMethod m[]={
        {"getAvailableLocalesNative","()[Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getAvailableLocalesNative},
        {"setDefaultLocale","(Ljava/lang/String;)V",(void*)Java_libcore_icu_ICU_setDefaultLocale},
        {"getDefaultLocale","()Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getDefaultLocale},
        {"initLocaleDataNative","(Ljava/lang/String;Llibcore/icu/LocaleData;)Z",(void*)Java_libcore_icu_ICU_initLocaleDataNative},
        {"getCurrencyCode","(Ljava/lang/String;)Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getCurrencyCode},
        {"getISO3Country","(Ljava/lang/String;)Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getISO3Country},
        {"getISO3Language","(Ljava/lang/String;)Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getISO3Language},
        {"getScript","(Ljava/lang/String;)Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getScript},
        {"getBestDateTimePatternNative","(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getBestDateTimePatternNative},
        {"getISOLanguagesNative","()[Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getISOLanguagesNative},
        {"getISOCountriesNative","()[Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getISOCountriesNative},
        {"getIcuVersion","()Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getIcuVersion},
        {"getUnicodeVersion","()Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getUnicodeVersion},
        {"getCldrVersion","()Ljava/lang/String;",(void*)Java_libcore_icu_ICU_getCldrVersion},
     }; tryReg(e,"libcore/icu/ICU",m,14);}
    {JNINativeMethod m[]={{"realpath","(Ljava/lang/String;)Ljava/lang/String;",(void*)Java_java_io_File_realpath},
        {"setLastModifiedImpl","(Ljava/lang/String;J)Z",(void*)Java_java_io_File_setLastModifiedImpl}};
     tryReg(e,"java/io/File",m,2);}
    {JNINativeMethod m[]={{"currentTimeMillis","()J",(void*)Java_java_lang_System_currentTimeMillis},
        {"nanoTime","()J",(void*)Java_java_lang_System_nanoTime},
        {"mapLibraryName","(Ljava/lang/String;)Ljava/lang/String;",(void*)Java_java_lang_System_mapLibraryName},
        {"initProperties","(Ljava/util/Properties;)Ljava/util/Properties;",(void*)Java_java_lang_System_initProperties},
        {"specialProperties","()[Ljava/lang/String;",(void*)Java_java_lang_System_specialProperties}};
     tryReg(e,"java/lang/System",m,5);}
    {JNINativeMethod m[]={{"freeMemory","()J",(void*)Java_java_lang_Runtime_freeMemory},
        {"totalMemory","()J",(void*)Java_java_lang_Runtime_totalMemory},
        {"maxMemory","()J",(void*)Java_java_lang_Runtime_maxMemory}};
     tryReg(e,"java/lang/Runtime",m,3);}
    return JNI_VERSION_1_6;
}

/* KitKat uses initSystemProperties instead of initProperties */
JNIEXPORT jobject JNICALL Java_java_lang_System_initSystemProperties(JNIEnv* e, jclass c, jobject p) {
    return Java_java_lang_System_initProperties(e, c, p);
}
