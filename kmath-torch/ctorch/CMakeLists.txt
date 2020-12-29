cmake_minimum_required(VERSION 3.12)

project(CTorch LANGUAGES C CXX)

# Require C++17
set(CMAKE_CXX_STANDARD 17)

# Build configuration
if(NOT CMAKE_BUILD_TYPE)
  set(CMAKE_BUILD_TYPE Release CACHE STRING "Build type" FORCE)
endif()
message(STATUS "Build type: ${CMAKE_BUILD_TYPE}")

find_package(Torch REQUIRED)

add_library(ctorch SHARED src/ctorch.cc)
target_include_directories(ctorch PRIVATE include)
target_link_libraries(ctorch PRIVATE torch)
target_compile_options(ctorch PRIVATE -Wall -Wextra -Wpedantic -O3 -fPIC)

include(GNUInstallDirs)
set_target_properties(ctorch PROPERTIES PUBLIC_HEADER include/ctorch.h)
install(TARGETS ctorch
    LIBRARY DESTINATION ${CMAKE_INSTALL_LIBDIR}
    PUBLIC_HEADER DESTINATION ${CMAKE_INSTALL_INCLUDEDIR})