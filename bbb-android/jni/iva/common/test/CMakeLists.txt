
INCLUDE_DIRECTORIES(${EadCel_SOURCE_DIR} ${EadCel_SOURCE_DIR}/common ${EadCel_SOURCE_DIR}/thread)

ADD_EXECUTABLE(commonTest 
gtest_Directory.cpp
gtest_Error.cpp
gtest_ErrorVector.cpp
gtest_IPV4.cpp
gtest_IvaOutController.cpp
gtest_IvaOutLogFile.cpp
gtest_IvaOutStream.cpp
gtest_IvaString.cpp
gtest_Location.cpp
gtest_LogData.cpp
gtest_Time.cpp
gtest_main.cpp
)

TARGET_LINK_LIBRARIES(commonTest common thread rt pthread gtest)

