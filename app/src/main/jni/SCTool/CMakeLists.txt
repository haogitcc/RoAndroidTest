# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

#CMake版本信息
cmake_minimum_required(VERSION 3.4.1)
#支持-std=gnu++11
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

add_definitions("-DANDROID")
add_definitions("-DTWIN_SERIAL")
add_definitions("-DHAVE_CONFIG_H")

#配置加载头文件依赖
include_directories(./)
include_directories(./openct)
include_directories(./towitoko)

file(GLOB sc_srcs
#        ./*.h
#        ./openct/*.h
#        ./towitoko/*.h
        ./openct/proto-t1.c
        ./openct/buffer.c
        ./openct/checksum.c
        ./ccid.c
        ./cainiao_crypto.c
        ./towitoko/atr.c
        ./towitoko/pps.c
        ./commands.c
        ./strlcpy.c
        ./utils.c
        ./ifdhandler.c
        ./ccid_serial.c
        ./debug.c
        ./sc_find_serial.c
        ./sc_tool.cpp)

#CPP文件夹下带编译的cpp文件
add_library(sc_tool SHARED ${sc_srcs})

#C++日志
find_library(log-lib log)

#加入依赖库
target_link_libraries(sc_tool
        ${log-lib})