
INCLUDE_DIRECTORIES(${EadCel_SOURCE_DIR} ${EadCel_SOURCE_DIR}/common ${EadCel_SOURCE_DIR}/thread)

ADD_EXECUTABLE(threadTest 
gtest_ConditionVariable.cpp
gtest_main.cpp
gtest_Microseconds.cpp
gtest_Milliseconds.cpp
gtest_Mutex.cpp
gtest_Seconds.cpp
gtest_Thread.cpp
gtest_Timer.cpp
)

TARGET_LINK_LIBRARIES(threadTest thread rt pthread gtest)