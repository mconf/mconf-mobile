
INCLUDE_DIRECTORIES(${EadCel_SOURCE_DIR} ${EadCel_SOURCE_DIR}/common ${EadCel_SOURCE_DIR}/thread ${EadCel_SOURCE_DIR}/sockets ${EadCel_SOURCE_DIR}/queue)

ADD_EXECUTABLE(queueTest 
gtest_main.cpp
gtest_QueueDiscard.cpp
#gtest_QueueDiscardPolicy.cpp
)

TARGET_LINK_LIBRARIES(queueTest queue sockets common thread rt pthread gtest)

