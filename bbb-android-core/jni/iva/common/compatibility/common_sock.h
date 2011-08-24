#ifndef _COMMON_SOCK_H_
#define _COMMON_SOCK_H_

#include "common_compatibility.h"

#ifdef _MSC_VER
#include <winsock2.h>
#include <ws2tcpip.h>
/** \struct ip_mreq
 *  \brief Estrutura necessária para transmissão multicast
 *
 */
//struct ip_mreq {
    //struct in_addr imr_multiaddr;    ///< Endereço
    //struct in_addr imr_interface;    ///< Interface
//};

//#else
    //#define SOCKET int
#endif



/** \brief Inicializa o uso de sockets.
 *  \return E_OK em caso de sucesso ou o código de erro gerado.
 *
 * No windows, chama o WSAStartup().
 */
int common_sock_startup();

/** \brief Finalização dos sockets.
 *  \return E_OK em caso de sucesso ou o código de erro gerado.
 *
 * No windows, chama o WSACleanup().
 */
int common_sock_cleanup();

int common_sock_printError(int error);

int common_sock_setBlocking(SOCKET sock, bool_t value);
int common_sock_waitWrite(SOCKET sock, long sec, long usec);
int common_sock_waitRead(SOCKET sock, long sec, long usec);



#endif // _COMMON_SOCK_H_

