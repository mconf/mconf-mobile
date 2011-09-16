#include "CommonLeaks.h"
#include "Directory.h"
#include <sys/stat.h>
#include <errno.h>
#include "CommonLeaksCpp.h"

Directory::Directory(IvaString name)
{
    name_ = name;
  //create();
};

Directory::~Directory()
{
};

