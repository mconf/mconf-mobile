#include "common_leaks.h"
#include "error.h"
#include "common_compatibility.h"
#include "common_sock.h"

int common_sock_setBlocking(SOCKET sock, bool_t value)
{
#ifdef _MSC_VER
    u_long blocking;
    blocking = !value; // 1 seta como NÃO bloqueante
    ioctlsocket(sock, 0x8004667e, &blocking);
#endif
    /*// Set non-blocking //linux
    long arg;
    arg = fcntl(CRemote.sock, F_GETFL, NULL); 
    arg |= O_NONBLOCK; 
    fcntl(CRemote.sock, F_SETFL, arg);*/

    return E_OK;
}

int common_sock_waitWrite(SOCKET sock, long sec, long usec)
{
    struct timeval tv;
    fd_set fdset;
    int ret;

    tv.tv_sec = sec;
    tv.tv_usec = usec;
    FD_ZERO(&fdset);
    FD_SET(sock, &fdset);
    ret = select((int)sock+1, NULL, &fdset, NULL, &tv);

    return ret;
}

int common_sock_waitRead(SOCKET sock, long sec, long usec)
{
    struct timeval tv;
    fd_set fdset;
    int ret;

    tv.tv_sec = sec;
    tv.tv_usec = usec;
    FD_ZERO(&fdset);
    FD_SET(sock, &fdset);
    ret = select((int)sock+1, &fdset, NULL, NULL, &tv);

    return ret;
}

int common_sock_startup()
{
#ifdef _MSC_VER
    WORD wVersionRequested;
    WSADATA wsaData;
    wVersionRequested = MAKEWORD(2,2);
    if (WSAStartup(wVersionRequested, &wsaData) != 0) {
        error_s(AT, E_SOCKET_WSASTARTUP, "Erro na chamada WSAStartup()");
        return E_SOCKET_WSASTARTUP;
    }
#endif

    return E_OK;
}

int common_sock_cleanup()
{
    int r;

#ifdef _MSC_VER
    r = WSACleanup(); // retorna zero se successo
    if (r != 0) {
        error_s(AT, E_SOCKET_WSACLEANUP, "Erro %d na chamada WSACleanup()", r);
        return r; // Retorna o erro do WSACleanup()
    }
#endif

    return E_OK;
}

int common_sock_printError(int error)
{
    char *str = NULL;

    switch(error) {
#ifdef _MSC_VER
        case WSA_INVALID_HANDLE:
            str = "Specified event object handle is invalid"; break;
        case WSA_NOT_ENOUGH_MEMORY:
            str = "Insufficient memory available"; break;
        case WSA_INVALID_PARAMETER:
            str = "One or more parameters are invalid"; break;
        case WSA_OPERATION_ABORTED:
            str = "Overlapped operation aborted"; break;
        case WSA_IO_INCOMPLETE: 
            str = "Overlapped I/O event object not in signaled state"; break;
        case WSA_IO_PENDING:
            str = "Overlapped operations will complete later"; break;
        case WSAEINTR:
            str = "Interrupted function call"; break;
        case WSAEBADF:
            str = "File handle is not valid"; break;
        case WSAEACCES:
            str = "Permission denied"; break;
        case WSAEFAULT:
            str = "Bad address"; break;
        case WSAEINVAL:
            str = "Invalid argument"; break;
        case WSAEMFILE:
            str = "Too many open files"; break;
        case WSAEWOULDBLOCK:
            str = "Resource temporarily unavailable"; break;
        case WSAEINPROGRESS:
            str = "Operation now in progress"; break;
        case WSAEALREADY:
            str = "Operation already in progress"; break;
        case WSAENOTSOCK:
            str = "Socket operation on nonsocket"; break;
        case WSAEDESTADDRREQ:
            str = "Destination address required"; break;
        case WSAEMSGSIZE:
            str = "Message too long"; break;
        case WSAEPROTOTYPE:
            str = "Protocol wrong type for socket"; break;
        case WSAENOPROTOOPT:
            str = "Bad protocol option"; break;
        case WSAEPROTONOSUPPORT:
            str = "Protocol not supported"; break;
        case WSAESOCKTNOSUPPORT:
            str = "Socket type not supported"; break;
        case WSAEOPNOTSUPP:
            str = "Operation not supported"; break;
        case WSAEPFNOSUPPORT:
            str = "Protocol family not supported"; break;
        case WSAEAFNOSUPPORT:
            str = "Address family not supported by protocol family"; break;
        case WSAEADDRINUSE:
            str = "Address already in use"; break;
        case WSAEADDRNOTAVAIL:
            str = "Cannot assign requested address"; break;
        case WSAENETDOWN:
            str = "Network is down"; break;
        case WSAENETUNREACH:
            str = "Network is unreachable"; break;
        case WSAENETRESET:
            str = "Network dropped connection on reset"; break;
        case WSAECONNABORTED:
            str = "Software caused connection abort"; break;
        case WSAECONNRESET:
            str = "Connection reset by peer"; break;
        case WSAENOBUFS:
            str = "No buffer space available"; break;
        case WSAEISCONN:
            str = "Socket is already connected"; break;
        case WSAENOTCONN:
            str = "Socket is not connected"; break;
        case WSAESHUTDOWN:
            str = "Cannot send after socket shutdown"; break;
        case WSAETOOMANYREFS:
            str = "Too many references"; break;
        case WSAETIMEDOUT:
            str = "Connection timed out"; break;
        case WSAECONNREFUSED:
            str = "Connection refused"; break;
        case WSAELOOP:
            str = "Cannot translate name"; break;
        case WSAENAMETOOLONG:
            str = "Name too long"; break;
        case WSAEHOSTDOWN:
            str = "Host is down"; break;
        case WSAEHOSTUNREACH:
            str = "No route to host"; break;
        case WSAENOTEMPTY:
            str = "Directory not empty"; break;
        case WSAEPROCLIM:
            str = "Too many processes"; break;
        case WSAEUSERS:
            str = "client quota exceeded"; break;
        case WSAEDQUOT:
            str = "Disk quota exceeded"; break;
        case WSAESTALE:
            str = "Stale file handle reference"; break;
        case WSAEREMOTE:
            str = "Item is remote"; break;
        case WSASYSNOTREADY:
            str = "Network subsystem is unavailable"; break;
        case WSAVERNOTSUPPORTED:
            str = "Winsock.dll version out of range"; break;
        case WSANOTINITIALISED:
            str = "Successful WSAStartup not yet performed"; break;
        case WSAEDISCON:
            str = "Graceful shutdown in progress"; break;
        case WSAENOMORE:
            str = "No more results"; break;
        case WSAECANCELLED:
            str = "Call has been canceled"; break;
        case WSAEINVALIDPROCTABLE:
            str = "Procedure call table is invalid"; break;
        case WSAEINVALIDPROVIDER:
            str = "Service provider is invalid"; break;
        case WSAEPROVIDERFAILEDINIT:
            str = "Service provider failed to initialize"; break;
        case WSASYSCALLFAILURE:
            str = "System call failure"; break;
        case WSASERVICE_NOT_FOUND:
            str = "Service not found"; break;
        case WSATYPE_NOT_FOUND:
            str = "Class type not found"; break;
        case WSA_E_NO_MORE:
            str = "No more results"; break;
        case WSA_E_CANCELLED:
            str = "Call was canceled"; break;
        case WSAEREFUSED:
            str = "Database query was refused"; break;
        case WSAHOST_NOT_FOUND:
            str = "Host not found"; break;
        case WSATRY_AGAIN:
            str = "Nonauthoritative host not found"; break;
        case WSANO_RECOVERY:
            str = "This is a nonrecoverable error"; break;
        case WSANO_DATA:
            str = "Valid name, no data record of requested type"; break;
        case WSA_QOS_RECEIVERS:
            str = "QOS receivers"; break;
        case WSA_QOS_SENDERS:
            str = "QOS senders"; break;
        case WSA_QOS_NO_SENDERS:
            str = "No QOS senders"; break;
        case WSA_QOS_NO_RECEIVERS:
            str = "QOS no receivers"; break;
        case WSA_QOS_REQUEST_CONFIRMED:
            str = "QOS request confirmed"; break;
        case WSA_QOS_ADMISSION_FAILURE:
            str = "QOS admission error"; break;
        case WSA_QOS_POLICY_FAILURE:
            str = "QOS policy failure"; break;
        case WSA_QOS_BAD_STYLE:
            str = "QOS bad style"; break;
        case WSA_QOS_BAD_OBJECT:
            str = "QOS bad object"; break;
        case WSA_QOS_TRAFFIC_CTRL_ERROR:
            str = "QOS traffic control error"; break;
        case WSA_QOS_GENERIC_ERROR:
            str = "QOS generic error"; break;
        case WSA_QOS_ESERVICETYPE:
            str = "QOS service type error"; break;
        case WSA_QOS_EFLOWSPEC:
            str = "QOS flowspec error"; break;
        case WSA_QOS_EPROVSPECBUF:
            str = "Invalid QOS provider buffer"; break;
        case WSA_QOS_EFILTERSTYLE:
            str = "Invalid QOS filter style"; break;
        case WSA_QOS_EFILTERTYPE:
            str = "Invalid QOS filter type"; break;
        case WSA_QOS_EFILTERCOUNT:
            str = "Incorrect QOS filter count"; break;
        case WSA_QOS_EOBJLENGTH:
            str = "Invalid QOS object length"; break;
        case WSA_QOS_EFLOWCOUNT:
            str = "Incorrect QOS flow count"; break;
        case WSA_QOS_EUNKOWNPSOBJ:
            str = "Unrecognized QOS object"; break;
        case WSA_QOS_EPOLICYOBJ:
            str = "Invalid QOS policy object"; break;
        case WSA_QOS_EFLOWDESC:
            str = "Invalid QOS flow descriptor"; break;
        case WSA_QOS_EPSFLOWSPEC:
            str = "Invalid QOS provider-specific flowspec"; break;
        case WSA_QOS_EPSFILTERSPEC:
            str = "Invalid QOS provider-specific filterspec"; break;
        case WSA_QOS_ESDMODEOBJ:
            str = "Invalid QOS shape discard mode object"; break;
        case WSA_QOS_ESHAPERATEOBJ:
            str = "Invalid QOS shaping rate object"; break;
        case WSA_QOS_RESERVED_PETYPE:
            str = "Reserved policy QOS element type"; break;
        default:
            str = "Unknown error"; break;
#else
        case EDEADLK:
            str = "Resource deadlock would occur"; break;
        case ENAMETOOLONG:
            str = "File name too long"; break;
        case ENOLCK:
            str = "No record locks available"; break;
        case ENOSYS:
            str = "Function not implemented"; break;
        case ENOTEMPTY:
            str = "Directory not empty"; break;
        case ELOOP:
            str = "Too many symbolic links encountered"; break;
        case EWOULDBLOCK:
            str = "Operation would block"; break;
        //case EAGAIN:
            //str = "Operation would block"; break;
        case ENOMSG:
            str = "No message of desired type"; break;
        case EIDRM:
            str = "Identifier removed"; break;
        case ECHRNG:
            str = "Channel number out of range"; break;
        case EL2NSYNC:
            str = "Level 2 not synchronized"; break;
        case EL3HLT:
            str = "Level 3 halted"; break;
        case EL3RST:
            str = "Level 3 reset"; break;
        case ELNRNG:
            str = "Link number out of range"; break;
        case EUNATCH:
            str = "Protocol driver not attached"; break;
        case ENOCSI:
            str = "No CSI structure available"; break;
        case EL2HLT:
            str = "Level 2 halted"; break;
        case EBADE:
            str = "Invalid exchange"; break;
        case EBADR:
            str = "Invalid request descriptor"; break;
        case EXFULL:
            str = "Exchange full"; break;
        case ENOANO:
            str = "No anode"; break;
        case EBADRQC:
            str = "Invalid request code"; break;
        case EBADSLT:
            str = "Invalid slot"; break;
        case EBFONT:
            str = "Bad font file format"; break;
        case ENOSTR:
            str = "Device not a stream"; break;
        case ENODATA:
            str = "No data available"; break;
        case ETIME:
            str = "Timer expired"; break;
        case ENOSR:
            str = "Out of streams resources"; break;
        case ENONET:
            str = "Machine is not on the network"; break;
        case ENOPKG:
            str = "Package not installed"; break;
        case EREMOTE:
            str = "Object is remote"; break;
        case ENOLINK:
            str = "Link has been severed"; break;
        case EADV:
            str = "Advertise error"; break;
        case ESRMNT:
            str = "Srmount error"; break;
        case ECOMM:
            str = "Communication error on send"; break;
        case EPROTO:
            str = "Protocol error"; break;
        case EMULTIHOP:
            str = "Multihop attempted"; break;
        case EDOTDOT:
            str = "RFS specific error"; break;
        case EBADMSG:
            str = "Not a data message"; break;
        case EOVERFLOW:
            str = "Value too large for defined data type"; break;
        case ENOTUNIQ:
            str = "Name not unique on network"; break;
        case EBADFD:
            str = "File descriptor in bad state"; break;
        case EREMCHG:
            str = "Remote address changed"; break;
        case ELIBACC:
            str = "Can not access a needed shared library"; break;
        case ELIBBAD:
            str = "Accessing a corrupted shared library"; break;
        case ELIBSCN:
            str = ".lib section in a.out corrupted"; break;
        case ELIBMAX:
            str = "Attempting to link in too many shared libraries"; break;
        case ELIBEXEC:
            str = "Cannot exec a shared library directly"; break;
        case EILSEQ:
            str = "Illegal byte sequence"; break;
        case ERESTART:
            str = "Interrupted system call should be restarted"; break;
        case ESTRPIPE:
            str = "Streams pipe error"; break;
        case EUSERS:
            str = "Too many users"; break;
        case ENOTSOCK:
            str = "Socket operation on non-socket"; break;
        case EDESTADDRREQ:
            str = "Destination address required"; break;
        case EMSGSIZE:
            str = "Message too long"; break;
        case EPROTOTYPE:
            str = "Protocol wrong type for socket"; break;
        case ENOPROTOOPT:
            str = "Protocol not available"; break;
        case EPROTONOSUPPORT:
            str = "Protocol not supported"; break;
        case ESOCKTNOSUPPORT:
            str = "Socket type not supported"; break;
        case EOPNOTSUPP:
            str = "Operation not supported on transport endpoint"; break;
        case EPFNOSUPPORT:
            str = "Protocol family not supported"; break;
        case EAFNOSUPPORT:
            str = "Address family not supported by protocol"; break;
        case EADDRINUSE:
            str = "Address already in use"; break;
        case EADDRNOTAVAIL:
            str = "Cannot assign requested address"; break;
        case ENETDOWN:
            str = "Network is down"; break;
        case ENETUNREACH:
            str = "Network is unreachable"; break;
        case ENETRESET:
            str = "Network dropped connection because of reset"; break;
        case ECONNABORTED:
            str = "Software caused connection abort"; break;
        case ECONNRESET:
            str = "Connection reset by peer"; break;
        case ENOBUFS:
            str = "No buffer space available"; break;
        case EISCONN:
            str = "Transport endpoint is already connected"; break;
        case ENOTCONN:
            str = "Transport endpoint is not connected"; break;
        case ESHUTDOWN:
            str = "Cannot send after transport endpoint shutdown"; break;
        case ETOOMANYREFS:
            str = "Too many references: cannot splice"; break;
        case ETIMEDOUT:
            str = "Connection timed out"; break;
        case ECONNREFUSED:
            str = "Connection refused"; break;
        case EHOSTDOWN:
            str = "Host is down"; break;
        case EHOSTUNREACH:
            str = "No route to host"; break;
        case EALREADY:
            str = "Operation already in progress"; break;
        case EINPROGRESS:
            str = "Operation now in progress"; break;
        case ESTALE:
            str = "Stale NFS file handle"; break;
        case EUCLEAN:
            str = "Structure needs cleaning"; break;
        case ENOTNAM:
            str = "Not a XENIX named type file"; break;
        case ENAVAIL:
            str = "No XENIX semaphores available"; break;
        case EISNAM:
            str = "Is a named type file"; break;
        case EREMOTEIO:
            str = "Remote I/O error"; break;
        case EDQUOT:
            str = "Quota exceeded"; break;
        case ENOMEDIUM:
            str = "No medium found"; break;
        case EMEDIUMTYPE:
            str = "Wrong medium type"; break;
        case ECANCELED:
            str = "Operation Canceled"; break;
        case ENOKEY:
            str = "Required key not available"; break;
        case EKEYEXPIRED:
            str = "Key has expired"; break;
        case EKEYREVOKED:
            str = "Key has been revoked"; break;
        case EKEYREJECTED:
            str = "Key was rejected by service"; break;
        default:
            str = "Unknown error"; break;
#endif
    }
    common_logprintf("Socket error: (%d) \"%s\"", error, str);

    return E_OK;
}


