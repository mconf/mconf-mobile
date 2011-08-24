#include <gtest/gtest.h>

using namespace std;

int main(int argc, char **argv)
{
    ::testing::InitGoogleTest(&argc, argv);
    if (RUN_ALL_TESTS() != 0)
        system("pause");
}

