#!/bin/bash
CPU=arm
PREFIX=$(pwd)/android/$CPU
NDK=/maple/android-ndk-r20
TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin
SYSROOT=$NDK/platforms/android-21/arch-arm
$NDK/build/tools/make_standalone_toolchain.py \
--arch arm64 \
--api 21 \
--install-dir=aarch64-linux-android
export PATH=$PATH:`pwd`/aarch64-linux-android/bin
target_host=aarch64-linux-android
export AR=$target_host-ar
export AS=$target_host-clang
export CC=$target_host-clang
export CXX=$target_host-clang++
export LD=$target_host-ld
export STRIP=$target_host-strip
export CFLAGS="-mfpu=neon -fPIE -fPIC"
export LDFLAGS="-pie"

function build_x264
{
echo "ffmpeg complie $CPU"
./configure \
--prefix=$PREFIX \
--enable-static \
--enable-debug \
--enable-pic \
--disable-cli \
--disable-asm \
--host=aarch64-linux-android
make clean
make install
make install-lib-dev
}

build_x264
