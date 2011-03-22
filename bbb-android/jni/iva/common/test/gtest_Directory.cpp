#include "Directory.h"
#include <gtest/gtest.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
using namespace std;

namespace {

    class DirectoryTest :
        public ::testing::Test
    {

    protected:

        DirectoryTest()
        {
        }

        virtual ~DirectoryTest() {
        }

        virtual void SetUp() {
        }

        virtual void TearDown() {
        }

    public:

        Directory * teste1;
    };
};

TEST_F(DirectoryTest, CreateAndRemove)
{
    teste1 = new Directory("diretorio_unico");
    teste1->create();
    EXPECT_TRUE(teste1->exists());
    teste1->remove();
    EXPECT_FALSE(teste1->exists());
}

TEST_F(DirectoryTest, ReCreateAndRemove)
{
    teste1 = new Directory("diretorio_unico");
    teste1->create();
    EXPECT_TRUE(teste1->exists());
    teste1->remove();
    EXPECT_FALSE(teste1->exists());
}


