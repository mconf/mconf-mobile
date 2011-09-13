#ifndef _SYS_INFO_H_
#define _SYS_INFO_H_

#include <string>
using namespace std;

#ifdef _MSC_VER
#include <objbase.h>
#include <psapi.h>
#endif

/** \brief Auxiliar para obter informações do sistema (pastas do sistema, etc.)
 */
class SysInfo
{
public:
    SysInfo();

    /** \brief Path do diretório de dados de aplicações para o usuário atual (e.g.
     *         C:\Documents and Settings\username\Application Data)
     */
    string getAppDataFolder();

    /** \brief Path do diretório de dados de aplicações para TODOS usuários (e.g.
     *         C:\Documents and Settings\All Users\Application Data)
     */
    string getCommonAppDataFolder();

    /** \brief Path do diretório do Windows (e.g. C:\Windows)
     */
    //string getFolder();

    /** \brief Path do diretório de sistema do Windows (e.g. C:\Windows\System32)
     */
    //string getWindowsSystemFolder();

    /** \brief Retorna o número de processadores da máquina
     */
    int getNumberOfProcessors();

private:

#ifdef _MSC_VER
    SYSTEM_INFO _sysinfo;
#endif

};

#endif // _SYSTEM_INFO_H_

