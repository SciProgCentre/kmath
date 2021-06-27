cmake_minimum_required(VERSION 3.12)

project(JNOA LANGUAGES C CXX)

# Require C++17
set(CMAKE_CXX_STANDARD 17)

find_package(Torch REQUIRED)
find_package(JNI REQUIRED)

include(FetchContent)

if(NOT TARGET noa)
    FetchContent_Declare(
        noa
        GIT_REPOSITORY https://github.com/grinisrit/noa.git
        GIT_TAG        kmath)

    FetchContent_GetProperties(noa)

    if(NOT noa_POPULATED)
        FetchContent_Populate(noa)

        set(INSTALL_NOA OFF CACHE BOOL "")
        set(BUILD_NOA_TESTS OFF CACHE BOOL "")
        set(BUILD_NOA_BENCHMARKS OFF CACHE BOOL "")
      
        add_subdirectory(
            ${noa_SOURCE_DIR}
            ${noa_BINARY_DIR})
    endif()
endif()

add_library(jnoa SHARED jnoa.cc)
target_include_directories(jnoa PRIVATE ${JNI_INCLUDE_DIRS})
target_link_libraries(jnoa PRIVATE torch NOA::NOA)
target_compile_options(jnoa PRIVATE -Wall -Wextra -Wpedantic -O3 -fPIC)
