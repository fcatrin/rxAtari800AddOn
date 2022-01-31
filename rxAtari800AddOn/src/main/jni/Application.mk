APP_PROJECT_PATH := $(call my-dir)/..

# Available libraries: mad sdl_mixer sdl_image sdl_ttf sdl_net sdl_blitpool
# sdl_mixer depends on tremor and optionally mad
# sdl_image depends on png and jpeg
# sdl_ttf depends on freetype

APP_OPTIM := release
#APP_OPTIM := debug
APP_MODULES := png application sdl sdl_main 

APP_ABI := x86 x86_64 armeabi-v7a arm64-v8a


