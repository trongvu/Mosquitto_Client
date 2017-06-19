cmake_minimum_required(VERSION 3.4.1)
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/")

add_library(libmosquittopp SHARED IMPORTED)
set_property(TARGET libmosquittopp PROPERTY IMPORTED_LOCATION "${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libmosquittopp.so")

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             mosquitto_callback_wrapper.cpp
             com_android_trongvu_mosquittomqtt_wrapper_MosquittoWrapper.cpp )

find_library(
                log-lib
                log )

target_link_libraries(
                       native-lib
                       libmosquittopp
                       ${log-lib} )