#ifndef _ERRORDEFS_H_
#define _ERRORDEFS_H_
  
#define ERROR_TO_E(a,b) ((a*10000) + b)

#define ERROR_LIBS_COMMON                              0
#define ERROR_LIBS_THREAD                              1
#define ERROR_LIBS_SOCKET                              2
#define ERROR_LIBS_QUEUE                               3
#define ERROR_LIBS_PARSER                              4
#define ERROR_LIBS_CAPVID                              5
#define ERROR_LIBS_ENCODE                              6
#define ERROR_LIBS_DECODE                              7
#define ERROR_LIBS_NET                                 8
#define ERROR_LIBS_STATISTICS                          9
#define ERROR_LIBS_NETCOM                              10
#define ERROR_LIBS_VIDEO                               11
#define ERROR_LIBS_CAPAUD                              12
#define ERROR_LIBS_CAPENC                              13
#define ERROR_LIBS_AUDIO                               14
#define ERROR_LIBS_SERVER                              15

#define ERROR_LIBS_SIZE                                16

/**
 * COMMON ERRORS
 */

#define ERROR_COMMON_OK                                0
#define ERROR_COMMON_UNKNOWN_ERROR                     1
#define ERROR_COMMON_MEMORY_ERROR                      2
#define ERROR_COMMON_OVERFLOW                          3
#define ERROR_COMMON_INSUFFICIENT                      4
#define ERROR_COMMON_NULL_PARAMETER                    5
#define ERROR_COMMON_INVALID_PARAMETER                 6
#define ERROR_COMMON_FILE_ERROR                        7
#define ERROR_COMMON_FILE_NOT_FOUND                    8
#define ERROR_COMMON_DEVICE_ERROR                      9
#define ERROR_COMMON_DEVICE_NOT_FOUND                  10
#define ERROR_COMMON_NULL_POINTER                      11

#define ERROR_COMMON_SIZE                              12

#define E_COMMON_OK                                    ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_OK)
#define E_OK                                           E_COMMON_OK
#define E_COMMON_UNKNOWN_ERROR                         ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_UNKNOWN_ERROR)
#define E_ERROR                                        E_COMMON_UNKNOWN_ERROR
#define E_COMMON_MEMORY_ERROR                          ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_MEMORY_ERROR)
#define E_COMMON_OVERFLOW                              ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_OVERFLOW)
#define E_COMMON_INSUFFICIENT                          ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_INSUFFICIENT)
#define E_COMMON_NULL_PARAMETER                        ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_NULL_PARAMETER)
#define E_COMMON_INVALID_PARAM                         ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_INVALID_PARAMETER)
#define E_COMMON_FILE_ERROR                            ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_FILE_ERROR)
#define E_COMMON_FILE_NOT_FOUND                        ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_FILE_NOT_FOUND)
#define E_COMMON_DEVICE_ERROR                          ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_DEVICE_ERROR)
#define E_COMMON_DEVICE_NOT_FOUND                      ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_DEVICE_NOT_FOUND)
#define E_COMMON_NULL_POINTER                          ERROR_TO_E(ERROR_LIBS_COMMON,ERROR_COMMON_NULL_POINTER)

/**
 * THREAD ERRORS
 */

#define ERROR_THREAD_KILL                              0
#define ERROR_THREAD_INIT                              1
#define ERROR_THREAD_JOIN                              2
#define ERROR_THREAD_MUTEX_LOCK                        3
#define ERROR_THREAD_MUTEX_UNLOCK                      4

#define ERROR_THREAD_SIZE                              5

#define E_THREAD_KILL                                  ERROR_TO_E(ERROR_LIBS_THREAD,ERROR_THREAD_KILL)
#define E_THREAD_INIT                                  ERROR_TO_E(ERROR_LIBS_THREAD,ERROR_THREAD_INIT)
#define E_THREAD_JOIN                                  ERROR_TO_E(ERROR_LIBS_THREAD,ERROR_THREAD_JOIN)
#define E_THREAD_MUTEX_LOCK                            ERROR_TO_E(ERROR_LIBS_THREAD,ERROR_THREAD_MUTEX_LOCK)
#define E_THREAD_MUTEX_UNLOCK                          ERROR_TO_E(ERROR_LIBS_THREAD,ERROR_THREAD_MUTEX_UNLOCK)

/**
 * SOCKET ERRORS
 */

#define ERROR_SOCKET_STARTUP                           0
#define ERROR_SOCKET_CLEANUP                           1 
#define ERROR_SOCKET_IOCTL                             2
#define ERROR_SOCKET_LISTEN                            3
#define ERROR_SOCKET_CREATE                            4
#define ERROR_SOCKET_SETSOCKOPT                        5
#define ERROR_SOCKET_BIND                              6
#define ERROR_SOCKET_CONNECT                           7
#define ERROR_SOCKET_ACCEPT                            8
#define ERROR_SOCKET_SELECT                            9
#define ERROR_SOCKET_RECV                              10
#define ERROR_SOCKET_SEND                              11
#define ERROR_SOCKET_CLOSE                             12
#define ERROR_SOCKET_SHUTDOWN                          13
#define ERROR_SOCKET_ADD_MEMBERSHIP                    14
#define ERROR_SOCKET_DROP_MEMBERSHIP                   15

#define ERROR_SOCKET_SIZE                              16

#define E_SOCKET_STARTUP                               ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_STARTUP)
#define E_SOCKET_CLEANUP                               ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_CLEANUP)
#define E_SOCKET_IOCTL                                 ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_IOCTL)
#define E_SOCKET_LISTEN                                ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_LISTEN)
#define E_SOCKET_CREATE                                ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_CREATE)
#define E_SOCKET_SETSOCKOPT                            ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_SETSOCKOPT)
#define E_SOCKET_BIND                                  ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_BIND)
#define E_SOCKET_CONNECT                               ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_CONNECT)
#define E_SOCKET_ACCEPT                                ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_ACCEPT)
#define E_SOCKET_SELECT                                ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_SELECT)
#define E_SOCKET_RECV                                  ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_RECV)
#define E_SOCKET_SEND                                  ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_SEND)
#define E_SOCKET_CLOSE                                 ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_CLOSE)
#define E_SOCKET_SHUTDOWN                              ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_SHUTDOWN)
#define E_SOCKET_ADD_MEMBERSHIP                        ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_ADD_MEMBERSHIP)
#define E_SOCKET_DROP_MEMBERSHIP                       ERROR_TO_E(ERROR_LIBS_SOCKET,ERROR_SOCKET_DROP_MEMBERSHIP)

  
/**
 * QUEUE ERRORS
 */

#define ERROR_QUEUE_EMPTY                              0
#define ERROR_QUEUE_ENDOF                              1
#define ERROR_QUEUE_INVALID_CONSUMER                   2
#define ERROR_QUEUE_FREE_NEEDED                        3
#define ERROR_QUEUE_MAX_CONSUMERS_REACHED              4
#define ERROR_QUEUE_MAX_LENGTH_REACHED                 5

#define ERROR_QUEUE_SIZE                               6

#define E_QUEUE_EMPTY                                  ERROR_TO_E(ERROR_LIBS_QUEUE,ERROR_QUEUE_EMPTY)
#define E_QUEUE_ENDOF                                  ERROR_TO_E(ERROR_LIBS_QUEUE,ERROR_QUEUE_ENDOF)
#define E_QUEUE_INVALID_CONSUMER                       ERROR_TO_E(ERROR_LIBS_QUEUE,ERROR_QUEUE_INVALID_CONSUMER)
#define E_QUEUE_FREE_NEEDED                            ERROR_TO_E(ERROR_LIBS_QUEUE,ERROR_QUEUE_FREE_NEEDED)
#define E_QUEUE_MAX_CONSUMERS_REACHED                  ERROR_TO_E(ERROR_LIBS_QUEUE,ERROR_QUEUE_MAX_CONSUMERS_REACHED)
#define E_QUEUE_MAX_LENGTH_REACHED                     ERROR_TO_E(ERROR_LIBS_QUEUE,ERROR_QUEUE_MAX_LENGTH_REACHED)

/**
 * PARSER ERRORS
 */
#define ERROR_PARSER_WRONG_SYNTAX                      0
#define ERROR_PARSER_PARAM_WRONG_TYPE                  1
#define ERROR_PARSER_WRONG_CHECKER_TYPE                2
#define ERROR_PARSER_NO_CHECKER                        3
#define ERROR_PARSER_PARAM_TOO_MANY_CHARS              4
#define ERROR_PARSER_CHECK_ERROR                       5

#define ERROR_PARSER_SIZE                              6

#define E_PARSER_WRONG_SYNTAX                          ERROR_TO_E(ERROR_LIBS_PARSER,ERROR_PARSER_WRONG_SYNTAX)
#define E_PARSER_PARAM_WRONG_TYPE                      ERROR_TO_E(ERROR_LIBS_PARSER,ERROR_PARSER_PARAM_WRONG_TYPE)
#define E_PARSER_WRONG_CHECKER_TYPE                    ERROR_TO_E(ERROR_LIBS_PARSER,ERROR_PARSER_WRONG_CHECKER_TYPE)
#define E_PARSER_NO_CHECKER                            ERROR_TO_E(ERROR_LIBS_PARSER,ERROR_PARSER_NO_CHECKER)
#define E_PARSER_PARAM_TOO_MANY_CHARS                  ERROR_TO_E(ERROR_LIBS_PARSER,ERROR_PARSER_PARAM_TOO_MANY_CHARS)
#define E_PARSER_CHECK_ERROR                           ERROR_TO_E(ERROR_LIBS_PARSER,ERROR_PARSER_CHECK_ERROR)


/**
 * CAPVID ERRORS
 */

#define ERROR_CAPVID_MICROSOFT_API                     0
#define ERROR_CAPVID_DEVICE_PARAMETERS                 1
#define ERROR_CAPVID_NOT_A_VIDEO_DEVICE                2
#define ERROR_CAPVID_NO_MEDIA_TYPE                     3
#define ERROR_CAPVID_NO_DEVICES                        4

#define ERROR_CAPVID_SIZE                              5

#define E_CAPVID_MICROSOFT_API                         ERROR_TO_E(ERROR_LIBS_CAPVID,ERROR_CAPVID_MICROSOFT_API)
#define E_CAPVID_DEVICE_PARAMETERS                     ERROR_TO_E(ERROR_LIBS_CAPVID,ERROR_CAPVID_DEVICE_PARAMETERS)
#define E_CAPVID_NOT_A_VIDEO_DEVICE                    ERROR_TO_E(ERROR_LIBS_CAPVID,ERROR_CAPVID_NOT_A_VIDEO_DEVICE)
#define E_CAPVID_NO_MEDIA_TYPE                         ERROR_TO_E(ERROR_LIBS_CAPVID,ERROR_CAPVID_NO_MEDIA_TYPE)
#define E_CAPVID_NO_DEVICES                            ERROR_TO_E(ERROR_LIBS_CAPVID,ERROR_CAPVID_NO_DEVICES)

/**
 * ENCODE ERRORS
 */

#define ERROR_ENCODE_FPS                               0
#define ERROR_ENCODE_OPEN                              1
#define ERROR_ENCODE_INCORRECT_CODEC_TYPE              2
#define ERROR_ENCODE_ALREADY_STARTED                   3
#define ERROR_ENCODE_NOT_STARTED                       4
#define ERROR_ENCODE_ALREADY_OPENED                    5
#define ERROR_ENCODE_NOT_OPENED                        6
#define ERROR_ENCODE_MULTITHREAD                       7
#define ERROR_ENCODE_PRESET_NOT_FOUND                  8
#define ERROR_ENCODE_CODEC                             9
#define ERROR_ENCODE_AUDIO                             10
#define ERROR_ENCODE_VIDEO                             11

#define ERROR_ENCODE_SIZE                              12

#define E_ENCODE_FPS                                   ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_FPS)
#define E_ENCODE_OPEN                                  ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_OPEN)
#define E_ENCODE_INCORRECT_CODEC_TYPE                  ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_INCORRECT_CODEC_TYPE)
#define E_ENCODE_ALREADY_STARTED                       ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_ALREADY_STARTED)
#define E_ENCODE_NOT_STARTED                           ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_NOT_STARTED)
#define E_ENCODE_ALREADY_OPENED                        ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_ALREADY_OPENED)
#define E_ENCODE_NOT_OPENED                            ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_NOT_OPENED)
#define E_ENCODE_MULTITHREAD                           ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_MULTITHREAD)
#define E_ENCODE_PRESET_NOT_FOUND                      ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_PRESET_NOT_FOUND)
#define E_ENCODE_CODEC                                 ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_CODEC)
#define E_ENCODE_AUDIO                                 ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_AUDIO)
#define E_ENCODE_VIDEO                                 ERROR_TO_E(ERROR_LIBS_ENCODE,ERROR_ENCODE_VIDEO)

/**
 * DECODE ERROR
 */

#define ERROR_DECODE_CODEC_NOT_FOUND                   0
#define ERROR_DECODE_CODEC_ERROR                       1
#define ERROR_DECODE_ALREADY_STARTED                   2
#define ERROR_DECODE_NOT_STARTED                       3
#define ERROR_DECODE_ALREADY_OPENED                    4
#define ERROR_DECODE_NOT_OPENED                        5
#define ERROR_DECODE_WAITING_FRAME_I                   6
#define ERROR_DECODE_ENQUEUE                           7
#define ERROR_DECODE_SWS_CONTEXT                       8
#define ERROR_DECODE_VIDEO                             9
#define ERROR_DECODE_AUDIO                             10

#define ERROR_DECODE_SIZE                              11

#define E_DECODE_CODEC_NOT_FOUND                       ERROR_TO_E(ERROR_LIBS_DECODE,ERROR_DECODE_CODEC_NOT_FOUND)
#define E_DECODE_CODEC_ERROR                           ERROR_TO_E(ERROR_LIBS_DECODE,ERROR_DECODE_CODEC_ERROR)
#define E_DECODE_ALREADY_STARTED                       ERROR_TO_E(ERROR_LIBS_DECODE,ERROR_DECODE_ALREADY_STARTED)
#define E_DECODE_NOT_STARTED                           ERROR_TO_E(ERROR_LIBS_DECODE,ERROR_DECODE_NOT_STARTED)
#define E_DECODE_ALREADY_OPENED                        ERROR_TO_E(ERROR_LIBS_DECODE,ERROR_DECODE_ALREADY_OPENED)
#define E_DECODE_NOT_OPENED                            ERROR_TO_E(ERROR_LIBS_DECODE,ERROR_DECODE_NOT_OPENED)
#define E_DECODE_WAITING_FRAME_I                       ERROR_TO_E(ERROR_LIBS_DECODE,ERROR_DECODE_WAITING_FRAME_I)
#define E_DECODE_ENQUEUE                               ERROR_TO_E(ERROR_LIBS_DECODE,ERROR_DECODE_ENQUEUE)
#define E_DECODE_SWS_CONTEXT                           ERROR_TO_E(ERROR_LIBS_DECODE,ERROR_DECODE_SWS_CONTEXT)
#define E_DECODE_VIDEO                                 ERROR_TO_E(ERROR_LIBS_DECODE,ERROR_DECODE_VIDEO)
#define E_DECODE_AUDIO                                 ERROR_TO_E(ERROR_LIBS_DECODE,ERROR_DECODE_AUDIO)


/**
 * NET ERRORS
 */

#define ERROR_NET_INVALID_SOCKET                       0
#define ERROR_NET_INVALID_IP                           1
#define ERROR_NET_GET_HOST_NAME                        2
#define ERROR_NET_ADD_MEMBERSHIP                       3
#define ERROR_NET_MCASTTTL                             4
#define ERROR_NET_MCASTLOOP                            5
#define ERROR_NET_MCASTIF                              6
#define ERROR_NET_PACKET_OVERFLOW                      7
#define ERROR_NET_PACKET_BUFFPTR                       8
#define ERROR_NET_PACKET_INITBUFFERS                   9
#define ERROR_NET_PACKET_ARRAYPTR                      10
#define ERROR_NET_UNREACHABLE                          11
#define ERROR_NET_FRAG_ALREADY_RECEIVED                12
#define ERROR_NET_INVALID_TYPE                         13

#define ERROR_NET_SIZE                                 14

#define E_NET_INVALID_SOCKET                           ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_INVALID_SOCKET)
#define E_NET_INVALID_IP                               ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_INVALID_IP)
#define E_NET_GET_HOST_NAME                            ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_GET_HOST_NAME)
#define E_NET_ADD_MEMBERSHIP                           ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_ADD_MEMBERSHIP)
#define E_NET_MCASTTTL                                 ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_MCASTTTL)
#define E_NET_MCASTLOOP                                ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_MCASTLOOP)
#define E_NET_MCASTIF                                  ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_MCASTIF)
#define E_NET_PACKET_OVERFLOW                          ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_PACKET_OVERFLOW)
#define E_NET_PACKET_BUFFPTR                           ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_PACKET_BUFFPTR)
#define E_NET_PACKET_INITBUFFERS                       ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_PACKET_INITBUFFERS)
#define E_NET_PACKET_ARRAYPTR                          ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_PACKET_ARRAYPTR)
#define E_NET_UNREACHABLE                              ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_UNREACHABLE)
#define E_NET_FRAG_ALREADY_RECEIVED                    ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_FRAG_ALREADY_RECEIVED)
#define E_NET_INVALID_TYPE                             ERROR_TO_E(ERROR_LIBS_NET,ERROR_NET_INVALID_TYPE)


/**
 * STATISTICS ERRORS
 */

#define ERROR_STATISTICS_TIMES_READ                    0

#define ERROR_STATISTICS_SIZE                          1

#define E_STATISTICS_TIMES_READ                        ERROR_TO_E(ERROR_LIBS_STATISTICS,ERROR_STATISTICS_TIMES_READ)

/**
 * NETCOM ERRORS
 */

#define ERROR_NETCOM_INVALID_DATA                       0
#define ERROR_NETCOM_INVALID_HEADER_CHECKSUM            1
#define ERROR_NETCOM_INVALID_TEXT_CHECKSUM              2
#define ERROR_NETCOM_INVALID_CODE                       3
#define ERROR_NETCOM_INVALID_PREVIEW                    4
#define ERROR_NETCOM_TEXT_EMPTY                         5
#define ERROR_NETCOM_CLIENT_NOT_FOUND                   6
#define ERROR_NETCOM_SUITE_NOT_CONNECTED                7
#define ERROR_NETCOM_NOT_CONNECTED                      8
#define ERROR_NETCOM_ALREADY_CONNECTED                  9
#define ERROR_NETCOM_NOT_REGISTERED                     10
#define ERROR_NETCOM_ALREADY_REGISTERED                 11
#define ERROR_NETCOM_NAME_ALREADY_EXISTS                12
#define ERROR_NETCOM_USERNAME_ALREADY_EXISTS            13
#define ERROR_NETCOM_NOT_TRANSMITTING                   14
#define ERROR_NETCOM_ALREADY_TRANSMITTING               15
#define ERROR_NETCOM_NOT_PROMOTED                       16
#define ERROR_NETCOM_ALREADY_PROMOTED                   17
#define ERROR_NETCOM_INVALID_IP                         18
#define ERROR_NETCOM_INVALID_VERSION                    19
#define ERROR_NETCOM_CONNECTION_LOST                    20
#define ERROR_NETCOM_CONNECTION_CLOSED                  21
#define ERROR_NETCOM_INVALID_MESSAGE                    22

#define ERROR_NETCOM_SIZE                               23

#define E_NETCOM_INVALID_DATA                           ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_INVALID_DATA)
#define E_NETCOM_INVALID_HEADER_CHECKSUM                ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_INVALID_HEADER_CHECKSUM)
#define E_NETCOM_INVALID_TEXT_CHECKSUM                  ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_INVALID_TEXT_CHECKSUM)
#define E_NETCOM_INVALID_CODE                           ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_INVALID_CODE)
#define E_NETCOM_INVALID_PREVIEW                        ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_INVALID_PREVIEW)
#define E_NETCOM_TEXT_EMPTY                             ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_TEXT_EMPTY)
#define E_NETCOM_CLIENT_NOT_FOUND                       ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_CLIENT_NOT_FOUND)
#define E_NETCOM_SUITE_NOT_CONNECTED                    ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_SUITE_NOT_CONNECTED)
#define E_NETCOM_NOT_CONNECTED                          ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_NOT_CONNECTED)
#define E_NETCOM_ALREADY_CONNECTED                      ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_ALREADY_CONNECTED)
#define E_NETCOM_NOT_REGISTERED                         ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_NOT_REGISTERED)
#define E_NETCOM_ALREADY_REGISTERED                     ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_ALREADY_REGISTERED)
#define E_NETCOM_NAME_ALREADY_EXISTS                    ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_NAME_ALREADY_EXISTS)
#define E_NETCOM_USERNAME_ALREADY_EXISTS                ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_USERNAME_ALREADY_EXISTS)
#define E_NETCOM_NOT_TRANSMITTING                       ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_NOT_TRANSMITTING)
#define E_NETCOM_ALREADY_TRANSMITTING                   ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_ALREADY_TRANSMITTING)
#define E_NETCOM_NOT_PROMOTED                           ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_NOT_PROMOTED)
#define E_NETCOM_ALREADY_PROMOTED                       ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_ALREADY_PROMOTED)
#define E_NETCOM_INVALID_IP                             ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_INVALID_IP)
#define E_NETCOM_INVALID_VERSION                        ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_INVALID_VERSION)
#define E_NETCOM_CONNECTION_LOST                        ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_CONNECTION_LOST)
#define E_NETCOM_CONNECTION_CLOSED                      ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_CONNECTION_CLOSED)
#define E_NETCOM_INVALID_MESSAGE                        ERROR_TO_E(ERROR_LIBS_NETCOM,ERROR_NETCOM_INVALID_MESSAGE)

/**
 * VIDEO ERRORS
 */

#define ERROR_VIDEO_ALREADY_INITIALIZED                 0
#define ERROR_VIDEO_NOT_INITIALIZED                     1
#define ERROR_VIDEO_INVALID_ID                          2
#define ERROR_VIDEO_SURFACE_UNAVAILABLE                 3

#define ERROR_VIDEO_SIZE                                4

#define E_VIDEO_ALREADY_INITIALIZED                     ERROR_TO_E(ERROR_LIBS_VIDEO,ERROR_VIDEO_ALREADY_INITIALIZED)
#define E_VIDEO_NOT_INITIALIZED                         ERROR_TO_E(ERROR_LIBS_VIDEO,ERROR_VIDEO_NOT_INITIALIZED)
#define E_VIDEO_INVALID_ID                              ERROR_TO_E(ERROR_LIBS_VIDEO,ERROR_VIDEO_INVALID_ID)
#define E_VIDEO_SURFACE_UNAVAILABLE                     ERROR_TO_E(ERROR_LIBS_VIDEO,ERROR_VIDEO_SURFACE_UNAVAILABLE)

/**
 * CAPAUD ERRORS
 */

#define ERROR_CAPAUD_NO_DEVICES                         0
#define ERROR_CAPAUD_PORTAUDIO                          1

#define ERROR_CAPAUD_SIZE                               2

#define E_CAPAUD_NO_DEVICES                             ERROR_TO_E(ERROR_LIBS_CAPAUD,ERROR_CAPAUD_NO_DEVICES)
#define E_CAPAUD_PORTAUDIO                              ERROR_TO_E(ERROR_LIBS_CAPAUD,ERROR_CAPAUD_PORTAUDIO)

/**
 * CAPENC ERRORS
 */

#define ERROR_CAPENC_ENCODE_VIDEO                       0

#define ERROR_CAPENC_SIZE                               1

#define E_CAPENC_ENCODE_VIDEO                           ERROR_TO_E(ERROR_LIBS_CAPENC,ERROR_CAPENC_ENCODE_VIDEO)

/**
 * AUDIO ERRORS
 */

#define ERROR_AUDIO_ALREADY_INITIALIZED                 0
#define ERROR_AUDIO_NOT_INITIALIZED                     1

#define ERROR_AUDIO_SIZE                                2

#define E_AUDIO_ALREADY_INITIALIZED                     ERROR_TO_E(ERROR_LIBS_AUDIO,ERROR_AUDIO_ALREADY_INITIALIZED)
#define E_AUDIO_NOT_INITIALIZED                         ERROR_TO_E(ERROR_LIBS_AUDIO,ERROR_AUDIO_NOT_INITIALIZED)

/**
 * SERVER ERRORS
 */

#define ERROR_SERVER_GSOAP_ERROR                        0

#define ERROR_SERVER_SIZE                               1

#define E_SERVER_GSOAP_ERROR                            ERROR_TO_E(ERROR_LIBS_AUDIO,ERROR_AUDIO_ALREADY_INITIALIZED)

#endif
