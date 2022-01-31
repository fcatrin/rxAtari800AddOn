#ifndef _RETROBOX_H_
#define _RETROBOX_H_

#include <stdarg.h>
#include <jni.h>
#include <android/log.h>

#define RBX_APPNAME "Atari800"

static inline void LOG_I(const char *fmt, ...) {
	   va_list ap;

	   va_start(ap, fmt);
	   __android_log_vprint(ANDROID_LOG_DEBUG, RBX_APPNAME, fmt, ap);
	   va_end(ap);
}

static inline void LOG_D(const char *fmt, ...) {
	   va_list ap;

	   va_start(ap, fmt);
	   __android_log_vprint(ANDROID_LOG_DEBUG, RBX_APPNAME, fmt, ap);
	   va_end(ap);
}

extern int retrobox_save_slot;
extern int retrobox_screenshot;
extern int retrobox_ui_function;

#endif
