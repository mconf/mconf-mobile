#ifndef _IVAOUTCONTROLLER_H_
#define _IVAOUTCONTROLLER_H_

#include <map>
#include "IvaString.h"
#include "IvaOutLogFile.h"

using namespace std;

#include <Mutex.h>

class IvaOutController :
    public Mutex
{

private:

    map<IvaString,IvaOutLogFile> files_;
    IvaString defaultLogFile_;

    bool _localDirs;
    string _appDataDir;

    void printString(IvaOutLogFile & file, IvaString & toPrint);


public:
    static const string CONFIG_FILE;
    static const string RES_FOLDER_NAME;
    static const string LOG_FOLDER_NAME;

    IvaOutController();
    ~IvaOutController();

    void print(IvaString & file, IvaString & toPrint);
    void print(IvaString & toPrint);
    void close();

    bool usingLocalDirs();
    string getAppDataDir();
    string getLogFolder();
    string getResFolder();

};

extern IvaOutController ivaOutControllerCtx;

#endif
