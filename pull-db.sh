#!/bin/sh
adb -d shell "run-as "jp.violetyk.android.w.app" cat /data/data/jp.violetyk.android.w.app/databases/w.sqlite > /sdcard/w.sqlite"
adb pull /sdcard/w.sqlite
