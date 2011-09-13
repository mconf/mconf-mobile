project(common)
cmake_minimum_required(VERSION 2.8)
include(../../build/ant/cmake.properties)
if(WIN32)
    include(../../build/ant/cmake.macros)
    add_files_to_project("compatibility")
    ADD_CUSTOM_COMMAND(
        TARGET ${PROJECT_NAME}
        POST_BUILD
        COMMAND ../../build/ant/cmake.copytodeps.bat ARGS compatibility
    )
elseif(UNIX)
    ADD_CUSTOM_COMMAND(
        TARGET ${PROJECT_NAME}
        POST_BUILD
        COMMAND sh ARGS ../../build/ant/cmake.copytodeps.sh ./compatibility/
    )
endif()


