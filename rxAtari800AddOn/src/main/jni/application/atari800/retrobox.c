#include "ui.h"
#include "retrobox.h"

#define ATARI_JAVA_PACKAGE_PATH xtvapps_retrobox_atari800

#define JAVA_EXPORT_NAME2(name,package) Java_##package##_##name
#define JAVA_EXPORT_NAME1(name,package) JAVA_EXPORT_NAME2(name,package)
#define JAVA_EXPORT_NAME(name) JAVA_EXPORT_NAME1(name,ATARI_JAVA_PACKAGE_PATH)

int retrobox_save_slot = 0;
int retrobox_screenshot = 0;
int retrobox_ui_function = 0;

JNIEXPORT void JNICALL JAVA_EXPORT_NAME(NativeInterface_saveState) ( JNIEnv*  env, jobject thiz, jint slot) {
	retrobox_save_slot = slot;
	retrobox_ui_function = UI_MENU_SAVESTATE;
	LOG_I("save state set for slot %d ui=%d", retrobox_save_slot, retrobox_ui_function);
	retrobox_screenshot = 2;
}

JNIEXPORT void JNICALL JAVA_EXPORT_NAME(NativeInterface_loadState) ( JNIEnv*  env, jobject thiz, jint slot) {
	retrobox_save_slot = slot;
	retrobox_ui_function = UI_MENU_LOADSTATE;
	LOG_I("load state set for slot %d ui=%d", retrobox_save_slot, retrobox_ui_function);
}
