#include "CommonLeaks.h"
#include "Directory.h"
#include <windows.h>
#include <sys/stat.h>
#include <errno.h>
#include "CommonLeaksCpp.h"

bool Directory::create()
{
    if (CreateDirectory(name_.toWchar(), NULL) == 0) {
        int error = GetLastError();
        if (error != ERROR_ALREADY_EXISTS) { // já existe, não é erro
            return false;
        }
    }

    return true;
}

bool Directory::remove()
{
    if (RemoveDirectory(name_.toWchar()) == 0) {
        return true;
    };
    return false;
}

bool Directory::exists()
{
    DWORD attr;
    attr = GetFileAttributes(name_.toWchar());
    if (attr == INVALID_FILE_ATTRIBUTES ||
        !(attr & FILE_ATTRIBUTE_DIRECTORY)) {
        return false;
    } else {
        return true;
    }
}
