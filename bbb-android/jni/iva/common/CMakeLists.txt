
ADD_LIBRARY(common STATIC 
AVConfigs.cpp
Directory_linux.cpp
Directory.cpp
ErrorController.cpp
ErrorData.cpp
ErrorStack.cpp
ErrorVector.cpp
Folders.cpp
IPV4.cpp
IvaOutBuffer.cpp
IvaOutController.cpp
IvaOutLogFile.cpp
IvaOutStream.cpp
IvaPixFmt.cpp
IvaPixFmt_linux.cpp
IvaString.cpp
IvaTime.cpp
IvaVideoFrame.cpp
Location.cpp
LogData.cpp
#Statistics.cpp
SysInfo_linux.cpp
)
INCLUDE_DIRECTORIES(${EadCel_SOURCE_DIR} ${EadCel_SOURCE_DIR}/common ${EadCel_SOURCE_DIR}/thread)
TARGET_LINK_LIBRARIES(common)

ADD_SUBDIRECTORY(test)
