#include "CommonLeaks.h"
#include "IvaOutController.h"
#include "IvaTime.h"
#include "Directory.h"
#include "SysInfo.h"
#include <sstream>
#include "CommonLeaksCpp.h"

using namespace std;

const string IvaOutController::CONFIG_FILE      = "folders.hid";
const string IvaOutController::RES_FOLDER_NAME  = "res/";
const string IvaOutController::LOG_FOLDER_NAME  = "log/";

IvaOutController::IvaOutController()
: Mutex(), _localDirs(false), _appDataDir()
{
    // se existe o arquivo CONFIG_FILE local, utilizará configurações locais
    // caso contrário busca as configurações nas pastas do sistema

    IvaString config = CONFIG_FILE;

    /// \todo adaptação p/ compiladores diferentes
#ifdef _MSC_VER
    ifstream myfile(config.toWchar(), ios::in);
#else
    ifstream myfile((char*)config.toWchar(), ios::in);
#endif

    // se conseguiu abrir o arquivo é porque ele existe
    if (!myfile) {
        _localDirs = true;
    } else {
        // busca do arquivo o diretório onde os arquivos da aplicação devem ser gravados
        myfile >> _appDataDir;
        myfile.close();
    }
  
    IvaString logPath(getLogFolder());
    stringstream defaultLogStream;
    IvaTime currentTime;

    defaultLogStream << logPath << "/log-";
    defaultLogStream.fill('0');
    defaultLogStream.width(4);
    defaultLogStream << currentTime.getYear() << ".";
    defaultLogStream.fill('0');
    defaultLogStream.width(2);
    defaultLogStream << currentTime.getMonth() << ".";
    defaultLogStream.fill('0');
    defaultLogStream.width(2);
    defaultLogStream << currentTime.getDay() << ".txt";

    Directory newDir(logPath);
    newDir.create();

    defaultLogFile_ = IvaString(defaultLogStream.str());

    //cout << defaultLogFile_ << endl;

    files_[defaultLogFile_] = defaultLogFile_;

}

string IvaOutController::getLogFolder()
{
    string tmp;
    if (!usingLocalDirs()) {
        tmp += SysInfo().getCommonAppDataFolder();
        tmp += getAppDataDir();
        if (!Directory(tmp).exists()) {
            tmp = "";
        }
    }
    tmp += LOG_FOLDER_NAME;
    return tmp;
}

string IvaOutController::getResFolder()
{
    string tmp;
    if (!usingLocalDirs()) {
        tmp += SysInfo().getCommonAppDataFolder();
        tmp += getAppDataDir();
        tmp += RES_FOLDER_NAME;
    } else {
        tmp += "../" + RES_FOLDER_NAME;
    }
    return tmp;
}

bool IvaOutController::usingLocalDirs()
{
    return _localDirs;
}

string IvaOutController::getAppDataDir()
{
    return _appDataDir;
}

IvaOutController::~IvaOutController()
{

  

};

void IvaOutController::printString(IvaOutLogFile & file, IvaString & toPrint)
{

  file.print(toPrint);

  cout << toPrint;

};

void IvaOutController::print(IvaString & file, IvaString & toPrint)
{

  files_[file] = file;
  
  printString(files_[file],toPrint);

};



void IvaOutController::print(IvaString & toPrint)
{

  print(defaultLogFile_, toPrint);

};

void IvaOutController::close () 
{

  for (map<IvaString,IvaOutLogFile>::iterator a = files_.begin(); a != files_.end(); ) {
  
    map<IvaString,IvaOutLogFile>::iterator b = a;
    a++;
    files_.erase(b);
    
  };

};

IvaOutController ivaOutControllerCtx;
