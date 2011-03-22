#include "CommonLeaks.h"
#include "Folders.h"
#include "CommonLeaksCpp.h"

extern IvaOutController ivaOutControllerCtx;

Folders::Folders()
{
}

string Folders::getLogFolder()
{
    return ivaOutControllerCtx.getLogFolder();
}

string Folders::getResFolder()
{
    return ivaOutControllerCtx.getResFolder();
}
