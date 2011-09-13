#include "Directory.h"
#include <sys/stat.h>
#include <errno.h>
#include <unistd.h>

bool Directory::create()
{

  if (mkdir(name_.c_str(), S_IRWXU | S_IRWXG | S_IROTH | S_IXOTH) != 0) {
    if (errno != EEXIST) { // já existe, não é erro
      return false;
    };
  };

  return true;

};

bool Directory::remove()
{

  if (!exists()) {
	  return false;
  };
  if (!rmdir(name_.c_str())) {
    return true;
  };
  return false;
  

};

//Added by Andre 09/11/2010
bool Directory::exists()
{
    struct stat st;
    if (stat(name_.c_str(),&st) == 0) return true;
    else return false;

};
