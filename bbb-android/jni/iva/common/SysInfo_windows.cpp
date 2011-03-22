#include "CommonLeaks.h"
#include "SysInfo.h"
#include "IvaString.h"
#include <Shlobj.h>
#include "CommonLeaksCpp.h"

// http://msdn.microsoft.com/en-us/library/bb762181(VS.85).aspx

SysInfo::SysInfo()
{
    GetSystemInfo(&_sysinfo);
}

string SysInfo::getAppDataFolder()
{
    wchar_t buffer[MAX_PATH];
    IvaString str;
    if (SHGetFolderPath(0, CSIDL_APPDATA, NULL, 0, buffer) == S_OK) {
        str.fromWchar((wchar_t *)buffer);
        return str;
    } else {
        return "";
    }
}

string SysInfo::getCommonAppDataFolder()
{
    wchar_t buffer[MAX_PATH];
    IvaString str;
    if (SHGetFolderPath(0, CSIDL_COMMON_APPDATA, NULL, 0, buffer) == S_OK) {
        str.fromWchar((wchar_t *)buffer);
        return str;
    } else {
        return "";
    }
}

/*string SysInfo::getWindowsFolder()
{
    wchar_t buffer[MAX_PATH];
    IvaString str;
    if (SHGetFolderPath(0, CSIDL_WINDOWS, NULL, 0, buffer) == S_OK) {
        str.fromWchar((wchar_t *)buffer);
        return str;
    } else {
        return "";
    }
}

string SysInfo::getWindowsSystemFolder()
{
    wchar_t buffer[MAX_PATH];
    IvaString str;
    if (SHGetFolderPath(0, CSIDL_SYSTEM, NULL, 0, buffer) == S_OK) {
        str.fromWchar((wchar_t *)buffer);
        return str;
    } else {
        return "";
    }
}*/

int SysInfo::getNumberOfProcessors()
{
    return _sysinfo.dwNumberOfProcessors;
}
