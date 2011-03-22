#include "CommonLeaks.h"
#include "SysInfo.h"
#include "IvaString.h"
#include "CommonLeaksCpp.h"


SysInfo::SysInfo()
{
}

string SysInfo::getAppDataFolder()
{
    return "~/"; // Retorna a HOME do usuário
}

string SysInfo::getCommonAppDataFolder()
{
    return "~/"; // Retorna a HOME do usuário
}

int SysInfo::getNumberOfProcessors()
{
    return 1; /// \todo Implementar
}

/// \todo tentar adaptar isso p/ LINUX
//SystemInfo::SystemInfo()
//{
//}
//
//string SystemInfo::getAppDataFolder()
//{
//    return "~/"; // Retorna a HOME do usuário
//}
//
//string SystemInfo::getCommonAppDataFolder()
//{
//    return "~/"; // Retorna a HOME do usuário
//}
//
//int SystemInfo::getNumberOfProcessors()
//{
//    return 1; /// \todo Implementar
//}
