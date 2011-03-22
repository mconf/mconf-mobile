
ADD_LIBRARY(queue STATIC 
queue.cpp
QueueBuffer.cpp
QueueDiscard.cpp
QueueDiscardPolicy.cpp
QueueExtraData.cpp
QueueExtraDataAudio.cpp
QueueExtraDataVideo.cpp
#TesteQueue.cpp
#TesteQueue2.cpp
)
INCLUDE_DIRECTORIES(${EadCel_SOURCE_DIR} ${EadCel_SOURCE_DIR}/common ${EadCel_SOURCE_DIR}/thread ${EadCel_SOURCE_DIR}/sockets ${EadCel_SOURCE_DIR}/queue)
TARGET_LINK_LIBRARIES(queue)

ADD_SUBDIRECTORY(test)