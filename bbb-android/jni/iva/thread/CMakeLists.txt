
ADD_LIBRARY(thread STATIC 
Mutex.cpp
Seconds.cpp
ConditionVariable.cpp
Interval_linux.cpp
Interval.cpp
Microseconds.cpp
Milliseconds.cpp
Thread.cpp
Timer.cpp
)
INCLUDE_DIRECTORIES(${EadCel_SOURCE_DIR} ${EadCel_SOURCE_DIR}/common ${EadCel_SOURCE_DIR}/thread)
TARGET_LINK_LIBRARIES(thread)

ADD_SUBDIRECTORY(test)