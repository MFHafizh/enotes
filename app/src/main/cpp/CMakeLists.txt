cmake_minimum_required(VERSION 3.6.0)

add_library(
    cpp_code

    SHARED

    cpp_code.cpp)

find_library (
    cpp_code

    log)

# Include libraries needed for lib
target_link_libraries(
        cpp_code
        android
        log)
