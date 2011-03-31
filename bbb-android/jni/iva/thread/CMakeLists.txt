project(thread)
cmake_minimum_required(VERSION 2.8)
include(../../build/ant/cmake.properties)
if(WIN32)
    # Cria um pre_build.bat para copiar as dependências para o deps
    set(PRE_BUILD_FILE "pre_build.bat")
    FILE(WRITE ${PRE_BUILD_FILE} "call ..\\..\\build\\ant\\cmake.copytodeps.bat ..\\common \n")
    FILE(APPEND ${PRE_BUILD_FILE} "call ..\\..\\build\\ant\\cmake.copytodeps.bat ..\\common\\compatibility")
    ADD_CUSTOM_COMMAND(
        TARGET ${PROJECT_NAME}
        PRE_BUILD
        COMMAND ${PRE_BUILD_FILE}
    )

# pre_build não funciona no unix!
# TODO: elaborar um método para resolver isso
elseif(UNIX)
    execute_process(
        COMMAND sh ../../build/ant/cmake.copytodeps.sh ../common/
        COMMAND sh ../../build/ant/cmake.copytodeps.sh ../common/compatibility/
        WORKING_DIRECTORY ${PROJECT_SOURCE_DIR}
    )
endif()


